package com.maxstudio.lotto.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.maxstudio.lotto.R;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VipActivity extends AppCompatActivity {

    private LinearLayout layout,layout2,layout3,layout4,layout5,layoutMain,layout7;
    private TextView textView,textView2,textView3,textView4,textView5,textView6,textView7;

    private BillingClient billingClient;
    private FrameLayout adContainerView;
    private AdView mAdView;
    private Toolbar mToolbar;

    private ProgressBar indicator;
    private Activity activity;

    private boolean isSubs;
    private SharedPreferences prefs;

    private String[] adUnitIds;
    private int currentAdIndex;
    private Runnable runnable;

    private String userName,groupMsg,groupTime,you,groupImage,
            currentUserId,sub,user_from,key;
    private String one_month,String,six_month;

    private DatabaseReference LottoRef, groupRef,userRef, usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

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

        mAuth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        InitialisedFields();

        //currentAdIndex2 = 0;
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

        adUnitIds = new String[]{
                getString(R.string.banner_add_id_2),
                getString(R.string.banner_add_id_2_1),
                getString(R.string.banner_add_id_2_2),
                getString(R.string.banner_add_id_2_3),
                getString(R.string.banner_add_id_2_4)};

        activity = this;

        //Initialize a BillingClient with PurchasesUpdatedListener onCreate method

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list !=null) {
                                    for (Purchase purchase: list){
                                        verifySubPurchase(purchase);
                                    }
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        //establishConnection();

    }

    @Override
    protected void onStart() {
        super.onStart();

        establishConnection();

        currentAdIndex = 0;

        SharedPreferences prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

        adContainerView = findViewById(R.id.ad_view_container);

        adContainerView.post(runnable);
    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                showProducts();
                showProducts2();
                showProducts3();
                showProducts4();
                showProducts5();
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                    showProducts2();
                    showProducts3();
                    showProducts4();
                    showProducts5();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    @SuppressLint("SetTextI18n")

    void showProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("ghana_lotto.first")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    // Process the result
                    for(ProductDetails productDetails : prodDetailsList)
                    {
                        if(productDetails.getProductId().equals("ghana_lotto.first"))
                        {
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    launchPurchaseFlow(productDetails);
                                }
                            });

                            String currencyCode = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();
                            long priceMicros = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros();

                            double fullLength = priceMicros/1000000.0;
                            String fullPrice = java.lang.String.format(Locale.getDefault(), "%s %.2f",
                                    currencyCode,fullLength);

                            runOnUiThread(()->{
                                textView.setText(fullPrice);
                                indicator.setVisibility(View.GONE);
                                layoutMain.setVisibility(View.VISIBLE);
                            });

                        }

                    }
                }
        );

    }

    @SuppressLint("SetTextI18n")

    void showProducts2() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("remove_ad_3_months")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    // Process the result
                    for(ProductDetails productDetails : prodDetailsList)
                    {

                        if(productDetails.getProductId().equals("remove_ad_3_months"))
                        {
                            layout2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    launchPurchaseFlow(productDetails);
                                }
                            });

                            String currencyCode = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();
                            long priceMicros = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros();

                            double fullLength = priceMicros/1000000.0;
                            String fullPrice = java.lang.String.format(Locale.getDefault(), "%s %.2f",
                                    currencyCode,fullLength);

                            runOnUiThread(()->{
                                textView2.setText(fullPrice);
                            });
                        }

                    }
                }
        );

    }

    @SuppressLint("SetTextI18n")

    void showProducts3() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("app")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    // Process the result
                    for(ProductDetails productDetails : prodDetailsList)
                    {
                        if(productDetails.getProductId().equals("app"))
                        {
                            layout3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    launchPurchaseFlow(productDetails);
                                }
                            });

                            String currencyCode = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();
                            long priceMicros = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros();

                            double fullLength = priceMicros/1000000.0;
                            String fullPrice = java.lang.String.format(Locale.getDefault(), "%s %.2f",
                                    currencyCode,fullLength);

                            runOnUiThread(()->{
                                textView3.setText(fullPrice);
                            });
                        }

                    }
                }
        );

    }

    @SuppressLint("SetTextI18n")

    void showProducts4() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("remove_ad_1_year")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    // Process the result
                    for(ProductDetails productDetails : prodDetailsList)
                    {
                        if(productDetails.getProductId().equals("remove_ad_1_year"))
                        {
                            layout4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    launchPurchaseFlow(productDetails);
                                }
                            });

                            String currencyCode = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();
                            long priceMicros = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros();

                            double fullLength = priceMicros/1000000.0;
                            String fullPrice = java.lang.String.format(Locale.getDefault(), "%s %.2f",
                                    currencyCode,fullLength);

                            runOnUiThread(()->{
                                textView4.setText(fullPrice);
                            });

                        }

                    }
                }
        );

    }

    @SuppressLint("SetTextI18n")

    void showProducts5() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("donations")
                            .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    // Process the result
                    for(ProductDetails productDetails : prodDetailsList)
                    {
                        if(productDetails.getProductId().equals("donations"))
                        {
                            layout5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    launchPurchaseFlow(productDetails);
                                }
                            });

                            String price = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
                            String currencyCode = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();
                            long priceMicros = productDetails.getSubscriptionOfferDetails().get(0)
                                    .getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros();

                            double fullLength = priceMicros/1000000.0;
                            String fullPrice = java.lang.String.format(Locale.getDefault(), "%s %.2f",
                                    currencyCode,fullLength);

                            runOnUiThread(()->{
                                textView5.setText(fullPrice);
                            });

                        }

                    }
                }
        );

    }

    void launchPurchaseFlow(ProductDetails productDetails) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()

                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Snackbar.make(findViewById(android.R.id.content),
                        "Subscription activated, please restart", Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(VipActivity.this, "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();
                //Setting premium to 1
                // 1 - premium
                // 0 - no premium
            }
        });

        Log.d(TAG, "Purchase Token: " + purchases.getPurchaseToken());
        Log.d(TAG, "Purchase Time: " + purchases.getPurchaseTime());
        Log.d(TAG, "Purchase OrderID: " + purchases.getOrderId());
    }


    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAdView !=null)
        {
            mAdView.destroy();

        }

        adContainerView.removeCallbacks(runnable);

    }

    @Override
    public void onPause() {
        super.onPause();
        //currentAdIndex = 0;
        //currentAdIndex2 = 0;

    }


    private void InitialisedFields() {

        layout = findViewById(R.id.ad_lay);
        layout2 = findViewById(R.id.ad_lay2);
        layout3 = findViewById(R.id.ad_lay3);
        layout4 = findViewById(R.id.ad_lay4);
        layout5 = findViewById(R.id.ad_lay5);
        layoutMain = findViewById(R.id.main_lay);

        textView = findViewById(R.id.ad_price);
        textView2 = findViewById(R.id.ad_price2);
        textView3 = findViewById(R.id.ad_price3);
        textView4 = findViewById(R.id.ad_price4);
        textView5 = findViewById(R.id.ad_price5);

        indicator = findViewById(R.id.circle_loading);


        mToolbar = findViewById(R.id.vip_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Subscriptions");
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
            }
        });

    }
}