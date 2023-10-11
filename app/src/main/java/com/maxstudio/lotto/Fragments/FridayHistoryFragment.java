package com.maxstudio.lotto.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Adapters.TuesAdapter;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.R;

import java.util.ArrayList;
import java.util.List;

public class FridayHistoryFragment extends Fragment {

    private RecyclerView ResultsRecyclerList;
    private String Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Mach1,Mach2,Mach3,Mach4,Mach5, Date;

    private DatabaseReference LottoRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator,indicator2;
    private boolean isLoading = false;
    private View View;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    public static boolean isSubs,isDestroyed,isScrolled;
    private boolean isMaxData = true;
    private FrameLayout adContainerView;
    private SharedPreferences prefs;

    private Runnable run,run2;
    private Handler handler;
    private String[] adUnitIds;
    private int currentAdIndex;
    private String[] adUnitIds2;
    private int currentAdIndex2 = 0;

    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;
    private int firstViewCount;

    private String day,last_key;
    private List<DailyResultsPicker> combinedDataList = new ArrayList<>();
    private List<DailyResultsPicker> newDataList = new ArrayList<>();
    private TuesAdapter adapter1;
    private Toolbar mToolBar;
    FirebaseRecyclerAdapter<DailyResultsPicker, RecyclerView.ViewHolder> adapter;

    public FridayHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_friday_history, container, false);

        LottoRef = FirebaseDatabase.getInstance().getReference()
                .child("Results History").child("Friday");

        day = "Friday";

        handler = new Handler();
        NestedScrollView scroll = View.findViewById(R.id.scroll);
        indicator2 = View.findViewById(R.id.circle_loading2);
        ResultsRecyclerList = View.findViewById(R.id.friday_recycler);
        //ResultsRecyclerList.setHasFixedSize(true);
        ResultsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && !isLoading)
                {
                    indicator2.setVisibility(android.view.View.VISIBLE);
                    isLoading = true;
                    nextLoad();
                    //handler.postDelayed(run,2000);
                    handler.postDelayed(run2,8000);
                }


                isScrolled = scrollY != 0;

            }
        });

        run = new Runnable() {
            @Override
            public void run() {

                nextLoad();
                handler.removeCallbacks(run);
            }
        };

        run2 = new Runnable() {
            @Override
            public void run() {

                isLoading = false;
                indicator2.setVisibility(android.view.View.GONE);
                handler.removeCallbacks(run2);
            }
        };

        //loadingBar = new ProgressDialog(getContext());
        indicator = View.findViewById(R.id.circle_loading);
        mToolBar = View.findViewById(R.id.main_page_toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null)
        {
            activity.setSupportActionBar(mToolBar);
            activity.getSupportActionBar().setTitle("Friday Bonanza History");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        InitialisedFields();

        currentAdIndex = 0;
        firstViewCount = 0;

        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .commit();


        SharedPreferences prefs = getActivity().getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", true);

        adUnitIds = new String[]{
                getString(R.string.banner_add_id_2),
                getString(R.string.banner_add_id_2_1),
                getString(R.string.banner_add_id_2_2),
                getString(R.string.banner_add_id_2_3),
                getString(R.string.banner_add_id_2_4)};


        adContainerView = View.findViewById(R.id.ad_view_container);

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


        //lastKey();

        adapter1 = new TuesAdapter(getContext(),combinedDataList,isScrolled,indicator
                ,LottoRef,day,isDestroyed,isLoading,indicator2);
        ResultsRecyclerList.setAdapter(adapter1);
        ViewCompat.setNestedScrollingEnabled(ResultsRecyclerList,false);

        listenData();

        return View;
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (adapter == null)
        {
            /**loadingBar.setTitle("Fetching Data");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            //loadingBar.show();**/

            //DataRequest();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getContext() !=null)
        {
            mAdView = new AdView(getContext());
            mAdView.destroy();
            //adapter.stopListening();

        }

        ResultsRecyclerList = View.findViewById(R.id.friday_recycler);
        ResultsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        //DataRequest2();
        isDestroyed = true;
    }

    private void listenData()
    {
        LottoRef.limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    combinedDataList.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        DailyResultsPicker dailyResultsPicker = s.getValue(DailyResultsPicker.class);
                        assert dailyResultsPicker != null;
                        dailyResultsPicker.setViewType(VIEW_TYPE_FIRST);
                        String d = s.getKey();
                        dailyResultsPicker.setKey(d);

                        combinedDataList.add(dailyResultsPicker);
                        //combinedDataList.add(newDataList)

                    }

                    adapter1.addAll(combinedDataList);
                    adapter1.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nextLoad()
    {

        DailyResultsPicker dailyResultsPicker = combinedDataList.get(combinedDataList.size()-1);
        String last = dailyResultsPicker.getKey();

        Query query = LottoRef.orderByKey().startAfter(last).limitToFirst(20);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    newDataList.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        DailyResultsPicker dailyResultsPicker = s.getValue(DailyResultsPicker.class);
                        assert dailyResultsPicker != null;
                        dailyResultsPicker.setViewType(VIEW_TYPE_FIRST);
                        String d = s.getKey();
                        dailyResultsPicker.setKey(d);

                        newDataList.add(dailyResultsPicker);

                    }


                    combinedDataList.addAll(newDataList);
                    adapter1.notifyItemRangeInserted(combinedDataList.size(),newDataList.size());
                    //isLoading = false;
                    //indicator2.setVisibility(android.view.View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });


    }

    private void lastKey()
    {
        Query query = LottoRef.orderByKey().limitToLast(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        last_key = s.getKey();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onResume() {
        super.onResume();

        //DataRequest();

    }

    private void InitialisedFields() {

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(Lotto1 == null && netInfo == null){

            //loadingBar.dismiss();
            indicator.setVisibility(android.view.View.GONE);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
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

        //adContainerView = View.findViewById(R.id.ad_view_container);

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
                    loadNextAd();

                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    adContainerView.setVisibility(android.view.View.VISIBLE);
                }
            });

        }

    }

}