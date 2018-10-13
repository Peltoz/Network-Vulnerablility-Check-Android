package com.test.networkvulnerablilitycheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.test.networkvulnerablilitycheck.Util.IoTCheck;
import com.test.networkvulnerablilitycheck.Util.Netinfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class SearchDeviceActivity extends AppCompatActivity {

    TextView tvWifiState;
    TextView tvScanning;
    ArrayList<InetAddress> inetAddresses;
    ArrayList<String> items;
    ListView tvResult;
    ArrayAdapter adapter;
    Boolean bType = true;
    Intent intent;
    String[] asIP;
    String sIP;
    ScanTask scanTaskA;
    //ScanTask2 scanTaskB;


    protected long start = 0;
    protected long end = 0;
    protected long size = 0;

    private long network_ip = 0;
    private long network_start = 0;
    private long network_end = 0;

    private ExecutorService mPool;
    private int pt_move = 2;

    private final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    private final static int BUF = 8 * 1024;

    ArrayList<String> canonicalHostNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        intent = new Intent(this, ProgressActivity.class);

        scanTaskA = new ScanTask(tvScanning, tvResult);
        //scanTaskB = new ScanTask2(tvScanning, tvResult);

        tvScanning = (TextView)findViewById(R.id.Scanning);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items);



        scanTaskA.execute();

    }


    private class ScanTask extends AsyncTask<Void, String, Void> {

        TextView tvCurrentScanning;
        ListView tvScanResullt;


        ProgressDialog asyncDialog = new ProgressDialog(SearchDeviceActivity.this);

        @Override
        protected void onPreExecute() {

                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("디바이스 검색중..");

                // show dialog
                asyncDialog.show();


                super.onPreExecute();

        }

        public ScanTask(TextView tvCurrentScanning, ListView tvScanResullt) {
            this.tvCurrentScanning = tvCurrentScanning;
            this.tvScanResullt = tvScanResullt;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


                for (int i = 0; i < inetAddresses.size(); i++) {
                    items.add(canonicalHostNames.get(i));
                }

                adapter.notifyDataSetChanged();

                asyncDialog.cancel();

                LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                LinearLayout linearLayout = (LinearLayout) inflater.inflate( R.layout.iotcheck_activity, null);
                setContentView(linearLayout);

                tvResult = (ListView)findViewById(R.id.Result);
                tvResult.setAdapter(adapter);

                Button confButton = (Button)findViewById(R.id.confirm);

                confButton.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View view){
                        SparseBooleanArray checkedItems = tvResult.getCheckedItemPositions();
                        int counter = 0;
                        if (checkedItems != null) {
                            int length = checkedItems.size();
                            for (int i = 0; i < length; i++) {
                                if (checkedItems.get(checkedItems.keyAt(i))) {
                                    counter++;
                                }
                            }
                        }
                        int count = counter;
                        Log.i("DDDDDD222", Integer.toString(count));
                        asIP = new String[count];

                        for (int i = count-1, j = 0; i >= 0; i--) {
                            if (checkedItems.get(i) != false){
                                //test.setText(items.remove(i)) ;
                                //String [] ip

                                Log.i("DDDDDD222", Integer.toString(i));
                                Log.i("DDDDD", items.get(i));
                                asIP[j] = items.get(i);
                                ++j;
                            }
                        }

                        // 모든 선택 상태 초기화.
                        tvResult.clearChoices() ;

                        adapter.notifyDataSetChanged();

 /*                       scanTaskB = new ScanTask2(tvScanning, tvResult);
                        scanTaskB.execute();*/


                        intent.putExtra("ip",asIP);
                        startActivity(intent);
                    }
                });

                Button cancButton = (Button)findViewById(R.id.cancel);

                cancButton.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View view){
                        finish();
                    }
                });

                super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... voids) {

                Netinfo net = new Netinfo(getApplicationContext());
                network_ip = Netinfo.getUnsignedLongFromIp(net.ip);
                Log.e("IP=",Netinfo.getIpFromLongUnsigned(network_ip));
                Log.e("cidr=",Netinfo.getIpFromLongUnsigned(net.cidr));


                int shift = (32 - net.cidr);

                if (net.cidr < 31) {
                    network_start = (network_ip >> shift << shift) + 1;
                    network_end = (network_start | ((1 << shift) - 1)) - 1;
                } else {
                    network_start = (network_ip >> shift << shift);
                    network_end = (network_start | ((1 << shift) - 1));
                }
                size = (int) (network_end - network_start + 1);

                if(inetAddresses == null){
                    inetAddresses = new ArrayList<>();
                }
                inetAddresses.clear();

                if(canonicalHostNames == null){
                    canonicalHostNames = new ArrayList<>();
                }
                canonicalHostNames.clear();

                //scanInetAddresses();
                scan();
                return null;

        }



        private void scanInetAddresses(){
            //May be you have to adjust the timeout
            final int timeout = 100;

            if(inetAddresses == null){
                inetAddresses = new ArrayList<>();
            }
            inetAddresses.clear();

            if(canonicalHostNames == null){
                canonicalHostNames = new ArrayList<>();
            }
            canonicalHostNames.clear();

            //For demonstration, scan 192.168.1.xxx only
            byte[] ip = {(byte) 172, (byte) 30, (byte) 1, 0};
            for (int j = 1; j < 100; j++) {
                ip[3] = (byte) j;
                try {
                    //asyncDialog.setProgress(j * 30);

                    InetAddress checkAddress = InetAddress.getByAddress(ip);
                    publishProgress(checkAddress.getCanonicalHostName());
                    if (checkAddress.isReachable(timeout)) {
                        inetAddresses.add(checkAddress);
                        canonicalHostNames.add(checkAddress.getCanonicalHostName());
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    publishProgress(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(e.getMessage());
                }
            }
        }

        private void scan(){

            Log.v("start","start=" + Netinfo.getIpFromLongUnsigned(network_start) + " (" + network_start
                    + "), end=" + Netinfo.getIpFromLongUnsigned(network_end) + " (" + network_end
                    + "), length=" + size);
            mPool = Executors.newFixedThreadPool(30);
            if (network_ip <= network_end && network_ip >= network_start) {
                Log.i("back and forth", "Back and forth scanning");
                // gateway
                launch(network_start);

                // hosts
                long pt_backward = network_ip;
                long pt_forward = network_ip + 1;
                long size_hosts = (size - 1)/2;
                Log.i("info",Netinfo.getIpFromLongUnsigned(network_ip) + Netinfo.getIpFromLongUnsigned(size));
                for (int i = 0; i < size_hosts; i++) {
                    // Set pointer if of limits
                    if (pt_backward <= network_start) {
                        pt_move = 2;
                    } else if (pt_forward > network_end) {
                        pt_move = 1;
                    }
                    // Move back and forth
                    if (pt_move == 1) {
                        launch(pt_backward);
                        pt_backward--;
                        pt_move = 2;
                    } else if (pt_move == 2) {
                        launch(pt_forward);
                        pt_forward++;
                        pt_move = 1;
                    }
                }
            } else {
                Log.i("sequencial", "Sequencial scanning");
                for (long i = network_start; i <= network_end; i++) {
                    launch(i);
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

/*    private class ScanTask2 extends AsyncTask<Void, String, Void>{

        TextView tvCurrentScanning;
        ListView tvScanResullt;

        ProgressDialog asyncDialog2 = new ProgressDialog(SearchDeviceActivity.this);

        protected void onPreExecute() {

            Log.i("AAAAA", "11111");
            asyncDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog2.setMessage("IoT 검사중..");

            // show dialog
            asyncDialog2.show();
            try {
                sleep(3000);
            }catch (Exception e) {

            }

            super.onPreExecute();
        }

        public ScanTask2(TextView tvCurrentScanning, ListView tvScanResullt) {
            this.tvCurrentScanning = tvCurrentScanning;
            this.tvScanResullt = tvScanResullt;
        }

        protected void onPostExecute(Void aVoid){
            try {
                sleep(60000);
                Log.i("CCCCCCC", sIP);
                asyncDialog2.dismiss();
                intent.putExtra("Dirname", LogHistory.sDName);
                startActivity(intent);
                super.onPostExecute(aVoid);
                finish();
            }catch (Exception e) {

                Log.i("CCCCCCC", "ERROR");
            }
        }

        protected Void doInBackground(Void... voids){
            Log.i("EEEEEE", Integer.toString(asIP.length));


            for(int i = 0; i < asIP.length; ++i) {
                sIP = asIP[i];

                if(asIP[i] != null)
                {
                    new CheckTask(asIP[i]).execute();
                }

*//*                switch(i) {
                    case 0: CheckTask taskA = new CheckTask(asIP[i]); taskA.execute(); break;
                    case 1: CheckTask taskB = new CheckTask(asIP[i]); taskB.execute(); break;
                    case 2: CheckTask taskC = new CheckTask(asIP[i]); taskC.execute(); break;
                    case 3: CheckTask taskD = new CheckTask(asIP[i]); taskD.execute(); break;
                    case 4: CheckTask taskE = new CheckTask(asIP[i]); taskE.execute(); break;
                    case 5: CheckTask taskF = new CheckTask(asIP[i]); taskF.execute(); break;
                    case 6: CheckTask taskG = new CheckTask(asIP[i]); taskG.execute(); break;
                    case 7: CheckTask taskH = new CheckTask(asIP[i]); taskH.execute(); break;
                    case 8: CheckTask taskI = new CheckTask(asIP[i]); taskI.execute(); break;
                    case 9: CheckTask taskJ = new CheckTask(asIP[i]); taskJ.execute(); break;
                }*//*
            }
            return null;
        }


    }*/

    private void launch(long i) {
        if(!mPool.isShutdown()) {
            mPool.execute(new CheckRunnable(Netinfo.getIpFromLongUnsigned(i)));
        }
    }

    private class CheckRunnable implements Runnable {

        private String addr;

        CheckRunnable(String addr) {
            this.addr = addr;
        }

        public void run() {



/*                if(isCancelled()) {
                    publish(null);
                }*/
            Log.e("run", "run="+addr);
            // Create host object
            //host.responseTime = getRate();
            try {
                InetAddress h = InetAddress.getByName(addr);
                // Rate control check

                // Arp Check #1
                if(!Netinfo.NOMAC.equals(getHardwareAddress(addr))){
                    Log.e(TAG, "found using arp #1 "+addr);
                    inetAddresses.add(h);
                    canonicalHostNames.add(h.getCanonicalHostName());
                    return;
                }
                // Native InetAddress check
                if (h.isReachable(500)) {
                    Log.e(TAG, "found using InetAddress ping "+addr);
                    //inetAddresses.add(h);
                    inetAddresses.add(h);
                    canonicalHostNames.add(h.getCanonicalHostName());
                    // Set indicator and get a rate
/*                        if (doRateControl && mRateControl.indicator == null) {
                            mRateControl.indicator = addr;
                            mRateControl.adaptRate();
                        }*/
                    return;
                }
                // Arp Check #2
                if(!Netinfo.NOMAC.equals(getHardwareAddress(addr))){
                    Log.e(TAG, "found using arp #2 "+addr);
                    inetAddresses.add(h);
                    canonicalHostNames.add(h.getCanonicalHostName());
                    return;
                }
                // Custom check
/*                    int port;
                    // TODO: Get ports from options
                    Socket s = new Socket();
                    for (int i = 0; i < DPORTS.length; i++) {
                        try {
                            s.bind(null);
                            s.connect(new InetSocketAddress(addr, DPORTS[i]), getRate());
                            Log.v(TAG, "found using TCP connect "+addr+" on port=" + DPORTS[i]);
                        } catch (IOException e) {
                        } catch (IllegalArgumentException e) {
                        } finally {
                            try {
                                s.close();
                            } catch (Exception e){
                            }
                        }
                    }*/

                    /*
                    if ((port = Reachable.isReachable(h, getRate())) > -1) {
                        Log.v(TAG, "used Network.Reachable object, "+addr+" port=" + port);
                        publish(host);
                        return;
                    }
                    */
                // Arp Check #3
                if(!Netinfo.NOMAC.equals(getHardwareAddress(addr))){
                    Log.e(TAG, "found using arp #3 "+addr);
                    inetAddresses.add(h);
                    canonicalHostNames.add(h.getCanonicalHostName());
                    return;
                }

            } catch (IOException e) {

                Log.e(TAG, e.getMessage());
            }
        }
    }

    public  String getHardwareAddress(String ip) {
        String hw = Netinfo.NOMAC;
        BufferedReader bufferedReader = null;
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), BUF);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
            } else {
                Log.e(TAG, "ip is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't open/read file ARP: " + e.getMessage());
            return hw;
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return hw;
    }


    private class CheckTask extends AsyncTask<Void, String, Void> {

        String sIP;

        CheckTask (String sIP) {
            this.sIP = sIP;
        }

        @Override
        protected void onPreExecute() {
                super.onPreExecute();
        }



        @Override
        protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {


            new IoTCheck(sIP);


            return null;
        }

    }

}