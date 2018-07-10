//Danson Njunge
// 4/25/2017
// Provide Google+ login capability

package com.chap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.chap.R;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.Manifest;


import android.widget.Toast;
import android.util.Log;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;


public class GoogleLoginActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener
         {

    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleLoginActivity";
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
    public static final String SIGN_IN_METHOD = "google";
     // Profile pic image size in pixels
     private static final int PROFILE_PIC_SIZE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_login);

        userPref = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        editPref = userPref.edit();



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }
    //Ensure login screen is in portrait mode only
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            editPref.putString("userSignIn", null);
            editPref.commit();
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
            else{
                Log.e(TAG, connectionResult.toString());
                loginSuccess();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "CHAP: Result Code: " + resultCode);
        Log.d(TAG, "CHAP: Request Code: " + requestCode);
        Log.d(TAG, "CHAP: RC_SIGN_IN: " + RC_SIGN_IN);
        Log.d(TAG, "CHAP: Activity.RESULT_OK: " + Activity.RESULT_OK);
        Log.d(TAG, "CHAP: top googleLoginButtonClicked: " + googleLoginButtonClicked);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                googleLoginButtonClicked = false;
            }
            googleLoginIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        Log.d(TAG, "CHAP: bottom googleLoginButtonClicked: " + googleLoginButtonClicked);
     }

    @Override
    public void onConnected(Bundle connectionHint) {
        googleLoginButtonClicked = false;
        // Get user's information
        handleSignInResult();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
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
        else{
             //permission already granted
             loginSuccess();
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

                            userName =  currentPerson.getDisplayName();
                            userPhotoURI = currentPerson.getImage().getUrl();
                            userGooglePlusID = currentPerson.getUrl();
                        //    final String userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                            userPhotoURI = userPhotoURI.substring(0,
                                    userPhotoURI.length() - 2)
                                    + PROFILE_PIC_SIZE;

                            //Store user info into shared pref
                            editPref.putString("userName", userName);
                            editPref.putString("userPhotoURI", userPhotoURI);
                            editPref.putString("userGooglePlusID", userGooglePlusID);
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
                            Log.d(TAG, "CHAP: Person information is null ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    loginSuccess();
                }
            }

        }

    }

        private void loginSuccess(){
        editPref.putString("userSignIn", SIGN_IN_METHOD);
        editPref.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

