package com.maxstudio.lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DatabaseReference;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Fragments.ResultsFragment;
import com.maxstudio.lotto.R;

import java.util.Map;

public class PredictActivity extends AppCompatActivity {

    private View PredictView;
    private DatabaseReference ChatRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator;
    private NestedScrollView scroll;

    private InterstitialAd mInterstitialAd;

    private AdRequest adRequest;
    private AdLoader adLoader;
    private NativeAdView nativeAdView;

    private boolean isSubs;

    private SharedPreferences prefs;

    private RewardedAd rewardedAd,rewardedAd2;
    private Toolbar toolbar;

    private AdView mAdView;

    private FrameLayout adContainerView;
    private final String TAG = "HomeActivity";

    private String[] adUnitIds;
    private int currentAdIndex;

    private String[] adUnitIds2,adUnitIds4,adUnitIds5,adUnitIds6,adUnitIds7;
    private Native.Natives natives2;
    private int currentAdIndex2,currentAdIndex7,inter_ad,rewarded_ad2;

    private static final long BACKGROUND_THRESHOLD = 60 * 60 * 1000; // 1 hour in milliseconds
    private boolean isInBackground = false;
    private Handler backgroundHandler;
    private Runnable backgroundRunnable;
    private long backgroundTime = 0;
    private SharedPreferences.Editor edit;
    private  boolean isLayout1, isLayout3;

    private LinearLayout layout,lay2,options;

    private RelativeLayout layout1,layout2,layout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

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

        currentAdIndex = 0;
        currentAdIndex2 = 0;

        InitialisedFields();

        toolbar = findViewById(R.id.predict_bar);

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        backgroundHandler = new Handler();
        backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                isInBackground = true;

                if (mAdView != null)
                {mAdView.destroy();
                    mAdView = null;
                    loadNextAd();}
                else{loadNextAd();}
                // Perform your background logic here
            }
        };

        adUnitIds = new String[]{
                getString(R.string.banner_add_id),
                getString(R.string.banner_add_id_1_1),
                getString(R.string.banner_add_id_1_2),
                getString(R.string.banner_add_id_1_3),
                getString(R.string.banner_add_id_1_4)};

        adUnitIds2 = new String[]{
                getString(R.string.Rewarded_ad_id_3),
                getString(R.string.Rewarded_ad_id_4),
                getString(R.string.Rewarded_ad_id_5),
                getString(R.string.Rewarded_ad_id_6),
                getString(R.string.Rewarded_ad_id_7)};

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

        adUnitIds7 = new String[]{
                getString(R.string.Interstitial_add_id_2),
                getString(R.string.Interstitial_add_id),
                getString(R.string.Interstitial_add_id_3),
                getString(R.string.Interstitial_add_id_4),
                getString(R.string.Interstitial_add_id_5)};


        natives2 = new Native.Natives(this,adUnitIds4,adUnitIds5,adUnitIds6,isDestroyed());

        adContainerView = findViewById(R.id.ad_view_container);

        adContainerView.post(backgroundRunnable);

        edit = prefs.edit();

        edit.putInt("rewarded_ad", 0);
        edit.putInt("inter_ad", 0);
        edit.commit();

        inter_ad = prefs.getInt("inter_ad", 0);
        rewarded_ad2 = prefs.getInt("rewarded_ad", 0);


        if (ResultsFragment.rewardedAd2 != null)
        {
            rewardedAd2 = ResultsFragment.rewardedAd2;

            rewardedAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd2 = null;
                    MyApplication.isAdFullScreen = false;

                    if (isLayout1)
                    {
                        Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                        startActivity(intent);
                        isLayout1 = false;
                    }
                    else if (isLayout3) {
                        Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                        startActivity(intent);
                        isLayout3 = false;
                    }

                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                    currentAdIndex2++;

                    edit.putInt("rewarded_ad", 0);
                    edit.commit();

                    rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                    if (rewarded_ad2 == 0)
                    {
                        loadRewardedAds();
                    }

                    if (isLayout1)
                    {
                        Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                        startActivity(intent);
                        isLayout1 = false;
                    }
                    else if (isLayout3) {
                        Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                        startActivity(intent);
                        isLayout3 = false;
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                    MyApplication.isAdFullScreen = true;

                    edit.putInt("rewarded_ad", 0);
                    edit.commit();

                    rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                    if (rewarded_ad2 == 0)
                    {
                        loadRewardedAds();
                    }
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    indicator.setVisibility(View.GONE);
                    scroll.setVisibility(View.VISIBLE);
                }
            },1500);

        }

        if (ResultsFragment.mInterstitialAd != null)
        {
            mInterstitialAd = ResultsFragment.mInterstitialAd;
        }

        if (rewarded_ad2 == 0)
        {
            loadRewardedAds();
        }
        if (inter_ad == 0)
        {
            loadAd();
        }
        buttonFunctions();
        if (!isSubs)
        {
            natives2.loadNativeAdMedium2();

        }
        //loadRewardedAd();

        setSupportActionBar(toolbar);
        setTitle("Predictions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void InitialisedFields() {

        layout = findViewById(R.id.results_layout);
        lay2 = findViewById(R.id.free_lays);
        options = findViewById(R.id.options_lay);

        layout1 = findViewById(R.id.free_lay);
        layout2 = findViewById(R.id.vip_lay);
        layout3 = findViewById(R.id.watch_lay);

        scroll = findViewById(R.id.scroll);
        indicator = findViewById(R.id.circle_loading);

        prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

    }

    /** Rewarded ad group 1 **/
    private void loadRewardedAds()
    {
        if (!isSubs)
        {
            if (currentAdIndex2 >= adUnitIds2.length) {
                // All ads have been loaded or failed to load
                indicator.setVisibility(View.GONE);
                scroll.setVisibility(View.VISIBLE);
                currentAdIndex2 = 0;
                return;
            }

            String adUnitId = adUnitIds2[currentAdIndex2];
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, adUnitId,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            rewardedAd = null;

                            currentAdIndex2++;

                            rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                            if (rewarded_ad2 == 0)
                            {
                                loadRewardedAds();
                            }

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd = ad;
                            Log.d(TAG, "Ad was loaded.");

                            rewarded_ad2++;

                            edit.putInt("rewarded_ad", rewarded_ad2);
                            edit.commit();

                            indicator.setVisibility(View.GONE);
                            scroll.setVisibility(View.VISIBLE);

                            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    // Called when a click is recorded for an ad.
                                    Log.d(TAG, "Ad was clicked.");
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    Log.d(TAG, "Ad dismissed fullscreen content.");
                                    rewardedAd = null;

                                    MyApplication.isAdFullScreen = false;

                                    inter_ad = prefs.getInt("inter_ad", 0);

                                    if (inter_ad == 0)
                                    {
                                        loadAd();
                                    }

                                    rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                                    if (rewarded_ad2 == 0)
                                    {
                                        loadRewardedAds();
                                    }

                                    if (isLayout1)
                                    {
                                        Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                                        startActivity(intent);
                                        isLayout1 = false;
                                    }
                                    else if (isLayout3) {
                                        Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                                        startActivity(intent);
                                        isLayout3 = false;
                                    }

                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when ad fails to show.
                                    Log.e(TAG, "Ad failed to show fullscreen content.");
                                    rewardedAd = null;
                                    currentAdIndex2++;

                                    edit.putInt("rewarded_ad", 0);
                                    edit.commit();

                                    inter_ad = prefs.getInt("inter_ad", 0);

                                    if (inter_ad == 0)
                                    {
                                        loadAd();
                                    }

                                    rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                                    if (rewarded_ad2 == 0)
                                    {
                                        loadRewardedAds();
                                    }

                                    if (isLayout1)
                                    {
                                        Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                                        startActivity(intent);
                                        isLayout1 = false;
                                    }
                                    else if (isLayout3) {
                                        Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                                        startActivity(intent);
                                        isLayout3 = false;
                                    }
                                }

                                @Override
                                public void onAdImpression() {
                                    // Called when an impression is recorded for an ad.
                                    Log.d(TAG, "Ad recorded an impression.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    Log.d(TAG, "Ad showed fullscreen content.");
                                    MyApplication.isAdFullScreen = true;

                                    edit.putInt("rewarded_ad", 0);
                                    edit.commit();


                                }
                            });
                        }
                    });
        }
        else
        {
            indicator.setVisibility(View.GONE);
            scroll.setVisibility(View.VISIBLE);
        }

    }

    public void loadAd()
    {
        if (!isSubs)
        {
            if (currentAdIndex7 >= adUnitIds7.length) {
                // All ads have been loaded or failed to load
                currentAdIndex7 = 0;
                return;
            }
            /**if (retryAttempts2 >= MAX_RETRY_ATTEMPTS) {
             shouldRetry2 = false; // Stop retrying
             Log.e("NativeAdHelper", "Maximum retry attempts reached");
             return;
             }**/
            String adUnitId = adUnitIds7[currentAdIndex7];
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(PredictActivity.this,adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

                            indicator.setVisibility(View.GONE);
                            scroll.setVisibility(View.VISIBLE);

                            mInterstitialAd = interstitialAd;

                            inter_ad++;

                            SharedPreferences.Editor edit= prefs.edit();
                            edit.putInt("inter_ad", inter_ad);
                            edit.commit();

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
                             loadAd2();

                             }**/
                            currentAdIndex7++;
                            loadAd();
                        }
                    });
        }

    }

    private void buttonFunctions()
    {
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rewardedAd != null) {
                    rewardedAd.show(PredictActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                        }
                    });

                    //loadRewardedAds();
                    isLayout1 = true;
                }
                else if (rewardedAd2 != null)
                {
                    rewardedAd2.show(PredictActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                        }
                    });

                    //loadRewardedAds();
                    isLayout1 = true;
                }
                else if (mInterstitialAd != null)
                {
                    //isLayout1 = true;
                    mInterstitialAd.show(PredictActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitialAd = null;

                            MyApplication.isAdFullScreen = false;

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                            rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                            if (rewarded_ad2 == 0)
                            {
                                loadRewardedAds();
                            }

                            Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                            startActivity(intent);
                            //isLayout1 = false;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            mInterstitialAd = null;

                            edit.putInt("inter_ad", 0);
                            edit.putInt("rewarded_ad", 0);
                            edit.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                            rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                            if (rewarded_ad2 == 0)
                            {
                                loadRewardedAds();
                            }

                            Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                            startActivity(intent);
                            //isLayout1 = false;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            edit.putInt("inter_ad", 0);
                            edit.putInt("rewarded_ad", 0);
                            edit.commit();

                        }
                    });

                } else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                    if (rewarded_ad2 == 0)
                    {
                        loadRewardedAds();
                    }

                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    Intent intent = new Intent(PredictActivity.this, FreeActivity.class);
                    startActivity(intent);
                }


            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PredictActivity.this, VipActivity.class);
                startActivity(intent);

            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rewardedAd != null) {
                    rewardedAd.show(PredictActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                        }
                    });

                    isLayout3 = true;
                }
                else if (rewardedAd2 != null)
                {
                    rewardedAd2.show(PredictActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                        }
                    });

                    //loadRewardedAds();
                    isLayout3 = true;
                }
                else if (mInterstitialAd != null) {
                    //isLayout3 = true;
                    mInterstitialAd.show(PredictActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitialAd = null;

                            MyApplication.isAdFullScreen = false;

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                            rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                            if (rewarded_ad2 == 0)
                            {
                                loadRewardedAds();
                            }

                            Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                            startActivity(intent);
                            //isLayout3 = false;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            mInterstitialAd = null;

                            edit.putInt("inter_ad", 0);
                            edit.putInt("rewarded_ad", 0);
                            edit.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0)
                            {
                                loadAd();
                            }

                            rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                            if (rewarded_ad2 == 0)
                            {
                                loadRewardedAds();
                            }


                            Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                            startActivity(intent);
                            //isLayout3 = false;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();

                            MyApplication.isAdFullScreen = true;

                            edit.putInt("inter_ad", 0);
                            edit.putInt("rewarded_ad", 0);
                            edit.commit();

                        }
                    });
                }
                else {

                    inter_ad = prefs.getInt("inter_ad", 0);

                    if (inter_ad == 0)
                    {
                        loadAd();
                    }

                    rewarded_ad2 = prefs.getInt("rewarded_ad",0);

                    if (rewarded_ad2 == 0)
                    {
                        loadRewardedAds();
                    }

                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    Intent intent = new Intent(PredictActivity.this, WatchActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAdView = new AdView(this);
        mAdView.destroy();

        adContainerView.removeCallbacks(backgroundRunnable);

    }

    @Override
    public void onPause() {
        super.onPause();

        isInBackground = true;
        backgroundTime = System.currentTimeMillis();
        backgroundHandler.postDelayed(backgroundRunnable, BACKGROUND_THRESHOLD);

    }

    @Override
    public void onResume() {
        super.onResume();

        //loadRewardedAds();
        adContainerView = findViewById(R.id.ad_view_container);

        if (isInBackground) {
            long foregroundTime = System.currentTimeMillis() - backgroundTime;
            if (foregroundTime > BACKGROUND_THRESHOLD) {
                // App has been in the background for more than an hour
                // Perform your action here
                //mInterstitialAd = null;
                rewardedAd = null;
                loadRewardedAds();

                adContainerView.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mAdView != null)
                        {mAdView.destroy();
                            mAdView = null;
                            loadNextAd();}
                        else{loadNextAd();}
                    }
                });
            }
            backgroundHandler.removeCallbacks(backgroundRunnable);
            isInBackground = false;

        }
        //loadRewardedAd();
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadNextAd() {
        if (currentAdIndex >= adUnitIds.length) {
            // All ads have been loaded or failed to load
            //currentAdIndex = 0;
            return;
        }

        String adUnitId = adUnitIds[currentAdIndex];
        AdView adView = new AdView(this);
        adView.setAdUnitId(adUnitId);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                //mAdView.setVisibility(View.GONE);
                currentAdIndex++;
                loadNextAd();

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Ad successfully loaded
                //currentAdIndex++;
                //loadNextAd();
            }
        });

    }

}