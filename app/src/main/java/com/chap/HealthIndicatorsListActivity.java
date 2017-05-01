//Danson Njunge
//4/26/2017
// Activity to display a list of health indicators, list depens on tables in the database

package com.chap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class HealthIndicatorsListActivity extends AppCompatActivity {

    private ArrayList tableNames;
    private DatabaseHelper myDB;
    ListView indicatorsList;
    String selectedIndicator;

    SharedPreferences userPref;
    public static SharedPreferences.Editor editPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(this);
        tableNames = myDB.getTableNames();
        tableNames.remove("android_metadata");
        tableNames.remove("Chart_Type");

        userPref = this.getSharedPreferences("userPref", MODE_PRIVATE);
        editPref = userPref.edit();

        setContentView(R.layout.activity_health_indicators_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        indicatorsList = (ListView) findViewById(R.id.indicators_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.content_health_indicators_list, R.id.indicators_textView, tableNames);
        indicatorsList.setAdapter(arrayAdapter);

        indicatorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndicator = (String) (indicatorsList.getItemAtPosition(position));
                Toast.makeText(HealthIndicatorsListActivity.this, selectedIndicator, Toast.LENGTH_SHORT).show();
                editPref.putString("currentIndicator", selectedIndicator);
                editPref.commit();
                Intent intent = new Intent(HealthIndicatorsListActivity.this, HealthIndicatorViewActivity.class);
                startActivity(intent);
            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }

            ;
        });

    }

    public String getActivityData() {
        return selectedIndicator;
    }

    @Override
    protected void onPause() {
        super.onPause();
        myDB.close();
    }
}
