package com.maxstudio.lotto.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.R;

import java.util.Objects;

public class PredictionFragment extends Fragment {

    private View PredictView;
    private DatabaseReference ChatRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator;
    private ScrollView scroll;

    private InterstitialAd mInterstitialAd;

    private AdRequest adRequest;
    private AdLoader adLoader;
    private NativeAdView nativeAdView;

    private boolean isSubs;

    private final int MAX_RETRY_ATTEMPTS = 3;
    private int retryAttempts = 0;
    private boolean shouldRetry = true;
    private SharedPreferences prefs;

    private RewardedAd rewardedAd;
    private RewardedAd rewardedAds;

    private AdView mAdView;
    private FrameLayout adContainerView;
    private final String TAG = "HomeActivity";

    private String Lotto_1,Lotto_2,Lotto_3,Lotto_4,Lotto_5,Mach_1,Mach_2,Mach_3,Mach_4,Mach_5, Date_1;

    private TextView Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Mach1,Mach2,Mach3,Mach4,Mach5,Date;
    private LinearLayout layout,lay2,options;

    private RelativeLayout layout1,layout2,layout3;
    private ImageView LottoImage,LottoImage1,LottoImage2,LottoImage3,LottoImage4,LottoImage5,LottoImage6;


    public PredictionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PredictView = inflater.inflate(R.layout.fragment_prediction, container, false);


        InitialisedFields();

        adContainerView = PredictView.findViewById(R.id.ad_view_container);

        adContainerView.post(new Runnable() {
            @Override
            public void run() {

                if (mAdView != null)
                {mAdView.destroy();
                    createBannerAd();}
                else{createBannerAd();}
            }
        });

        if (getActivity() != null)
        {
            //loadRewardedAds();
            //loadRewardedAd();
            //buttonFunctions();
        }
        //results();

        return PredictView;
    }


    private void InitialisedFields() {

        layout = PredictView.findViewById(R.id.results_layout);
        lay2 = PredictView.findViewById(R.id.free_lays);
        options = PredictView.findViewById(R.id.options_lay);

        layout1 = PredictView.findViewById(R.id.free_lay);
        layout2 = PredictView.findViewById(R.id.vip_lay);
        layout3 = PredictView.findViewById(R.id.watch_lay);

        scroll = PredictView.findViewById(R.id.scroll);
        indicator = PredictView.findViewById(R.id.circle_loading);

        prefs = getActivity().getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", true);

    }

    /**private void loadRewardedAds()
    {
        if (!isSubs && getActivity() != null)
        {
            if (retryAttempts >= MAX_RETRY_ATTEMPTS) {
                shouldRetry = false; // Stop retrying
                Log.e("NativeAdHelper", "Maximum retry attempts reached");
                return;
            }

            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(getActivity(), getString(R.string.Rewarded_ad_id_2),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            rewardedAd = null;

                            if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                            {
                                retryAttempts++;
                                loadRewardedAds();
                            }

                            indicator.setVisibility(View.GONE);
                            scroll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd = ad;
                            Log.d(TAG, "Ad was loaded.");

                            retryAttempts = 0;
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
                                    if (getActivity() != null)
                                    {
                                        loadRewardedAds();
                                    }
                                    Intent intent = new Intent(getActivity(), WatchActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when ad fails to show.
                                    Log.e(TAG, "Ad failed to show fullscreen content.");
                                    rewardedAd = null;
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

    private void loadRewardedAd()
    {
        if (!isSubs && getActivity() != null)
        {
            if (retryAttempts >= MAX_RETRY_ATTEMPTS) {
                shouldRetry = false; // Stop retrying
                Log.e("NativeAdHelper", "Maximum retry attempts reached");
                return;
            }
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(getActivity(), getString(R.string.Rewarded_ad_id_2),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());

                            rewardedAds = null;
                            if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                            {
                                retryAttempts++;
                                loadRewardedAds();
                            }
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAds = ad;
                            Log.d(TAG, "Ad was loaded.");

                            retryAttempts = 0;

                            rewardedAds.setFullScreenContentCallback(new FullScreenContentCallback() {
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
                                    rewardedAds = null;
                                    if (getActivity() != null)
                                    {
                                        loadRewardedAd();
                                    }
                                    Intent intent = new Intent(getActivity(), FreeActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when ad fails to show.
                                    Log.e(TAG, "Ad failed to show fullscreen content.");
                                    rewardedAds = null;
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

    private void buttonFunctions()
    {
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rewardedAds != null && getActivity() != null) {
                    rewardedAds.show(getActivity(), new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    Intent intent = new Intent(getActivity(), FreeActivity.class);
                    startActivity(intent);
                }


            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), VipActivity.class);
                startActivity(intent);

            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rewardedAd != null && getActivity() != null) {
                    rewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    Intent intent = new Intent(getActivity(), WatchActivity.class);
                    startActivity(intent);
                }
            }
        });
    }**/

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAdView !=null)
        {

            retryAttempts = 0;
            rewardedAd = null;
            rewardedAds = null;
            mAdView.destroy();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        retryAttempts = 0;

    }

    @Override
    public void onResume() {
        super.onResume();

        //loadRewardedAds();
        //loadRewardedAd();
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        if (getContext() != null)
        {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float density = outMetrics.density;

            float adWidthPixels = adContainerView.getWidth();

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0) {
                adWidthPixels = outMetrics.widthPixels;
            }

            int adWidth = (int) (adWidthPixels / density);
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
        }
        return null;
    }

    void createBannerAd()
    {
        if (!isSubs && getContext() != null)
        {
            if (retryAttempts >= MAX_RETRY_ATTEMPTS) {
                shouldRetry = false; // Stop retrying
                Log.e("NativeAdHelper", "Maximum retry attempts reached");
                return;
            }
            mAdView = new AdView(getContext());
            mAdView.setAdUnitId(getString(R.string.banner_add_id));
            adContainerView.removeAllViews();
            adContainerView.addView(mAdView);

            AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    //mAdView.setVisibility(View.GONE);
                    mAdView = null;
                    if (shouldRetry && retryAttempts < MAX_RETRY_ATTEMPTS)
                    {
                        retryAttempts++;
                        createBannerAd();
                    }

                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    mAdView.setVisibility(View.VISIBLE);
                    retryAttempts = 0;

                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });
        }


    }

}