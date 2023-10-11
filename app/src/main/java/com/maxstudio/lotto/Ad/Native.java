package com.maxstudio.lotto.Ad;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class Native {
    private static final long AD_REFRESH_TIME = 2 * 60 * 1000; // 3 minutes
    public Context context;
    public TemplateView templateView,templateView2,templateView3;
    private Handler handler;
    private Runnable adRefreshRunnable,adRefreshRunnable2,adRefreshRunnable3;
    public AdLoader adLoader, adLoader2,adLoader3;
    private NativeAd nativeAd,nativeA2,nativeAd3;
    private String[] adUnitIds,adUnitIds2,adUnitIds3;
    public static int currentAdIndex = 0;
    public static int currentAdIndex2 = 0;
    public static int currentAdIndex3 = 0;
    public static int refreshCount = 0;
    public static int refreshCount2 = 0;
    public static int refreshCount3 = 0;
    private boolean isScrolled,isDestroyed;

    public static NativeAd nativeAd1, nativeAdMedium2, nativeAdSmall3;
    private static final int MAX_REFRESH_COUNT = 15;


    public Native(Context context, TemplateView templateView,
                  TemplateView templateView2,TemplateView templateView3,
                  String[] adUnitIds,String[] adUnitIds2,String[] adUnitIds3, boolean isScrolled,
                  boolean isDestroyed) {

        this.context = context;
        this.isScrolled = isScrolled;
        this.isDestroyed = isDestroyed;
        this.templateView = templateView;
        this.adUnitIds = adUnitIds;
        this.templateView2 = templateView2;
        this.adUnitIds2 = adUnitIds2;
        this.templateView3 = templateView3;
        this.adUnitIds3 = adUnitIds3;
        this.handler = new Handler();
        this.adRefreshRunnable = new Runnable() {
            @Override
            public void run() {

                if (nativeAd1 != null)
                {
                    nativeAd1.destroy();
                }

                if (!isScrolled)
                {
                    loadNativeAd();
                }

            }
        };
        this.adRefreshRunnable2 = new Runnable() {
            @Override
            public void run() {

                if (nativeAdMedium2 != null)
                {
                    nativeAdMedium2.destroy();
                }
                if (!isScrolled)
                {
                    loadNativeAdMedium();
                }

            }
        };
        this.adRefreshRunnable3 = new Runnable() {
            @Override
            public void run() {

                if (nativeAdSmall3 != null)
                {
                    nativeAdSmall3.destroy();
                }
                if (!isScrolled)
                {
                    loadNativeAdSmall();
                }
            }
        };

    }

    public static class Natives
    {
        private Context context;
        private boolean isDestroyed;
        private AdLoader adLoader, adLoader2,adLoader3;
        private String[] adUnitIds,adUnitIds2,adUnitIds3;

        public Natives(Context context,String[] adUnitIds,String[] adUnitIds2
                ,String[] adUnitIds3,boolean isDestroyed)
        {
            this.adUnitIds = adUnitIds;
            this.adUnitIds2 = adUnitIds2;
            this.adUnitIds3 = adUnitIds3;
            this.context = context;
            this.isDestroyed = isDestroyed;
        }

        public void loadNativeAd2() {
            if (currentAdIndex >= adUnitIds.length) {
                // All ads have been loaded or failed to load
                currentAdIndex = 0;
                return;
            }
            String adUnitId = adUnitIds[currentAdIndex];
            adLoader = new AdLoader.Builder(context, adUnitId)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            // Show the ad.

                            nativeAd1 = nativeAd;

                            if (isDestroyed)
                            {
                                nativeAd.destroy();
                            }

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            /**if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                             {
                             retryAttempts++;
                             NativeAd();
                             }**/
                            currentAdIndex++;
                            //handler.postDelayed(adRefreshRunnable, AD_REFRESH_TIME);
                            //NativeAd();
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }

        public void loadNativeAdMedium2() {

            if (currentAdIndex2 >= adUnitIds2.length) {
                // All ads have been loaded or failed to load
                currentAdIndex2 = 0;
                return;
            }
            String adUnitId2 = adUnitIds2[currentAdIndex2];
            adLoader2 = new AdLoader.Builder(context, adUnitId2)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            // Show the ad.

                            nativeAdMedium2 = nativeAd;

                            if (isDestroyed)
                            {
                                nativeAd.destroy();
                            }

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            /**if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                             {
                             retryAttempts++;
                             NativeAd();
                             }**/
                            currentAdIndex2++;
                            //handler.postDelayed(adRefreshRunnable2, AD_REFRESH_TIME);
                            //NativeAd();
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();

            adLoader2.loadAd(new AdRequest.Builder().build());
        }

        public void loadNativeAdSmall2() {

            if (currentAdIndex3 >= adUnitIds3.length) {
                // All ads have been loaded or failed to load
                currentAdIndex3 = 0;
                return;
            }
            String adUnitId3 = adUnitIds3[currentAdIndex3];
            adLoader3 = new AdLoader.Builder(context, adUnitId3)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            // Show the ad.

                            nativeAdSmall3 = nativeAd;

                            if (isDestroyed)
                            {
                                nativeAd.destroy();
                            }

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            /**if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                             {
                             retryAttempts++;
                             NativeAd();
                             }**/
                            currentAdIndex3++;
                            //handler.postDelayed(adRefreshRunnable3, AD_REFRESH_TIME);
                            //NativeAd();
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();

            adLoader3.loadAd(new AdRequest.Builder().build());
        }


    }

    public void loadNativeAd() {
        if (currentAdIndex >= adUnitIds.length) {
            // All ads have been loaded or failed to load
            currentAdIndex = 0;
            return;
        }
        String adUnitId = adUnitIds[currentAdIndex];
        adLoader = new AdLoader.Builder(context, adUnitId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        // Show the ad.

                        nativeAd1 = nativeAd;

                        if (isDestroyed)
                        {
                            //stopAds(nativeAd);
                            nativeAd.destroy();
                        }

                        if (refreshCount < MAX_REFRESH_COUNT)
                        {
                            displayNativeAd(nativeAd);
                            handler.postDelayed(adRefreshRunnable, AD_REFRESH_TIME);
                            refreshCount++;
                        }

                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        /**if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                         {
                         retryAttempts++;
                         NativeAd();
                         }**/
                        currentAdIndex++;
                        handler.postDelayed(adRefreshRunnable, AD_REFRESH_TIME);
                        //NativeAd();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void loadNativeAdMedium() {

        if (currentAdIndex2 >= adUnitIds2.length) {
            // All ads have been loaded or failed to load
            currentAdIndex2 = 0;
            return;
        }
        String adUnitId2 = adUnitIds2[currentAdIndex2];
        adLoader2 = new AdLoader.Builder(context, adUnitId2)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        // Show the ad.

                        nativeAdMedium2 = nativeAd;

                        if (isDestroyed)
                        {
                            //stopAds(nativeAd);
                            nativeAd.destroy();
                        }

                        if (refreshCount2 < MAX_REFRESH_COUNT)
                        {
                            displayNativeAd2(nativeAd);
                            handler.postDelayed(adRefreshRunnable2, AD_REFRESH_TIME);
                            refreshCount2++;
                        }


                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        /**if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                         {
                         retryAttempts++;
                         NativeAd();
                         }**/
                        currentAdIndex2++;
                        handler.postDelayed(adRefreshRunnable2, AD_REFRESH_TIME);
                        //NativeAd();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader2.loadAd(new AdRequest.Builder().build());
    }

    public void loadNativeAdSmall() {

        if (currentAdIndex3 >= adUnitIds3.length) {
            // All ads have been loaded or failed to load
            currentAdIndex3 = 0;
            return;
        }
        String adUnitId3 = adUnitIds3[currentAdIndex3];
        adLoader3 = new AdLoader.Builder(context, adUnitId3)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        // Show the ad.

                        nativeAdSmall3 = nativeAd;

                        if (isDestroyed)
                        {
                            //stopAds(nativeAd);
                            nativeAd.destroy();
                        }

                        if (refreshCount3 < MAX_REFRESH_COUNT)
                        {
                            displayNativeAd3(nativeAd);
                            handler.postDelayed(adRefreshRunnable3, AD_REFRESH_TIME);
                            refreshCount3++;
                        }


                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        /**if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                         {
                         retryAttempts++;
                         NativeAd();
                         }**/
                        currentAdIndex3++;
                        handler.postDelayed(adRefreshRunnable3, AD_REFRESH_TIME);
                        //NativeAd();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader3.loadAd(new AdRequest.Builder().build());
    }

    private void displayNativeAd(NativeAd nativeAd) {

        ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();

        //TemplateView template = findViewById(R.id.my_template);
        templateView.setStyles(styles);
        templateView.setNativeAd(nativeAd);
        templateView.setVisibility(View.VISIBLE);

        if (isDestroyed)
        {
            templateView.setVisibility(View.GONE);
        }

    }

    private void displayNativeAd2(NativeAd nativeAdMedium) {

        ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();

        //TemplateView template = findViewById(R.id.my_template);

        templateView2.setStyles(styles);
        templateView2.setNativeAd(nativeAdMedium);
        templateView2.setVisibility(View.VISIBLE);
        if (isDestroyed)
        {
            templateView2.setVisibility(View.GONE);
        }

    }

    private void displayNativeAd3(NativeAd nativeAdSmall) {

        ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();

        //TemplateView template = findViewById(R.id.my_template);

        templateView3.setStyles(styles);
        templateView3.setNativeAd(nativeAdSmall);
        templateView3.setVisibility(View.VISIBLE);
        if (isDestroyed)
        {
            templateView3.setVisibility(View.GONE);
        }

    }

    public void startLoadingAds() {
        // Start loading the native ad
        loadNativeAd();

    }

    public void startLoadingAdsMedium() {
        // Start loading the native ad

        loadNativeAdMedium();

    }

    public void startLoadingAdsSmall() {
        // Start loading the native ad

        loadNativeAdSmall();
    }

    public void stopAds(NativeAd nativeAd) {

        if (nativeAd != null)
        {
            nativeAd.destroy();
        }
    }

    public void stopLoadingAds() {
        // Cancel any scheduled ad refresh


        if (nativeAd1 != null)
        {
            nativeAd1.destroy();
        }
        handler.removeCallbacks(adRefreshRunnable);
    }

    public void stopLoadingAdsMedium() {
        // Cancel any scheduled ad refresh
        if (nativeAdMedium2 != null)
        {
            nativeAdMedium2.destroy();
        }
        handler.removeCallbacks(adRefreshRunnable2);
    }

    public void stopLoadingAdsSmall() {
        // Cancel any scheduled ad refresh
        if (nativeAdSmall3 != null)
        {
            nativeAdSmall3.destroy();
        }
        handler.removeCallbacks(adRefreshRunnable3);
    }

}

