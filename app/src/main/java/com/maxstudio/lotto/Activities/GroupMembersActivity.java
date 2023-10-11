package com.maxstudio.lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Adapters.GroupMembersAdapter;
import com.maxstudio.lotto.Interfaces.RecyclerViewInterface;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.Models.GroupPicker;
import com.maxstudio.lotto.Models.UserPicker;
import com.maxstudio.lotto.R;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMembersActivity extends AppCompatActivity implements RecyclerViewInterface {

    private RecyclerView recyclerView,recyclerView2;
    private Toolbar mToolbar;
    private ProgressBar indicator,indicator2;
    private LinearLayoutManager layoutManager;
    private NestedScrollView scroll;
    private boolean isLoading = false;

    private Runnable run,run2;
    private Handler handler;
    private Picasso picasso;

    Map<String, GroupPicker> userDataMap = new HashMap<>();
    private List<String> combinedDataList = new ArrayList<>();
    private List<String> newDataList = new ArrayList<>();
    private GroupMembersAdapter adapters;
    private RecyclerViewInterface Interface;
    private String groupName,groupId,groupKeys,groupMsg,groupTime,groupMsgCount,groupImage, currentUserId, Uid, img,user_from;


    private DatabaseReference LottoRef, groupRef,groupsRef,userRef, usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        groupId = getIntent().getExtras().get("groupId").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                .child("Members");
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        groupRef.keepSynced(true);
        userRef.keepSynced(true);
        groupsRef.keepSynced(true);

        handler = new Handler();
        indicator = findViewById(R.id.circle_loading);
        indicator2 = findViewById(R.id.circle_loading2);
        scroll = findViewById(R.id.scroll);
        recyclerView = findViewById(R.id.mem_rec);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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


            }
        });

        Context context = GroupMembersActivity.this;
        picasso = new Picasso.Builder(context)
                .memoryCache(new LruCache(10 * 1024 * 1024)) // 10 MB memory cache
                .build();

        run = new Runnable() {
            @Override
            public void run() {

                nextSearch();
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

        mToolbar = findViewById(R.id.members_bar);
        setSupportActionBar(mToolbar);
        setTitle("Members");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapters = new GroupMembersAdapter(this,combinedDataList,indicator,groupsRef,groupRef,
                userRef,groupId,currentUserId,this,isLoading,indicator2);
        recyclerView.setAdapter(adapters);

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
    protected void onStart() {
        super.onStart();

        searchFunction();
        //InitializedFields();

        //membersList();

    }

    @Override
    protected void onPause() {
        super.onPause();

        Intent RequestIntent = new Intent();
        RequestIntent.putExtra("groupKey", groupId);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    private void searchFunction()
    {
        groupRef.limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    combinedDataList.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {

                        String d = s.getKey();
                        assert d != null;
                        combinedDataList.add(d);
                        //combinedDataList.add(newDataList)

                    }

                    adapters.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nextSearch()
    {
        String d = combinedDataList.get(combinedDataList.size()-1);

        Query query = groupRef.orderByKey().startAfter(d).limitToFirst(15);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    newDataList.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {

                        String d = s.getKey();
                        assert d != null;
                        newDataList.add(d);

                    }

                    combinedDataList.addAll(newDataList);
                    adapters.notifyItemRangeInserted(combinedDataList.size(),newDataList.size());
                    //isLoading = false;
                    //indicator2.setVisibility(android.view.View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //isLoading = false;
                //indicator2.setVisibility(android.view.View.GONE);

            }
        });
    }


    @Override
    public void delete(int position) {

        combinedDataList.remove(position);

        adapters.notifyItemRemoved(position);
    }
}