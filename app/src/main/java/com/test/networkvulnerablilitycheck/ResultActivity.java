package com.test.networkvulnerablilitycheck;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.test.networkvulnerablilitycheck.Adapter.CustomExpandableListViewAdapter;
import com.test.networkvulnerablilitycheck.List.ChildListData;

import java.util.ArrayList;
import java.util.HashMap;

import static com.test.networkvulnerablilitycheck.MainActivity.logHistory;


public class ResultActivity extends AppCompatActivity {
    public ExpandableListView expandableListView; // ExpandableListView 변수 선언
    public CustomExpandableListViewAdapter mCustomExpListViewAdapter; // 위 ExpandableListView를 받을 CustomAdapter(2번 class에 해당)를 선언
    public ArrayList<String> parentList; // ExpandableListView의 Parent 항목이 될 List 변수 선언
    public ArrayList<ChildListData> fruit; // ExpandableListView의 Child 항목이 될 List를 각각 선언
    public ArrayList<ChildListData> vegetables;
    public ArrayList<ChildListData> etc;
    public HashMap<String, ArrayList<ChildListData>> childList; // 위 ParentList와 ChildList를 연결할 HashMap 변수 선언



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity); // activity_main.xml을 MainActivity에 연결

        Intent intent = getIntent();

        String[][] asLog = logHistory.loadLog(intent.getStringExtra("Dirname"));

        parentList = new ArrayList<String>();
        childList = new HashMap<String, ArrayList<ChildListData>>();

        for(int i=0; i < asLog.length; ++i) {

            parentList.add(asLog[i][0]);
            fruit = new ArrayList<ChildListData>();

            for(int j=1; j < asLog[i].length; ++j) {
                ChildListData apple = new ChildListData(getResources().getDrawable(R.drawable.open), asLog[i][j]);
                fruit.add(apple);
            }

            childList.put(parentList.get(i), fruit);
        }


        expandableListView = (ExpandableListView)findViewById(R.id.iotresult);
        mCustomExpListViewAdapter = new CustomExpandableListViewAdapter(this, parentList, childList);
        expandableListView.setAdapter(mCustomExpListViewAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //각 항목 클릭했을때

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = inflater.inflate(R.layout.detail_activity, null);
                TextView txt = (TextView) v1.findViewById(R.id.detail_1);
                txt.setText("테스트");//이쪽에 DB값 넣기

                AlertDialog.Builder dialog = new AlertDialog.Builder(ResultActivity.this);
                dialog.setView(v1);
                dialog.setTitle("보안 가이드");
                dialog.show();
            return false;
            }
        });
    }

}