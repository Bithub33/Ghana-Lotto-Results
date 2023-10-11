package com.maxstudio.lotto.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Fragments.ResultsFragment;
import com.maxstudio.lotto.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private EditText editText1,editText2,editText3,editText4,editText5,editText6;
    private Button button1,button2,button3;
    private TextView textView1,textView2,forgetPass,spam;
    private LinearLayout layout1,layout2,layout3;
    private CircleImageView imageView;
    private String currentUserID,userRef, dev_token;
    private static final int GalleryPicker = 1;
    private  int forgetTracker = 0;

    private Native.Natives natives2;
    public static RewardedAd rewardedAd;
    private RelativeLayout loadingBar,loadingBar2;
    private Toolbar mToolBar;

    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private DatabaseReference usersRef, groupRef;

    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAd2;
    private AdView mAdView;
    private FrameLayout adContainerView;
    private Runnable runnable;

    private static final long BACKGROUND_THRESHOLD = 60 * 60 * 1000; // 1 hour in milliseconds

    private String[] adUnitIds,adUnitIds2,adUnitIds4,adUnitIds5,adUnitIds6;
    private int currentAdIndex, currentAdIndex2;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isSubs;
    private Uri selImageUri;
    int Counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        usersRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").push().getKey();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("User_image");
        mAuth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        currentAdIndex = 0;
        currentAdIndex2 = 0;

        SharedPreferences prefs = getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);

        InitialisedFields();

        //currentAdIndex2 = 0;
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

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                    {
                        selImageUri =result.getData().getData();
                        onImagesPicked();
                    }
                }
        );

        adUnitIds = new String[]{
                getString(R.string.Interstitial_add_id_2),
                getString(R.string.Interstitial_add_id),
                getString(R.string.Interstitial_add_id_3),
                getString(R.string.Interstitial_add_id_4),
                getString(R.string.Interstitial_add_id_5)};

        adUnitIds2 = new String[]{
                getString(R.string.banner_add_id_2),
                getString(R.string.banner_add_id_2_1),
                getString(R.string.banner_add_id_2_2),
                getString(R.string.banner_add_id_2_3),
                getString(R.string.banner_add_id_2_4)};

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


        natives2 = new Native.Natives(this,adUnitIds4,adUnitIds5,adUnitIds6,isDestroyed());

        //loadAd();

        if (ResultsFragment.rewardedAd != null)
        {
            rewardedAd = ResultsFragment.rewardedAd;
        }

        adContainerView = findViewById(R.id.ad_view_container);

        adContainerView.post(runnable);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToLogin();

            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout1.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);

            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout3.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);

            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgetTracker++;
                if (forgetTracker == 1)
                {
                    editText6.setVisibility(View.GONE);
                    button2.setText("RESET");
                    forgetPass.setText("Login");
                    forgetPass.setTextColor(Color.parseColor("#101010"));
                    //forgetPass.setVisibility(View.GONE);

                    layout2.setVisibility(View.GONE);
                }
                else if (forgetTracker == 2) {

                    editText6.setVisibility(View.VISIBLE);
                    button2.setText("LOGIN");
                    forgetPass.setText("forgot password?");
                    forgetPass.setTextColor(Color.parseColor("#EB464C"));
                    layout2.setVisibility(View.VISIBLE);
                    forgetTracker = 0;
                }

                //layout1.setVisibility(View.VISIBLE);
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SignUpActivity.this, "Create account first before uploading profile picture", Toast.LENGTH_SHORT).show();

            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(Task<String> task) {

                        if (task.isSuccessful())
                        {
                            dev_token = task.getResult();

                        }
                    }
                });

        if (!isSubs)
        {
            natives2.loadNativeAdMedium2();
            natives2.loadNativeAd2();
            natives2.loadNativeAdSmall2();
        }
        mToolBar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Signup");
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

    /**@Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            FirebaseUser users = mAuth.getCurrentUser();

            assert users != null;

            boolean emailVerified = user.isEmailVerified();
            if (emailVerified)
            {
                button1.setVisibility(View.VISIBLE);
            }
            else
            {
                button3.setVisibility(View.VISIBLE);
            }

        }
    }**/

    @Override
    protected void onStart() {
        super.onStart();


    }

    /**@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //int id = item.getItemId();

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return false;
    }**/



    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdView = new AdView(this);
        mAdView.destroy();

        adContainerView.removeCallbacks(runnable);

        //InterstitialAd.load(this,null,null,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //currentAdIndex = 0;
        //currentAdIndex2 = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //NativeAdMedium();


    }

    private void InitialisedFields() {

        editText1 = findViewById(R.id.user_name);
        editText2 = findViewById(R.id.user_email);
        editText3 = findViewById(R.id.user_pass);
        editText4 = findViewById(R.id.confirm_pass);
        editText5 = findViewById(R.id.log_email);
        editText6 = findViewById(R.id.log_pass);


        button1 = findViewById(R.id.sign_button);
        button2 = findViewById(R.id.log_button);

        textView1 = findViewById(R.id.signup);
        textView2 = findViewById(R.id.login);
        forgetPass = findViewById(R.id.f_pass);
        //spam = findViewById(R.id.spamText);

        layout1 = findViewById(R.id.signup_lay);
        layout3 = findViewById(R.id.login_lay);
        layout2 = findViewById(R.id.signLay);

        imageView = findViewById(R.id.p_image);

        loadingBar = findViewById(R.id.login_loading);
        loadingBar2 = findViewById(R.id.login_loading2);

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
            adContainerView = findViewById(R.id.ad_view_container);
            adContainerView.setVisibility(View.GONE);
        }

    }


    private void onImagesPicked()
    {
        if (selImageUri !=null)
        {
            loadingBar2.setVisibility(View.VISIBLE);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selImageUri);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();

            final StorageReference filePath = UserProfileImageRef.child(userRef + ".jpg");

            filePath.putBytes(fileInBytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    usersRef.child("Users").child(currentUserID).child("image")
                                            .setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

                                                        loadingBar2.setVisibility(View.GONE);

                                                        Picasso.get().load(downloadUrl).placeholder(R.drawable.profile_image).into(imageView);

                                                        if (mInterstitialAd != null) {
                                                            mInterstitialAd.show(SignUpActivity.this);
                                                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                                @Override
                                                                public void onAdDismissedFullScreenContent() {
                                                                    super.onAdDismissedFullScreenContent();

                                                                    mInterstitialAd = null;
                                                                    loadAd();
                                                                    setResult(RESULT_OK);
                                                                    MyApplication.isAdFullScreen = false;
                                                                    finish();

                                                                }

                                                                @Override
                                                                public void onAdShowedFullScreenContent() {
                                                                    super.onAdShowedFullScreenContent();

                                                                    MyApplication.isAdFullScreen = true;

                                                                }
                                                            });

                                                        } else {

                                                            loadAd();
                                                            loadingBar2.setVisibility(View.GONE);
                                                            setResult(RESULT_OK);
                                                            finish();

                                                        }

                                                    }
                                                    else{
                                                        loadingBar2.setVisibility(View.GONE);
                                                        String message = task.getException().toString();
                                                        Toast.makeText(SignUpActivity.this, "*" + message,Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });

                                }
                            });

                        }
                    });
            //Toast.makeText(getContext(), "success GGHFHHHJFHF  HHH  GDHYRH", Toast.LENGTH_LONG).show();
        }
    }

    private void CreateNewAccount()
    {
        String email = editText2.getText().toString();
        String username = editText1.getText().toString();
        String password = editText3.getText().toString();
        String passwordConfirm = editText4.getText().toString();

        if (!password.equals(passwordConfirm))
        {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();

            //text_input2.setError("password does not match");
        }
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty())
        {
            Toast.makeText(this, "Empty field is required", Toast.LENGTH_SHORT).show();

        }
        else
        {
            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null)
            {
                loadingBar2.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    //spam.setVisibility(View.GONE);
                                    currentUserID = mAuth.getCurrentUser().getUid();

                                    String UserData = "Users" + "/" + currentUserID;


                                    Map<String, String> userProfileBody = new HashMap<String, String>();
                                    userProfileBody.put("username", username);
                                    userProfileBody.put("email", email);
                                    userProfileBody.put("password", passwordConfirm);
                                    userProfileBody.put("userId", currentUserID);
                                    userProfileBody.put("device_token", dev_token);

                                    Map<String, Object> userDataDetails = new HashMap<>();
                                    userDataDetails.put(UserData, userProfileBody);

                                    //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
                                    //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                                    usersRef.updateChildren(userDataDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                //loadingBar.dismiss();
                                                loadingBar2.setVisibility(View.GONE);

                                                imageView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                                        imagePickerLauncher.launch(intent);

                                                        MyApplication.isAdFullScreen = true;

                                                        //loadingBar2.setVisibility(View.VISIBLE);

                                                    }
                                                });

                                                Toast.makeText(SignUpActivity.this, "Profile created successfully", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(SignUpActivity.this, "Set profile image to continue", Toast.LENGTH_LONG).show();

                                            } else {
                                                String message = task.getException().toString();
                                                Toast.makeText(SignUpActivity.this, "*" + message, Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    });

                                }
                                else
                                {
                                    loadingBar2.setVisibility(View.GONE);
                                    String message = task.getException().toString();
                                    Toast.makeText(SignUpActivity.this, "*" + message, Toast.LENGTH_SHORT).show();
                                }
                                //loadingBar.dismiss();
                            }
                        });
            }


        }

    }


    private void AllowUserToLogin()
    {
        String email = editText5.getText().toString();
        String password = editText6.getText().toString();

        if (editText6.getVisibility() == View.GONE)
        {
            if(TextUtils.isEmpty(email))
            {
                Toast.makeText(this, "*Email is required", Toast.LENGTH_SHORT).show();
            }
            else
            {
                loadingBar.setVisibility(View.VISIBLE);

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    loadingBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUpActivity.this, "Password reset link has been sent to your mail", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        }
        else
        {
            if(TextUtils.isEmpty(email))
            {
                Toast.makeText(this, "*Email is required", Toast.LENGTH_SHORT).show();
            }
            if(TextUtils.isEmpty(password))
            {
                Toast.makeText(this, "*Password is required", Toast.LENGTH_SHORT).show();
            }
            else{
                loadingBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful())
                                {
                                    currentUserID = mAuth.getCurrentUser().getUid();

                                    usersRef.child("Users").child(currentUserID)
                                            .child("device_token").setValue(dev_token)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful())
                                                    {
                                                        //loadingBar.setVisibility(View.VISIBLE);

                                                        setResult(RESULT_OK);
                                                        finish();
                                                        //startActivity(HomeIntent);

                                                    }

                                                }
                                            });


                                }
                                else
                                {
                                    loadingBar.setVisibility(View.GONE);
                                    String message = task.getException().toString();
                                    Toast.makeText(SignUpActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        }

    }

    public void loadAd()
    {
        if (!isSubs)
        {
            if (currentAdIndex >= adUnitIds.length) {
                // All ads have been loaded or failed to load
                //currentAdIndex = 0;
                return;
            }
            /**if (retryAttempts2 >= MAX_RETRY_ATTEMPTS) {
             shouldRetry2 = false; // Stop retrying
             loadAd2();
             Log.e("NativeAdHelper", "Maximum retry attempts reached");
             return;
             }**/
            String adUnitId = adUnitIds[currentAdIndex];
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(SignUpActivity.this,adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

                            mInterstitialAd = interstitialAd;

                            //retryAttempts2 = 0;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mInterstitialAd = null;
                                    currentAdIndex++;
                                    loadAd();

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
                             loadAd();

                             }**/
                            currentAdIndex++;
                            loadAd();

                        }
                    });
        }

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
        if (currentAdIndex2 >= adUnitIds2.length) {
            // All ads have been loaded or failed to load
            currentAdIndex2 = 0;
            return;
        }

        String adUnitId = adUnitIds2[currentAdIndex2];
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
                currentAdIndex2++;
                loadNextAd();

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Ad successfully loaded
                //currentAdIndex++;
                //loadNextAd();
                adContainerView.setVisibility(View.VISIBLE);
            }
        });

    }
}