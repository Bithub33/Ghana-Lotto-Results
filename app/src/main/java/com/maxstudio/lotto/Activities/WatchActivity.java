package com.maxstudio.lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.R;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchActivity extends AppCompatActivity {

    private DatabaseReference ChatRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator;
    private ScrollView scroll;

    private Toolbar toolbar;

    private View view;

    private InterstitialAd mInterstitialAd;


    private AdRequest adRequest;
    private AdLoader adLoader4,adLoader5,adLoader6;
    private NativeAdView nativeAdView;

    private FrameLayout adContainerView;
    private AdView mAdView;

    private String[] adUnitIds;

    private String[] adUnitIds2,adUnitIds3,adUnitIds1;
    private Native natives;
    private int currentAdIndex;

    private ExecutorService executor;
    private Runnable runnable;
    private int currentAdIndex2;
    private boolean isSubs,isDestroyed,isScrolled;

    private String Lotto_1,Lotto_2,Lotto_3,Lotto_4,Lotto_5,Mach_1,Mach_2,Mach_3,Mach_4,Mach_5, Date_1;

    private TextView Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Mach1,Mach2,Mach3,Mach4,Mach5,Date;
    private LinearLayout layout,lay2,options;

    private RelativeLayout layout1,layout2,layout3;
    private ImageView LottoImage,LottoImage1,LottoImage2,LottoImage3,LottoImage4,LottoImage5,LottoImage6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

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

        ChatRef = FirebaseDatabase.getInstance().getReference().child("Sure Banker");

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        //loadingBar = new ProgressDialog(this);
        //indicator = findViewById(R.id.circle_loading);
        toolbar = findViewById(R.id.free_app_bar);

        currentAdIndex = 0;
        currentAdIndex2 = 0;

        executor = Executors.newSingleThreadExecutor();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (mAdView != null)
                {mAdView.destroy();
                    mAdView = null;
                    loadNextAd();}
                else{loadNextAd();}

            }
        };

        SharedPreferences prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

        adUnitIds = new String[]{
                getString(R.string.banner_add_id_3),
                getString(R.string.banner_add_id_3_1),
                getString(R.string.banner_add_id_3_2),
                getString(R.string.banner_add_id_3_3),
                getString(R.string.banner_add_id_3_4)};

        NativeAdInitialized();

        adContainerView = findViewById(R.id.ad_view_container);

        adContainerView.post(runnable);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sure Banker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        InitialisedFields();

        NativeAdMedium();
        //createBannerAd();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {

            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        natives.stopLoadingAdsMedium();

        adContainerView.removeCallbacks(runnable);

        if (executor != null && !executor.isShutdown())
        {
            executor.shutdown();
        }
        super.onBackPressed();
    }

    private  void NativeAdInitialized()
    {
        adUnitIds1 = new String[]{
                getString(R.string.Native_add_id),
                getString(R.string.Native_add_id_1_2),
                getString(R.string.Native_add_id_1_3),
                getString(R.string.Native_add_id_1_4)};

        adUnitIds2 = new String[]{
                getString(R.string.Native_add_medium_id),
                getString(R.string.Native_add_medium_id_2),
                getString(R.string.Native_add_medium_id_3),
                getString(R.string.Native_add_medium_id_4)};

        adUnitIds3 = new String[]{
                getString(R.string.Native_add_small_id),
                getString(R.string.Native_add_small_id_2),
                getString(R.string.Native_add_small_id_3),
                getString(R.string.Native_add_small_id_4)};

        TemplateView templateView = findViewById(R.id.my_template);
        TemplateView templateView2 = findViewById(R.id.my_template2);
        TemplateView templateView3 = findViewById(R.id.my_template3);

        natives = new Native(this, templateView,templateView2,
                templateView3, adUnitIds1,adUnitIds2,adUnitIds3,isScrolled,isDestroyed);
    }

    @Override
    public void onStart() {
        super.onStart();

        ChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    if (dataSnapshot.hasChild("2sure1"))
                    {
                        //loadingBar.dismiss();

                        if (dataSnapshot.hasChild("monday"))
                        {
                            LottoImage.setVisibility(View.VISIBLE);
                        }

                        if (dataSnapshot.hasChild("tuesday"))
                        {
                            LottoImage1.setVisibility(View.VISIBLE);
                        }

                        if (dataSnapshot.hasChild("wednesday"))
                        {
                            LottoImage2.setVisibility(View.VISIBLE);
                        }

                        if (dataSnapshot.hasChild("thursday"))
                        {
                            LottoImage3.setVisibility(View.VISIBLE);
                        }

                        if (dataSnapshot.hasChild("friday"))
                        {
                            LottoImage4.setVisibility(View.VISIBLE);
                        }

                        if (dataSnapshot.hasChild("saturday"))
                        {
                            LottoImage5.setVisibility(View.VISIBLE);
                        }

                        if (dataSnapshot.hasChild("sunday"))
                        {
                            LottoImage6.setVisibility(View.VISIBLE);
                        }

                        //layout.setVisibility(View.VISIBLE);


                        Lotto_2 = dataSnapshot.child("2sure1").getValue().toString();
                        Lotto_4 = dataSnapshot.child("2sure2").getValue().toString();

                        Mach_3 = dataSnapshot.child("banker").getValue().toString();

                        Date_1 = dataSnapshot.child("date").getValue().toString();


                        Lotto2.setText(Lotto_2);
                        Lotto4.setText(Lotto_4);

                        Mach3.setText(Mach_3);

                        Date.setText(Date_1);


                        //Picasso.get().load(LottoPic).placeholder(R.drawable.ic_launcher_foreground).into(LottoImage);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdView = new AdView(this);
        mAdView.destroy();

        isDestroyed = true;
        //NativeAdInitialized();
        natives.stopLoadingAdsMedium();

        adContainerView.removeCallbacks(runnable);

        if (executor != null && !executor.isShutdown())
        {
            executor.shutdown();
        }


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void InitialisedFields() {

        Lotto2 = findViewById(R.id.lotto_2);
        Lotto4 = findViewById(R.id.lotto_4);

        Mach3 = findViewById(R.id.mach_3);

        LottoImage = findViewById(R.id.monday_logo);
        LottoImage1 = findViewById(R.id.tuesday_logo);
        LottoImage2 = findViewById(R.id.midweek_logo);
        LottoImage3 = findViewById(R.id.thursday_logo);
        LottoImage4 = findViewById(R.id.friday_logo);
        LottoImage5 = findViewById(R.id.saturday_logo);
        LottoImage6 = findViewById(R.id.sunday_logo);

        Date = findViewById(R.id.result_date);

        scroll = findViewById(R.id.scroll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                    if (i1 != i3)
                    {
                        isScrolled = true;
                    }
                    else
                    {
                        isScrolled = false;
                    }

                }
            });
        }

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
            currentAdIndex = 0;
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
                adContainerView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void NativeAdMedium()
    {
        if (!isSubs)
        {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Perform background task here

                    natives.startLoadingAdsMedium();
                }
            });

        }

    }


}