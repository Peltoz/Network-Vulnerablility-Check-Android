package com.test.networkvulnerablilitycheck;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.test.networkvulnerablilitycheck.Util.IoTCheck;
import com.test.networkvulnerablilitycheck.Util.LogHistory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Result;

public class ProgressActivity extends AppCompatActivity {

    Handler handler = new Handler();
    int value = 0; // progressBar 값
    int add = 1; // 증가량, 방향

    private ExecutorService mPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_activity);

        ImageView glass = (ImageView) findViewById(R.id.glasshour);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(glass);
        Glide.with(this).load(R.drawable.hourglass).into(gifImage);


        final Intent intent = new Intent(this, ResultActivity.class);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);


        Thread t = new Thread(new Runnable() {

            Intent getintent = getIntent();
            String[] asIP = getintent.getStringArrayExtra("ip");
            int i;
            boolean go = true;

            @Override
            public void run() { // Thread 로 작업할 내용을 구현
                mPool = Executors.newFixedThreadPool(20);


                for (i = 0; i < asIP.length; ++i) {

                    if (asIP[i] != null) {
                        Log.i("where", Integer.toString(i));
                        //new CheckTask(asIP[i]).execute();
                        launch(asIP[i]);
                    }
                }

                while (go) {
                    value = value + i;
                    Log.i("asIP", Integer.toString(asIP.length));
                    Log.i("value.", Integer.toString(value));
                    handler.post(new Runnable() {
                        @Override
                        public void run() { // 화면에 변경하는 작업을 구현
                            pb.setProgress(value/(15*asIP.length));
                            if (value == 1200*asIP.length) {
                                pb.setProgress(100);
                                intent.putExtra("Dirname", LogHistory.sDName);
                                startActivity(intent);
                                go = false;
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                Thread.currentThread().interrupt();
                            }
                        }
                    });

                    try {
                        Thread.sleep(100); // 시간지연
                    } catch (InterruptedException e) {
                    }
                } // end of while
            }
        });

        t.start(); // 쓰레드 시작
    }// end of oncreate


    private void launch(String sIP) {
        if (!mPool.isShutdown()) {
            mPool.execute(new CheckTask(sIP));
        }
    }
    private class CheckTask implements Runnable {

        String sIP;


        CheckTask(String sIP) {
            this.sIP = sIP;
        }

        public void run() {
            Log.i("AAAAAA",sIP);
            new IoTCheck(sIP);
        }

    }

}
/*    private class CheckTask extends AsyncTask<Void, String, Void> {

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

    }*/

