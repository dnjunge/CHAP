//Danson Njunge
// 4/25/2017
// Provide Google+ login capability

package com.chap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.widget.Toast;
import android.util.Log;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    String userName;
    String userPhotoURI;
    String userGooglePlusID;
    String userEmail;
    //To create shared pref to hold user profile and settings
    SharedPreferences userPref;
    public static Editor editPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPref = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        editPref = userPref.edit();

        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.googleLoginButton).setOnClickListener(this);

    }
    //Ensure login screen is in portrait mode only
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    //Handle a click of the login button
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.googleLoginButton:
                signInWithGoogle();
                break;
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        }
    }
    //Check if sign-in was successful
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            //Get user info
            userName = acct.getDisplayName();
            userPhotoURI = acct.getPhotoUrl().toString();
            userGooglePlusID = acct.getId();
            userEmail = acct.getEmail();
            // Store user info into shared pref
            editPref.putString("userName", userName);
            editPref.putString("userPhotoURI", userPhotoURI);
            editPref.putString("userGooglePlusID", userGooglePlusID);
            editPref.putString("userEmail", userEmail);
            editPref.commit();
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.signed_in_fmt, userName);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
            toast.show();
            loginSuccess(true);
        } else {
            loginSuccess(false);
        }
    }

    private void loginSuccess(boolean loggedIn)
    {
        if(loggedIn){
            Intent intent = new Intent(this, SelectOptionActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "There was a problem signing in.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.googleLoginButton).setVisibility(View.VISIBLE);
        }

    }

}

