package com.chap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class ChartViewFragment extends Fragment{

    private ArrayList columnContent;
    private DatabaseHelper myDB;
    ListView countyList;
    String currentTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(getActivity());
        columnContent = myDB.getEntriesInCol("County", "kenya_population_per_county");
        currentTable = getArguments().getString("currentTable");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart_view, container, false);
        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        countyList = (ListView)getView().findViewById(R.id.chart_county_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_chart_view, R.id.county_chart_textView, columnContent);
        countyList.setAdapter(arrayAdapter);

        ArrayList<String> columnData = myDB.getEntriesInCol("Population", currentTable);

        BarChart barChart = (BarChart)getView().findViewById(R.id.single_bar_chart);


        List<BarEntry> entries = new ArrayList<BarEntry>();

        int i = 0;
        for(String data : columnData){
            data= data.replace(",", "");
            entries.add(new BarEntry(Float.parseFloat(data), ));
            i++;
        }

        BarDataSet dataset = new BarDataSet(entries, "population");
        //dataset.setColor(R.color.colorAccent);
       // dataset.setValueTextColor(R.color.colorPrimaryDark);

        BarData data = new BarData(dataset);

        barChart.setData(data);

    }


}

