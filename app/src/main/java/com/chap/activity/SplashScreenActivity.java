// Danson Njunge
//4-25-2017
// Launcher splash screen
package com.chap.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

import com.chap.R;

// Class to display the splash screen for 3 seconds
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
       // getSupportActionBar().setDisplayShowTitleEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 2000);


    }

     // prevent splash screen from showing up via back button after user leaves
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
