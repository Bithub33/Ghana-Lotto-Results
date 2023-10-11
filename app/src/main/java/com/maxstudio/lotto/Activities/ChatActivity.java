package com.maxstudio.lotto.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Adapters.MessageAdapter;
import com.maxstudio.lotto.Models.Messages;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Interfaces.RecyclerViewInterface;
import com.maxstudio.lotto.Utils.MyFirebaseMessagingService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.vanniktech.emoji.EmojiPopup;

public class ChatActivity extends AppCompatActivity implements RecyclerViewInterface {

    private ImageView imageView, imageView2, gIcon;
    private CircleImageView chat_image;
    private CoordinatorLayout layout;
    public RelativeLayout layout1,layout2;
    public static ArrayList<String> list = new ArrayList<>();
    public static List<String> list2 = new ArrayList<>();

    private List<String> offline_mem = new ArrayList<>();
    private EditText editText;
    private BadgeDrawable badgeDrawable;
    private SharedPreferences prefs;
    public static boolean back = false;
    public static boolean sent = false;

    public static boolean start;
    private String groupId,Username,groupImage,group_Name,group_Image,groupCount,key;
    private TextView group_name, msgDelivered,group_count,rCount, msgCount;
    private Toolbar mToolbar;
    private static final int GalleryPicker = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;
    private RelativeLayout delete,copy,arrow_back,arrow_back2;

    private String msgReceiverId, msgReceiverName, msgReceiverImage, msgSenderId, ChatId;
    private String getCurrentDate, getCurrentTime, messagePushId;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private static final int REQUEST_CODE_PICK_IMAGES = 101;
    private MessageAdapter messageAdapter;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private RecyclerView usersMessagesList;
    private Parcelable recycler;
    private int lastPosition;
    private Runnable run,run2;
    private Handler handler;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private AdRequest adRequest;
    private boolean isClick,isClick2;

    int Counter = 0;
    public static int lastRecMsg;
    private MediaPlayer mp_send,mp_rec;

    private MyFirebaseMessagingService my;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, usersRef,groupRef, groupMessageRef;

    private  String admin, currentUserID, pImage, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        groupId = getIntent().getExtras().get("groupKey").toString();
        group_Name = getIntent().getExtras().get("groupName").toString();
        group_Image = getIntent().getExtras().get("groupImage").toString();
        groupCount = getIntent().getExtras().get("count").toString();
        admin = getIntent().getExtras().get("admin").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Chat_image");

        userRef.keepSynced(true);
        groupRef.keepSynced(true);

        mp_send = MediaPlayer.create(this,R.raw.sen);
        mp_rec = MediaPlayer.create(this,R.raw.rec);

        start = true;

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        userRef.child("Groups").child(groupId).setValue("");
        groupRef.child("Members").child(currentUserID).setValue("online");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    pImage = snapshot.child("image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                this::onImagesPicked
        );

        InitialisedFields();


        /**userRef.child("Developer").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            String cost = editText.getText().toString();

                            if (TextUtils.isEmpty(cost))
                            {
                                imageView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        if (!task.isSuccessful()) {
                                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                            return;
                                                        }

                                                        // Get new FCM registration token
                                                        String token = task.getResult();

                                                        editText.setText(token);

                                                        // Log and toast

                                                    }
                                                });

                                    }
                                });

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });**/

        handler = new Handler();

        run = new Runnable() {
            @Override
            public void run() {

                if (!offline_mem.isEmpty())
                {

                    String key = usersRef.push().getKey();

                    Map<String, String> map = new HashMap<>();
                    map.put("from", currentUserID);
                    map.put("messageId", messagePushId);
                    map.put("groupId", groupId);
                    map.put("groupName", group_Name);
                    map.put("groupImage", group_Image);
                    map.put("groupCount", groupCount);
                    map.put("username", Username);
                    map.put("admin", admin);
                    map.put("type", "text");
                    map.put("prof_img", pImage);

                    Map<String, Object> maps = new HashMap<>();
                    maps.put(key,map);

                    groupRef.child("Notifications").child("Messages")
                            .updateChildren(maps);

                    for (String l : offline_mem)
                    {
                        assert key != null;
                        groupRef.child("Notifications").child("Messages")
                                .child(key).child("Members").child(l).setValue("");
                    }
                }

            }
        };
        run2 = new Runnable() {
            @Override
            public void run() {

                if (!offline_mem.isEmpty())
                {

                    String key = usersRef.push().getKey();

                    Map<String, String> map = new HashMap<>();
                    map.put("from", currentUserID);
                    map.put("messageId", messagePushId);
                    map.put("groupId", groupId);
                    map.put("groupName", group_Name);
                    map.put("groupImage", group_Image);
                    map.put("groupCount", groupCount);
                    map.put("username", Username);
                    map.put("admin", admin);
                    map.put("type", "image");
                    map.put("prof_img", pImage);

                    Map<String, Object> maps = new HashMap<>();
                    maps.put(key,map);

                    groupRef.child("Notifications").child("Messages")
                            .updateChildren(maps);

                    for (String l : offline_mem)
                    {
                        assert key != null;
                        groupRef.child("Notifications").child("Messages")
                                .child(key).child("Members").child(l).setValue("");
                    }
                }

            }
        };

        getMessages();

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //isClick = true;
                onBackPressed();
                /**Intent HomeIntent = new Intent(ChatActivity.this, HomeActivity.class);
                HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(HomeIntent);
                finish();**/

            }
        });

        arrow_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //isClick2 = true;
                onBackPressed();
                /**Intent HomeIntent = new Intent(ChatActivity.this, HomeActivity.class);
                HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(HomeIntent);
                finish();**/

            }
        });


        gIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent RequestIntent = new Intent(ChatActivity.this, RequestActivity.class);
                RequestIntent.putExtra("groupId", groupId);
                RequestIntent.putExtra("userId", currentUserID);
                startActivity(RequestIntent);

            }
        });


        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessageToDatabase();

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imagePickerLauncher.launch("image/*");
                MyApplication.isAdFullScreen = true;

                //my.showNotification("Emma Agbedu", "I am going to be great one day",ChatActivity.this);

                /**Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, GalleryPicker);**/

            }

        });

        GroupInfo();

        prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

    }

    private void onImagesPicked(List<Uri> uris)
    {
        for (Uri imageUri : uris) {
            // Do something with each imageUri
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();

            String imgKey = userRef.push().getKey();
            final StorageReference filePath = UserProfileImageRef.child(groupId).child(imgKey + ".jpg");

            Calendar callForCalender = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            getCurrentDate = currentDateFormat.format(callForCalender.getTime());

            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            getCurrentTime = currentTimeFormat.format(callForTime.getTime());

            filePath.putBytes(fileInBytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(ChatActivity.this, "success GGHFHHHJFHF  HHH  GDHYRH", Toast.LENGTH_LONG).show();
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    DatabaseReference groupMessageKeyRef = groupRef.push();

                                    messagePushId = groupMessageKeyRef.getKey();

                                    Map<String, String> messageTextBody = new HashMap<String, String>();
                                    messageTextBody.put("message", downloadUrl);
                                    messageTextBody.put("type", "image");
                                    messageTextBody.put("messageId", messagePushId);
                                    messageTextBody.put("from", currentUserID);
                                    messageTextBody.put("time", getCurrentTime);
                                    messageTextBody.put("date", getCurrentDate);
                                    messageTextBody.put("status", "");
                                    messageTextBody.put("groupId", groupId);
                                    messageTextBody.put("username", Username);
                                    messageTextBody.put("you", "You");

                                    String messageRef = "Messages" + "/" + messagePushId;

                                    Map<String, Object> messageBodyDetails = new HashMap<>();
                                    messageBodyDetails.put(messageRef,messageTextBody);

                                    groupRef.updateChildren(messageBodyDetails)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        sent = true;
                                                        //MediaPlayer mp = MediaPlayer.create(ChatActivity.this,R.raw.sent2);
                                                        //mp.start();
                                                        groupRef.child("Messages").child(messagePushId)
                                                                .child("status").setValue("Del");


                                                        groupRef.child("Members").
                                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                        if (snapshot.exists())
                                                                        {

                                                                            int index = 0;
                                                                            offline_mem.clear();
                                                                            list.clear();
                                                                            for (DataSnapshot ds : snapshot.getChildren())
                                                                            {
                                                                                key = ds.getKey();
                                                                                assert key != null;
                                                                                list.add(key);


                                                                            }

                                                                            for (String o : list)
                                                                            {
                                                                                if (!o.equals(currentUserID))
                                                                                {
                                                                                    groupRef.child("Members").child(o)
                                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                    if (snapshot.exists())
                                                                                                    {
                                                                                                        String value = snapshot.getValue().toString();

                                                                                                        if (value.equals("offline"))
                                                                                                        {
                                                                                                            String k = snapshot.getKey();
                                                                                                            offline_mem.add(k);
                                                                                                            usersRef.child(o).child("Groups").child(groupId)
                                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                        @Override
                                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                                            if (snapshot.exists())
                                                                                                                            {
                                                                                                                                String num = snapshot.getValue().toString();

                                                                                                                                if (!num.isEmpty())
                                                                                                                                {
                                                                                                                                    if (num.equals("on") || num.equals("off"))
                                                                                                                                    {
                                                                                                                                        if (!o.equals(currentUserID))
                                                                                                                                        {
                                                                                                                                            usersRef.child(o).child("Groups").child(groupId).
                                                                                                                                                    setValue("1");
                                                                                                                                        }


                                                                                                                                    }
                                                                                                                                    else
                                                                                                                                    {
                                                                                                                                        if (!o.equals(currentUserID))
                                                                                                                                        {
                                                                                                                                            int p = Integer.parseInt(num) + 1;
                                                                                                                                            usersRef.child(o).child("Groups").child(groupId).
                                                                                                                                                    setValue(String.valueOf(p));
                                                                                                                                        }

                                                                                                                                    }
                                                                                                                                }
                                                                                                                                else
                                                                                                                                {
                                                                                                                                    if (!o.equals(currentUserID))
                                                                                                                                    {
                                                                                                                                        usersRef.child(o).child("Groups").child(groupId).
                                                                                                                                                setValue("1");
                                                                                                                                    }

                                                                                                                                }
                                                                                                                            }


                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

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

                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                        handler.postDelayed(run2,2000);
                                                    }
                                                    else{
                                                        String message = task.getException().toString();
                                                        Toast.makeText(ChatActivity.this, "*" + message,Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });

                                }
                            });

                        }
                    });
        }
    }

    private void getMessages() {

        messagesList.clear();
        groupRef.child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if (dataSnapshot.exists())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);

                    messagesList.add(messages);

                    messageAdapter.notifyDataSetChanged();
                    if (messageAdapter.getItemCount() >= 0)
                    {
                        usersMessagesList.scrollToPosition(messagesList.size());
                        //lastRecMsg++;
                    }


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if (dataSnapshot.exists())
                {
                    //DisplayGroupMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //messagesList.remove();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        groupRef.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    list2.clear();
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        //Messages messages = snapshot.getValue(Messages.class);

                        String k = s.getKey();
                        list2.add(k);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void GroupInfo() {

        Picasso.get().load(group_Image).fetch(new Callback() {
            @Override
            public void onSuccess() {

                Picasso.get().load(group_Image)
                        .fit().centerCrop()
                        .placeholder(R.drawable.profile_image)
                        .into(chat_image);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        group_name.setText(group_Name);

        if (groupCount != null)
        {
            group_count.setText(groupCount);
        }
        else
        {
            groupRef.child("Members").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        long count = snapshot.getChildrenCount();

                        String counts = String.valueOf(count);

                        group_count.setText(counts);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }


    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.isAdFullScreen = true;

    }

    @Override
    public void onBackPressed() {

        if (layout2.getVisibility() == View.VISIBLE )
        {
            layout2.setVisibility(View.GONE);
            layout1.setVisibility(View.VISIBLE);
            back = true;
            messageAdapter.notifyDataSetChanged();

        }
        else
        {

            groupRef.child("Members").child(currentUserID).setValue("offline");
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mp_send.isPlaying())
        {
            mp_send.stop();
        }

        if(mp_rec.isPlaying())
        {
            mp_rec.stop();
        }

        handler.removeCallbacks(run);

    }

    @Override
    protected void onStop() {

        groupRef.child("Members").child(currentUserID).setValue("offline");

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        groupId = getIntent().getExtras().get("groupKey").toString();
        group_Name = getIntent().getExtras().get("groupName").toString();
        group_Image = getIntent().getExtras().get("groupImage").toString();

        MyApplication.isAdFullScreen = false;


    }

    private void InitialisedFields() {

        msgDelivered = findViewById(R.id.msg_delivered);

        mToolbar = findViewById(R.id.chat_toolbar);
        layout1 = findViewById(R.id.app_bar_lay);
        layout2 = findViewById(R.id.copy_del_layout);
        delete = findViewById(R.id.delete);
        copy = findViewById(R.id.copy);
        msgCount = findViewById(R.id.count);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true);

            /**Drawable customArrow = ContextCompat.getDrawable(this, R.drawable.arrow_back);
            actionBar.setHomeAsUpIndicator(customArrow);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setHomeActionContentDescription(null);**/
        }

        editText = findViewById(R.id.chat_edit);
        imageView = findViewById(R.id.chat_emoji);
        imageView2 = findViewById(R.id.send_but);
        chat_image = findViewById(R.id.custom_pImage);
        group_name = findViewById(R.id.custom_friend);
        arrow_back = findViewById(R.id.arrowBack);
        arrow_back2 = findViewById(R.id.arrowBack2);
        gIcon = findViewById(R.id.g_icon);
        layout = findViewById(R.id.g_cord);
        rCount = findViewById(R.id.req_count);
        group_count = findViewById(R.id.custom_friend_last_seen);

        int color = ContextCompat.getColor(this, R.color.selectedHighColor);
        editText.setHighlightColor(color);


        if (admin != null && admin.equals(currentUserID))
        {
            layout.setVisibility(View.VISIBLE);

            groupRef.child("Requests").child("users_id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists() && snapshot.hasChildren())
                    {
                        long count = snapshot.getChildrenCount();

                        String counts = String.valueOf(count);

                        rCount.setVisibility(View.VISIBLE);
                        rCount.setText(counts);
                    }
                    else
                    {
                        rCount.setVisibility(View.GONE);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            groupRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        String admins = snapshot.getValue().toString();

                        if (admins.equals(currentUserID))
                        {
                            layout.setVisibility(View.VISIBLE);

                            groupRef.child("Requests").child("users_id").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists() && snapshot.hasChildren())
                                    {
                                        long count = snapshot.getChildrenCount();

                                        String counts = String.valueOf(count);

                                        rCount.setVisibility(View.VISIBLE);
                                        rCount.setText(counts);
                                    }
                                    else
                                    {
                                        rCount.setVisibility(View.GONE);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    Username = snapshot.child("username").getValue().toString();
                }


            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

        //messageAdapter.setHasStableIds(true);
        usersMessagesList = findViewById(R.id.chat_rec);
        messageAdapter = new MessageAdapter(messagesList,this,layout1,
                layout2,copy,delete,msgCount,mp_send,mp_rec);
        linearLayoutManager = new LinearLayoutManager(this);
        usersMessagesList.setLayoutManager(linearLayoutManager);
        usersMessagesList.setAdapter(messageAdapter);
        usersMessagesList.setNestedScrollingEnabled(false);
        usersMessagesList.setHasFixedSize(true);
        usersMessagesList.setItemViewCacheSize(20);
        usersMessagesList.getRecycledViewPool().setMaxRecycledViews(0,0);
        //messageAdapter.setStateRestorationPolicy
                //(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    String admin = snapshot.child("admin").getValue().toString();
                    if (admin.equals(currentUserID))
                    {
                        menu.findItem(R.id.nav_request).setVisible(true);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /**final MenuItem menuItem = menu.findItem(R.id.nav_request);
        badgeDrawable =
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);**/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId() == R.id.nav_exit)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);

            builder.setTitle("Exit Group");
            builder.setMessage("Confirm action to exit group");
            //builder.setIcon(R.drawable.ic_baseline_error_24);
            builder.setCancelable(false);

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //holder.relativeLayout.setVisibility(View.GONE);

                    dialog.cancel();
                }
            });

            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    userRef.child("Groups").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            if (snapshot.exists() && snapshot.hasChild(groupId))
                            {

                                userRef.child("Groups").child(groupId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                groupRef.child("Members").child(currentUserID)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        //loadingBar.dismiss();

                                                        Intent HomeIntent = new Intent(ChatActivity.this, HomeActivity.class);
                                                        //HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(HomeIntent);
                                                        finish();

                                                    }
                                                });

                                            }
                                        });
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            builder.show();

        }

        if (item.getItemId() == R.id.nav_members)
        {
            Intent MembersIntent = new Intent(this, GroupMembersActivity.class);
            MembersIntent.putExtra("groupId", groupId);
            MembersIntent.putExtra("userId", currentUserID);
            startActivity(MembersIntent);
        }

        if (item.getItemId() == R.id.nav_request)
        {
            Intent RequestIntent = new Intent(this, RequestActivity.class);
            RequestIntent.putExtra("groupId", groupId);
            RequestIntent.putExtra("userId", currentUserID);
            startActivity(RequestIntent);
        }

        if (item.getItemId() == R.id.nav_group_info)
        {
            Intent RequestIntent = new Intent(this, GroupInfoActivity.class);
            RequestIntent.putExtra("groupId", groupId);
            RequestIntent.putExtra("userId", currentUserID);
            startActivity(RequestIntent);
        }


        return true;
    }

    private void SendMessageToDatabase()
    {
        String message = editText.getText().toString();
        String messageKey = groupRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Enter text first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar callForCalender = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            getCurrentDate = currentDateFormat.format(callForCalender.getTime());

            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            getCurrentTime = currentTimeFormat.format(callForTime.getTime());

            editText.setText("");

            //String messageReceiverRef = "Messages" + "/" + msgReceiverId + "/" + msgSenderId;

            DatabaseReference groupMessageKeyRef = groupRef.push();

            messagePushId = groupMessageKeyRef.getKey();

            String messageRef = "Messages" + "/" + messagePushId;

            Map<String, String> messageTextBody = new HashMap<>();
            messageTextBody.put("message", message);
            messageTextBody.put("type", "text");
            messageTextBody.put("messageId", messagePushId);
            messageTextBody.put("from", currentUserID);
            messageTextBody.put("time", getCurrentTime);
            messageTextBody.put("date", getCurrentDate);
            messageTextBody.put("status", "");
            messageTextBody.put("groupId", groupId);
            messageTextBody.put("username", Username);
            messageTextBody.put("you", "You");


            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageRef,messageTextBody);

            groupRef.updateChildren(messageBodyDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        sent = true;
                        //mp.start();

                        groupRef.child("Messages").child(messagePushId)
                                .child("status").setValue("Del");

                        groupRef.child("Members").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists())
                                        {

                                            int index = 0;
                                            offline_mem.clear();
                                            list.clear();
                                            for (DataSnapshot ds : snapshot.getChildren())
                                            {
                                                key = ds.getKey();
                                                assert key != null;
                                                //list.clear();
                                                list.add(key);
                                            }

                                            for (String o : list)
                                            {
                                                if (!o.equals(currentUserID))
                                                {
                                                    groupRef.child("Members").child(o)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    if (snapshot.exists())
                                                                    {
                                                                        String value = snapshot.getValue().toString();

                                                                        if (value.equals("offline"))
                                                                        {
                                                                            String k = snapshot.getKey();
                                                                            offline_mem.add(k);
                                                                            usersRef.child(o).child("Groups").child(groupId)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                            if (snapshot.exists())
                                                                                            {
                                                                                                String num = snapshot.getValue().toString();

                                                                                                if (!num.isEmpty())
                                                                                                {
                                                                                                    if (num.equals("on") || num.equals("off"))
                                                                                                    {
                                                                                                        if (!o.equals(currentUserID))
                                                                                                        {
                                                                                                            usersRef.child(o).child("Groups").child(groupId).
                                                                                                                    setValue("1");
                                                                                                        }


                                                                                                    }
                                                                                                    else
                                                                                                    {
                                                                                                        if (!o.equals(currentUserID))
                                                                                                        {
                                                                                                            int p = Integer.parseInt(num) + 1;
                                                                                                            usersRef.child(o).child("Groups").child(groupId).
                                                                                                                    setValue(String.valueOf(p));
                                                                                                        }

                                                                                                    }
                                                                                                }
                                                                                                else
                                                                                                {
                                                                                                    if (!o.equals(currentUserID))
                                                                                                    {
                                                                                                        usersRef.child(o).child("Groups").child(groupId).
                                                                                                                setValue("1");
                                                                                                    }

                                                                                                }
                                                                                            }


                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

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

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        handler.postDelayed(run,2000);

                    }
                    else
                    {
                        String message = task.getException().toString();
                        Toast.makeText(ChatActivity.this, "*" + message, Toast.LENGTH_SHORT).show();
                    }
                    editText.setText("");

                }
            });

        }
    }


    @Override
    public void delete(int position) {

        //boolean lastItem = position == messagesList.size()-1;

        messageAdapter.notifyItemRemoved(position);

        messagesList.remove(position-1);


    }

}