package com.maxstudio.lotto.Fragments;

import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Activities.FreeActivity;
import com.maxstudio.lotto.Activities.HomeActivity;
import com.maxstudio.lotto.Activities.MainActivity;
import com.maxstudio.lotto.Activities.PredictActivity;
import com.maxstudio.lotto.Activities.SearchActivity;
import com.maxstudio.lotto.Activities.SignUpActivity;
import com.maxstudio.lotto.Activities.VipActivity;
import com.maxstudio.lotto.Activities.WatchActivity;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Adapters.RegAdapter;
import com.maxstudio.lotto.Adapters.TabsAdapter;
import com.maxstudio.lotto.Interfaces.Refresh;
import com.maxstudio.lotto.Utils.DynamicHeightViewPager;
import com.maxstudio.lotto.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultsFragment extends Fragment {

    private View view;

    private TextView lotto1,lotto2,lotto3,lotto4,lotto5,mach1,
            mach2,mach3,mach4,mach5,date,dNum,dType,tab_text;

    private LinearLayout layout,Lay, monLay,tuesLay,wedLay,thursLay,friLay,satLay,sunLay,homeLay, resLay;

    private RelativeLayout clickLay, clickHere;

    private String Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Mach1,Mach2,Mach3,Mach4,Mach5, Date,orderRef,stDate;
    private TextView txtDay, txtHours, txtMinutes,txtSeconds,txtMicro, txtHours1, txtMinutes1,txtSeconds1;
    private String Lotto_1,Lotto_2,Lotto_3,Lotto_4,Lotto_5,Mach_1,Mach_2,Mach_3,Mach_4,Mach_5,
            Date_1,Draw_num,Draw_type,Future_date,live;

    private AlertDialog.Builder builder;
    public DialogInterface dialog;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;

    final int[] fragmentHeight = {0};
    private TabLayout tabLayout;
    private DynamicHeightViewPager viewPager;
    private TabsAdapter tabsAdapter;

    private ExecutorService executor;
    Handler handler;

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 100;

    private final String[] title = {"Gen","Daywa","Super 6","Vag"};

    public static InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private AdLoader adLoader;
    private NativeAdView nativeAdView;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private OnUserEarnedRewardListener onUserEarnedRewardListener;
    private String TAG = "ResultsFragment";
    private int click_count,dialogs;
    private DatabaseReference LottoRef,LottoRefs,DataRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator,indicator2;
    public static RewardedAd rewardedAd;
    public static RewardedAd rewardedAd2;
    private boolean isSubs,isVisible,isRecreated;
    private FrameLayout adContainerView;
    private AdView mAdView;
    private Handler handlers;
    private SharedPreferences prefs;

    private String[] adUnitIds,adUnitIds3;
    private int currentAdIndex;
    public static boolean isSwiped;
    private String[] adUnitIds2;
    private int currentAdIndex2 = 0;
    private int currentAdIndex3 = 0;
    private int interstitial_count,inter_ad,rewarded_ad,rewarded_ad2;
    private static final long BACKGROUND_THRESHOLD = 60 * 60 * 1000; // 1 hour in milliseconds
    private boolean isInBackground = false;
    private Handler backgroundHandler;
    private Runnable backgroundRunnable;
    private SharedPreferences.Editor edit;
    private SwipeRefreshLayout swipe;
    private NestedScrollView scrollView;
    private Runnable runnable,run,run1,run2,run3,run4,run5,run6,run7,run8,run9,run10;
    private View.OnClickListener onClickListener;
    public static boolean isReady;
    private long backgroundTime = 0;
    private ValueEventListener val,val2;
    private int recreated;

    public ResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_results, container, false);

        recreated++;

        if (recreated == 2)
        {
            isRecreated = true;
            recreated = 1;
        }

        DataRef = FirebaseDatabase.getInstance().getReference();

        InitialisedFields();
        InitializedFields();

        currentAdIndex = 0;
        runnable = new Runnable() {
            @Override
            public void run() {

                if (mAdView != null) {
                    mAdView.destroy();
                    mAdView = null;
                    loadNextAd();
                } else {
                    loadNextAd();
                }

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

        adUnitIds3 = new String[]{
                getString(R.string.Interstitial_add_id_2),
                getString(R.string.Interstitial_add_id),
                getString(R.string.Interstitial_add_id_3),
                getString(R.string.Interstitial_add_id_4),
                getString(R.string.Interstitial_add_id_5)};

        adContainerView = view.findViewById(R.id.ad_view_container);
        adContainerView.setVisibility(View.GONE);


        adContainerView.post(runnable);

        executor = Executors.newSingleThreadExecutor();

        backgroundHandler = new Handler();
        handlers = new Handler();
        backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                isInBackground = true;
                // Perform your background logic here
            }
        };

        val = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Lotto_1 = snapshot.child("lotto1").getValue().toString();

                    if (!Lotto_1.isEmpty()) {

                        handlers.postDelayed(run, 3000);

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

                        lotto1.setText(Lotto_1);
                        lotto2.setText(Lotto_2);
                        lotto3.setText(Lotto_3);
                        lotto4.setText(Lotto_4);
                        lotto5.setText(Lotto_5);
                        mach1.setText(Mach_1);
                        mach2.setText(Mach_2);
                        mach3.setText(Mach_3);
                        mach4.setText(Mach_4);
                        mach5.setText(Mach_5);

                        date.setText(Date_1);
                        dNum.setText(Draw_num);
                        dType.setText(Draw_type);

                        swipe.setRefreshing(false);

                    }
                }
                else
                {
                    handlers.postDelayed(run9,3000);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        val2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Lotto_1 = snapshot.child("lotto1").getValue().toString();

                    if (!Lotto_1.isEmpty()) {
                        if (interstitial_count < 3) {
                            handlers.postDelayed(run, 2000);
                        }

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

                        lotto1.setText(Lotto_1);
                        lotto2.setText(Lotto_2);
                        lotto3.setText(Lotto_3);
                        lotto4.setText(Lotto_4);
                        lotto5.setText(Lotto_5);
                        mach1.setText(Mach_1);
                        mach2.setText(Mach_2);
                        mach3.setText(Mach_3);
                        mach4.setText(Mach_4);
                        mach5.setText(Mach_5);

                        date.setText(Date_1);
                        dNum.setText(Draw_num);
                        dType.setText(Draw_type);

                        swipe.setRefreshing(false);

                    }
                }
                else
                {
                    handlers.postDelayed(run9,3000);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        run = new Runnable() {
            @Override
            public void run() {

                indicator.setVisibility(View.GONE);
                homeLay.setVisibility(View.VISIBLE);
                swipe.setEnabled(true);
                //VagFragment.ready = false;
                handlers.removeCallbacks(run);

            }
        };
        run1 = new Runnable() {
            @Override
            public void run() {

                rewarded_ad2 = prefs.getInt("rewarded_ad_2", 0);

                if (rewarded_ad2 == 0) {
                    loadRewardedAds2();

                }

            }
        };
        run2 = new Runnable() {
            @Override
            public void run() {

                RegularFragment regularFragment = (RegularFragment) tabsAdapter.getItem(0);
                regularFragment.refresh();
                DaywaFragment daywa = (DaywaFragment) tabsAdapter.getItem(1);
                daywa.refresh();
                SuperFragment supers = (SuperFragment) tabsAdapter.getItem(2);
                supers.refresh();
                VagFragment vag = (VagFragment) tabsAdapter.getItem(3);
                vag.refresh();

            }
        };
        run3 = new Runnable() {
            @Override
            public void run() {

                clickLay.setVisibility(View.GONE);
                resLay.setVisibility(View.VISIBLE);
                //handlers.removeCallbacks(run3);

            }
        };
        run4 = new Runnable() {
            @Override
            public void run() {

                indicator2.setVisibility(View.GONE);
                clickLay.setVisibility(View.GONE);
                resLay.setVisibility(View.VISIBLE);

                handlers.removeCallbacks(run4);
            }
        };
        run5 = new Runnable() {
            @Override
            public void run() {

                rewarded_ad = prefs.getInt("rewarded_ad", 0);

                if (rewarded_ad == 0) {
                    loadRewardedAds();

                }

            }
        };
        run6 = new Runnable() {
            @Override
            public void run() {

                mInterstitialAd = null;
                //currentAdIndex++;
                loadAd();

            }
        };
        run7 = new Runnable() {
            @Override
            public void run() {

                if (inter_ad == 0) {
                    loadAd();

                }
            }
        };
        run8 = new Runnable() {
            @Override
            public void run() {

                isSwiped = true;
                todayDataRequest();

                handler.postDelayed(run2,200);

                if (isRecreated)
                {
                    /**FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    ResultsFragment resultsFragment = new ResultsFragment();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, resultsFragment);
                    fragmentTransaction.commit();**/

                    isRecreated = false;
                }
                /**else
                {
                    isSwiped = true;
                    todayDataRequest();

                    handler.postDelayed(run2,200);
                }**/

            }
        };
        run9 = new Runnable() {
            @Override
            public void run() {

                swipe.setRefreshing(false);
                Toast.makeText(getContext(), "network error", Toast.LENGTH_SHORT).show();
            }
        };
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //click_count = prefs.getInt("click_count", 0);
                click_count++;

                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("click_count", click_count);
                edit.commit();

                if (click_count > 1) {
                    if (interstitial_count < 3) {
                        edit.putInt("dialog",0);
                        edit.commit();
                        if (mInterstitialAd != null) {

                            if (getContext() != null) {
                                mInterstitialAd.show((Activity) getContext());
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();

                                        mInterstitialAd = null;
                                        MainActivity.mInterstitialAd = null;
                                        MyApplication.isAdFullScreen = false;

                                        edit.putInt("click_count", 0);
                                        edit.putBoolean("gone", false);
                                        edit.commit();

                                        inter_ad = prefs.getInt("inter_ad", 0);

                                        if (inter_ad == 0) {
                                            loadAd();

                                        }

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent();

                                        MyApplication.isAdFullScreen = true;

                                        handlers.postDelayed(run3, 2000);

                                        interstitial_count = prefs.getInt("inter_count", 0);
                                        interstitial_count++;

                                        edit.putInt("inter_count", interstitial_count);
                                        edit.putInt("inter_ad", 0);
                                        edit.putBoolean("gone", true);
                                        edit.commit();

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);

                                        isReady = true;

                                        Lay.setVisibility(View.GONE);
                                        indicator2.setVisibility(View.VISIBLE);

                                        handlers.postDelayed(run4, 3000);

                                        interstitial_count = prefs.getInt("inter_count", 0);
                                        interstitial_count++;

                                        edit.putInt("inter_count", interstitial_count);
                                        edit.putInt("inter_ad", 0);
                                        edit.commit();

                                        inter_ad = prefs.getInt("inter_ad", 0);

                                        if (inter_ad == 0) {
                                            loadAd();
                                        }

                                    }
                                });

                            }

                        }
                        else {

                            Lay.setVisibility(View.GONE);
                            indicator2.setVisibility(View.VISIBLE);

                            handlers.postDelayed(run4, 3000);


                            edit.putInt("click_count", 1);
                            //edit.putInt("inter_ad", 0);
                            edit.commit();

                            inter_ad = prefs.getInt("inter_ad", 0);

                            if (inter_ad == 0) {
                                loadAd();
                            }
                        }
                    }
                    else {

                        dialogs = prefs.getInt("dialog",0);
                        if (dialogs == 0)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);

                            builder.setTitle("Remove Ads");
                            builder.setIcon(R.drawable.ic_baseline_error_24);
                            builder.setMessage("Get rid of ads for a better experience");
                            builder.setCancelable(false);

                            builder.setNegativeButton("Watch ad", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

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


                                    }
                                    else {
                                        rewarded_ad = prefs.getInt("rewarded_ad", 0);

                                        if (rewarded_ad == 0) {
                                            loadRewardedAds();
                                        }

                                        Log.d(TAG, "The rewarded ad wasn't ready yet.");

                                        Lay.setVisibility(View.GONE);
                                        indicator2.setVisibility(View.VISIBLE);

                                        handlers.postDelayed(run4, 3000);

                                    }
                                    dialog.cancel();
                                }
                            });

                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getContext(), VipActivity.class);
                                    startActivity(intent);

                                }
                            });
                            builder.show();
                            dialogs++;
                            edit.putInt("dialog",dialogs);
                            edit.commit();
                        }
                        else
                        {

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
                                rewarded_ad = prefs.getInt("rewarded_ad", 0);

                                if (rewarded_ad == 0) {
                                    loadRewardedAds();
                                }

                                Log.d(TAG, "The rewarded ad wasn't ready yet.");

                                Lay.setVisibility(View.GONE);
                                indicator2.setVisibility(View.VISIBLE);

                                handlers.postDelayed(run4, 3000);

                            }

                        }

                    }

                } else {
                    Lay.setVisibility(View.GONE);
                    indicator2.setVisibility(View.VISIBLE);

                    handlers.postDelayed(run4, 3000);

                }

            }
        };

        CacheResults();

        if (MainActivity.rewardedAd != null) {
            rewardedAd = MainActivity.rewardedAd;
            handlers.postDelayed(run, 2000);
        }

        if (SignUpActivity.rewardedAd != null) {
            rewardedAd = SignUpActivity.rewardedAd;
            handlers.postDelayed(run, 2000);
        }

        if (MainActivity.mInterstitialAd != null) {
            mInterstitialAd = MainActivity.mInterstitialAd;
        }

        edit = prefs.edit();

        click_count = prefs.getInt("click_count", 0);
        interstitial_count = prefs.getInt("inter_count", 0);

        inter_ad = prefs.getInt("inter_ad", 0);
        rewarded_ad = prefs.getInt("rewarded_ad", 0);

        rewarded_ad2 = prefs.getInt("rewarded_ad_2", 0);

        handlers.post(new Runnable() {
            @Override
            public void run() {

                if (inter_ad == 0) {
                    loadAd();
                }
                if (rewarded_ad == 0) {
                    loadRewardedAds();
                }
                if (rewarded_ad2 == 0) {
                    loadRewardedAds2();
                }

                todayDataRequest();
                clickEventListener();

            }
        });

        countDownStart();
        swipeLayout();

        isVisible = prefs.getBoolean("results_visible", false);


        /**mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                                AppUpdateType.FLEXIBLE,getActivity(),RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        mAppUpdateManager.registerListener(installStateUpdatedListener);**/


        return  view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("restored", true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
        {
            isRecreated = savedInstanceState.getBoolean("restored");
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        //isRecreated = true;
    }

    private void loadRewardedAds2()
    {
        if (!isSubs && getContext() != null)
        {
            if (currentAdIndex2 >= adUnitIds2.length) {
                // All ads have been loaded or failed to load

                currentAdIndex2 = 0;
                return;
            }

            String adUnitId = adUnitIds2[currentAdIndex2];
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(getContext(), adUnitId,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            rewardedAd2 = null;

                            currentAdIndex2++;
                            handlers.post(run1);

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd2 = ad;
                            Log.d(TAG, "Ad was loaded.");

                            rewarded_ad2++;

                            edit.putInt("rewarded_ad_2",rewarded_ad2);
                            edit.commit();

                            handlers.removeCallbacks(run1);

                        }
                    });
        }


    }

    private void InitialisedFields() {

        txtDay =  view.findViewById(R.id.tDay);
        txtHours =  view.findViewById(R.id.tHours);
        txtMinutes =  view.findViewById(R.id.tMin);
        txtSeconds =  view.findViewById(R.id.tSec);

        txtMicro =  view.findViewById(R.id.t_Micro);
        txtHours1 =  view.findViewById(R.id.t_Hours);
        txtMinutes1 =  view.findViewById(R.id.t_Mins);
        txtSeconds1 =  view.findViewById(R.id.t_Sec);

        homeLay = view.findViewById(R.id.home_lay);

        monLay = view.findViewById(R.id.mon_lay);

        //sunLay = view.findViewById(R.id.sun_lay);

        resLay = view.findViewById(R.id.res_lay);

        clickLay = view.findViewById(R.id.click_layout);
        clickHere = view.findViewById(R.id.click_here);
        Lay = view.findViewById(R.id.lay);

        indicator = view.findViewById(R.id.circle_loading);
        indicator2 = view.findViewById(R.id.circle_loading2);
        swipe = view.findViewById(R.id.swipe);
        scrollView = view.findViewById(R.id.scroll);
        //swipe.setEnabled(false);

        tabLayout = view.findViewById(R.id.home_tab2);
        tabsAdapter = new TabsAdapter(getChildFragmentManager());

        viewPager = view.findViewById(R.id.view_pager2);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setSwipeEnabled(true);
        viewPager.setOffscreenPageLimit(3);

        int selectedColor = getResources().getColor(R.color.selected);
        int unSelectedColor = getResources().getColor(R.color.unselected);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext())
                    .inflate(R.layout.tab_back_layout,null);
            tab_text = layout.findViewById(R.id.text);
            RelativeLayout layout1 = layout.findViewById(R.id.tab);

            tab_text.setText("" + title[i]);

            tab_text.setTextColor(i == 0? selectedColor:unSelectedColor);
            Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
            tab_text.setTypeface(typeface);
            tab_text.setTextSize(13.5F);

            tabLayout.getTabAt(i).setCustomView(layout1);

        }

        TabLayout.Tab firstTab = tabLayout.getTabAt(0);
        if (firstTab != null)
        {
            firstTab.select();
            LinearLayout layout1 = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0))
                    .getChildAt(firstTab.getPosition());
            layout1.setBackgroundColor(ContextCompat.
                    getColor(getApplicationContext(),R.color.selectedBackColor));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Get the custom view for the selected tab
                View customView = tab.getCustomView();
                LinearLayout layout1 = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0))
                        .getChildAt(tab.getPosition());
                layout1.setBackgroundColor(ContextCompat.
                        getColor(getApplicationContext(),R.color.selectedBackColor));

                // Update the text color of the selected tab
                assert customView != null;
                TextView tabText = customView.findViewById(R.id.text);
                tabText.setTextColor(selectedColor);

                viewPager.setCurrentItem(tab.getPosition(),false);// Set your desired color

                // Optionally, update other UI elements for the selected tab
                // ...
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Get the custom view for the unselected tab
                View customView = tab.getCustomView();

                LinearLayout layout1 = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0))
                        .getChildAt(tab.getPosition());
                layout1.setBackgroundColor(ContextCompat.
                        getColor(getApplicationContext(),R.color.unSelectedBackColor));

                // Update the text color of the unselected tab
                TextView tabText = customView.findViewById(R.id.text);
                tabText.setTextColor(unSelectedColor);
                // Set your desired color

                // Optionally, update other UI elements for the unselected tab
                // ...
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });

        /**viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position == 0)
                {
                    viewPager.requestLayout();
                }

                viewPager.requestLayout();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });**/

        layout = view.findViewById(R.id.tab_lay);

        cm = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog);


        if(netInfo == null){

            //loadingBar.dismiss();
            indicator.setVisibility(View.GONE);
            builder.setTitle("Network Error");
            builder.setMessage("It appears your network is offline: can't connect to server");
            builder.setIcon(R.drawable.ic_baseline_error_24);
            builder.setCancelable(false);


            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else if (netInfo != null){
            dialog = builder.create();
            dialog.dismiss();
            //builder.setView(null);
            //loadingBar.show();
            indicator.setVisibility(View.VISIBLE);
        }


    }

    private void InitializedFields() {

        lotto1 = view.findViewById(R.id.lotto_1);
        lotto2 = view.findViewById(R.id.lotto_2);
        lotto3 = view.findViewById(R.id.lotto_3);
        lotto4 = view.findViewById(R.id.lotto_4);
        lotto5 = view.findViewById(R.id.lotto_5);
        mach1 =  view.findViewById(R.id.mach_1);
        mach2 = view.findViewById(R.id.mach_2);
        mach3 = view.findViewById(R.id.mach_3);
        mach4 = view.findViewById(R.id.mach_4);
        mach5 = view.findViewById(R.id.mach_5);

        Typeface typeface = Typeface.create("sans-serif",Typeface.BOLD);

        lotto1.setTypeface(typeface);
        lotto2.setTypeface(typeface);
        lotto3.setTypeface(typeface);
        lotto4.setTypeface(typeface);
        lotto5.setTypeface(typeface);
        mach1.setTypeface(typeface);
        mach2.setTypeface(typeface);
        mach3.setTypeface(typeface);
        mach4.setTypeface(typeface);
        mach5.setTypeface(typeface);

        date = view.findViewById(R.id.t_date);
        dNum = view.findViewById(R.id.draw_num);
        dType = view.findViewById(R.id.draw_type);

        prefs = getActivity().getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

        //layout = view.findViewById(R.id.results_layout);
    }

    public void countDownStart() {

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 10);

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

                    if (Future_date != null)
                    {
                        java.util.Date futureDate = dateFormat.parse(Future_date);
                        Date currentDate = new Date();

                        if (!currentDate.after(futureDate))
                        {
                            long diff = futureDate.getTime() - currentDate.getTime();
                            long days = diff / (24 * 60 * 60 * 1000);
                            diff -= days*(24 * 60 * 60 * 1000);
                            long hours = diff / (60 * 60 * 1000);
                            diff -= hours*(60 * 60 * 1000);
                            long minutes = diff / (60 * 1000);
                            diff -= minutes*(60 * 1000);
                            long seconds = diff / 1000;
                            diff -= seconds * 1000;
                            long millisecond = diff;


                            txtDay.setText(""+String.format("%02d",days));
                            txtHours.setText(""+String.format("%02d",hours));
                            txtMinutes.setText(""+String.format("%02d",minutes));
                            txtSeconds.setText(""+String.format("%02d",seconds));
                            //txtDay.setText(""+String.format("$02d",days));

                        }else{
                            txtDay.setText("00");
                            txtHours.setText("00");
                            txtMinutes.setText("00");
                            txtSeconds.setText("00");
                        }
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
        handler.postDelayed(runnable, 1*10);
    }

    private void todayDataRequest()
    {
        if (isSubs)
        {
            DataRef.child("Today").child("Results")
                    .addListenerForSingleValueEvent(val);

        }
        else
        {
            DataRef.child("Today").child("Results")
                    .addListenerForSingleValueEvent(val2);

        }

    }

    private void CacheResults()
    {
        if (MainActivity.Lotto_1 != null && MainActivity.Mach_1 != null)
        {
            Lotto_1 = MainActivity.Lotto_1;
            lotto1.setText(MainActivity.Lotto_1);
            mach1.setText(MainActivity.Mach_1);
        }
        if (MainActivity.Lotto_2 != null && MainActivity.Mach_2 != null)
        {
            lotto2.setText(MainActivity.Lotto_2);
            mach2.setText(MainActivity.Mach_2);
        }
        if (MainActivity.Lotto_3 != null && MainActivity.Mach_3 != null)
        {
            lotto3.setText(MainActivity.Lotto_3);
            mach3.setText(MainActivity.Mach_3);
        }
        if (MainActivity.Lotto_4 != null && MainActivity.Mach_4 != null)
        {
            lotto4.setText(MainActivity.Lotto_4);
            mach4.setText(MainActivity.Mach_4);
        }
        if (MainActivity.Lotto_5 != null && MainActivity.Mach_5 != null)
        {
            lotto5.setText(MainActivity.Lotto_5);
            mach5.setText(MainActivity.Mach_5);
        }

        if (MainActivity.Date_1 != null && MainActivity.Draw_num != null
                && MainActivity.Draw_type != null && MainActivity.Future_date != null)
        {
            date.setText(MainActivity.Date_1);
            dNum.setText(MainActivity.Draw_num);
            dType.setText(MainActivity.Draw_type);
            Future_date = MainActivity.Future_date;
        }

    }

    private void swipeLayout()
    {
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                boolean isAtTOP = scrollView.getScrollY() == 0;

                swipe.setEnabled(isAtTOP);

            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                handlers.postDelayed(run8, 2000);


            }
        });
        swipe.setColorSchemeColors(
                getResources().getColor(R.color.selected)
        );
    }

    private void clickEventListener(){

        clickHere.setOnClickListener(onClickListener);
    }

    private void loadRewardedAds()
    {
        if (!isSubs)
        {
            if (getContext() != null)
            {
                if (currentAdIndex2 >= adUnitIds2.length) {
                    // All ads have been loaded or failed to load
                    currentAdIndex2 = 0;
                    indicator.setVisibility(View.GONE);
                    homeLay.setVisibility(View.VISIBLE);

                    return;
                }

                String adUnitId = adUnitIds2[currentAdIndex2];
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(getContext(), adUnitId,
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                Log.d(TAG, loadAdError.toString());
                                rewardedAd = null;

                                currentAdIndex2++;

                                handlers.postDelayed(run5,2000);
                                //isReady = false;
                                //todayDataRequest();
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                rewardedAd = ad;
                                //rewardedAd2 = ad;
                                Log.d(TAG, "Ad was loaded.");

                                rewarded_ad++;

                                edit.putInt("rewarded_ad", rewarded_ad);
                                edit.commit();

                                handlers.removeCallbacks(run5);

                                if (!isReady)
                                {
                                    indicator.setVisibility(View.GONE);
                                    homeLay.setVisibility(View.VISIBLE);
                                }

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

                                        rewarded_ad = prefs.getInt("rewarded_ad",0);

                                        if (rewarded_ad == 0)
                                        {
                                            loadRewardedAds();

                                        }

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);

                                        Log.e(TAG, "Ad failed to show fullscreen content.");
                                        rewardedAd = null;
                                        MainActivity.rewardedAd = null;
                                        SignUpActivity.rewardedAd = null;
                                        //currentAdIndex2++;

                                        //daywaFragment.isNativeVisible(true);

                                        handlers.postDelayed(run3,2000);

                                        interstitial_count = prefs.getInt("inter_count", 0);
                                        interstitial_count++;

                                        //SharedPreferences.Editor edit = prefs.edit();
                                        edit.putInt("rewarded_ad",0);
                                        edit.putInt("inter_count", interstitial_count);
                                        edit.commit();

                                        if (interstitial_count > 5)
                                        {

                                            edit.putInt("inter_count", 0);
                                            edit.putInt("click_count", 0);
                                            edit.commit();
                                        }

                                        rewarded_ad = prefs.getInt("rewarded_ad",0);

                                        if (rewarded_ad == 0)
                                        {
                                            loadRewardedAds();
                                        }

                                    }

                                    @Override
                                    public void onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                        Log.d(TAG, "Ad recorded an impression.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent();

                                        handlers.postDelayed(run3,2000);
                                        MyApplication.isAdFullScreen = true;
                                        //isReady = true;

                                        interstitial_count = prefs.getInt("inter_count", 0);
                                        interstitial_count++;

                                        //SharedPreferences.Editor edit = prefs.edit();
                                        edit.putInt("rewarded_ad",0);
                                        edit.putInt("inter_count", interstitial_count);
                                        edit.commit();

                                        if (interstitial_count > 5)
                                        {

                                            edit.putInt("inter_count", 0);
                                            edit.putInt("click_count", 0);
                                            edit.commit();
                                        }

                                    }
                                });
                            }
                        });
            }
        }
        else
        {
            todayDataRequest();
        }

    }

    public void loadAd()
    {
        if (!isSubs)
        {
            if (getContext() != null)
            {
                if (currentAdIndex3 >= adUnitIds3.length) {
                    // All ads have been loaded or failed to load
                    currentAdIndex3 = 0;
                    return;
                }
                String adUnitId = adUnitIds3[currentAdIndex3];
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                InterstitialAd.load(getContext(),adUnitId, adRequest,
                        new InterstitialAdLoadCallback() {

                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                inter_ad++;

                                edit.putInt("inter_ad", inter_ad);
                                edit.commit();

                                mInterstitialAd = interstitialAd;
                                //retryAttempts2 = 0;
                                handlers.postDelayed(run6, BACKGROUND_THRESHOLD);

                                handlers.removeCallbacks(run6);
                                handlers.removeCallbacks(run7);

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
                                currentAdIndex3++;
                                handlers.post(run7);

                            }

                        });
            }
        }

    }


    @Override
    public void onDestroy() {

        if (getContext() != null)
        {

            mAdView = new AdView(getContext());
            mAdView.destroy();

        }

        if (isSubs)
        {
            DataRef.child("Today").child("Results")
                    .removeEventListener(val);

        }
        else
        {
            DataRef.child("Today").child("Results")
                    .removeEventListener(val2);

        }

        handlers.removeCallbacks(run);
        handlers.removeCallbacks(run1);
        handlers.removeCallbacks(run2);
        handlers.removeCallbacks(run3);
        handlers.removeCallbacks(run4);
        handlers.removeCallbacks(run5);
        handlers.removeCallbacks(run6);
        handlers.removeCallbacks(run7);
        handlers.removeCallbacks(run8);
        handlers.removeCallbacks(run9);

        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //viewPager.setOffscreenPageLimit(1);

        if (getContext() != null)
        {

            mAdView = new AdView(getContext());
            mAdView.destroy();

        }
        adContainerView.removeCallbacks(runnable);
        if (isSubs)
        {
            DataRef.child("Today").child("Results")
                    .removeEventListener(val);

        }
        else
        {
            DataRef.child("Today").child("Results")
                    .removeEventListener(val2);

        }

    }

    @Override
    public void onPause() {
        super.onPause();

        isInBackground = true;
        backgroundTime = System.currentTimeMillis();
        backgroundHandler.postDelayed(backgroundRunnable, BACKGROUND_THRESHOLD);

        if (isSubs)
        {
            DataRef.child("Today").child("Results")
                    .removeEventListener(val);

        }
        else
        {
            DataRef.child("Today").child("Results")
                    .removeEventListener(val2);

        }


    }

    @Override
    public void onResume() {
        super.onResume();

        //loadRewardedAds();

        if (isInBackground) {
            long foregroundTime = System.currentTimeMillis() - backgroundTime;
            if (foregroundTime > BACKGROUND_THRESHOLD) {
                // App has been in the background for more than an hour
                // Perform your action here
                //mInterstitialAd = null;
                rewardedAd = null;

                adContainerView = view.findViewById(R.id.ad_view_container);
                adContainerView.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mAdView != null)
                        {mAdView.destroy();
                            mAdView = null;
                            loadNextAd();}
                        else{loadNextAd();}

                        //loadRewardedAds();
                    }
                });
            }
            backgroundHandler.removeCallbacks(backgroundRunnable);
            isInBackground = false;

        }

        if (isSubs)
        {
            DataRef.child("Today").child("Results")
                    .addListenerForSingleValueEvent(val);

        }
        else
        {
            DataRef.child("Today").child("Results")
                    .addListenerForSingleValueEvent(val2);

        }

    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        if (getActivity() != null)
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
        return getAdSize();
    }

    private void loadNextAd() {

        if (!isSubs && getContext() != null)
        {
            if (currentAdIndex >= adUnitIds.length) {
                // All ads have been loaded or failed to load
                currentAdIndex = 0;
                return;
            }

            String adUnitId = adUnitIds[currentAdIndex];
            mAdView = new AdView(getContext());
            mAdView.setAdUnitId(adUnitId);
            adContainerView.removeAllViews();
            adContainerView.addView(mAdView);

            AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    //mAdView.setVisibility(View.GONE);
                    currentAdIndex++;
                    adContainerView = view.findViewById(R.id.ad_view_container);
                    adContainerView.post(new Runnable() {
                        @Override
                        public void run() {

                            loadNextAd();
                        }
                    });

                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    // Ad successfully loaded
                    //currentAdIndex = 0;
                    //loadNextAd();
                    adContainerView.setVisibility(View.VISIBLE);
                }
            });

        }

    }


    /**private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {

            if (installState.installStatus() == InstallStatus.DOWNLOADED)
            {
                showCompletedUpdate();
            }

        }
    };**/

    /**private void showCompletedUpdate() {

        Snackbar snackbar = Snackbar.make(view.findViewById(android.R.id.content),
                "New app is ready", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAppUpdateManager.completeUpdate();

            }
        });
        snackbar.show();
    }**/

    /**@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK)
        {

            Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }**/

}