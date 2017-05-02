//Danson Njunge
//4/26/2017
//Activity to handle instances where user has not picked a default county

package com.chap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Objects;

public class SelectOptionActivity extends AppCompatActivity implements OnClickListener {

    Button healthIndicatorsButton;
    Button countySummaryButton;
    Button audioSignalsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        healthIndicatorsButton = (Button) findViewById(R.id.indicators_button);
        healthIndicatorsButton.setOnClickListener(this);

        countySummaryButton = (Button) findViewById(R.id.countyselect_button);
        countySummaryButton.setOnClickListener(this);

        audioSignalsButton = (Button) findViewById(R.id.audio_signal_button);
        audioSignalsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Button buttonPressed = (Button)view;
        String selectedOption = buttonPressed.getText().toString();

        if(selectedOption.equals("Population Health Indicators")) {
            Intent intent = new Intent(this, HealthIndicatorsListActivity.class);
            startActivity(intent);

        }
        else if(selectedOption.equals("County Summary")){
             Intent intent = new Intent(this, CountySummaryActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, MyAudioSignalActivity.class);
            startActivity(intent);
        }

    }

}
