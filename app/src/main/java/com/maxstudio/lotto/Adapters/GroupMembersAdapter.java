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
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Interfaces.RecyclerViewInterface;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.Models.GroupPicker;
import com.maxstudio.lotto.Models.UserPicker;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Utils.NumberUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DatabaseReference groupsRef,groupRef,userRef;
    private ProgressBar indicator,indicator2;

    private boolean isReq;
    private boolean isJoin;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View View;

    private AdView mAdView;
    private boolean isSubs;
    private FrameLayout adContainerView;
    private String groupName,groupId,search,groupMemCounts,groupImage, currentUserId, img;

    public static String day;
    public Context context;
    private boolean isDestroyed,isScrolled,isDay,isLoading;
    private AdLoader adLoader4;
    private RecyclerViewInterface Interface;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;
    private static final int VIEW_TYPE_THIRD = 2;
    private List<String> combinedDataList;

    FirebaseRecyclerAdapter<DailyResultsPicker, RecyclerView.ViewHolder> adapter;

    public GroupMembersAdapter(Context context, List<String> combinedDataList,
                               ProgressBar indicator, DatabaseReference groupsRef,
                               DatabaseReference groupRef,DatabaseReference userRef,
                               String groupId,String currentUserId,
                               RecyclerViewInterface Interface,boolean isLoading, ProgressBar indicator2)
    {
        this.context = context;
        this.combinedDataList = combinedDataList;
        this.indicator = indicator;
        this.groupsRef = groupsRef;
        this.groupRef = groupRef;
        this.userRef = userRef;
        this.groupId = groupId;
        this.currentUserId = currentUserId;
        this.Interface = Interface;
        this.isLoading = isLoading;
        this.indicator2 = indicator2;
    }


    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, about,group_msg_empty, group_remove, group_msg_count, Username;
        LinearLayout layout,group_msg_image, UsernameLay;
        ImageView group_image;

        public DailyResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            group_name = itemView.findViewById(R.id.g_name);
            about = itemView.findViewById(R.id.gMsg);
            group_remove = itemView.findViewById(R.id.join);

            group_image = itemView.findViewById(R.id.gImage);

            layout = itemView.findViewById(R.id.msgLayout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_FIRST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent, false);
            return new DailyResultsViewHolder(view);
        }
        else {
            throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        DailyResultsViewHolder dailyResultsViewHolder = (DailyResultsViewHolder) holder;
        String groupKey = combinedDataList.get(position);

        //String groupKey = userPicker.getKey();
        assert groupKey != null;

        dailyResultsViewHolder.group_remove.setVisibility(android.view.View.GONE);
        dailyResultsViewHolder.layout.setVisibility(android.view.View.GONE);

        userRef.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    indicator.setVisibility(android.view.View.GONE);
                    dailyResultsViewHolder.layout.setVisibility(android.view.View.VISIBLE);
                    isLoading = false;
                    indicator2.setVisibility(android.view.View.GONE);

                    if (snapshot.hasChild("image"))
                    {

                        groupImage = snapshot.child("image").getValue().toString();

                        Picasso.get().load(groupImage)
                                .fit().centerCrop()
                                .placeholder(R.drawable.profile_image)
                                .into(dailyResultsViewHolder.group_image);

                    }

                    if(snapshot.hasChild("username"))
                    {
                        groupName = snapshot.child("username").getValue().toString();

                        dailyResultsViewHolder.group_name.setText(groupName);
                    }
                    else
                    {
                        dailyResultsViewHolder.layout.setVisibility(View.GONE);

                    }

                    if (snapshot.hasChild("about"))
                    {
                        String about = snapshot.child("about").getValue().toString();
                        dailyResultsViewHolder.about.setText(about);
                    }
                    else
                    {
                        String about = "Hello, glad to be here.";
                        dailyResultsViewHolder.about.setText(about);
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        groupsRef.child(groupId).addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            String admin = snapshot.child("admin").getValue().toString();

                            if (admin.equals(currentUserId))
                            {
                                dailyResultsViewHolder.group_remove.setVisibility(android.view.View.VISIBLE);
                                dailyResultsViewHolder.group_remove.setText("Remove");

                                dailyResultsViewHolder.group_remove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int pos = dailyResultsViewHolder.getBindingAdapterPosition();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                        builder.setTitle("Confirm action to remove this member");
                                        //builder.setMessage("Confirm action to delete message");

                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                groupsRef.child(groupId).child("Members").child(groupKey).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful())
                                                                {
                                                                    userRef.child(groupKey).child("Groups").child(groupId)
                                                                            .removeValue();

                                                                    if (Interface != null)
                                                                    {
                                                                        dailyResultsViewHolder.layout.setVisibility(android.view.View.GONE);

                                                                        if (pos != RecyclerView.NO_POSITION)
                                                                        {
                                                                            Interface.delete(pos);
                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        });

                                            }
                                        });

                                        builder.show();
                                    }
                                });

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public int getItemCount() {

        return combinedDataList.size();

    }
}
