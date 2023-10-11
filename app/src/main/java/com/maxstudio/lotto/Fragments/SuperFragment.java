package com.maxstudio.lotto.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Activities.MainActivity;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Adapters.AltAdapter;
import com.maxstudio.lotto.Adapters.SupAdapter;
import com.maxstudio.lotto.Interfaces.Refresh;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperFragment extends Fragment implements Refresh {
    private View view;

    private DatabaseReference LottoRef,LottoRefs,DataRef;

    private static boolean isDestroyed;
    private boolean isScrolled;
    private SupAdapter supAdapter;
    private ProgressBar indicator;
    private RecyclerView rec;
    public static boolean isRefreshed;
    private int VIEW_TYPE_FIRST = 0;
    private List<DailyResultsPicker> combinedDataList = new ArrayList<>();
    private final String[] com = {"Monday","Tuesday","Wednesday","Thursday","Friday"};

    public SuperFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_super, container, false);

        DataRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference LottoRef = FirebaseDatabase.getInstance()
                .getReference().child("Other Games")
                .child("Super");


        //indicator = view.findViewById(R.id.circle_loading);
        rec = view.findViewById(R.id.recycler);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        //rec.setHasFixedSize(true);
        rec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy == 0)
                {
                    isScrolled = false;
                }
                else
                {
                    isScrolled = true;
                }

            }
        });

        supAdapter = new SupAdapter(getContext(),combinedDataList,
                LottoRef,isDestroyed,isScrolled,com,rec);
        rec.setAdapter(supAdapter);
        ViewCompat.setNestedScrollingEnabled(rec,false);

        requests();


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isDestroyed = true;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

        //removeListener();

    }

    private void requests()
    {

        if (MainActivity.combinedDataList2.isEmpty())
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
                        combinedDataList.clear();
                        for (DataSnapshot s : snapshot.getChildren())
                        {
                            DailyResultsPicker daily = s.getValue(DailyResultsPicker.class);
                            assert daily != null;
                            daily.setViewType(VIEW_TYPE_FIRST);
                            String key = s.getKey();
                            daily.setKey(key);

                            combinedDataList.add(daily);

                        }


                        if (supAdapter != null)
                        {
                            supAdapter.notifyDataSetChanged();
                            //rec.requestLayout();
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(getContext(), "failed to fetch data", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else
        {
            combinedDataList.clear();
            combinedDataList.addAll(MainActivity.combinedDataList2);
            if (supAdapter != null)
            {
                supAdapter.notifyDataSetChanged();
                //rec.requestLayout();
            }

        }
    }


    @Override
    public void refresh() {

        isRefreshed = true;
        requests();

    }
}