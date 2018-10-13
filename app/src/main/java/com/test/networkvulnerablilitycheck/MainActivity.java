package com.test.networkvulnerablilitycheck;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com .test.networkvulnerablilitycheck.Util.LogHistory;
import com.test.networkvulnerablilitycheck.Util.PwdCheck;
import com.test.networkvulnerablilitycheck.Util.RouterCheck;
import com.test.networkvulnerablilitycheck.Util.WIFIUtil;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    static public LogHistory logHistory;
    static public WifiManager wifiManager;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }


        logHistory  = new LogHistory(getFilesDir().getAbsolutePath());

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        button = (ImageButton) findViewById(R.id.check_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, WiFiCheckActivity.class));
                wificheck();
            }
        });


    }

    public void onClickLog(View view){
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    public void wificheck()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //final Intent intent = new Intent(this, SearchDeviceActivity.class);

        if (mWifi.isConnected()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("와이파이 비밀번호 입력");
            builder.setMessage("정확한 검사를 위해 필요합니다!");
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            input.setLayoutParams(lp);
            builder.setView(input);

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                    String WifiPwd = "";
                    Log.i("Wifipwd11",WifiPwd);
                    if (input.getText().toString().length() !=  0) {
                        WifiPwd = input.getText().toString();

                    } else {
                        WifiPwd = "";
                    }
                    Log.i("Wifipwd1",WifiPwd);
                    String WifiPwdCheckResult ;
                    WIFIUtil wifiUtil = WIFIUtil.getInstance();
                    if(wifiUtil.isConnectWIFI(getApplicationContext())) {
                        wifiUtil.setPwd(WifiPwd);
                        if(wifiUtil.WifiPwdCheck(getApplicationContext())) {
                            Log.i("테스트", "비밀번호가 정확합니다.");

                            //권한 확인
                            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                            if(permissionCheck== PackageManager.PERMISSION_DENIED){
                                Log.i("테스t", "권한없음");
                                String WifiEncryptChResult = "확인필요. 위치 권한이 없어 Wifi 통신 암호 형식을 확인할 수 없습니다.";
                                Log.i("테스t", WifiEncryptChResult );
                            }else{
                                //Log.i("테스t", "권한있음");

                                String WifiEncryptChResult  = wifiUtil.EncryptCheck(getApplicationContext());
                                Log.i("테스t", WifiEncryptChResult );


                                PwdCheck pwdCheck = new PwdCheck();
                                WifiPwdCheckResult = pwdCheck.verify(WifiPwd);
                                WifiPwdCheckResult = WifiPwdCheckResult + "\r\n" + pwdCheck.Pwd10000(getApplicationContext(), WifiPwd);
                                Log.i("테스t",WifiPwdCheckResult);
                                startActivity(new Intent(MainActivity.this, SearchDeviceActivity.class));
                                dialog.cancel();
                            }
                        } else {
                            Log.i("테스트", "비밀번호가 맞지 않습니다.");
                            dialog.cancel();
                        }
                    } else {
                        Log.i("테스트", "Wifi 연결이 정확하지 않음");
                        //검사 취소
                        dialog.cancel();
                    }
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create();
            builder.show().getWindow().setLayout(850,630);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("경고");
            builder.setMessage("와이파이를 연결해주세요!");
            builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create();
            builder.show().getWindow().setLayout(800,500);
        }
    }
}
