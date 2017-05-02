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

public class CountySummaryActivity extends AppCompatActivity {
    private ArrayList columnContent;
    private DatabaseHelper myDB;
    ListView countyList;
    String selectedCounty;
    SharedPreferences userPref;
    int countySelectedPosition;
    public static SharedPreferences.Editor editPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(this);
        columnContent = myDB.getEntriesInCol("County", "kenya_population_per_county");
        userPref = this.getSharedPreferences("userPref", MODE_PRIVATE);
        editPref = userPref.edit();

        setContentView(R.layout.activity_county_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        countyList = (ListView)findViewById(R.id.county_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.content_county_summary, R.id.county_textView, columnContent);
        countyList.setAdapter(arrayAdapter);

        getSupportActionBar().setTitle("Select County");

        countyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCounty = (String) (countyList.getItemAtPosition(position));
                countySelectedPosition = position + 1;
                editPref.putString("currentCounty", selectedCounty);
                editPref.putInt("currentCountyID", countySelectedPosition);
                editPref.commit();
                Intent intent = new Intent(CountySummaryActivity.this, CountySummaryViewActivity.class);
                startActivity(intent);
            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }

            ;
        });

    }

    public String getActivityData() {
        return selectedCounty;
    }

    @Override
    protected void onPause() {
        super.onPause();
        myDB.close();
    }
}
