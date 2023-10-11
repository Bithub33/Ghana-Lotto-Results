package com.maxstudio.lotto.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;**/
/**import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;**/
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.Utils.AppStatus;
import com.maxstudio.lotto.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private BillingClient billingClient;

    public static String Lotto_1,Lotto_2,Lotto_3,Lotto_4,Lotto_5,Mach_1,Mach_2,Mach_3,Mach_4,Mach_5,
            Date_1,Draw_num,Draw_type,Future_date,live;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private boolean isSubs;
    public static boolean isIntialized;
    private SharedPreferences prefs;

    private DatabaseReference userRef;

    public static InterstitialAd mInterstitialAd;
    public static RewardedAd rewardedAd;

    private int retryAttempts2 = 0;
    private boolean shouldRetry2 = true;
    private final int MAX_RETRY_ATTEMPTS = 3;

    private Handler handler;
    private Runnable runnable;
    private ValueEventListener val,val1,val2,val3,val4,val5,val6,val7;

    public static List<DailyResultsPicker> combinedDataList = new ArrayList<>();
    public static List<DailyResultsPicker> combinedDataList2 = new ArrayList<>();
    public static List<DailyResultsPicker> combinedDataList3 = new ArrayList<>();
    public static List<DailyResultsPicker> combinedDataList4 = new ArrayList<>();
    private int appCount,inter_ad,rewarded_ad;
    private Native.Natives natives2;
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;
    private DatabaseReference DataRef,LottoRef,LottoRefs;
    private String[] adUnitIds2,adUnitIds3,adUnitIds4,adUnitIds5,adUnitIds6;
    private int currentAdIndex2,currentAdIndex3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isTaskRoot()
        && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
        && getIntent().getAction() != null
        && getIntent().getAction().equals(Intent.ACTION_MAIN))
        {
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                isIntialized = true;

                // Start loading ads here...
                //loadAd();

            }
        });

        checkSubscription();

        DataRef = FirebaseDatabase.getInstance().getReference();
        LottoRef = FirebaseDatabase.getInstance().getReference().child("Days");
        LottoRefs = FirebaseDatabase.getInstance().getReference().child("Days(Sunday)");

        currentAdIndex2 =0;
        currentAdIndex3 = 0;
        adUnitIds2 = new String[]{
                getString(R.string.Interstitial_add_id_2),
                getString(R.string.Interstitial_add_id),
                getString(R.string.Interstitial_add_id_3),
                getString(R.string.Interstitial_add_id_4),
                getString(R.string.Interstitial_add_id_5)};

        adUnitIds3 = new String[]{
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


        natives2 = new Native.Natives(this,adUnitIds4,adUnitIds5,adUnitIds6,isDestroyed());


        prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

        appCount = prefs.getInt("app_open_count", 0);
        appCount++;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("app_open_count", appCount);
        editor.putInt("inter_ad", 0);
        editor.putInt("rewarded_ad", 0);
        editor.putInt("rewarded_ad_2", 0);
        editor.commit();

        inter_ad = prefs.getInt("inter_ad", 0);
        rewarded_ad = prefs.getInt("rewarded_ad", 0);

        handler = new Handler();

        val = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    if (snapshot.hasChild("lotto1"))
                    {
                        //loadingBar.dismiss();


                        Lotto_1 = snapshot.child("lotto1").getValue().toString();
                        Lotto_2 = snapshot.child("lotto2").getValue().toString();
                        Lotto_3 = snapshot.child("lotto3").getValue().toString();
                        Lotto_4 = snapshot.child("lotto4").getValue().toString();
                        Lotto_5 = snapshot.child("lotto5").getValue().toString();
                        Mach_1 = snapshot.child("mach1").getValue().toString();
                        Mach_2 = snapshot.child("mach2").getValue().toString();
                        Mach_3 = snapshot.child("mach3").getValue().toString();
                        Mach_4 = snapshot.child("mach4").getValue().toString();
                        Mach_5 = snapshot.child("mach5").getValue().toString();
                        Date_1 = snapshot.child("date").getValue().toString();
                        Draw_num = snapshot.child("event").getValue().toString();
                        Draw_type = snapshot.child("draw_type").getValue().toString();
                        Future_date = snapshot.child("future_date").getValue().toString();
                        Date_1 = snapshot.child("date").getValue().toString();
                        Draw_num = snapshot.child("event").getValue().toString();
                        Draw_type = snapshot.child("draw_type").getValue().toString();
                        Future_date = snapshot.child("future_date").getValue().toString();

                        DataRef.child("Today").child("Results").removeEventListener(val);


                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        getConsentStatus();

        todayDataRequest();

        updateInterfaces();

        requests();
        requests2();
        requests3();
        requests4();

        //CheckVerification();

    }

    public void getConsentStatus() {

        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("F2D4DC79B28D75DB4A01885A71BB453B")
                .build();

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                //.setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        //Activity activity = this;
        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.

                        if(consentInformation.isConsentFormAvailable())
                        {
                            loadForm();
                        }
                        else
                        {
                            if (appCount == 4 && inter_ad == 0)
                            {
                                loadAd();
                            }

                            if (rewarded_ad == 0)
                            {
                                loadRewardedAds();
                            }

                            if (!isSubs)
                            {
                                natives2.loadNativeAdMedium2();
                                natives2.loadNativeAd2();
                                natives2.loadNativeAdSmall2();
                            }
                            InterLoad();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(@NonNull FormError formError) {
                        // Handle the error.
                        if (appCount == 4 && inter_ad == 0)
                        {
                            loadAd();
                        }

                        if (rewarded_ad == 0)
                        {
                            loadRewardedAds();
                        }
                        if (!isSubs)
                        {
                            natives2.loadNativeAdMedium2();
                            natives2.loadNativeAd2();
                            natives2.loadNativeAdSmall2();
                        }
                        InterLoad();

                    }
                });

    }

    public void loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(
                this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(@NonNull ConsentForm consentForms) {
                        MainActivity.this.consentForm = consentForms;
                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    MainActivity.this,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(FormError formError) {
                                            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                                // App can start requesting ads.
                                                if (appCount == 4 && inter_ad == 0)
                                                {
                                                    loadAd();
                                                }

                                                if (rewarded_ad == 0)
                                                {
                                                    loadRewardedAds();
                                                }
                                                if (!isSubs)
                                                {
                                                    natives2.loadNativeAdMedium2();
                                                    natives2.loadNativeAd2();
                                                    natives2.loadNativeAdSmall2();
                                                }
                                                InterLoad();
                                            }

                                            // Handle dismissal by reloading form.
                                            loadForm();
                                        }
                                    });
                        }

                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.UNKNOWN) {
                            consentForm.show(
                                    MainActivity.this,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(FormError formError) {

                                            loadForm();
                                        }
                                    });
                        }

                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                            // App can start requesting ads.
                            if (appCount == 4 && inter_ad == 0)
                            {
                                loadAd();
                            }

                            if (rewarded_ad == 0)
                            {
                                loadRewardedAds();
                            }
                            if (!isSubs)
                            {
                                natives2.loadNativeAdMedium2();
                                natives2.loadNativeAd2();
                                natives2.loadNativeAdSmall2();
                            }
                            InterLoad();
                        }

                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(@NonNull FormError formError) {
                        // Handle the error.
                        loadForm();

                    }
                }
        );
    }

    public void InterLoad()
    {

        runnable = new Runnable() {
            @Override
            public void run() {

                SharedPreferences.Editor edit = prefs.edit();

                if (appCount == 4) {

                    if (mInterstitialAd != null)
                    {
                        mInterstitialAd.show(MainActivity.this);
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();

                                edit.putInt("app_open_count", 0);
                                edit.commit();

                                mInterstitialAd = null;
                                MyApplication.isAdFullScreen = false;
                                //loadAd3();
                                currentAdIndex2++;

                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                handler.removeCallbacks(runnable);
                                finish();

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();

                                MyApplication.isAdFullScreen = true;

                                edit.putInt("inter_ad", 0);
                                edit.commit();

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);

                                //SharedPreferences.Editor edit = prefs.edit();
                                edit.putInt("inter_ad", 0);
                                edit.commit();

                            }
                        });
                        if (inter_ad == 0)
                        {
                            loadAd();
                        }
                    }
                    else {
                        if (inter_ad == 0)
                        {
                            loadAd();
                        }

                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        handler.removeCallbacks(runnable);
                        finish();

                    }


                } else {

                    if (appCount > 4)
                    {
                        edit.putInt("app_open_count", 0);
                        edit.putInt("inter_ad", 0);
                        edit.commit();
                    }
                    //handler.removeCallbacks(runnable);

                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    handler.removeCallbacks(runnable);
                    finish();

                }

            }
        };
        handler.postDelayed(runnable,7000);

    }

    private void todayDataRequest()
    {

        DataRef.child("Today").child("Results")
                .addListenerForSingleValueEvent(val);


    }

    private void requests()
    {
        DatabaseReference LottoRef = FirebaseDatabase.getInstance().getReference()
                .child("Other Games").child("Vag");
        LottoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    combinedDataList.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        DailyResultsPicker daily = s.getValue(DailyResultsPicker.class);
                        assert daily != null;
                        String key = s.getKey();
                        daily.setKey(key);

                        combinedDataList.add(daily);

                    }

                    //ready = true;

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "failed to fetch data", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void requests2()
    {
        DatabaseReference LottoRef = FirebaseDatabase.getInstance().
                getReference().child("Other Games")
                .child("Super");
        LottoRef.keepSynced(true);

        LottoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    combinedDataList2.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        DailyResultsPicker daily = s.getValue(DailyResultsPicker.class);
                        assert daily != null;
                        String key = s.getKey();
                        daily.setKey(key);

                        combinedDataList2.add(daily);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "failed to fetch data", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void requests3()
    {
        DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference();
        DataRef.keepSynced(true);
        DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    //ready = true;
                    combinedDataList3.clear();
                    for (DataSnapshot s : snapshot.child("Days").getChildren())
                    {
                        DailyResultsPicker daily = s.getValue(DailyResultsPicker.class);
                        assert daily != null;
                        String key = s.getKey();
                        daily.setKey(key);

                        combinedDataList3.add(daily);

                    }
                    for (DataSnapshot s : snapshot.child("Days(Sunday)").getChildren())
                    {
                        DailyResultsPicker daily = s.getValue(DailyResultsPicker.class);
                        assert daily != null;
                        String key = s.getKey();
                        daily.setKey(key);

                        combinedDataList3.add(daily);


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "failed to fetch data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void requests4()
    {
        DatabaseReference LottoRef = FirebaseDatabase.getInstance().getReference()
                .child("Other Games").child("Daywa");
        LottoRef.keepSynced(true);
        LottoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    combinedDataList4.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        DailyResultsPicker daily = s.getValue(DailyResultsPicker.class);
                        assert daily != null;
                        String key = s.getKey();
                        daily.setKey(key);

                        combinedDataList4.add(daily);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "failed to fetch data", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DataRef.child("Today").child("Results")
                .removeEventListener(val);

        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppStatus.isAppInBackground = true;

        DataRef.child("Today").child("Results")
                .removeEventListener(val);

        handler.removeCallbacks(runnable);

    }

    @Override
    protected void onResume() {
        super.onResume();

        AppStatus.isAppInBackground = false;

        DataRef.child("Today").child("Results")
                .addListenerForSingleValueEvent(val);
        handler.postDelayed(runnable,7000);
        //InterLoad();

    }

    public void loadAd()
    {
        if (!isSubs)
        {
            if (currentAdIndex2 >= adUnitIds2.length) {
                // All ads have been loaded or failed to load
                currentAdIndex2 = 0;
                return;
            }
            /**if (retryAttempts2 >= MAX_RETRY_ATTEMPTS) {
             shouldRetry2 = false; // Stop retrying
             Log.e("NativeAdHelper", "Maximum retry attempts reached");
             return;
             }**/
            String adUnitId = adUnitIds2[currentAdIndex2];
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(MainActivity.this,adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

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
                            currentAdIndex2++;
                            loadAd();
                        }
                    });
        }

    }

    private void loadRewardedAds()
    {
        if (!isSubs)
        {
            if (currentAdIndex3 >= adUnitIds3.length) {
                // All ads have been loaded or failed to load
                currentAdIndex3 = 0;

                return;
            }

            String adUnitId = adUnitIds3[currentAdIndex3];
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, adUnitId,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            rewardedAd = null;

                            currentAdIndex3++;
                            loadRewardedAds();
                            //isReady = false;
                            //todayDataRequest();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd = ad;
                            Log.d(TAG, "Ad was loaded.");

                            rewarded_ad++;

                            SharedPreferences.Editor edit= prefs.edit();
                            edit.putInt("rewarded_ad", rewarded_ad);
                            edit.commit();

                        }
                    });
        }

    }

    void checkSubscription(){

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {

                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                                    Log.d("testOffer",list.size() +" size");
                                    if(list.size()>0){
                                        //prefs.setPremium(1); // set 1 to activate premium feature

                                        int i = 0;
                                        for (Purchase purchase: list){
                                            //Here you can manage each product, if you have multiple subscription

                                            for (String id : purchase.getProducts())
                                            {
                                                if (!id.equals("donations"))
                                                {
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putBoolean("service_status", true);
                                                    editor.commit();
                                                }
                                            }

                                            Log.d("testOffer",purchase.getOriginalJson()); // Get to see the order information
                                            Log.d("testOffer", " index" + i);
                                            i++;
                                        }
                                    }else {

                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("service_status", false);
                                        editor.commit();
                                    }
                                }
                            });

                }

            }
        });
    }

    public void updateInterface()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            currentUserId = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(currentUserId);

            userRef.child("Subscribed").setValue("0");
        }
    }

    public void updateInterfaces()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            currentUserId = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(currentUserId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        if (!snapshot.hasChild("image"))
                        {
                            currentUser.delete();

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void CheckVerification()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            user.reload();
            boolean emailVerified = user.isEmailVerified();
            if (emailVerified)
            {
                //
            }
            else
            {
                user.delete();
            }
        }
    }
}