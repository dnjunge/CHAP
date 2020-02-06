package com.chap.activity;

import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;

import com.chap.R;
import com.chap.fragment.ChartViewFragment;
import com.chap.fragment.DataTableFragment;

public class HealthIndicatorViewActivity extends AppCompatActivity {

    SharedPreferences userPref;
    public static SharedPreferences.Editor editPref;
    Bundle sendIndicatorBundle;
    String currentIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPref = this.getSharedPreferences("userPref", MODE_PRIVATE);
        currentIndicator = userPref.getString("currentIndicator", null);

        sendIndicatorBundle = new Bundle();

        setContentView(R.layout.activity_health_indicator_view);

        sendIndicatorBundle.putString("currentTable", currentIndicator);

        BottomNavigationView bottomViewSelection = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomViewSelection.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        showSelectedView(item.getItemId());
                        return true;
                    }
                });

        //make table the default view
        showSelectedView(R.id.action_view_table);
    }

    public void showSelectedView(int viewId) {
        Fragment selectedViewFragment = null;

        String viewTitle = currentIndicator;

        switch (viewId) {
            case R.id.action_view_table:
                selectedViewFragment = new DataTableFragment();
                selectedViewFragment.setArguments(sendIndicatorBundle);
                break;
            case R.id.action_view_chart:


                selectedViewFragment = new ChartViewFragment();
                selectedViewFragment.setArguments(sendIndicatorBundle);
                break;

        }

        if (selectedViewFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.indicator_view_fragment_container, selectedViewFragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(viewTitle);
        }

    }


}