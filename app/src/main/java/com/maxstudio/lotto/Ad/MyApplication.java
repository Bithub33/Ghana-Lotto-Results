package com.maxstudio.lotto.Ad;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.maxstudio.lotto.R;

import java.util.Date;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private static AppOpenAdManager appOpenAdManager;
    public static boolean isAdFullScreen;
    private Activity currentActivity;
    public long loadTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.

        if (!isAdFullScreen)
        {
            appOpenAdManager.showAdIfAvailable(currentActivity);

        }

    }

    public class AppOpenAdManager {
        private static final String LOG_TAG = "AppOpenAdManager";
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;
        private boolean isSubs;

        private String[] adUnitIds;
        private int currentAdIndex = 0;


        /** Constructor. */
        public AppOpenAdManager(MyApplication myApplication) {}

        /** Request an ad. */
        private void loadAd(Context context) {
            // We will implement this below.

            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;

            SharedPreferences prefs = getSharedPreferences("com.maxstudio.lotto",
                    Context.MODE_PRIVATE);

            isSubs = prefs.getBoolean("service_status", false);

            adUnitIds = new String[]{
                    getString(R.string.Open_ad_id),
                    getString(R.string.Open_ad_id_2),
                    getString(R.string.Open_ad_id_3),
                    getString(R.string.Open_ad_id_4)};

            if (!isSubs)
            {

                if (currentAdIndex >= adUnitIds.length) {
                    // All ads have been loaded or failed to load
                    currentAdIndex = 0;
                    return;
                }

                String adUnitId = adUnitIds[currentAdIndex];
                AdRequest request = new AdRequest.Builder().build();
                AppOpenAd.load(
                        context, adUnitId, request,
                        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                        new AppOpenAd.AppOpenAdLoadCallback() {
                            @Override
                            public void onAdLoaded(AppOpenAd ad) {
                                // Called when an app open ad has loaded.
                                Log.d(LOG_TAG, "Ad was loaded.");
                                appOpenAd = ad;
                                //showAdIfAvailable(currentActivity);
                                isLoadingAd = false;
                                loadTime = (new Date()).getTime();
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                // Called when an app open ad has failed to load.
                                Log.d(LOG_TAG, loadAdError.getMessage());
                                isLoadingAd = false;
                                currentAdIndex++;
                                loadAd(context);
                            }
                        });

            }
        }

        /**private final LifecycleEventObserver lifecycleEventObserver = (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {

            }
        };**/


        public void showAdIfAvailable(@NonNull final Activity activity){
            // If the app open ad is already showing, do not show the ad again.


            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                //onShowAdCompleteListener.onShowAdComplete();
                loadAd(MyApplication.this);
                return;
            }

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d(LOG_TAG, "Ad dismissed fullscreen content.");
                    appOpenAd = null;
                    isShowingAd = false;

                    //onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when fullscreen content failed to show.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d(LOG_TAG, adError.getMessage());
                    appOpenAd = null;
                    isShowingAd = false;

                    //onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    Log.d(LOG_TAG, "Ad showed fullscreen content.");
                }
            });
            isShowingAd = true;
            appOpenAd.show(activity);
        }

        /** Check if ad exists and can be shown. */
        private boolean isAdAvailable() {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        // Updating the currentActivity only when an ad is not showing.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}


}
