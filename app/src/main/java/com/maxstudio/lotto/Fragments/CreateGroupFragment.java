package com.maxstudio.lotto.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maxstudio.lotto.Activities.HomeActivity;
import com.maxstudio.lotto.Activities.SignUpActivity;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupFragment extends Fragment {

    private View View;
    private CircleImageView image;
    private EditText editText;
    private Button button;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private ProgressDialog loadingBar;

    private AdView mAdView;

    private boolean isSubs;
    private FrameLayout adContainerView;

    private static final int GalleryPicker = 1;
    private StorageReference UserProfileImageRef;
    private FirebaseAuth mAuth;
    DatabaseReference rootRef,usersRef;
    private  String currentUserID, groupRef;
    private OnSuccessListener<Uri> onSuccessListener;
    private OnCompleteListener<Void> onCompleteListener;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selImageUri;
    private Bitmap bmp;
    private String[] adUnitIds,adUnitIds4,adUnitIds5,adUnitIds6;
    private Native.Natives natives2;
    private boolean isDestroyed;
    private Toolbar mToolBar;
    private int currentAdIndex;

    private static final long BACKGROUND_THRESHOLD = 60 * 60 * 1000; // 1 hour in milliseconds

    public CreateGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View =  inflater.inflate(R.layout.fragment_create_group, container, false);

        rootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").push().getKey();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Group_image");
        mAuth = FirebaseAuth.getInstance();


        InitialisedFields();

        currentAdIndex = 0;

        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .commit();

        SharedPreferences prefs = getActivity().getSharedPreferences("com.maxstudio.lotto",
                Context.MODE_PRIVATE);

        isSubs = prefs.getBoolean("service_status", false);
        mToolBar = View.findViewById(R.id.main_page_toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null)
        {
            activity.setSupportActionBar(mToolBar);
            activity.getSupportActionBar().setTitle("Create Group");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        onSuccessListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        };

        adUnitIds = new String[]{
                getString(R.string.banner_add_id),
                getString(R.string.banner_add_id_1_1),
                getString(R.string.banner_add_id_1_2),
                getString(R.string.banner_add_id_1_3)};

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

        adContainerView.post(new Runnable() {
            @Override
            public void run() {

                if (mAdView != null)
                {mAdView.destroy();
                    loadNextAd();}
                else{loadNextAd();}
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                    {
                        selImageUri = result.getData().getData();
                        onImagesPicked();
                    }
                }
        );

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                imagePickerLauncher.launch(intent);
                MyApplication.isAdFullScreen = true;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                CreateGroup();

            }

        });

        if (!isSubs && getContext() != null)
        {
            natives2.loadNativeAdMedium2();
            natives2.loadNativeAd2();
            natives2.loadNativeAdSmall2();
        }

        return View;
    }

    private void onImagesPicked()
    {
        if (selImageUri !=null && getActivity() != null)
        {
            bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selImageUri);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();

            final StorageReference filePath = UserProfileImageRef.child(groupRef + ".jpg");

            filePath.putBytes(fileInBytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getContext(), "success GGHFHHHJFHF  HHH  GDHYRH", Toast.LENGTH_LONG).show();
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    rootRef.child(groupRef).child("image")
                                            .setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        MyApplication.isAdFullScreen = false;
                                                        Picasso.get().load(downloadUrl).fit().centerCrop()
                                                                .placeholder(R.drawable.profile_image).into(image);

                                                        Toast.makeText(getContext(), "Profile picture uploaded successfully",
                                                                Toast.LENGTH_SHORT).show();

                                                    }
                                                    else{
                                                        MyApplication.isAdFullScreen = false;
                                                        String message = task.getException().toString();
                                                        Toast.makeText(getContext(), "*" + message,Toast.LENGTH_LONG).show();
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            Intent SignupIntent = new Intent(getContext(), SignUpActivity.class);
            SignupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SignupIntent);
        }else
        {
            relativeLayout.setVisibility(android.view.View.VISIBLE);
            //linearLayout.setVisibility(android.view.View.GONE);
            //VerifyUserExists();

        }

    }

    private void CreateGroup() {

        String g_name = editText.getText().toString();
        currentUserID = mAuth.getCurrentUser().getUid();

        if(TextUtils.isEmpty(g_name))
        {
            Toast.makeText(getContext(), "Username is required", Toast.LENGTH_SHORT).show();
        }
        else
        {

            rootRef.child(groupRef).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        loadingBar.setTitle("Creating Group");
                        loadingBar.setMessage("Please wait...");
                        loadingBar.setCanceledOnTouchOutside(true);
                        loadingBar.show();

                        HashMap<String, Object> ProfileMap = new HashMap<>();
                        ProfileMap.put("group_id", groupRef);
                        ProfileMap.put("name", g_name);
                        ProfileMap.put("admin", currentUserID);

                        rootRef.child(groupRef).updateChildren(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    HashMap<String, Object> GroupMap = new HashMap<>();
                                    GroupMap.put(groupRef, "");

                                    usersRef.child(currentUserID).child("Groups").
                                            updateChildren(GroupMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        loadingBar.dismiss();

                                                        Intent HomeIntent = new Intent(getContext(), HomeActivity.class);
                                                        HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(HomeIntent);

                                                    }
                                                    else
                                                    {
                                                        loadingBar.dismiss();

                                                        String message = task.getException().toString();
                                                        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    rootRef.child(groupRef).child("Members").child(currentUserID).setValue("");

                                }else
                                {
                                    loadingBar.dismiss();

                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(getContext(), "Set group image first", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    /**@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPicker && resultCode==RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            if (imageUri !=null)
            {
                final StorageReference filePath = UserProfileImageRef.child(groupRef + ".jpg");

                filePath.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Toast.makeText(getContext(), "success GGHFHHHJFHF  HHH  GDHYRH", Toast.LENGTH_LONG).show();
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        rootRef.child(groupRef).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            Picasso.get().load(downloadUrl).into(image);

                                                            Toast.makeText(getContext(), "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            String message = task.getException().toString();
                                                            Toast.makeText(getContext(), "*" + message,Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });

                                    }
                                });

                            }
                        });
                //Toast.makeText(getContext(), "success GGHFHHHJFHF  HHH  GDHYRH", Toast.LENGTH_LONG).show();
            }



            /**CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        else {
            Toast.makeText(getContext(), "unsuccessful", Toast.LENGTH_LONG).show();
        }

        /**if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Uploading profile picture");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        rootRef.child("Users").child(currentUserID).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(SettingsActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            String message = task.getException().toString();
                                                            Toast.makeText(SettingsActivity.this, "*" + message,Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });

                                    }
                                });

                            }
                        });
            }
        }
    }**/

    private void VerifyUserExists()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("username"))
                {
                    relativeLayout.setVisibility(android.view.View.VISIBLE);
                    linearLayout.setVisibility(android.view.View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitialisedFields() {

        image = View.findViewById(R.id.group_image);
        editText = View.findViewById(R.id.group_name);
        button = View.findViewById(R.id.group_button);

        relativeLayout = View.findViewById(R.id.group_lay);
        //linearLayout = View.findViewById(R.id.upgrade_lay);


        loadingBar = new ProgressDialog(getContext());

    }

    @Override
    public void onDestroyView() {

        if (bmp != null)
        {
            bmp.recycle();
            bmp = null;
        }

        if (adContainerView != null && mAdView != null)
        {
            adContainerView.removeAllViews();
            mAdView.destroy();
        }

        isDestroyed = true;

        super.onDestroyView();
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
                    // Ad successfully loaded
                    // currentAdIndex = 0;
                    //loadNextAd();

                }
            });

        }

    }

}