package com.maxstudio.lotto.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;**/
/**import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;**/
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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
import com.maxstudio.lotto.Activities.ChatActivity;
import com.maxstudio.lotto.Activities.HomeActivity;
import com.maxstudio.lotto.Activities.SearchActivity;
import com.maxstudio.lotto.Activities.SignUpActivity;
import com.maxstudio.lotto.Activities.UserProfileActivity;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Adapters.MessageAdapter;
import com.maxstudio.lotto.Models.Messages;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Utils.MyFirebaseMessagingService;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class GroupFragment extends Fragment {

    private static final String TAG = "watch";
    private View View;
    private RecyclerView ResultsRecyclerList;
    private String userName,groupMsg,groupTime,you,admin,
            currentUserId,sub,user_from,key,lastMsgId;

    private DatabaseReference LottoRef, groupRef,userRef, usersRef;

    public static final List<Messages> messagesList = new ArrayList<>();
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;
    private SharedPreferences sharedPrefs;

    private LinearLayout layout, layout1;
    private RelativeLayout layout2,searchView;
    private TextView mTextView, Verify;
    private LinearLayout VerifyText;
    private boolean checked, isText, isLayout;

    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAd2;

    private boolean isSubs,isDestroyed;
    private FrameLayout adContainerView;
    private AdView mAdView;


    Handler handler;
    private Runnable runnable;
    private  ValueEventListener val;

    private String[] adUnitIds,adUnitIds4,adUnitIds5,adUnitIds6;
    private Native.Natives natives2;
    private int currentAdIndex;
    private String[] adUnitIds2;
    private int currentAdIndex2,inter_ad;

    private long count;

    private static final long BACKGROUND_THRESHOLD = 60 * 60 * 1000; // 1 hour in milliseconds
    private static final int REQUEST_CODE_LOGIN = 123;
    private boolean isInBackground = false;
    private Handler backgroundHandler;
    private SharedPreferences.Editor editor;
    private Runnable backgroundRunnable;
    private long backgroundTime = 0;

    FirebaseRecyclerAdapter<String, GroupFragment.DailyResultsViewHolder> adapter;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_group, container, false);

        mAuth = FirebaseAuth.getInstance();


        ResultsRecyclerList = View.findViewById(R.id.group_recycler);
        ResultsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPrefs = getActivity().getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = sharedPrefs.getBoolean("service_status", false);
        editor = sharedPrefs.edit();

        searchView = View.findViewById(R.id.search_view);

        loadingBar = new ProgressDialog(getContext());

        layout = View.findViewById(R.id.msgLayout);
        layout1 = View.findViewById(R.id.rec_view);
        layout2 = View.findViewById(R.id.upgrade_lay);
        mTextView = View.findViewById(R.id.upgrade);
        VerifyText = View.findViewById(R.id.verifyText);
        Verify = View.findViewById(R.id.verify);

        currentAdIndex = 0;

        SearchFunction();
        adUnitIds = new String[]{
                getString(R.string.banner_add_id_3),
                getString(R.string.banner_add_id_3_1),
                getString(R.string.banner_add_id_3_2),
                getString(R.string.banner_add_id_3_3),
                getString(R.string.banner_add_id_3_4)};

        currentAdIndex2 =0;
        adUnitIds2 = new String[]{
                getString(R.string.Interstitial_add_id_2),
                getString(R.string.Interstitial_add_id),
                getString(R.string.Interstitial_add_id_3),
                getString(R.string.Interstitial_add_id_4),
                getString(R.string.Interstitial_add_id_5)};

        adUnitIds4 = new String[]{
                getString(R.string.Native_add_id),
                getString(R.string.Native_add_id_1_2),
                getString(R.string.Native_add_id_1_3),
                getString(R.string.Native_add_id_1_4)};

        adUnitIds5 = new String[]{
                getString(R.string.Native_add_medium_id),
                getString(R.string.Native_add_medium_id_2),
                getString(R.string.Native_add_medium_id_3),
                getString(R.string.Native_add_medium_id_4)};

        adUnitIds6 = new String[]{
                getString(R.string.Native_add_small_id),
                getString(R.string.Native_add_small_id_2),
                getString(R.string.Native_add_small_id_3),
                getString(R.string.Native_add_small_id_4)};


        natives2 = new Native.Natives(getContext(),adUnitIds4,adUnitIds5,adUnitIds6,isDestroyed);


        adContainerView = View.findViewById(R.id.ad_view_container);

        backgroundHandler = new Handler();
        backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                isInBackground = true;
                // Perform your background logic here
            }
        };


        inter_ad = sharedPrefs.getInt("inter_ad", 0);

        runnable = new Runnable() {
            @Override
            public void run() {

                if (mAdView != null)
                {mAdView.destroy();
                    mAdView = null;
                    loadNextAd();}
                else{loadNextAd();}

                if (inter_ad == 0)
                {
                    loadAd();
                }

                if (!isSubs && getContext() != null)
                {
                    natives2.loadNativeAd2();
                }

            }
        };

        adContainerView.post(runnable);
        //InterLoad();

        return View;
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            layout1.setVisibility(android.view.View.VISIBLE);
            layout2.setVisibility(android.view.View.GONE);
            DataRequest();


        }else
        {
            //adapter.stopListening();

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    isText = true;
                    InterLoad();
                    //AppLovinSdk.getInstance(getContext()).showMediationDebugger();
                }
            });

        }

    }

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Start a new instance of HomeActivity and finish the current one
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
    );
    public void InterLoad()
    {
        int mon = sharedPrefs.getInt("mon", 0);
        mon++;

        editor.putInt("mon", mon);
        editor.commit();

        if (mInterstitialAd != null) {

            if(getContext() != null)
            {
                if (isLayout)
                {
                    if (mon > 1)
                    {
                        mInterstitialAd.show((Activity) getContext());
                        editor.putInt("mon", 0);
                        editor.commit();
                    }
                    else
                    {
                        Intent SearchIntent = new Intent(getContext(), SearchActivity.class);
                        //SignupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(SearchIntent);
                        isLayout = false;
                    }

                }

                if (isText)
                {
                    mInterstitialAd.show((Activity) getContext());
                }

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        mInterstitialAd = null;

                        MyApplication.isAdFullScreen = false;
                        if (isText)
                        {
                            Intent SignupIntent = new Intent(getContext(), SignUpActivity.class);
                            //SignupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                            loginLauncher.launch(SignupIntent);
                            isText = false;
                        } else if (isLayout) {
                            Intent SearchIntent = new Intent(getContext(), SearchActivity.class);
                            //SignupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(SearchIntent);
                            isLayout = false;
                        }

                        //finish();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();

                        MyApplication.isAdFullScreen = true;

                        editor.putInt("inter_ad",0);
                        editor.commit();

                        inter_ad = sharedPrefs.getInt("inter_ad", 0);

                        if (inter_ad == 0)
                        {
                            loadAd();
                        }

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);

                        editor.putInt("inter_ad",0);
                        editor.commit();

                        inter_ad = sharedPrefs.getInt("inter_ad", 0);

                        if (inter_ad == 0)
                        {
                            loadAd();
                        }

                    }
                });
            }
        } else {

            editor.putInt("inter_ad",0);
            editor.commit();
            inter_ad = sharedPrefs.getInt("inter_ad", 0);

            if (inter_ad == 0)
            {
                loadAd();
            }

            if (mon >1)
            {
                editor.putInt("mon", 0);
                editor.commit();
            }

            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            if (isText)
            {
                Intent SignupIntent = new Intent(getContext(), SignUpActivity.class);
                //SignupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                loginLauncher.launch(SignupIntent);
                isText = false;
            } else if (isLayout) {
                Intent SearchIntent = new Intent(getContext(), SearchActivity.class);
                //SignupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(SearchIntent);
                isLayout = false;
            }
        }
        //loadAd();
    }

    public void loadAd()
    {
        if (!isSubs && getContext() != null)
        {
            if (currentAdIndex2 >= adUnitIds2.length) {
                // All ads have been loaded or failed to load
                //currentAdIndex2 = 0;
                return;
            }
            /**if (retryAttempts2 >= MAX_RETRY_ATTEMPTS) {
                shouldRetry2 = false; // Stop retrying
                Log.e("NativeAdHelper", "Maximum retry attempts reached");
                return;
            }**/
            String adUnitId = adUnitIds2[currentAdIndex2];
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(getContext(),adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

                            inter_ad++;

                            editor.putInt("inter_ad", inter_ad);
                            editor.commit();

                            mInterstitialAd = interstitialAd;
                            //retryAttempts2 = 0;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mInterstitialAd = null;

                                }
                            }, BACKGROUND_THRESHOLD);

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
                                loadAd2();

                            }**/
                            currentAdIndex2++;
                            loadAd();
                        }
                    });
        }

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
                    //adContainerView = View.findViewById(R.id.ad_view_container);
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

                    adContainerView.setVisibility(android.view.View.VISIBLE);

                }
            });

        }

    }

    @Override
    public void onDestroyView() {

        //adapter.stopListening();
        if (mAdView !=null && mInterstitialAd != null)
        {
            mAdView.destroy();
            mInterstitialAd = null;

        }
        adContainerView.removeCallbacks(runnable);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {DataRequest2();}
        isDestroyed = true;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        //adapter.stopListening();

    }

    @Override
    public void onPause() {
        super.onPause();


        isInBackground = true;
        backgroundTime = System.currentTimeMillis();
        backgroundHandler.postDelayed(backgroundRunnable, BACKGROUND_THRESHOLD);
    }

    @Override
    public void onResume() {
        super.onResume();

        adContainerView = View.findViewById(R.id.ad_view_container);

        if (isInBackground) {
            long foregroundTime = System.currentTimeMillis() - backgroundTime;
            if (foregroundTime > BACKGROUND_THRESHOLD) {
                // App has been in the background for more than an hour
                // Perform your action here
                mInterstitialAd = null;
                mInterstitialAd2 = null;
                loadAd();
                //loadAd2();

                currentAdIndex = 0;

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
            }
            backgroundHandler.removeCallbacks(backgroundRunnable);
            isInBackground = false;

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null)
        {
            user.reload();
            if (user.isEmailVerified())
            {
                ResultsRecyclerList.setVisibility(android.view.View.VISIBLE);
                VerifyText.setVisibility(android.view.View.GONE);
            }else
            {
                VerifyText.setVisibility(android.view.View.VISIBLE);
                ResultsRecyclerList.setVisibility(android.view.View.GONE);

                Verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        Intent HomeIntent = new Intent(getContext(), UserProfileActivity.class);
                        startActivity(HomeIntent);
                    }
                });
            }
        }
    }
    private void DataRequest() {

        currentUserId = mAuth.getCurrentUser().getUid();

        LottoRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("Groups");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        //Query query = LottoRef.orderByValue().endAt("");

        LottoRef.keepSynced(true);
        userRef.keepSynced(true);
        usersRef.keepSynced(true);
        groupRef.keepSynced(true);

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(LottoRef, String.class)
                        .build();
        FirebaseRecyclerAdapter<String, GroupFragment.DailyResultsViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, GroupFragment.DailyResultsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final GroupFragment.DailyResultsViewHolder holder, int position, @NonNull String model) {

                        String groupKey = getRef(position).getKey();

                        assert groupKey != null;

                        /**val = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };**/

                        groupRef.child(groupKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists())
                                {
                                    if (snapshot.hasChild("group_id"))
                                    {

                                        String gId = sharedPrefs.getString("group_id", "");
                                        String admin = snapshot.child("admin").getValue().toString();

                                        if(gId.equals(groupKey))
                                        {
                                            lastMsgId = sharedPrefs.getString("last_message_id", "");
                                        }

                                        if (snapshot.hasChild("image"))
                                        {

                                        }
                                        else {

                                            groupRef.child(groupKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    LottoRef.child(groupKey).removeValue();

                                                }
                                            });
                                        }

                                        if (snapshot.hasChild("name") && snapshot.hasChild("image"))
                                        {
                                            loadingBar.dismiss();
                                            //String LottoPic = dataSnapshot.child("image").getValue().toString();

                                            holder.layout.setVisibility(android.view.View.VISIBLE);

                                            String groupName = snapshot.child("name").getValue().toString();
                                            holder.group_name.setText(groupName);

                                            //String groupImage;
                                            String groupImage = snapshot.child("image").getValue().toString();

                                            Picasso.get().load(groupImage).fit().centerCrop()
                                                    .placeholder(R.drawable.profile_image).into(holder.group_image);

                                            long count = snapshot.child("Members").getChildrenCount();


                                            groupRef.child(groupKey).child("Messages")
                                                    .addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                                            if (snapshot.exists()) {
                                                                holder.group_msg_empty.setVisibility(android.view.View.GONE);
                                                                holder.UsernameLay.setVisibility(android.view.View.VISIBLE);

                                                                groupMsg = snapshot.getValue(Messages.class).getMessage();

                                                                String[] formats = {
                                                                       "MMM. dd, yyyy",
                                                                        "MMM dd, yyyy"
                                                                };

                                                                SimpleDateFormat date_format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                                                                String todayDate = date_format.format(new Date());

                                                                SimpleDateFormat dateFormat = new SimpleDateFormat();
                                                                String msgDates = null;

                                                                String groupDate = snapshot.getValue(Messages.class).getDate();

                                                                for (String format : formats)
                                                                {
                                                                    dateFormat.applyPattern(format);
                                                                    try {
                                                                        Date msgDate = dateFormat.parse(groupDate);

                                                                        assert msgDate != null;
                                                                        msgDates = dateFormat.format(msgDate);

                                                                        //assert msgDates != null;
                                                                        if (msgDates.equals(todayDate)) {

                                                                            groupTime = snapshot.getValue(Messages.class).getTime();
                                                                            holder.group_time.setText(groupTime);

                                                                        } else {

                                                                            try {

                                                                                Date mesObj = dateFormat.parse(groupDate);
                                                                                Date todayObj = dateFormat.parse(todayDate);

                                                                                long difInMills = todayObj.getTime() - mesObj.getTime();
                                                                                long difInDays = TimeUnit.MILLISECONDS.toDays(difInMills);

                                                                                if (difInDays == 1) {
                                                                                    holder.group_time.setText("Yesterday");
                                                                                } else {
                                                                                    holder.group_time.setText(groupDate);
                                                                                }
                                                                            } catch (ParseException e) {
                                                                                holder.group_time.setText(groupDate);
                                                                            }
                                                                        }

                                                                        break;

                                                                    }catch (ParseException e)
                                                                    {
                                                                        //holder.group_time.setText(groupDate);
                                                                    }

                                                                }

                                                                user_from = snapshot.getValue(Messages.class).getFrom();

                                                                userName = Objects.requireNonNull(snapshot.getValue(Messages.class)).getUsername();
                                                                you = Objects.requireNonNull(snapshot.getValue(Messages.class)).getYou();


                                                                usersRef.child(currentUserId).child("Groups").child(groupKey)
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                if (snapshot.exists()) {
                                                                                    String val = snapshot.getValue().toString();

                                                                                    if (!val.isEmpty()) {
                                                                                        if (val.equals("on") || val.equals("off")) {
                                                                                            holder.layout.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(android.view.View v) {

                                                                                                    /**userRef.child("Groups").child(groupKey).child("message_status")
                                                                                                     .setValue("read");**/

                                                                                                    Intent ChatIntent = new Intent(getContext(), ChatActivity.class);
                                                                                                    ChatIntent.putExtra("groupName", groupName);
                                                                                                    ChatIntent.putExtra("groupImage", groupImage);
                                                                                                    ChatIntent.putExtra("groupKey", groupKey);
                                                                                                    ChatIntent.putExtra("msgId", lastMsgId);
                                                                                                    ChatIntent.putExtra("count", count);
                                                                                                    ChatIntent.putExtra("admin", admin);
                                                                                                    startActivity(ChatIntent);

                                                                                                    holder.relativeLayout.setVisibility(android.view.View.GONE);

                                                                                                    LottoRef.child(groupKey).setValue("off");

                                                                                                    //checked = true;

                                                                                                }
                                                                                            });
                                                                                            usersRef.child(user_from).addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snap) {

                                                                                                    if (snap.exists() && snap.hasChild("username")) {
                                                                                                        //String user_name = snap.child("username").getValue().toString();

                                                                                                        if (user_from.equals(currentUserId)) {
                                                                                                            //holder.Username.setText("You");
                                                                                                            holder.rel.setVisibility(android.view.View.GONE);

                                                                                                        } else {

                                                                                                            usersRef.child(currentUserId).child("Groups").child(groupKey)
                                                                                                                    .addValueEventListener(new ValueEventListener() {
                                                                                                                        @Override
                                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                                            if (snapshot.exists()) {
                                                                                                                                String val = snapshot.getValue().toString();

                                                                                                                                if (val.equals("on")) {
                                                                                                                                    holder.rel.setVisibility(android.view.View.VISIBLE);
                                                                                                                                    //holder.relativeLayout.setText("");
                                                                                                                                } else {
                                                                                                                                    holder.rel.setVisibility(android.view.View.GONE);

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
                                                                                            //holder.relativeLayout.setVisibility(android.view.View.VISIBLE);
                                                                                        } else {
                                                                                            holder.layout.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(android.view.View v) {

                                                                                                    Intent ChatIntent = new Intent(getContext(), ChatActivity.class);
                                                                                                    ChatIntent.putExtra("groupName", groupName);
                                                                                                    ChatIntent.putExtra("groupImage", groupImage);
                                                                                                    ChatIntent.putExtra("groupKey", groupKey);
                                                                                                    ChatIntent.putExtra("msgId", lastMsgId);
                                                                                                    ChatIntent.putExtra("count", count);
                                                                                                    ChatIntent.putExtra("admin", admin);
                                                                                                    startActivity(ChatIntent);

                                                                                                    //holder.relativeLayout.setVisibility(android.view.View.GONE);

                                                                                                    //LottoRef.child(groupKey).setValue("");

                                                                                                    //checked = true;

                                                                                                }
                                                                                            });

                                                                                            if (user_from.equals(currentUserId)) {
                                                                                                holder.relativeLayout.setVisibility(android.view.View.GONE);
                                                                                            } else {
                                                                                                holder.relativeLayout.setVisibility(android.view.View.VISIBLE);
                                                                                                holder.relativeLayout.setText(val);
                                                                                            }
                                                                                        }

                                                                                    }
                                                                                    else {
                                                                                        holder.relativeLayout.setVisibility(android.view.View.GONE);
                                                                                        holder.layout.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(android.view.View v) {

                                                                                                Intent ChatIntent = new Intent(getContext(), ChatActivity.class);
                                                                                                ChatIntent.putExtra("groupName", groupName);
                                                                                                ChatIntent.putExtra("groupImage", groupImage);
                                                                                                ChatIntent.putExtra("groupKey", groupKey);
                                                                                                ChatIntent.putExtra("msgId", lastMsgId);
                                                                                                ChatIntent.putExtra("count", count);
                                                                                                ChatIntent.putExtra("admin", admin);
                                                                                                startActivity(ChatIntent);

                                                                                                //holder.relativeLayout.setVisibility(android.view.View.GONE);

                                                                                                //LottoRef.child(groupKey).setValue("");

                                                                                                //checked = true;

                                                                                            }
                                                                                        });
                                                                                    }


                                                                                }


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });


                                                                String messageType = snapshot.getValue(Messages.class).getType();

                                                                if (messageType.equals("image")) {
                                                                    holder.group_msg_image.setVisibility(android.view.View.VISIBLE);
                                                                    holder.group_msg.setVisibility(android.view.View.GONE);

                                                                } else {
                                                                    holder.group_msg_image.setVisibility(android.view.View.GONE);
                                                                    holder.group_msg.setVisibility(android.view.View.VISIBLE);

                                                                    holder.group_msg.setText(groupMsg);

                                                                }
                                                                //holder.group_msg.setText(groupMsg);

                                                                if (user_from.equals(currentUserId)) {
                                                                    holder.Username.setText(you);
                                                                } else {
                                                                    holder.Username.setText(userName);
                                                                }


                                                            }


                                                        }

                                                        @Override
                                                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                                        }

                                                        @Override
                                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {


                                                        }

                                                        @Override
                                                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                            groupRef.child(groupKey).child("Messages")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                            if (!snapshot.exists())
                                                            {
                                                                holder.layout.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(android.view.View v) {

                                                                        Intent ChatIntent = new Intent(getContext(), ChatActivity.class);
                                                                        ChatIntent.putExtra("groupName", groupName);
                                                                        ChatIntent.putExtra("groupImage", groupImage);
                                                                        ChatIntent.putExtra("groupKey", groupKey);
                                                                        ChatIntent.putExtra("msgId", lastMsgId);
                                                                        ChatIntent.putExtra("count", count);
                                                                        ChatIntent.putExtra("admin", admin);
                                                                        startActivity(ChatIntent);

                                                                        //holder.relativeLayout.setVisibility(android.view.View.GONE);

                                                                        //LottoRef.child(groupKey).setValue("");

                                                                        //checked = true;

                                                                    }
                                                                });
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


                    }

                    @NonNull
                    @Override
                    public GroupFragment.DailyResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grouplayout, parent, false);
                        return new DailyResultsViewHolder(view);
                    }
                };

        ResultsRecyclerList.setAdapter(adapter);
        adapter.startListening();

    }

    private void DataRequest2() {

        currentUserId = mAuth.getCurrentUser().getUid();

        LottoRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("Groups");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        LottoRef.keepSynced(true);
        userRef.keepSynced(true);
        groupRef.keepSynced(true);

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(LottoRef, String.class)
                        .build();
        FirebaseRecyclerAdapter<String, GroupFragment.DailyResultsViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, GroupFragment.DailyResultsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final GroupFragment.DailyResultsViewHolder holder, int position, @NonNull String model) {




                    }

                    @NonNull
                    @Override
                    public GroupFragment.DailyResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grouplayout, parent, false);
                        GroupFragment.DailyResultsViewHolder viewHolder = new DailyResultsViewHolder(view);
                        return viewHolder;
                    }
                };

        ResultsRecyclerList.setAdapter(adapter);
        adapter.stopListening();

    }

    private void SearchFunction()
    {
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                isLayout = true;
                InterLoad();
            }
        });
    }

    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, group_msg,group_msg_empty, group_time, group_msg_count, Username;
        LinearLayout layout,group_msg_image, UsernameLay;
        ImageView group_image;
        TextView relativeLayout,rel;

        //SearchView searchView;

        public DailyResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            group_name = itemView.findViewById(R.id.g_name);
            group_msg = itemView.findViewById(R.id.gMsg);
            group_msg_empty = itemView.findViewById(R.id.gMsg_empty);
            //group_msg_count = itemView.findViewById(R.id.gMsgCount);
            group_time = itemView.findViewById(R.id.gTime);
            Username = itemView.findViewById(R.id.username);

            group_image = itemView.findViewById(R.id.gImage);
            group_msg_image = itemView.findViewById(R.id.msgImageLay);

            layout = itemView.findViewById(R.id.msgLayout);
            relativeLayout = itemView.findViewById(R.id.message_on);
            rel = itemView.findViewById(R.id.message_on2);

            UsernameLay = itemView.findViewById(R.id.username_lay);

            //LottoImage = itemView.findViewById(R.id.lotto_image);
        }
    }


}