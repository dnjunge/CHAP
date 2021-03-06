package com.chap.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chap.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;

    String userName;
    String userPictureURL;
    String userEmail;
    //To create shared pref to hold user profile and settings
    SharedPreferences userPref;
    public static SharedPreferences.Editor editPref;

    public static final String SIGN_IN_METHOD = "facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wait_for_login);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.chap",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        userPref = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        editPref = userPref.edit();

        FacebookSdk.sdkInitialize(this);
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, mCallBack);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("Success", "Login");

           // AccessToken accessToken = loginResult.getAccessToken();
           // Profile profile = Profile.getCurrentProfile();

            // Facebook Email address
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.v("LoginActivity Response ", response.toString());

                            try {
                                userName = object.getString("name");

                               userEmail = object.getString("email");

                                userPictureURL= object.getJSONObject("picture").getJSONObject("data").getString("url");
                                Log.v("Email = ", " " + userEmail);

                                Context context = getApplicationContext();
                                CharSequence text = getString(R.string.signed_in_fmt, userName);
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                toast.show();

                                editPref.putString("userName", userName);
                                editPref.putString("userEmail", userEmail);
                                editPref.putString("userPhotoURI", userPictureURL);
                                editPref.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            loginSuccess();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,picture");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
            editPref.putString("userSignIn", null);
            editPref.commit();
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(FacebookException exception) {
           // Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            loginSuccess();
        }
    };

    private void loginSuccess()
    {
        editPref.putString("userSignIn", SIGN_IN_METHOD);
        editPref.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
