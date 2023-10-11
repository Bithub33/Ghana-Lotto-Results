package com.maxstudio.lotto.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Adapters.SearchAdapter;
import com.maxstudio.lotto.Adapters.TuesAdapter;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.Models.GroupPicker;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Models.Messages;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Utils.NumberUtil;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private String groupName,search,groupMemCounts,groupImage, currentUserId, img;
    private long groupMemCount,item_count;
    private DatabaseReference groupRef, userRef;
    private Query query;
    private boolean isReq;
    private Runnable runnable;
    private int currentPage = 1;
    private SharedPreferences sharedPref;
    private ValueEventListener valueEventListener;

    private FirebaseAuth mAuth;

    private long count;

    private AdView mAdView;

    private FirebaseUser user;
    private boolean isSubs,isScrolled,isDestroyed;
    private boolean isJoin;

    private FrameLayout adContainerView;

    private ProgressBar indicator,indicator2;
    private String[] adUnitIds;
    private int currentAdIndex;
    private ExecutorService executor;
    private Runnable run,run2;
    private Handler handler;
    private String d,last_key;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;

    private List<GroupPicker> combinedDataList = new ArrayList<>();
    private List<GroupPicker> new_list = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isMaxItem = false;
    FirebaseRecyclerAdapter<GroupPicker, RecyclerView.ViewHolder> adapter;
    private SearchAdapter adapter1;
    int Counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        //createBannerAd();

        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId);
        user = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.sec_rec);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NestedScrollView scroll = findViewById(R.id.scroll);

        scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && !isLoading)
                {
                    isLoading = true;
                    indicator2.setVisibility(View.VISIBLE);
                    handler.postDelayed(run,2000);
                    handler.postDelayed(run2,6000);
                }


                if (scrollY == 0)
                {
                    isScrolled = false;
                }
                else
                {
                    isScrolled = true;
                }

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
                indicator2.setVisibility(View.GONE);
                handler.removeCallbacks(run2);
            }
        };

        handler = new Handler();

        mToolbar = findViewById(R.id.search_page_toolbar);
        setSupportActionBar(mToolbar);
        setTitle("Find Groups");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentAdIndex = 0;
        //isScrolled = false;

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

        //currentAdIndex2 = 0;

        adUnitIds = new String[]{
                getString(R.string.banner_add_id),
                getString(R.string.banner_add_id_1_1),
                getString(R.string.banner_add_id_1_2),
                getString(R.string.banner_add_id_1_3),
                getString(R.string.banner_add_id_1_4)};

        adContainerView = findViewById(R.id.ad_view_container);
        indicator = findViewById(R.id.circle_loading);
        indicator2 = findViewById(R.id.circle_loading2);
        //indicator2.setVisibility(View.GONE);

        adContainerView.post(runnable);

        InitialisedFields();
        searchFunction();

        listenData();
        //lastKey();

        //requestGroup();

    }

    private void listenData()
    {
        groupRef.limitToFirst(12).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    combinedDataList.clear();
                    List<GroupPicker> new_list = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        GroupPicker groupPicker = s.getValue(GroupPicker.class);
                        assert groupPicker != null;
                        groupPicker.setViewType(VIEW_TYPE_FIRST);
                        d = s.getKey();
                        assert d != null;
                        groupPicker.setKey(d);

                        //combinedDataList.clear();
                        combinedDataList.add(groupPicker);

                    }

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
        GroupPicker groupPicker = combinedDataList.get(combinedDataList.size()-1);
        String groupKey = groupPicker.getKey();

        Query query = groupRef.orderByKey().startAfter(groupKey).limitToFirst(8);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    new_list.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        GroupPicker dailyResults = s.getValue(GroupPicker.class);
                        assert dailyResults != null;
                        dailyResults.setViewType(VIEW_TYPE_FIRST);
                        d = s.getKey();
                        assert d != null;
                        dailyResults.setKey(d);


                        //new_list.clear();
                        new_list.add(dailyResults);

                    }

                    combinedDataList.addAll(new_list);
                    adapter1.notifyItemRangeInserted(combinedDataList.size(),new_list.size());
                    isLoading = false;
                    indicator2.setVisibility(View.GONE);


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //isLoading = false;
                //indicator2.setVisibility(View.GONE);
            }
        });


    }

    private void lastKey()
    {
        Query query = groupRef.orderByKey().limitToLast(1);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //searchFunction2("");
        indicator2.setVisibility(android.view.View.GONE);
    }

    private void InitialisedFields() {

        final SharedPreferences countRef = getSharedPreferences("Counter", Context.MODE_PRIVATE);
        Counter=countRef.getInt("count",0);

        SharedPreferences.Editor editAdControl = countRef.edit();
        if (countRef.getLong("ExpiredDate", -1)<System.currentTimeMillis())
        {
            editAdControl.clear();
            editAdControl.apply();
        }

        if (Counter> 4)
        {
            //createBannerAd();
            mAdView = new AdView(this);
            mAdView.setVisibility(View.GONE);
        }
    }


    private  void requestGroup(String search)
    {
        query = groupRef.orderByChild("name").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<GroupPicker> options;
        options = new FirebaseRecyclerOptions.Builder<GroupPicker>()
                .setQuery(query, GroupPicker.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<GroupPicker, RecyclerView.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull GroupPicker model) {

                String groupKey = getRef(position).getKey();

                assert groupKey != null;

                DailyResultsViewHolder dailyResultsViewHolder = (DailyResultsViewHolder) holder;

                groupRef.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            dailyResultsViewHolder.layout.setVisibility(View.VISIBLE);

                            groupName = snapshot.child("name").getValue().toString();
                            dailyResultsViewHolder.group_name.setText(groupName);

                            if (snapshot.hasChild("image"))
                            {
                                groupImage = snapshot.child("image").getValue().toString();
                                Picasso.get().load(groupImage).fit().centerCrop()
                                        .placeholder(R.drawable.profile_image)
                                        .into(dailyResultsViewHolder.group_image);

                            }

                            if (snapshot.hasChild("about"))
                            {
                                String about = snapshot.child("about").getValue().toString();
                                dailyResultsViewHolder.group_msg.setText(about);
                            }

                            if (snapshot.hasChild("Members"))
                            {
                                groupMemCounts = NumberUtil.formatNumber(snapshot.child("Members").getChildrenCount());
                                String about = groupMemCounts + " member(s) joined this group";
                                dailyResultsViewHolder.group_msg.setText(about);
                            }


                            groupRef.child(groupKey).child("Members").child(currentUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.exists())
                                            {
                                                dailyResultsViewHolder.group_join.setText("Joined");

                                                dailyResultsViewHolder.group_join.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Toast.makeText(SearchActivity.this, "Already Joined", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }
                                            else if (!isJoin)
                                            {
                                                groupRef.child(groupKey).child("Requests").child("users_id")
                                                        .child(currentUserId)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                if (snapshot.exists())
                                                                {
                                                                    dailyResultsViewHolder.group_join.setText("Requested");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        }else
                        {

                            //loadingBar.dismiss();
                            Toast.makeText(SearchActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                        //loadingBar.dismiss();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                FirebaseUser user = mAuth.getCurrentUser();

                if (user.isEmailVerified())
                {
                    dailyResultsViewHolder.group_join.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!dailyResultsViewHolder.group_join.getText().equals("Requested"))
                            {
                                groupRef.child(groupKey).child("Requests").child("users_id").child(currentUserId)
                                        .setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    isJoin = true;
                                                    dailyResultsViewHolder.group_join.setText("Requested");

                                                    groupRef.child(groupKey).child("Requests")
                                                            .child("users_id")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    if (snapshot.exists())
                                                                    {
                                                                        count = snapshot.getChildrenCount();

                                                                        groupRef.child(groupKey).
                                                                                child("Requests")
                                                                                .child("count").setValue(count);

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                    groupRef.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                            if (snapshot.exists())
                                                            {
                                                                String not_key = groupRef.child(groupKey).child("Notifications").push().getKey();
                                                                assert not_key != null;

                                                                String admin = snapshot.child("admin").getValue().toString();

                                                                Map<String, String> NotificationMap = new HashMap<String, String>();
                                                                NotificationMap.put("from", currentUserId);
                                                                NotificationMap.put("to", admin);

                                                                String messageRef = "Requests" + "/" + not_key;

                                                                Map<String, Object> NotificationMaps = new HashMap<String, Object>();
                                                                NotificationMaps.put(messageRef, currentUserId);

                                                                groupRef.child(groupKey).child("Notifications")
                                                                        .updateChildren(NotificationMaps);
                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                }

                                            }
                                        });

                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this, R.style.AlertDialog);

                                builder.setTitle("Confirm action to cancel request");
                                //builder.setMessage("Confirm action to delete message");

                                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        isJoin = true;
                                        groupRef.child(groupKey).child("Requests").child("users_id").child(currentUserId)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful())
                                                        {
                                                            isReq = false;
                                                            dailyResultsViewHolder.group_join.setText("Join");
                                                            notifyItemChanged(dailyResultsViewHolder.getBindingAdapterPosition());

                                                            groupRef.child(groupKey).child("Requests").child("users_id")
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                            if (snapshot.exists())
                                                                            {
                                                                                count = snapshot.getChildrenCount();

                                                                                groupRef.child(groupKey).
                                                                                        child("Requests")
                                                                                        .child("count").setValue(count);

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });


                                                        }


                                                    }
                                                });

                                    }
                                });

                                builder.show();

                            }

                        }
                    });


                }
                else
                {
                    Toast.makeText(SearchActivity.this, "Please verify your email before you can join a group",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent, false);
                return new DailyResultsViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void searchFunction()
    {
        //String lowerSearch = search.toLowerCase();

        adapter1 = new SearchAdapter(this,combinedDataList,isScrolled,
                indicator,groupRef,isDestroyed,mAuth,
                search,userRef,currentUserId,user,isLoading,indicator2);
        recyclerView.setAdapter(adapter1);

    }

    private void searchFunction2(String search)
    {
        //String lowerSearch = search.toLowerCase();
        query = groupRef.orderByChild("name").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<GroupPicker> options =
                new FirebaseRecyclerOptions.Builder<GroupPicker>()
                        .setQuery(query, GroupPicker.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<GroupPicker, RecyclerView.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull GroupPicker model) {


            }

            @NonNull
            @Override
            public SearchActivity.DailyResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent, false);
                SearchActivity.DailyResultsViewHolder viewHolder = new DailyResultsViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.stopListening();
    }


    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, group_msg,group_msg_empty, group_join, group_msg_count, Username;
        LinearLayout layout,group_msg_image, UsernameLay;
        ImageView group_image;

        public DailyResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            group_name = itemView.findViewById(R.id.g_name);
            group_msg = itemView.findViewById(R.id.gMsg);
            //group_msg_empty = itemView.findViewById(R.id.gMsg_empty);
            //group_msg_count = itemView.findViewById(R.id.gMsgCount);
            group_join = itemView.findViewById(R.id.join);
            //Username = itemView.findViewById(R.id.username);

            group_image = itemView.findViewById(R.id.gImage);
            //group_msg_image = itemView.findViewById(R.id.msgImageLay);

            layout = itemView.findViewById(R.id.msgLayout);
            //UsernameLay =itemView.findViewById(R.id.username_lay);

            //LottoImage = itemView.findViewById(R.id.lotto_image);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null)
        {
            user.reload();
        }

        //adapter.startListening();
        //searchFunction("");

        //InitializedField();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdView = new AdView(this);
        mAdView.destroy();

        recyclerView = findViewById(R.id.sec_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        searchFunction2("");
        adContainerView.removeCallbacks(runnable);
        //InterstitialAd.load(this,null,null,null)

        isDestroyed = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        //NativeAdMedium();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {

                if (!search.isEmpty())
                {
                    requestGroup(search);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {

                if (!search.isEmpty())
                {
                    requestGroup(search);

                }
                else
                {
                    searchFunction();
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                adContainerView.setVisibility(View.VISIBLE);
            }
        });

    }
}