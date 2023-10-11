package com.maxstudio.lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.lang.String;
import java.util.Objects;

import com.maxstudio.lotto.R;
import com.squareup.picasso.Picasso;

public class RequestActivity extends AppCompatActivity{

    private RecyclerView recyclerView,recyclerView2;
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private Query query;
    private ValueEventListener val;
    private TextView textView;
    private String userKey;
    private String groupName,groupId,groupMsg,groupTime,groupMsgCount,groupImage, currentUserId, img,user_from;


    private DatabaseReference LottoRef, groupRef,groupsRef,userRef, usersRef;
    private FirebaseAuth mAuth;
    FirebaseRecyclerAdapter<String, RequestActivity.DailyResultsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        groupId = getIntent().getExtras().get("groupId").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                .child("Requests");
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);

        recyclerView = findViewById(R.id.req_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textView = findViewById(R.id.text);

        mToolbar = findViewById(R.id.request_bar);
        setSupportActionBar(mToolbar);
        setTitle("Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        //requestList();

    }

    @Override
    protected void onPause() {
        super.onPause();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        searchFunction2();
    }

    private void searchFunction()
    {
        //String lowerSearch = search.toLowerCase();
        //query = userRef.orderByChild("username").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(groupRef.child("users_id"), String.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<String, RequestActivity.DailyResultsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RequestActivity.DailyResultsViewHolder holder, int position, @NonNull String model) {

                String userKey = getRef(position).getKey();

                assert userKey != null;

                groupRef.child("users_id").child(userKey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists())
                                {
                                    textView.setVisibility(View.GONE);
                                    userRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.exists())
                                            {

                                                holder.layout.setVisibility(View.VISIBLE);

                                                //recyclerView2.setVisibility(View.GONE);

                                                groupName = snapshot.child("username").getValue().toString();
                                                holder.group_name.setText(groupName);

                                                if (snapshot.hasChild("about"))
                                                {
                                                    String about = snapshot.child("about").getValue().toString();
                                                    holder.about.setText(about);
                                                }
                                                else
                                                {
                                                    String about = "Hello, glad to be here.";
                                                    holder.about.setText(about);
                                                }

                                                if (snapshot.hasChild("image"))
                                                {
                                                    groupImage = snapshot.child("image").getValue().toString();

                                                    Picasso.get().load(groupImage).fit().centerCrop()
                                                            .placeholder(R.drawable.profile_image).into(holder.group_image);

                                                }
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.exists())
                                            {
                                                String admin = snapshot.child("admin").getValue().toString();

                                                if (admin.equals(currentUserId))
                                                {
                                                    holder.group_remove.setVisibility(View.VISIBLE);
                                                    holder.group_remove.setText("Accept");

                                                    holder.group_remove.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            CharSequence[] options = new CharSequence[]
                                                                    {
                                                                            "Accept Request",
                                                                            "Decline Request"
                                                                    };

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                                            builder.setTitle("Choose an option");
                                                            //builder.setMessage("Confirm action to delete message");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    if (which == 0)
                                                                    {
                                                                        groupsRef.child("Members").child(userKey).setValue("")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            userRef.child(userKey).child("Groups").child(groupId)
                                                                                                    .setValue("");

                                                                                            groupsRef.child("Requests").child("users_id").child(userKey)
                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                            if (snapshot.exists())
                                                                                                            {
                                                                                                                groupsRef.child("Requests").child("users_id").child(userKey)
                                                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                if (task.isSuccessful())
                                                                                                                                {
                                                                                                                                    groupsRef.child("Requests").child("users_id")
                                                                                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                                @Override
                                                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                                                                    if (snapshot.exists())
                                                                                                                                                    {
                                                                                                                                                        long count = snapshot.getChildrenCount();

                                                                                                                                                        groupsRef.child("Requests")
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
                                                                                                            else
                                                                                                            {
                                                                                                                groupsRef.child("Requests").child(userKey).removeValue();
                                                                                                            }

                                                                                                            if (!snapshot.hasChildren())
                                                                                                            {
                                                                                                                textView.setVisibility(View.VISIBLE);
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });

                                                                                        }

                                                                                        //holder.group_remove.setText("Accepted");

                                                                                    }
                                                                                });

                                                                    }
                                                                    if (which == 1)
                                                                    {
                                                                        groupsRef.child("Requests").child("users_id").child(userKey)
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                        if (snapshot.exists())
                                                                                        {
                                                                                            groupsRef.child("Requests").child("users_id").child(userKey)
                                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            if (task.isSuccessful())
                                                                                                            {
                                                                                                                groupsRef.child("Requests").child("users_id")
                                                                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                            @Override
                                                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                                                if (snapshot.exists())
                                                                                                                                {
                                                                                                                                    long count = snapshot.getChildrenCount();

                                                                                                                                    groupsRef.child("Requests")
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
                                                                                        else
                                                                                        {
                                                                                            groupsRef.child("Requests").child(userKey).removeValue();
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });


                                                                    }

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


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }

            @NonNull
            @Override
            public RequestActivity.DailyResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent, false);
                RequestActivity.DailyResultsViewHolder viewHolder = new DailyResultsViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void searchFunction2()
    {
        //String lowerSearch = search.toLowerCase();
        //query = userRef.orderByChild("username").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(groupRef, String.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<String, RequestActivity.DailyResultsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RequestActivity.DailyResultsViewHolder holder, int position, @NonNull String model) {

                String userKey = getRef(position).getKey();

                assert userKey != null;

                val = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            textView.setVisibility(View.GONE);

                            holder.layout.setVisibility(View.VISIBLE);

                            //recyclerView2.setVisibility(View.GONE);

                            groupName = snapshot.child("username").getValue().toString();
                            holder.group_name.setText(groupName);


                            if (snapshot.hasChild("image"))
                            {
                                groupImage = snapshot.child("image").getValue().toString();

                                Picasso.get().load(groupImage).fit().centerCrop()
                                        .placeholder(R.drawable.profile_image).into(holder.group_image);

                            }
                        }
                        else
                        {
                            textView.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                userRef.child(userKey).removeEventListener(val);

            }

            @NonNull
            @Override
            public RequestActivity.DailyResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_search_layout, parent, false);
                RequestActivity.DailyResultsViewHolder viewHolder = new DailyResultsViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.stopListening();
    }

    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, group_remove, about;
        LinearLayout layout,group_msg_image, UsernameLay;
        ImageView group_image;
        RelativeLayout relativeLayout;

        public DailyResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            group_name = itemView.findViewById(R.id.g_name);
            group_remove = itemView.findViewById(R.id.join);
            about = itemView.findViewById(R.id.gMsg);

            group_image = itemView.findViewById(R.id.gImage);

            layout = itemView.findViewById(R.id.msgLayout);
            //relativeLayout = itemView.findViewById(R.id.message_on);

        }
    }

}