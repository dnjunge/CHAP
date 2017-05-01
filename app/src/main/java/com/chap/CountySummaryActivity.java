package com.chap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CountySummaryActivity extends AppCompatActivity {
    private ArrayList columnContent;
    private DatabaseHelper myDB;
    ListView countyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(this);
        columnContent = myDB.getEntriesInCol("County", "kenya_population_per_county");

        setContentView(R.layout.activity_county_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        countyList = (ListView)findViewById(R.id.county_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.content_county_summary, R.id.county_textView, columnContent);
        countyList.setAdapter(arrayAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        myDB.close();
    }
}
