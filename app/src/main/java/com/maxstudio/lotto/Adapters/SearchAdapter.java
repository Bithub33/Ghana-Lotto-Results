package com.maxstudio.lotto.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Activities.SearchActivity;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.Models.GroupPicker;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Utils.NumberUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DatabaseReference groupRef,userRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator,indicator2;
    private boolean isReq;
    private boolean isJoin;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View View;

    private AdView mAdView;
    private long count;
    private FrameLayout adContainerView;
    private String groupName,groupKey,search,groupMemCounts,groupImage, currentUserId, img;

    public static String day;
    public Context context;
    private boolean isDestroyed,isScrolled,isDay,isLoading;
    private AdLoader adLoader4;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;
    private static final int VIEW_TYPE_THIRD = 2;
    private List<GroupPicker> combinedDataList;

    FirebaseRecyclerAdapter<DailyResultsPicker, RecyclerView.ViewHolder> adapter;

    public SearchAdapter(Context context, List<GroupPicker> combinedDataList, boolean isScrolled,
                          ProgressBar indicator, DatabaseReference groupRef, boolean isDestroyed,
                         FirebaseAuth mAuth,String search,DatabaseReference userRef,
                         String currentUserId,FirebaseUser user,boolean isLoading, ProgressBar indicator2)
    {
        this.context = context;
        this.combinedDataList = combinedDataList;
        this.isScrolled = isScrolled;
        this.indicator = indicator;
        this.groupRef = groupRef;
        this.isDestroyed = isDestroyed;
        this.mAuth = mAuth;
        this.userRef = userRef;
        this.search = search;
        this.user = user;
        this.isLoading = isLoading;
        this.currentUserId = currentUserId;
        this.indicator2 = indicator2;
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

    public class DailyResultsViewHolder2 extends RecyclerView.ViewHolder {

        TemplateView template2;
        boolean isSubs;
        String[] adUnitIds2,adUnitIds,adUnitIds3;
        int currentAdIndex2;

        Native natives;
        public DailyResultsViewHolder2(@NonNull View itemView) {
            super(itemView);

            adUnitIds = new String[]{
                    itemView.getContext().getString(R.string.Native_add_id),
                    itemView.getContext().getString(R.string.Native_add_id_1_2),
                    itemView.getContext().getString(R.string.Native_add_id_1_3),
                    itemView.getContext().getString(R.string.Native_add_id_1_4)};

            adUnitIds2 = new String[]{
                    itemView.getContext().getString(R.string.Native_add_medium_id),
                    itemView.getContext().getString(R.string.Native_add_medium_id_2),
                    itemView.getContext().getString(R.string.Native_add_medium_id_3),
                    itemView.getContext().getString(R.string.Native_add_medium_id_4)};

            adUnitIds3 = new String[]{
                    itemView.getContext().getString(R.string.Native_add_small_id),
                    itemView.getContext().getString(R.string.Native_add_small_id_2),
                    itemView.getContext().getString(R.string.Native_add_small_id_3),
                    itemView.getContext().getString(R.string.Native_add_small_id_4)};

            TemplateView templateView = itemView.findViewById(R.id.my_template_1);
            TemplateView templateView2 = itemView.findViewById(R.id.my_template_2);
            TemplateView templateView3 = itemView.findViewById(R.id.my_template_3);

            natives = new Native(context, templateView,templateView2,
                    templateView3, adUnitIds,adUnitIds2,adUnitIds3,isScrolled,isDestroyed);

            currentAdIndex2 = 0;

            SharedPreferences prefs = itemView.getContext().getSharedPreferences("com.maxstudio.lotto",
                    Context.MODE_PRIVATE);

            isSubs = prefs.getBoolean("service_status", false);
        }

        public void NativeAdMedium()
        {
            if (!isSubs)
            {
                natives.startLoadingAds();
            }
        }

        public void NativeAdSmallDestroy()
        {
            if (!isSubs)
            {
                natives.stopLoadingAds();

            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_FIRST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent, false);
            return new DailyResultsViewHolder(view);
        } else if (viewType == VIEW_TYPE_SECOND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
            return new DailyResultsViewHolder2(view);
        }
        /**else if (viewType == VIEW_TYPE_THIRD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_layout, parent, false);
            return new DailyResultsViewHolder2(view);
        }**/
        else {
            throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_FIRST) {
            //Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();

            DailyResultsViewHolder dailyResultsViewHolder = (DailyResultsViewHolder) holder;

            int positions = position - (position > 0 ? 1 : 0) - (position > 5 ? 1 : 0);
            // ...
            GroupPicker dailyResultsPicker = combinedDataList.get(positions);
            String groupKey = dailyResultsPicker.getKey();

            assert groupKey != null;

            groupRef.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {
                        indicator.setVisibility(android.view.View.GONE);
                        dailyResultsViewHolder.layout.setVisibility(android.view.View.VISIBLE);
                        //isLoading = false;
                        //indicator2.setVisibility(android.view.View.GONE);

                        if (snapshot.hasChild("name"))
                        {
                            groupName = snapshot.child("name").getValue().toString();
                            dailyResultsViewHolder.group_name.setText(groupName);

                            if (groupName.equals("TestGroup")||groupName.equals("Test Group 2")
                                    ||groupName.equals("Test Group 3"))
                            {
                                dailyResultsViewHolder.layout.setVisibility(android.view.View.GONE);

                            }

                        }

                        userRef.child("Developer").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists())
                                {
                                    if (groupName.equals("TestGroup")||groupName.equals("Test Group 2")
                                            ||groupName.equals("Test Group 3"))
                                    {
                                        dailyResultsViewHolder.layout.setVisibility(android.view.View.VISIBLE);

                                    }


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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

                                                    Toast.makeText(context, "Already Joined", Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                        else if (!isJoin)
                                        {
                                            groupRef.child(groupKey).child("Requests").child(currentUserId)
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
                        /**groupRef.child(groupKey).child("Requests").child(currentUserId)
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
                        });**/


                    }else
                    {

                        //loadingBar.dismiss();
                        Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                    //loadingBar.dismiss();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null && user.isEmailVerified())
            {
                dailyResultsViewHolder.group_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!dailyResultsViewHolder.group_join.getText().equals("Requested"))
                        {
                            groupRef.child(groupKey).child("Requests").child("users_id")
                                    .child(currentUserId)
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

                                                groupRef.child(groupKey)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        if (snapshot.exists())
                                                        {
                                                            String not_key = groupRef.child(groupKey).
                                                                    child("Notifications").push().getKey();
                                                            assert not_key != null;

                                                            String admin = snapshot.child("admin").getValue().toString();

                                                            Map<String, String> NotificationMap = new HashMap<String, String>();
                                                            NotificationMap.put("from", currentUserId);
                                                            NotificationMap.put("to", admin);

                                                            String messageRef = "Requests" + "/" + not_key;

                                                            Map<String, Object> NotificationMaps = new HashMap<String, Object>();
                                                            NotificationMaps.put(messageRef, NotificationMap);

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);

                            builder.setTitle("Confirm action to cancel request");
                            //builder.setMessage("Confirm action to delete message");

                            builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    isJoin = true;
                                    groupRef.child(groupKey).child("Requests").child("users_id")
                                            .child(currentUserId)
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
                Toast.makeText(context, "Please verify your email before you can join a group",
                        Toast.LENGTH_SHORT).show();
            }

        }
        else if (getItemViewType(position) == VIEW_TYPE_SECOND)
        {
            //Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
            DailyResultsViewHolder2 dailyResultsViewHolder2 = (DailyResultsViewHolder2) holder;
            // Bind data and UI for the second view
            // ...
            dailyResultsViewHolder2.NativeAdMedium();

            if (isDestroyed)
            {
                dailyResultsViewHolder2.NativeAdSmallDestroy();
            }

        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0 || position == 5)
        {
            return VIEW_TYPE_SECOND;
        }
        /**else if (position >= combinedDataList.size())
        {
            return VIEW_TYPE_THIRD;
        }**/
        else
        {
            return VIEW_TYPE_FIRST;
        }



    }

    @Override
    public int getItemCount() {

        int co = combinedDataList.size();

        if (combinedDataList.size() >= 5) {
            co += 2;
        }
        // If the original item count is greater than or equal to 1, add 1 for the native ad at position 0
        else if (combinedDataList.size() >= 1) {
            co += 1;
        }

        return co;

    }
}
