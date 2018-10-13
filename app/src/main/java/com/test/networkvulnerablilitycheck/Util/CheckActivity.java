package com.test.networkvulnerablilitycheck.Util;


import com.test.networkvulnerablilitycheck.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckActivity implements Runnable {
    Boolean bScan = false;
    String[] asIP;
    String sIP;

    public CheckActivity(MainActivity mainActivity) {

        String[] abc = {"abc", "def","dfaf"};
        try{
            Thread thread = new Thread(this);
            thread.start();

            
            bScan = true;

            for(String sIP : asIP) {
                this.sIP = sIP;
                Thread thread1 = new Thread(this);
                thread1.start();

            }
        } catch(Exception e) {

        }

    }

    public String[] scanIoTDev() {

        return new String[10];
    }

    public void run() {

        if(bScan) {
            new IoTCheck(sIP);
        }else //new RouterCheck(mainActivity);

        return;
    }
}
