package com.chap.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chap.R;
import com.google.android.gms.common.SignInButton;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {


    Button googleLoginButton;
    Button fbLoginButton;
    String googleLoginButtonText = "Sign in with Google";
    private static final String GOOGLESIGNIN = "google";
    private static final String FACEBOOKSIGNIN = "facebook";

    SharedPreferences userProfile;
    public static SharedPreferences.Editor editProfile;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userProfile = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        String userSignIn = userProfile.getString("userSignIn", null);
        editProfile = userProfile.edit();

        if (userSignIn != null) {
            if (userSignIn.contains(GOOGLESIGNIN)) {
                Intent intent = new Intent(this, GoogleLoginActivity.class);
                startActivity(intent);
                finish();
            }
            if (userSignIn.contains(FACEBOOKSIGNIN)) {
                Intent intent = new Intent(this, FacebookLoginActivity.class);
                startActivity(intent);
                finish();
            }

        } else {
            setContentView(R.layout.activity_welcome);

            googleLoginButton = (Button) findViewById(R.id.googleLoginButton);
            googleLoginButton.setOnClickListener(this);
            fbLoginButton = (Button) findViewById(R.id.fbLoginButton);
            fbLoginButton.setOnClickListener(this);

          //  setGooglePlusButtonText(googleLoginButton, googleLoginButtonText);

            //Temp place holder for button colors
           // fbLoginButton.setBackgroundColor(Color.rgb(59, 89, 182));
           // fbLoginButton.setTextColor(Color.WHITE);

        }
    }

    public void onStart() {
        super.onStart();
        SharedPreferences editProfile = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        String userSignIn = editProfile.getString("userSignIn", null);

        if(userSignIn != null){
            if(userSignIn.contains(GOOGLESIGNIN)){
                Intent intent = new Intent(this, GoogleLoginActivity.class);
                startActivity(intent);
            }
            if(userSignIn.contains(FACEBOOKSIGNIN)){
                Intent intent = new Intent(this, FacebookLoginActivity.class);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.googleLoginButton:
                // gmail Signin button clicked
                signInWithGoogle();
                break;
            case R.id.fbLoginButton:
                // fb Signin button clicked
                signInWithFb();
                break;
        }
    }

    /**
     * Sign-in using gmail
     * */
    private void signInWithGoogle() {
        //Toast.makeText(getApplicationContext(), "Google Sign in Clicked!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, GoogleLoginActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * Sign-in using fb
     * */
    private void signInWithFb() {
        //Toast.makeText(getApplicationContext(), "FB Sign in Clicked!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}