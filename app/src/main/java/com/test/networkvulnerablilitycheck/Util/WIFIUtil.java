package com.test.networkvulnerablilitycheck.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WIFIUtil {
    private static WIFIUtil __sharedWifiUtil = null;

    private String mSSID = null;
    private String mPwd = null;
    private String typeEncrypt = "";
    private int networkId;

    //1회 객체 생성
    public static WIFIUtil getInstance() {
        if ( __sharedWifiUtil == null )
            __sharedWifiUtil = new WIFIUtil();
        return __sharedWifiUtil;
    }

    private WIFIUtil()
    {
        super(); //명시적 입력
    }


    public String EncryptCheck(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> networkList = wifiManager.getScanResults();
        Log.i ("테스t", "ScanResult 갯수 : " + String.valueOf(networkList.size()));
        String typeEncrypt = "";
        if (networkList != null && networkList.size() != 0) {
            for (ScanResult network : networkList)
            {
                //check if current connected SSID
                //String Capabilities =  network.capabilities;
                //Log.d ("테스t", network.SSID + " capabilities : " + Capabilities);

                if (mSSID.equals(network.SSID)){
                    //get capabilities of current connection
                    String Capabilities =  network.capabilities;
                    Log.d ("테스t", network.SSID + " capabilities : " + Capabilities);

                    if (Capabilities.contains("WPA2")) {
                        typeEncrypt = "안전. 사용중인 Wifi 암호형식인 WPA2은 보안수준이 높습니다.";
                        if (Capabilities.contains("WPA-")) {
                            typeEncrypt = "안전. 사용중인 Wifi 암호형식인 WPA/WPA2은 보안수준이 높습니다.";
                        }
                    }
                    else if (Capabilities.contains("WPA")) {
                        typeEncrypt = "주의. 사용중인 Wifi의 암호형식인 WPA은 보안수준이 낮습니다. WPA2로 변경 권장합니다.";
                    }
                    else if (Capabilities.contains("WEP")) {
                        typeEncrypt = "경고. 사용중인 Wifi의 암호형식인 WEP은 보안수준이 매우 낮습니다. 즉시 변경이 필요합니다.";
                    } else {
                        typeEncrypt = "확인필요. 사용중인 Wifi의 암호형식이 발견되지 않았습니다. 관리자의 확인이 필요합니다.";
                    }
                }
            }
        }
        Log.i ("테스t", "암호화 타입 :" + typeEncrypt);
        return typeEncrypt;
    }

    //Pwd 설정
    public void setPwd(String Pwd) {
        Log.i("테스트", "Wifi 패스워트 입력 완료");
        mPwd = Pwd;
    }

    //와이파이 연결
    public boolean WifiPwdCheck(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        int networkId = wifiManager.getConnectionInfo().getNetworkId();
        Log.i("테스트", "networkId is: " + networkId);

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + mSSID + "\"";
        conf.preSharedKey = "\"" + mPwd + "\"";

        wifiManager.disableNetwork(networkId);
        wifiManager.removeNetwork(networkId);
        wifiManager.disconnect();

        List<WifiConfiguration> configurations_2 = wifiManager.getConfiguredNetworks();
        //Log.i("테스트", "configurations is: " + configurations_2);

        int networkId_3 = wifiManager.addNetwork(conf);
        wifiManager.enableNetwork(networkId_3, true);
        wifiManager.reconnect();
        Log.i("테스트", "networkId is: " + networkId_3);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {

        }

        List<WifiConfiguration> configurations_3 = wifiManager.getConfiguredNetworks(); //configurations is null
        //Log.i("테스트", "configurations is: " + configurations_3); //error thrown here


        WifiManager wifiManager_2 = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        int networkId_2 = wifiManager_2.getConnectionInfo().getNetworkId();
        Log.i("테스트", "networkId is: " + networkId_2);

        if( networkId_2 == -1) {
            wifiManager.removeNetwork(networkId_3);
            return false; //연결실패
        } else {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                Log.i("테스트", "타이머 에러");
            }

            wifiManager.removeNetwork(networkId_3);
            wifiManager.disconnect();
            wifiManager.enableNetwork(networkId, true);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                Log.i("테스트", "타이머 에러");
            }

            WifiManager wifiManager_3 = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            int networkId_4 = wifiManager_3.getConnectionInfo().getNetworkId();
            Log.i("테스트", "networkId is: " + networkId_4);

            return true;
        }
    }


    //Wifi 연결 확인
    public boolean isConnectWIFI(Context context) {
        //인터넷 연결 유형 확인
        boolean bIsWiFiConnect = false;
        ConnectivityManager oManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo oInfo = oManager.getActiveNetworkInfo();
        if(oInfo != null)
        {
            NetworkInfo.State oState = oInfo.getState();
            if(oState == NetworkInfo.State.CONNECTED)
            {
                switch(oInfo.getType())
                {
                    //와이파이 연결시에만 true 및 SSID 받아옴
                    case ConnectivityManager.TYPE_WIFI:
                    WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo;
                    String ssid = null;
                    wifiInfo = wifiManager.getConnectionInfo();

                    if (wifiInfo.getSupplicantState()== SupplicantState.COMPLETED) {
                        ssid = wifiInfo.getSSID().replaceAll("\"", "");
                    }

                    if ( ssid != null ) {
                        bIsWiFiConnect = true;
                        mSSID = ssid;
                        Log.i("테스트", "연결된 Wifi의 SSID를 읽어옴: "+ssid);
                    } else {
                        Log.i("테스트", "작업중지 : ssid를 가져오지 못했습니다.");
                    }
                    break;
                    case ConnectivityManager.TYPE_MOBILE:
                        break;
                    default:
                        break;
                }
            }
        }
        return bIsWiFiConnect;
    }
}
