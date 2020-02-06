package com.chap.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chap.R;
import com.chap.fragment.AnalyticsFragment;
import com.chap.fragment.CountryOverviewFragment;
import com.chap.fragment.CountySummaryFragment;
import com.chap.fragment.HealthIndicatorsListFragment;
import com.chap.fragment.NotificationsFragment;
import com.chap.fragment.SettingsFragment;
import com.chap.other.CircleTransform;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AnalyticsFragment.OnFragmentInteractionListener,
CountryOverviewFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener{

    private GoogleApiClient mGoogleApiClient;
    SharedPreferences userPref;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    // urls to load navigation header background image
    // and profile image
    String urlProfileImg, userSignInMethod;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_COUNTRY_OVERVIEW = "menu_country_overview";
    private static final String TAG_COUNTY_OVERVIEW = "county_overview";
    private static final String TAG_POP_HEALTH = "pop_health_indicators";
    private static final String TAG_ANALYTICS = "analytics";
    private static final String TAG_NOTIFICATIONS = "menu_notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_COUNTRY_OVERVIEW;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    public static SharedPreferences.Editor editPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userPref = this.getSharedPreferences("userProfile", MODE_PRIVATE);
        urlProfileImg = userPref.getString("userPhotoURI", null);
        userSignInMethod = userPref.getString("userSignIn", null);
        editPref = userPref.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_COUNTRY_OVERVIEW;
            loadHomeFragment();
        }


    }

    @Override
    protected void onStart(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              //  .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }
    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, menu_notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText(userPref.getString("userName", null));

        // loading header background image
        Glide.with(this).load(R.drawable.nav_menu_header_bg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to menu_notifications label
       // navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the menu_main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

         //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // countryOverviewFragment
                CountryOverviewFragment countryOverviewFragment = new CountryOverviewFragment();
                return countryOverviewFragment;
            case 1:
                // countySummaryFragment
                CountySummaryFragment countySummaryFragment = new CountySummaryFragment();
                return countySummaryFragment;
            case 2:
                // healthIndicatorsListFragment
                HealthIndicatorsListFragment healthIndicatorsListFragment = new HealthIndicatorsListFragment();
                return healthIndicatorsListFragment;
            case 3:
                // menu_notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
  /**          case 4:
                // analyticsFragment
                AnalyticsFragment analyticsFragment = new AnalyticsFragment();
                return analyticsFragment;
            case 5:
                // settingsFragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment; **/
            default:
                return new CountryOverviewFragment();
        }
    }

    private void setToolbarTitle() {

        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the menu_main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_country_overview:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_COUNTRY_OVERVIEW;
                        break;
                    case R.id.nav_county_overview:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_COUNTY_OVERVIEW;
                        break;
                    case R.id.nav_pop_health_indicators:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_POP_HEALTH;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
           /**         case R.id.nav_analytics:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_ANALYTICS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SETTINGS;
                        break; **/
                    case R.id.nav_about:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        drawer.closeDrawers();
                        return true;
                   /** case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true; **/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_COUNTRY_OVERVIEW;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected load the menu created for menu_country_overview
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.menu_country_overview, menu);
        }

        // when fragment is CountyOverview, load the menu created for menu_county_overview
        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.menu_county_overview, menu);
        }

        // when fragment is HealthIndicatorsListFragment, load the menu created for menu_population_health_indicators
        if (navItemIndex == 2) {
            getMenuInflater().inflate(R.menu.menu_population_health_indicators_list, menu);
        }

        // when fragment is Notifications, load the menu created for menu_notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.menu_notifications, menu);
        }
/**
        // when fragment is Analytics, load the menu created for menu_analytics
        if (navItemIndex == 4) {
            getMenuInflater().inflate(R.menu.menu_analytics, menu);
        }

        // when fragment is Settings, load the menu created for menu_settings
        if (navItemIndex == 5) {
            getMenuInflater().inflate(R.menu.menu_settings, menu);
        } **/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            if(userSignInMethod.contentEquals("google")){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...

                            }
                        });
            }
            else {
                LoginManager.getInstance().logOut();
            }
            Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
            editPref.putString("userSignIn", null);
            editPref.commit();
            Intent i=new Intent(getApplicationContext(),SplashScreenActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        // user is in menu_notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All menu_notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in menu_notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all menu_notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_country_overview) {
            // Handle the camera action
        } else if (id == R.id.nav_county_overview) {

        } else if (id == R.id.nav_pop_health_indicators) {

      //  } else if (id == R.id.nav_analytics) {

        } else if (id == R.id.nav_notifications) {

      //  } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
