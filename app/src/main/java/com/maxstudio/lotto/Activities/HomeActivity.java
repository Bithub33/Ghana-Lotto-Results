package com.maxstudio.lotto.Activities;

import static com.maxstudio.lotto.Activities.ChatActivity.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

/**import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;**/
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.mediation.MediationExtrasReceiver;
import com.google.android.gms.appset.AppSet;
import com.google.android.gms.appset.AppSetIdClient;
import com.google.android.gms.appset.AppSetIdInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Adapters.HomeTabsAdapter;
import com.maxstudio.lotto.BuildConfig;
import com.maxstudio.lotto.Fragments.AdminFragment;
import com.maxstudio.lotto.Fragments.CreateGroupFragment;
import com.maxstudio.lotto.Fragments.GroupFragment;
import com.maxstudio.lotto.Fragments.TuesdayHistoryFragment;
import com.maxstudio.lotto.Fragments.FridayHistoryFragment;
import com.maxstudio.lotto.Models.Messages;
import com.maxstudio.lotto.Fragments.MondayHistoryFragment;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Fragments.SaturdayHistoryFragment;
import com.maxstudio.lotto.Fragments.SundayFragment;
import com.maxstudio.lotto.Fragments.ThursdayHistoryFragment;
import com.maxstudio.lotto.Fragments.WednesdayHistoryFragment;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private LinearLayout moreLay, monLay,tuesLay,wedLay,thursLay,friLay,satLay,sunLay,homeLay;
    private RelativeLayout upLay;
    private RecyclerView ResultsRecyclerList;
    private NavigationView navigationView;

    private SharedPreferences prefs;
    private String currentUserId;
    private ArrayList<String> log = new ArrayList<>();
    private ArrayList<String> log2 = new ArrayList<>();

    private boolean isSubs;

    private AlertDialog.Builder builder;
    public DialogInterface dialog;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private TextView update;

    Handler handler;
    Runnable runnable;
    int Counter = 0;
    int Code = 0;
    int pp = 0;

    private DatabaseReference LottoRef,LottoRefs,DataRef,userRef,groupRef, verRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Toolbar mToolbar;
    private SwipeRefreshLayout swipe;
    private int appCount = 0;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeTabsAdapter homeTabsAdapter;

    private final int[] icons = {R.drawable.ic_home,
    R.drawable.ic_group};

    private final String[] title = {"Home","Groups"};

    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAd2;

    private int retryAttempts2 = 0;
    private boolean shouldRetry2 = true;
    private final int MAX_RETRY_ATTEMPTS = 3;
    private AdLoader adLoader;

    private FrameLayout adContainerView;
    public static AdView mAdView;
    private AdRequest adRequest;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String[] adUnitIds,adUnitIds4,adUnitIds5,adUnitIds6;
    private int currentAdIndex,inter_ad;
    private FirebaseUser user;
    private Native.Natives natives2;
    private SharedPreferences.Editor editor;
    private static final long BACKGROUND_THRESHOLD = 60 * 60 * 1000; // 1 hour in milliseconds
    private boolean isInBackground = false;
    private Handler backgroundHandler;
    private Runnable backgroundRunnable;
    private long backgroundTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(this);

        if (!MainActivity.isIntialized)
        {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                    for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus status = statusMap.get(adapterClass);
                        assert status != null;
                        Log.d("MyApp", String.format(
                                "Adapter name: %s, Description: %s, Latency: %d",
                                adapterClass, status.getDescription(), status.getLatency()));
                    }

                    MainActivity.isIntialized = true;

                    // Start loading ads here...
                    //loadAd();

                }
            });
        }

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        DataRef = FirebaseDatabase.getInstance().getReference();
        LottoRef = FirebaseDatabase.getInstance().getReference().child("Days");
        LottoRefs = FirebaseDatabase.getInstance().getReference().child("Days(Sunday)");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        verRef = FirebaseDatabase.getInstance().getReference().child("App Version");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null)
        {
            currentUserId = mAuth.getCurrentUser().getUid();

        }

        InitialisedFields();

        prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

        currentAdIndex =0;
        adUnitIds = new String[]{
                getString(R.string.Interstitial_add_id_frag),
                getString(R.string.Interstitial_add_id_frag_2),
                getString(R.string.Interstitial_add_id_frag_3),
                getString(R.string.Interstitial_add_id_frag_4),
                getString(R.string.Interstitial_add_id_frag_5)};

        adUnitIds4 = new String[]{
                getString(R.string.Native_add_id),
                getString(R.string.Native_add_id_1_2),
                getString(R.string.Native_add_id_1_3),
                getString(R.string.Native_add_id_1_4)};

        adUnitIds5 = new String[]{
                getString(R.string.Native_add_medium_id),
                getString(R.string.Native_add_medium_id_2),
                getString(R.string.Native_add_medium_id_3),
                getString(R.string.Native_add_medium_id_4)};

        adUnitIds6 = new String[]{
                getString(R.string.Native_add_small_id),
                getString(R.string.Native_add_small_id_2),
                getString(R.string.Native_add_small_id_3),
                getString(R.string.Native_add_small_id_4)};


        natives2 = new Native.Natives(this,adUnitIds4,adUnitIds5,adUnitIds6,isDestroyed());

        editor = prefs.edit();

        inter_ad = prefs.getInt("inter_ad", 0);


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                if (inter_ad == 0)
                {
                    loadAd();
                }
                if (!isSubs)
                {
                    natives2.loadNativeAdMedium2();

                }

            }
        });

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        builder = new AlertDialog.Builder(this, R.style.AlertDialog);

        backgroundHandler = new Handler();
        backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                isInBackground = true;
                // Perform your background logic here
            }
        };

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.open_nav, R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        hideItem();
        checkUpdate();
        //createBannerAd();

    }

    private void checkUpdate() {

        try {
            int Code = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionCode;

            verRef.child("VersionCode").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        String vCode = snapshot.getValue().toString();
                        int vCodes = Integer.parseInt(vCode);


                        if (vCodes > Code)
                        {
                            //upLay.setVisibility(View.VISIBLE);

                            builder.setTitle("New Update");
                            builder.setMessage("Your current version of the app is outdated, please update to the latest version");
                            builder.setIcon(R.drawable.outline_update);
                            builder.setCancelable(false);


                            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                                    }catch (ActivityNotFoundException e)
                                    {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                                    }
                                }
                            });
                            builder.show();

                            /**update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                            }catch (ActivityNotFoundException e)
                            {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                            }

                            }
                            });**/
                            //verRef.child("newCode").setValue(versionCode);

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();

        }

    }

    private void hideItem() {

        //NavigationView navigationView = findViewById(R.id.nav_view);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.isEmailVerified())
        {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_groups).setVisible(true);

            String AdminId = user.getUid();

            userRef.child(AdminId).child("Developer")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        Menu menu = navigationView.getMenu();
                        menu.findItem(R.id.nav_admin).setVisible(true);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            Menu menu2 = navigationView.getMenu();
            menu2.findItem(R.id.nav_groups).setVisible(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null)
        {
            user.reload();
        }

        //loadAd();
        //createBannerAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //InterstitialAd.load(this,null,null,null);

        if (mInterstitialAd != null)
        {
            mInterstitialAd = null;

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isInBackground) {
            long foregroundTime = System.currentTimeMillis() - backgroundTime;
            if (foregroundTime > BACKGROUND_THRESHOLD) {
                // App has been in the background for more than an hour
                // Perform your action here
                mInterstitialAd = null;

            }
            backgroundHandler.removeCallbacks(backgroundRunnable);
            isInBackground = false;

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        isInBackground = true;
        backgroundTime = System.currentTimeMillis();
        backgroundHandler.postDelayed(backgroundRunnable, BACKGROUND_THRESHOLD);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menus, menu);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            menu.findItem(R.id.logout).setVisible(false);
            menu.findItem(R.id.nav_pro).setVisible(false);

        }else
        {
            menu.findItem(R.id.nav_pro).setVisible(true);
            menu.findItem(R.id.logout).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout)
        {
            mAuth.signOut();

            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if (firebaseAuth.getCurrentUser() == null)
                    {
                        editor.putInt("inter_ad", 0);
                        editor.commit();

                        mAuth.removeAuthStateListener(authStateListener);

                        Intent SignupIntent = new Intent(HomeActivity.this, HomeActivity.class);
                        SignupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(SignupIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }

                }
            };

            mAuth.addAuthStateListener(authStateListener);

        }

        if(item.getItemId() == R.id.nav_pro)
        {
            Intent SignupIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
            //SignupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SignupIntent);

        }

        if (item.getItemId() == android.R.id.home)
        {

            onBackPressed();
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.nav_invite)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(HomeActivity.this, VipActivity.class);
                    startActivity(intent);

                }
            },500);

        }

        if(item.getItemId() == R.id.nav_monday)
        {
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("mon", 0);
            mon++;


            editor.putInt("mon", mon);
            editor.commit();

            if (mon > 1) {

                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            editor.putInt("mon", 0);
                            editor.commit();


                            //currentAdIndex++;

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new MondayHistoryFragment()).addToBackStack(null).commit();


                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    editor.putInt("mon", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new MondayHistoryFragment()).addToBackStack(null).commit();

                        }
                    },1000);

                }


            } else {

                inter_ad = prefs.getInt("inter_ad", 0);

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new MondayHistoryFragment()).addToBackStack(null).commit();

                    }
                },1000);

            }

        }

        if(item.getItemId() == R.id.nav_Tuesday)
        {
            //tabLayout.setVisibility(View.GONE);
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("tue", 0);
            mon++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("tue", mon);
            editor.commit();

            if (mon > 1) {

                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("tue", 0);
                            editor.commit();

                            //currentAdIndex++;
                            //loadAd();

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new TuesdayHistoryFragment()).addToBackStack(null).commit();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    editor.putInt("tue", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new TuesdayHistoryFragment()).addToBackStack(null).commit();

                        }
                    },1000);

                }

            } else {

                inter_ad = prefs.getInt("inter_ad", 0);

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new TuesdayHistoryFragment()).addToBackStack(null).commit();

                    }
                },1000);

            }

        }

        if(item.getItemId() == R.id.nav_Wednesday)
        {
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("wed", 0);
            mon++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("wed", mon);
            editor.commit();

            if (mon > 1) {


                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("wed", 0);
                            editor.commit();

                            //currentAdIndex++;
                            //loadAd();

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new WednesdayHistoryFragment()).addToBackStack(null).commit();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {
                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }


                    editor.putInt("wed", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new WednesdayHistoryFragment()).addToBackStack(null).commit();

                        }
                    },1000);

                }

            } else {
                inter_ad = prefs.getInt("inter_ad", 0);

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new WednesdayHistoryFragment()).addToBackStack(null).commit();

                    }
                },1000);

            }

            //loadAd();

        }

        if(item.getItemId() == R.id.nav_Thursday)
        {
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("thur", 0);
            mon++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("thur", mon);
            editor.commit();

            //tabLayout.setVisibility(View.GONE);
            if (mon > 1) {

                inter_ad = prefs.getInt("inter_ad", 0);
                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("thur", 0);
                            editor.commit();

                            //currentAdIndex++;
                            //loadAd();

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new ThursdayHistoryFragment()).addToBackStack(null).commit();


                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    editor.putInt("thur", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadAd();
                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new ThursdayHistoryFragment()).addToBackStack(null).commit();

                        }
                    },1000);
                }

            } else {

                inter_ad = prefs.getInt("inter_ad", 0);

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAd();
                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new ThursdayHistoryFragment()).addToBackStack(null).commit();

                    }
                },1000);
            }

            //loadAd();

        }

        if(item.getItemId() == R.id.nav_Friday)
        {
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("fri", 0);
            mon++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("fri", mon);
            editor.commit();

            //tabLayout.setVisibility(View.GONE);
            if (mon > 1) {

                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("fri", 0);
                            editor.commit();

                            //currentAdIndex++;
                            //loadAd();

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new FridayHistoryFragment()).addToBackStack(null).commit();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    editor.putInt("fri", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new FridayHistoryFragment()).addToBackStack(null).commit();

                        }
                    },1000);
                }

            } else {

                inter_ad = prefs.getInt("inter_ad", 0);

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new FridayHistoryFragment()).addToBackStack(null).commit();

                    }
                },1000);
            }

            //loadAd();

        }

        if(item.getItemId() == R.id.nav_Saturday)
        {
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("sat", 0);
            mon++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("sat", mon);
            editor.commit();

            //tabLayout.setVisibility(View.GONE);
            if (mon > 1) {

                inter_ad = prefs.getInt("inter_ad", 0);
                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("sat", 0);
                            editor.commit();

                            //currentAdIndex++;
                            //loadAd();

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new SaturdayHistoryFragment()).addToBackStack(null).commit();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    editor.putInt("sat", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new SaturdayHistoryFragment()).addToBackStack(null).commit();

                        }
                    },1000);

                }

            } else {

                inter_ad = prefs.getInt("inter_ad", 0);

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new SaturdayHistoryFragment()).addToBackStack(null).commit();

                    }
                },1000);

            }

            //loadAd();

        }

        if(item.getItemId() == R.id.nav_Sunday)
        {
            drawer.closeDrawer(GravityCompat.START);

            int mon = prefs.getInt("sun", 0);
            mon++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("sun", mon);
            editor.commit();

            //tabLayout.setVisibility(View.GONE);
            if (mon > 1) {

                inter_ad = prefs.getInt("inter_ad", 0);
                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(HomeActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                            mInterstitialAd = null;
                            MyApplication.isAdFullScreen = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("sun", 0);
                            editor.commit();

                            //currentAdIndex++;

                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new SundayFragment()).addToBackStack(null).commit();

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);

                            editor.putInt("inter_ad", 0);
                            editor.commit();

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                        }
                    });

                }
                else {

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    editor.putInt("sun", 0);
                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                    new SundayFragment()).addToBackStack(null).commit();

                        }
                    },1000);

                }

            } else {

                if (inter_ad == 0)
                {
                    loadAd();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new SundayFragment()).addToBackStack(null).commit();

                    }
                },1000);

            }


        }

        if(item.getItemId() == R.id.nav_share)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=" + getPackageName();
            String shareSub = "Download this app for current lotto results";
            intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            intent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(intent, "Share using"));
        }

        if(item.getItemId() == R.id.nav_rate)
        {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }catch (ActivityNotFoundException e)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }

        if(item.getItemId() == R.id.nav_predict)
        {
            //drawer.closeDrawer(GravityCompat.START);

            loadAd();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(HomeActivity.this, PredictActivity.class);
                    startActivity(intent);

                }
            },500);


        }

        if(item.getItemId() == R.id.nav_admin)
        {

            this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                    new AdminFragment()).addToBackStack(null).commit();
        }

        if(item.getItemId() == R.id.nav_groups)
        {

            this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                    new CreateGroupFragment()).addToBackStack(null).commit();
        }

        if(item.getItemId() == R.id.nav_home)
        {
            if (getSupportFragmentManager().findFragmentById(R.id.frame_layout) != null)
            {
                this.getSupportFragmentManager().beginTransaction().remove(Objects.
                        requireNonNull(getSupportFragmentManager().
                        findFragmentById(R.id.frame_layout))).commit();
                //mToolbar.setTitle("Ghana Lotto Results");

            }

        }

        /**if(item.getItemId() == R.id.nav_play)
        {

            this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                    new PlayFragment()).addToBackStack(null).commit();
        }**/

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadAd()
    {
        if (!isSubs)
        {
            if (currentAdIndex >= adUnitIds.length) {
                // All ads have been loaded or failed to load
                currentAdIndex = 0;
                return;
            }
            /**if (retryAttempts2 >= MAX_RETRY_ATTEMPTS) {
                shouldRetry2 = false; // Stop retrying
                loadAd2();
                Log.e("NativeAdHelper", "Maximum retry attempts reached");
                return;
            }**/
            String adUnitId = adUnitIds[currentAdIndex];
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            InterstitialAd.load(HomeActivity.this,adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

                            inter_ad++;

                            editor.putInt("inter_ad", inter_ad);
                            editor.commit();

                            mInterstitialAd = interstitialAd;
                            //retryAttempts2 = 0;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mInterstitialAd = null;
                                    //currentAdIndex++;
                                    loadAd();

                                }
                            }, BACKGROUND_THRESHOLD);

                            Log.i("TAG", "onAdLoaded");

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("TAG", loadAdError.getMessage());
                            mInterstitialAd = null;

                            /**if (shouldRetry2 && retryAttempts2 < MAX_RETRY_ATTEMPTS)
                            {
                                retryAttempts2++;
                                loadAd();

                            }**/
                            currentAdIndex++;
                            loadAd();

                        }

                    });
        }

    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //loadingBar.dismiss();
            super.onBackPressed();
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            //loadingBar.dismiss();
            mToolbar.setTitle("Ghana Lotto Results");
            getSupportFragmentManager().popBackStackImmediate
                    (null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            //tabLayout.setVisibility(View.VISIBLE);
        }else
        {
            //loadingBar.dismiss();
            super.onBackPressed();
            //finish();
        }

    }

    private void InitialisedFields() {

        tabLayout = findViewById(R.id.home_tab);
        homeTabsAdapter = new HomeTabsAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(homeTabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.tab_back_layout,null);
            TextView tab_text = layout.findViewById(R.id.text);
            TextView badge = layout.findViewById(R.id.msgCount);
            RelativeLayout layout1 = layout.findViewById(R.id.tab);
            CoordinatorLayout layout2 = layout.findViewById(R.id.tabs);

            tab_text.setText("" + title[i]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                tab_text.setCompoundDrawablesWithIntrinsicBounds(icons[i],0,0,0);
            }
            tabLayout.getTabAt(i).setCustomView(layout1);

            if (i == 1)
            {

                if (user != null)
                {
                    userRef.child(currentUserId).child("Groups")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists())
                                    {

                                        for (DataSnapshot o : snapshot.getChildren())
                                        {
                                            String os = o.getKey();
                                            assert os != null;
                                            //log.clear();
                                            log.add(os);
                                        }

                                        for (String l : log)
                                        {
                                            userRef.child(currentUserId).child("Groups").child(l)
                                                    .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.exists())
                                                    {
                                                        String oo = snapshot.getValue().toString();

                                                        if (!oo.isEmpty())
                                                        {
                                                            //pp = pp + 1;

                                                            String mov = snapshot.getKey();
                                                            assert mov != null;
                                                            log2.add(mov);

                                                            for (String o : log2)
                                                            {
                                                                userRef.child(currentUserId).child("Groups").child(o)
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                if (snapshot.exists())
                                                                                {
                                                                                    String ooo = snapshot.getValue().toString();

                                                                                    if (ooo.isEmpty())
                                                                                    {
                                                                                        //pp = pp - 1;
                                                                                        String k = snapshot.getKey();
                                                                                        log2.remove(k);
                                                                                    }
                                                                                }

                                                                                if (log2.size() > 0)
                                                                                {
                                                                                    layout2.setVisibility(View.VISIBLE);
                                                                                    badge.setVisibility(View.VISIBLE);
                                                                                    badge.setText(String.valueOf(log2.size()));
                                                                                }
                                                                                else
                                                                                {
                                                                                    layout2.setVisibility(View.GONE);
                                                                                    badge.setVisibility(View.GONE);
                                                                                    //badge.setText(String.valueOf(pp));
                                                                                }



                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                            }


                                                        }
                                                        else
                                                        {

                                                            String k = snapshot.getKey();
                                                            log2.remove(k);


                                                            /**userRef.child(currentUserId).child("Groups").child(l)
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                            if (snapshot.exists())
                                                                            {
                                                                                String oo = snapshot.getValue().toString();

                                                                                if (!oo.isEmpty())
                                                                                {
                                                                                    //pp = pp + 1;

                                                                                    String mov = snapshot.getKey();
                                                                                    assert mov != null;
                                                                                    log2.add(mov);

                                                                                    for (String o : log2)
                                                                                    {
                                                                                        userRef.child(currentUserId).child("Groups").child(o)
                                                                                                .addValueEventListener(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                        if (snapshot.exists())
                                                                                                        {
                                                                                                            String ooo = snapshot.getValue().toString();

                                                                                                            if (ooo.isEmpty())
                                                                                                            {
                                                                                                                //pp = pp - 1;
                                                                                                                String k = snapshot.getKey();
                                                                                                                log2.remove(k);
                                                                                                            }
                                                                                                        }

                                                                                                        if (log2.size() > 0)
                                                                                                        {
                                                                                                            layout2.setVisibility(View.VISIBLE);
                                                                                                            badge.setVisibility(View.VISIBLE);
                                                                                                            badge.setText(String.valueOf(log2.size()));
                                                                                                        }
                                                                                                        else
                                                                                                        {
                                                                                                            layout2.setVisibility(View.GONE);
                                                                                                            badge.setVisibility(View.GONE);
                                                                                                            //badge.setText(String.valueOf(pp));
                                                                                                        }



                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                    }
                                                                                                });
                                                                                    }


                                                                                }

                                                                            }


                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });**/
                                                        }

                                                        if (log2.size() > 0)
                                                        {
                                                            layout2.setVisibility(View.VISIBLE);
                                                            badge.setVisibility(View.VISIBLE);
                                                            badge.setText(String.valueOf(log2.size()));
                                                        }
                                                        else
                                                        {
                                                            layout2.setVisibility(View.GONE);
                                                            badge.setVisibility(View.GONE);
                                                            //badge.setText(String.valueOf(pp));
                                                        }

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }


            }


        }


        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            getWindow().setStatusBarColor(Color.WHITE);
        }**/

        final SharedPreferences countRef = getSharedPreferences("Counter",Context.MODE_PRIVATE);
        Counter=countRef.getInt("count",0);

        SharedPreferences.Editor editAdControl = countRef.edit();
        if (countRef.getLong("ExpiredDate", -1)<System.currentTimeMillis())
        {
            editAdControl.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editAdControl.apply();
            }
        }

        if (Counter> 4)
        {
            //createBannerAd();
            mAdView = new AdView(this);
            mAdView.setVisibility(View.GONE);
        }

        //Permission for SDK between 23 and 29
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.CALL_PHONE}, 100);
            }

            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }


        //Permission for SDK between 30 and above
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.CALL_PHONE}, 100);
            }

            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }

        Context context = getApplicationContext();
        AppSetIdClient client = AppSet.getClient(context);
        Task<AppSetIdInfo> task = client.getAppSetIdInfo();

        task.addOnSuccessListener(new OnSuccessListener<AppSetIdInfo>() {
            @Override
            public void onSuccess(AppSetIdInfo info) {
                // Determine current scope of app set ID.
                int scope = info.getScope();

                // Read app set ID value, which uses version 4 of the
                // universally unique identifier (UUID) format.
                String id = info.getId();
            }

        });

    }


}
