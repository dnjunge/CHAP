package com.chap.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.chap.R;
import com.chap.other.DatabaseHelper;
import com.chap.other.MyXAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class CountySummaryViewActivity extends AppCompatActivity {

    SharedPreferences userPref;
    public static SharedPreferences.Editor editPref;
    String currentCounty;
    int currentCountyID;
    private DatabaseHelper myDB;
    static final String FIRST_LINE_CHART = "number_of_secondary_school_enrolment_by_county_2007_2013";
    static final String SECOND_LINE_CHART = "county_full_immunization_coverage_rate_for_children_under_one_year_2011_to_2013";
    static final String[] POPULATION_DB = {"kenya_population_per_county", "Population"};
    static final String[] POVERTY_RATE_DB = {"County_Poverty_Rates20056", "poverty_rate__percent"};
    static final String[] POVERTY_NUM_DB = {"County_Poverty_Rates20056", "number_of_poor_2005"};
    static final String[] PHYSICIAN_DB = {"kenya_physicians_density_per_county", "PhysiciansDensity"};
    static final String[] ELEC_ACCESS_DB = {"kenya_access_to_electricity_rates_per_county", "Access_to_electricity"};
    static final String[] DEV_INDEX_DB = {"kenya_human_development_index_per_county", "HumanDevelopmentIndex"};

    TableLayout tableLayout;
    String titleLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_county_summary_view);

        userPref = this.getSharedPreferences("userPref", MODE_PRIVATE);
        currentCounty = userPref.getString("currentCounty", null);
        currentCountyID = userPref.getInt("currentCountyID",-1);
        myDB = new DatabaseHelper(this);

        setContentView(R.layout.activity_county_summary_view);
        tableLayout=(TableLayout)findViewById(R.id.county_summary_table);

        titleLabel = String.format(" %s County Health Stats", currentCounty);
        getSupportActionBar().setTitle(titleLabel);

        int tbl = 0;
        int col = 1;


        addRowToTable(POPULATION_DB[col], getCountyStats(POPULATION_DB[tbl], POPULATION_DB[col], currentCountyID));
        addRowToTable(POVERTY_NUM_DB[col], getCountyStats(POVERTY_NUM_DB[tbl], POVERTY_NUM_DB[col], currentCountyID));
        addRowToTable(POVERTY_RATE_DB[col], getCountyStats(POVERTY_RATE_DB[tbl], POVERTY_RATE_DB[col], currentCountyID));
        addRowToTable(PHYSICIAN_DB[col], getCountyStats(PHYSICIAN_DB[tbl], PHYSICIAN_DB[col], currentCountyID));
        addRowToTable(ELEC_ACCESS_DB[col], getCountyStats(ELEC_ACCESS_DB[tbl], ELEC_ACCESS_DB[col], currentCountyID));
        addRowToTable(DEV_INDEX_DB[col], getCountyStats(DEV_INDEX_DB[tbl], DEV_INDEX_DB[col], currentCountyID));

        int firstChartArea = R.id.Sec_School_Line_chart;
        createLineChart(FIRST_LINE_CHART, currentCounty, firstChartArea, currentCountyID);

        int secondChartArea = R.id.immunization_Line_chart;
        createLineChart(SECOND_LINE_CHART, currentCounty, secondChartArea, currentCountyID);

    }

    public void createLineChart(String Table, String County, int R, int CountyID){
        String[] DataColumns = myDB.getColumnHeaders(Table);
        LineChart lineChart = (LineChart)findViewById(R);

        ArrayList<String> xValues = new ArrayList<String>();
        for(int i=0; i < DataColumns.length; i++) {
            if (DataColumns[i].equals("_id") || (DataColumns[i].equals("County"))) {

            }
            else {
                xValues.add(DataColumns[i]);
            }
        }

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        Float tableRecord;
        String record;

        ArrayList<Entry> yValues = new ArrayList<Entry>();
        float counter = 0;
        for(int i=0; i < xValues.size(); i++) {
            record = myDB.getDatabaseRecordwithID(Table, xValues.get(i), CountyID);
            tableRecord = Float.parseFloat(record);
            yValues.add(new Entry(counter, tableRecord));
            counter++;
        }

        LineDataSet set = new LineDataSet(yValues, County);
        //set.setColor(countyColors[x]);
        dataSets.add(set);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xValues));


        LineData data = new LineData(dataSets);
        lineChart.setData(data);

        lineChart.getDescription().setText(Table);

    }

    public String getCountyStats(String Table, String Column, int CountyID){

        return  myDB.getDatabaseRecordwithID(Table, Column, CountyID);

    }

    public void addRowToTable(String header, String data){

        ArrayList<String> colText = new ArrayList<String>();

        colText.add(header);
        colText.add(data);

        // data rows

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        for(String text:colText) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setPadding(5, 5, 5, 5);
            tv.setBackground(getResources().getDrawable(R.drawable.table_view_backgrd));
            tv.setText(text);
            row.addView(tv);
        }
        tableLayout.addView(row);
    }

}