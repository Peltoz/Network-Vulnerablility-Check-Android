package com.test.networkvulnerablilitycheck.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Netinfo {
    private final String TAG = "NetInfo";
    private static final String NOIF = "0";
    public static final String NOIP = "0.0.0.0";
    public static final String NOMASK = "255.255.255.255";
    public static final String NOMAC = "00:00:00:00:00:00";

    private Context ctxt;
    private WifiInfo info;
    private SharedPreferences prefs;

    public String intf = "eth0";
    public String ip = NOIP;
    public int cidr = 24;

    public String macAddress = NOMAC;
    public String netmaskIp = NOMASK;
    public String gatewayIp = NOIP;

    public Netinfo(final Context ctxt){
        this.ctxt= ctxt;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        getWifiInfo();
        getIp();
    }
    public void getIp() {
        intf = prefs.getString("interface", null);
        try {
            if (intf == null || NOIF.equals(intf)) {
                // Automatic interface selection
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                        .hasMoreElements();) {
                    NetworkInterface ni = en.nextElement();
                    intf = ni.getName();
                    ip = getInterfaceFirstIp(ni);
                    if (ip != NOIP) {
                        break;
                    }
                }
            } else {
                // Defined interface from Prefs
                ip = getInterfaceFirstIp(NetworkInterface.getByName(intf));
            }
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
            //Editor edit = prefs.edit();
            //edit.putString(Prefs.KEY_INTF, Prefs.DEFAULT_INTF);
            //edit.commit();
        }
        getCidr();
    }

    private String getInterfaceFirstIp(NetworkInterface ni) {
        if (ni != null) {
            for (Enumeration<InetAddress> nis = ni.getInetAddresses(); nis.hasMoreElements();) {
                InetAddress ia = nis.nextElement();
                if (!ia.isLoopbackAddress()) {
                    if (ia instanceof Inet6Address) {
                        Log.i(TAG, "IPv6 detected and not supported yet!");
                        continue;
                    }
                    return ia.getHostAddress();
                }
            }
        }
        return NOIP;
    }

    private void getCidr() {
            cidr = IpToCidr(netmaskIp);
    }

    public boolean getWifiInfo(){
        WifiManager wifi = (WifiManager)ctxt.getSystemService(Context.WIFI_SERVICE);
        if(wifi != null){
            info = wifi.getConnectionInfo();
            macAddress = info.getMacAddress();
            gatewayIp = getIpFromIntSigned(wifi.getDhcpInfo().gateway);
            netmaskIp = getIpFromIntSigned(wifi.getDhcpInfo().netmask);
            return true;
        }
        return false;
    }

    public static String getIpFromIntSigned(int ip_int) {
        String ip = "";
        for (int k = 0; k < 4; k++) {
            ip = ip + ((ip_int >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    public String getNetIp(){
        int shift = (32 - cidr);
        int start = (int) (getUnsignedLongFromIp(ip) >> shift << shift);
        return getIpFromLongUnsigned((long) start);
    }

    public static String getIpFromLongUnsigned(long ip_long) {
        String ip = "";
        for (int k = 3; k > -1; k--) {
            ip = ip + ((ip_long >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }
    public static long getUnsignedLongFromIp(String ip_addr) {
        String[] a = ip_addr.split("\\.");
        return (Integer.parseInt(a[0]) * 16777216 + Integer.parseInt(a[1]) * 65536
                + Integer.parseInt(a[2]) * 256 + Integer.parseInt(a[3]));
    }

    private int IpToCidr(String ip) {
        double sum = -2;
        String[] part = ip.split("\\.");
        for (String p : part) {
            sum += 256D - Double.parseDouble(p);
        }
        return 32 - (int) (Math.log(sum) / Math.log(2d));
    }
}
