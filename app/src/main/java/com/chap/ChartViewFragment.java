package com.chap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartViewFragment extends Fragment{

    private ArrayList columnContent;
    private DatabaseHelper myDB;
    String currentTable;
    int[] countyColors;
    int[] stackedBarColors;
    String chartType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(getActivity());
        columnContent = myDB.getEntriesInCol("County", "kenya_population_per_county");
        currentTable = getArguments().getString("currentTable");
        countyColors = getContext().getResources().getIntArray(R.array.county_colors);
        stackedBarColors = getContext().getResources().getIntArray(R.array.stacked_bar_colors);
        chartType = myDB.getChartType(currentTable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int fragmentRef = 0;

        switch (chartType){
            case "Bar":
                fragmentRef = R.layout.fragment_chart_view;
                break;
            case "Multi_Line":
                fragmentRef= R.layout.fragment_chart_view_multiline;
                break;
            case "Stacked_Bar":
                fragmentRef = R.layout.fragment_chart_view_stackedbarchart;
                break;
            default:
                fragmentRef = R.layout.fragment_chart_view;
        }
        // Inflate the layout for this fragment
        view = inflater.inflate(fragmentRef, container, false);
        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        String[] columnHeaders = myDB.getColumnHeaders(currentTable);

        Context context = getActivity();
        CharSequence text = "Use Two Finger Pinch to Scale the Chart \nVertically or Horizontally";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
        toast.show();

        switch (chartType){
            case "Bar":
                createBarChart(currentTable, columnHeaders[1]);
                break;
            case "Multi_Line":
                createMultiLineChart(currentTable);
                break;
            case "Stacked_Bar":
                createStackedBarChart(currentTable);
                break;
            default:
                Toast.makeText(getActivity(), "issue getting chart type", Toast.LENGTH_SHORT).show();
        }

    }

    public void createStackedBarChart(String Table){

        String[] DataColumns = myDB.getColumnHeaders(Table);
        ArrayList<String> counties = myDB.getEntriesInCol("County", currentTable);

        BarChart barChart = (BarChart)getView().findViewById(R.id.stacked_bar_chart);

        List<BarEntry> entries = new ArrayList<BarEntry>();

        Float tableRecord1;
        String firstRecord;
        Float tableRecord2;
        String secondRecord;
        int record1 = 1;
        int record2 = 2;

        float i = 0;
        for(int x=0; x < counties.size(); x++ ) {
            firstRecord = myDB.getDatabaseRecord(currentTable, DataColumns[record1], counties.get(x).replace("'", "''"));
            firstRecord = firstRecord.replace(",", "");
            tableRecord1 = Float.parseFloat(firstRecord);
            secondRecord = firstRecord.replace(",", "");
            secondRecord = myDB.getDatabaseRecord(currentTable, DataColumns[record2], counties.get(x).replace("'", "''"));
            tableRecord2 = Float.parseFloat(secondRecord);
            entries.add(new BarEntry(i, new float[]{tableRecord1, tableRecord2}));
            i++;
        }

        String[] labels = {DataColumns[record1], DataColumns[record2]};
        Legend l = barChart.getLegend();

        List<LegendEntry> legendEntries = new ArrayList<>();
        for(int y=0; y < labels.length; y++){
            LegendEntry entry = new LegendEntry();
            entry.formColor = stackedBarColors[y];
            entry.label = labels[y];
            legendEntries.add(entry);
        }

        l.setCustom(legendEntries);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(counties));

        BarDataSet dataset = new BarDataSet(entries, Table);
        dataset.setColors(stackedBarColors);
        BarData data = new BarData(dataset);
        barChart.setData(data);
    }

    public void createMultiLineChart(String Table){
        createChartLegend();
        String[] DataColumns = myDB.getColumnHeaders(Table);
        LineChart lineChart = (LineChart) getView().findViewById(R.id.Multi_Line_chart);
        ArrayList<String> counties = myDB.getEntriesInCol("County", currentTable);

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

        for(int x=0; x < counties.size(); x++ ){
            ArrayList<Entry> yValues = new ArrayList<Entry>();
            float counter = 0;
            for(int i=0; i < xValues.size(); i++) {
                record = myDB.getDatabaseRecord(currentTable, xValues.get(i), counties.get(x).replace("'","''"));
                tableRecord = Float.parseFloat(record);
                yValues.add(new Entry(counter, tableRecord));
                counter++;
            }

            LineDataSet set = new LineDataSet(yValues,counties.get(x));
            set.setColor(countyColors[x]);
            dataSets.add(set);
        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xValues));

        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);

    }


    public void createBarChart(String Table, String DataColumn){
        createChartLegend();

        ArrayList<String> columnData = myDB.getEntriesInCol(DataColumn, Table);
        ArrayList<String> counties = myDB.getEntriesInCol("County", currentTable);

        BarChart barChart = (BarChart)getView().findViewById(R.id.single_bar_chart);

        List<BarEntry> entries = new ArrayList<BarEntry>();

        float i = 0;
        int x = 0;
        for(String data : columnData){
            data= data.replace(",", "");
            entries.add(new BarEntry(i, Float.parseFloat(data), columnContent.get(x)));
            i++;
            x++;
        }

        Legend l = barChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(counties));

        BarDataSet dataset = new BarDataSet(entries, "County Population");
        dataset.setColors(countyColors);
        BarData data = new BarData(dataset);
        barChart.setData(data);
    }

    public void createChartLegend() {
        //Create chart legend
        ArrayList<String> counties = myDB.getEntriesInCol("County", currentTable);
        LinearLayout chartLinearLayout = (LinearLayout) getView().findViewById(R.id.county_chart_linearLayout);
        List<TextView> countyListView = new ArrayList<TextView>(counties.size());
        for (int i = 0; i < counties.size(); i++) {
            TextView newTxtView = new TextView(getContext());
            newTxtView.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
            newTxtView.append(counties.get(i));
            newTxtView.setTextColor(countyColors[i]);
            chartLinearLayout.addView(newTxtView);
            countyListView.add(newTxtView);
        }
    }


}


