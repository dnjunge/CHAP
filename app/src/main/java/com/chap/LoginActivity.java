//Danson Njunge
// 4/25/2017
// Provide Google+ login capability

package com.chap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.Manifest;
import android.content.pm.PackageManager;

import android.widget.Toast;
import android.util.Log;


public class LoginActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    boolean googleLoginIntentInProgress;
    String userName;
    String userPhotoURI;
    String userGooglePlusID;
    String userEmail;
    //To create shared pref to hold user profile and settings
    SharedPreferences userPref;
    public static Editor editPref;
    /* Store the connection result from onConnectionFailed callbacks so that we can
* resolve them when the user clicks sign-in.
*/
    private ConnectionResult googleLoginConnectionResult;
    /* Track whether the sign-in button has been clicked so that we know to resolve
 * all issues preventing sign-in without waiting.
 */
    private boolean googleLoginButtonClicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPref = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        editPref = userPref.edit();

        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


/*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        //  .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
        // .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
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

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (googleLoginConnectionResult == null){
            Toast.makeText(this, "googleLoginConnectionResult null!", Toast.LENGTH_LONG).show();
        }
        if (googleLoginConnectionResult.hasResolution()) {
            try {
                googleLoginIntentInProgress = true;
                googleLoginConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                googleLoginIntentInProgress = false;
                mGoogleApiClient.connect();
            }
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
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!googleLoginIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            googleLoginConnectionResult = connectionResult;

            if (googleLoginButtonClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            //handleSignInResult(result);
            if (resultCode != Activity.RESULT_OK) {
                googleLoginButtonClicked = false;
            }
            googleLoginIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        googleLoginButtonClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        handleSignInResult();


        // Update the UI after signin
        //updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    //sign-in was successful
    private void handleSignInResult() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)  {
                            Person currentPerson = Plus.PeopleApi
                                    .getCurrentPerson(mGoogleApiClient);

                            //Get user info
                            userName =  currentPerson.getDisplayName();
                            //  userPhotoURI = currentPerson.getImage().getUrl().toString();
                            //  userGooglePlusID = currentPerson.getUrl();
                            //   final String userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                            // Store user info into shared pref
                            editPref.putString("userName", userName);
                            // editPref.putString("userPhotoURI", userPhotoURI);
                            // editPref.putString("userGooglePlusID", userGooglePlusID);
                            // editPref.putString("userEmail", userEmail);
                            editPref.commit();
                            Context context = getApplicationContext();
                            CharSequence text = getString(R.string.signed_in_fmt, userName);
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            toast.show();
                            loginSuccess();

                        } else {
                            Toast.makeText(this,
                                    "Person information is null", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    loginSuccess();
                }
                return;
            }

        }
    }



    /*
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
    } */



        private void loginSuccess()
    {
        Intent intent = new Intent(this, SelectOptionActivity.class);
        startActivity(intent);
        finish();
    }
    /*
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

    }*/

}

