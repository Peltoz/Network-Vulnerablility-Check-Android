package com.test.networkvulnerablilitycheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.test.networkvulnerablilitycheck.MainActivity.logHistory;


public class LogActivity extends Activity {

    ArrayList<String> items;
    ArrayAdapter adapter;
    ListView logResult;
    public void onCreate(Bundle savedInstanceSate)
    {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.log);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);


        logResult = (ListView)findViewById(R.id.loglist);
        logResult.setAdapter(adapter);

        final String[] asLog = logHistory.loadDir();

        for(int i = 1; i < asLog.length; i+=2) {
            items.add(asLog[i]);
        }

        logResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LogActivity.this, ResultActivity.class);
                intent.putExtra("Dirname", asLog[position*2]);
                startActivity(intent);
            }
        });
    }
}
