package com.chap.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chap.activity.CountySummaryViewActivity;
import com.chap.other.DatabaseHelper;
import com.chap.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CountySummaryFragment extends Fragment {
    private ArrayList columnContent;
    private DatabaseHelper myDB;
    ListView countyList;
    String selectedCounty;
    SharedPreferences userPref;
    int countySelectedPosition;
    public static SharedPreferences.Editor editPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(getContext());
        columnContent = myDB.getEntriesInCol("County", "kenya_population_per_county");
        userPref = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);
        editPref = userPref.edit();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_county_summary, container, false);

        return view;

    }

    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

     //   Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
       // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        countyList = (ListView)view.findViewById(R.id.county_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_county_summary, R.id.county_textView, columnContent);
        countyList.setAdapter(arrayAdapter);

    //    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Select County");

        countyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCounty = (String) (countyList.getItemAtPosition(position));
                countySelectedPosition = position + 1;
                editPref.putString("currentCounty", selectedCounty);
                editPref.putInt("currentCountyID", countySelectedPosition);
                editPref.commit();
                Intent intent = new Intent(getContext(), CountySummaryViewActivity.class);
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
    public void onPause() {
        super.onPause();
        myDB.close();
    }
}
