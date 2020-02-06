//Danson Njunge
//4/26/2017
// Activity to display a list of health indicators, list depens on tables in the database

package com.chap.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chap.activity.HealthIndicatorViewActivity;
import com.chap.other.DatabaseHelper;
import com.chap.R;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class HealthIndicatorsListFragment extends Fragment {

    private ArrayList tableNames;
    private DatabaseHelper myDB;
    ListView indicatorsList;
    String selectedIndicator;

    SharedPreferences userPref;
    public static SharedPreferences.Editor editPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(getActivity());
        tableNames = myDB.getTableNames();
        tableNames.remove("android_metadata");
        tableNames.remove("Chart_Type");
        Collections.sort(tableNames);

         userPref = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);
        editPref = userPref.edit();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_health_indicators_list, container, false);

        return view;

    }

    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> formatedTableNames = format(tableNames);

      //  Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    //    ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        indicatorsList = (ListView) view.findViewById(R.id.indicators_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_health_indicators_list, R.id.indicators_textView, formatedTableNames);
        indicatorsList.setAdapter(arrayAdapter);
     //  ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Select Health Indicator");

        indicatorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selectedIndicator = (String) (indicatorsList.getItemAtPosition(position));
                selectedIndicator = tableNames.get(position).toString();
                Toast.makeText(getActivity(), selectedIndicator, Toast.LENGTH_SHORT).show();
                editPref.putString("currentIndicator", selectedIndicator);
                editPref.commit();
                Intent intent = new Intent(getActivity(), HealthIndicatorViewActivity.class);
                startActivity(intent);
            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }

            ;
        });
    }



    public ArrayList format(ArrayList tableNames){
        ArrayList<String> formated = new ArrayList<>();

        String tempString;
        for (int i = 0; i < tableNames.size(); i++){
            tempString = tableNames.get(i).toString();
            tempString = tempString.replace("_", " ");
            formated.add(tempString.substring(0,1).toUpperCase() + tempString.substring(1));
        }

        return formated;
    }

    public String getActivityData() {
        return selectedIndicator;
    }

    @Override
    public void onPause() {
        super.onPause();
        myDB.close();
    }
}
