package com.test.networkvulnerablilitycheck;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WiFiCheckActivity extends AppCompatActivity{

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final Intent intent = new Intent(this, SearchDeviceActivity.class);

        if (mWifi.isConnected()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(WiFiCheckActivity.this);
            builder.setTitle("와이파이 비밀번호 입력");
            builder.setMessage("정확한 검사를 위해 필요합니다!");

            final EditText input = new EditText(WiFiCheckActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            input.setLayoutParams(lp);
            builder.setView(input);
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(intent);
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.create();
            builder.show().getWindow().setLayout(750,630);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(WiFiCheckActivity.this);
            builder.setTitle("경고");
            builder.setMessage("와이파이를 연결해주세요!");
            builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create();
            builder.show().getWindow().setLayout(700,500);
        }
    }
}