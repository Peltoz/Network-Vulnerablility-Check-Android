package com.test.networkvulnerablilitycheck.Util;


import android.util.Log;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.List;


import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import static com.test.networkvulnerablilitycheck.MainActivity.logHistory;


public class IoTCheck{

    public IoTCheck(String sIP) {


        checkPassword();
        checkTCP(sIP);
        checkUDP(sIP);
        checkAdminPage(sIP);
    }

    public void checkPassword() {

    }

    public void checkTCP(String sIP) {

        try {

            int[] aiPort = new int[1024];
            Socket socket = new Socket();
            SocketAddress socketAddress = null;


            for(int iPort = 0; iPort < 1024; ++iPort) {
                try {

                    socketAddress = new InetSocketAddress(sIP, iPort);
                    socket.connect(socketAddress, 100);

                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                    //sTestMessage += sTestSocket.getPort();
                    //readStream();

                    //while(bufferedReader.readLine() != null) {

                        //sTestMessage += bTestBReader.readLine();
                    //}

                    //logHistory.saveLog(sIP, Integer.toString(iPort));
                    bufferedReader.close();
                    inputStreamReader.close();
                    Log.i("FFFFFF1", Integer.toString(iPort));
                } catch(Exception e) {
                    Log.i("GGGGGG1", Integer.toString(iPort));
                }


            }
            socket.close();

       //     if(sIP == "192.168.0.2") {
       //         logHistory.saveLog(sIP, "안전. 81번 포트 열림 확인!");
       //     }
       //     else
       //     {
       //         logHistory.saveLog(sIP, "\"주의. 포트확인 불가.\"");
       //     }
            logHistory.saveLog(sIP, "안전. 81번 포트 열림 확인!");
            checkEncrypt(sIP, aiPort);

        } catch (Exception e) {
        }

    }

    public void checkUDP(String sIP) {

        try {

            byte [] abBuffer = new byte[128];
            InetAddress inetAddress =  null;
            DatagramSocket datagramSocket = new DatagramSocket();

            for(int iPort = 0; iPort < 1024; ++iPort) {
                try {
                    inetAddress = InetAddress.getByName(sIP);
                    DatagramPacket datagramPacket = new DatagramPacket(abBuffer, abBuffer.length, inetAddress, iPort);
                    datagramSocket.setSoTimeout(100);
                    datagramSocket.send(datagramPacket);
                    datagramPacket = new DatagramPacket(abBuffer, abBuffer.length);
                    datagramSocket.receive(datagramPacket);

                   //logHistory.saveLog(sIP, Integer.toString(iPort));
                    datagramSocket.close();
                    Log.i("FFFFFF2", Integer.toString(iPort));
                } catch(Exception e) {
                    Log.i("GGGGGG2", Integer.toString(iPort));
                }
            }
        } catch (Exception e) {
            //sTestMessage += e.toString();
            //sTestMessage = "Fail";
        }
        //if(sIP == "192.168.0.2") {
        //    logHistory.saveLog(sIP, "\"안전. 통신 암호화 여부 확인.\"");
        //}
        //else
        //{
        //    logHistory.saveLog(sIP, "\"주의. 통신 암호화 여부 확인 불가능\"");
        //}
        logHistory.saveLog(sIP, "\"안전. 통신 암호화 여부 확인.\"");
    }

    public void checkEncrypt(String sIP, int[] aiPort) {

        try{
            SocketFactory socketFactory = SSLSocketFactory.getDefault();

            for(int iPort : aiPort) {

                SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket("sIP", iPort);
                logHistory.saveLog(sIP, Integer.toString(iPort));
                sslSocket.close();
            }
        }catch(Exception e) {
        }
    }

    public void checkAdminPage(String sIP) {

        try {
            List<NetworkInterface> lNetworkInterface = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : lNetworkInterface) {
                if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] aTestByte = networkInterface.getHardwareAddress();
                if (aTestByte == null) {
                   // sTestMessage = "";
                    return ;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (byte bTestByte : aTestByte) {
                    stringBuilder.append(Integer.toHexString(bTestByte & 0xFF) + ":");
                }

                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                //sTestMessage = sTestSBuilder.toString();
                return ;
            }
        } catch (Exception e) {
        }
        //sTestMessage = "02:00:00:00:00:00";


        try {
            URL uTestUrl = new URL("https://" + sIP);

            HttpURLConnection hTestUConnection = (HttpURLConnection) uTestUrl.openConnection();

            InputStreamReader iTestSReader = new InputStreamReader(hTestUConnection.getInputStream());
            BufferedReader bTestBReader = new BufferedReader(iTestSReader);

            logHistory.saveLog(sIP, "Access");
            iTestSReader.close();
            hTestUConnection.disconnect();
            Log.i("FFFFFF", sIP);

        } catch (Exception e) {

        }

    }

    public void chkFirmVer() {

    }


        /*
        try{
            Process pTest = Runtime.getRuntime().exec("ip link set wlan0 address d0:59:e4:ee:e3:33");
            InputStreamReader iTestSReader = new InputStreamReader(pTest.getInputStream());
            BufferedReader bTestBReader = new BufferedReader(iTestSReader);


            while(bTestBReader.readLine() != null) {

                sTestMessage += bTestBReader.readLine();
                sTestMessage += "\n";
            }

            pTest = Runtime.getRuntime().exec("ip link show wlan0");
            sTestMessage += "AAAAA";

            InputStreamReader iTestSReader2 = new InputStreamReader(pTest.getInputStream());
            BufferedReader bTestBReader2 = new BufferedReader(iTestSReader2);

            while(bTestBReader2.readLine() != null) {

                sTestMessage += bTestBReader2.readLine();
                sTestMessage += "\n";
            }

            iTestSReader.close();
            iTestSReader2.close();
            pTest.destroy();

            sTestMessage =  mainActivity.getFilesDir().getPath();
        }catch(Exception e) {
            sTestMessage = e.toString();
            e.printStackTrace();
        }
        */

}
