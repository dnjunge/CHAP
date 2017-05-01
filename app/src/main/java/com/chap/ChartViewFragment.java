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

import java.util.ArrayList;

public class ChartViewFragment extends Fragment{

    private ArrayList columnContent;
    private DatabaseHelper myDB;
    ListView countyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(getActivity());
        columnContent = myDB.getEntriesInCol("County", "kenya_population_per_county");

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

        BarChart barChart = (BarChart)getView().findViewById(R.id.single_bar_chart);
        


    }


}

