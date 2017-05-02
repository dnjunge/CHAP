package com.chap;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_county_summary_view);

        userPref = this.getSharedPreferences("userPref", MODE_PRIVATE);
        currentCounty = userPref.getString("currentCounty", null);
        currentCountyID = userPref.getInt("currentCountyID",-1);
        myDB = new DatabaseHelper(this);

        setContentView(R.layout.activity_county_summary_view);
       // ListView mainLv = (ListView) findViewById(R.id.summary_list_view);

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

   }

}
