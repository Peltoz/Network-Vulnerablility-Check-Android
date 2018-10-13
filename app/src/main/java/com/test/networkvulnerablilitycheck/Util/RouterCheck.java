package com.test.networkvulnerablilitycheck.Util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;

import com.test.networkvulnerablilitycheck.MainActivity;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.test.networkvulnerablilitycheck.MainActivity.wifiManager;

public class RouterCheck {

    String sIP="";

    public RouterCheck() {

        //checkRouterIP();
       //checkPassword();
       checkSSID();
        //checkAdminPage();
        try {
            Thread.sleep(100000);

        }catch (Exception e){

        }
    }
/*
    public void checkPassword() {

    }

    public void checkRouterIP( ) {
        WifiManager wifiManager = (WifiManager) .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        sIP = Formatter.formatIpAddress(wifiInfo.getIpAddress());

        return ;
    }
*/

    public void checkSSID( ) {

       // WifiManager wifiManager = (WifiManager)mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifiInfo.getIpAddress();
       // logHistory.saveLog(sIP, wifiInfo.getSSID());
        //checkSecProtocol(wifiInfo.getSSID());
        if(wifiInfo.getHiddenSSID()){
            Log.i("TTTTT", "SUCCCCCC");
        }else{
            Log.i("TTTTT", "FFFFFFFF");
        }


        return ;
    }
    /*

    public void checkSecProtocol(String sSSID) {

        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> lScanResult = wifiManager.getScanResults();

            for(ScanResult scanResult: lScanResult) {
                if(sSSID.equals("\"" + scanResult.SSID + "\"")) {
                 //   logHistory.saveLog(sIP, scanResult.capabilities);
                    break;
                }
            }
        } catch (Exception e) {
        }

        return ;
    }


    public void checkAdminPage() {

        try {
            URL uTestUrl = new URL("https://" + sIP);

            HttpURLConnection hTestUConnection = (HttpURLConnection) uTestUrl.openConnection();
            InputStreamReader iTestSReader = new InputStreamReader(hTestUConnection.getInputStream());
            //BufferedReader bTestBReader = new BufferedReader(iTestSReader);

            iTestSReader.close();
            hTestUConnection.disconnect();

        } catch (Exception e) {

        }

        return;

    }
    */

}
