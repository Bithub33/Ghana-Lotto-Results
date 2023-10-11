package com.maxstudio.lotto.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private LinearLayout layout,layout1,layout2;
    private TextView textView,textView1,textView2,textView3,spam;
    private RelativeLayout updateImg;
    private CircleImageView imageView;
    private String currentUserID,userRef,email;

    private static final int GalleryPicker = 1;
    boolean EmailUpdated;

    private Toolbar mToolBar;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isSubs;
    private Uri selImageUri;

    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private DatabaseReference usersRef;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setStatusBarColor(Color.parseColor("#CC2C2C"));

        usersRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").push().getKey();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("User_image");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        InitialisedFields();

        profileUpdateFunction();

        mToolBar = findViewById(R.id.pro_toolbar);
        setSupportActionBar(mToolBar);
        setTitle("Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                imagePickerLauncher.launch(intent);

                MyApplication.isAdFullScreen = true;

            }
        });


    }

    private void onImagesPicked()
    {
        if (selImageUri !=null)
        {
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selImageUri);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null)
            {
                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            }
            byte[] fileInBytes = baos.toByteArray();

            final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

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

                                                        MyApplication.isAdFullScreen = false;

                                                        Picasso.get().load(downloadUrl).fetch(new Callback() {
                                                            @Override
                                                            public void onSuccess() {

                                                                Picasso.get().load(downloadUrl).fit().centerCrop()
                                                                        .placeholder(R.drawable.profile_image).into(imageView);
                                                            }

                                                            @Override
                                                            public void onError(Exception e) {

                                                            }
                                                        });

                                                        Toast.makeText(UserProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        String message = task.getException().toString();
                                                        Toast.makeText(UserProfileActivity.this, "*" + message,Toast.LENGTH_LONG).show();
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

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
        {
            user.reload();

            boolean emailVerified = user.isEmailVerified();
            if (emailVerified)
            {
                textView2.setText("Verified");
            }else
            {

                textView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sendVerification();
                    }
                });
            }
        }


    }

    private void profileUpdateFunction() {

        usersRef.child("Users").child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.hasChild("username"))
                {
                    String user_name = snapshot.child("username").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();

                    textView.setText(user_name);
                    textView1.setText(email);

                    if (snapshot.hasChild("image"))
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).fetch(new Callback() {
                            @Override
                            public void onSuccess() {

                                Picasso.get().load(image).fit().centerCrop()
                                        .placeholder(R.drawable.profile_image).into(imageView);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

                    }

                    if (snapshot.hasChild("about"))
                    {
                        String about = snapshot.child("about").getValue().toString();
                        textView3.setText(about);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this, R.style.AlertDialog);
                builder.setTitle("Please enter your username below");

                final EditText emailField = new EditText(UserProfileActivity.this);
                emailField.setWidth(150);
                builder.setView(emailField);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = emailField.getText().toString();

                        if (TextUtils.isEmpty(username))
                        {
                            Toast.makeText(UserProfileActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            usersRef.child("Users").child(currentUserID)
                                    .child("username").setValue(username)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(UserProfileActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                                                textView.setText(username);
                                            }

                                        }
                                    });
                        }
                    }
                });

                builder.show();

            }
        });

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this, R.style.AlertDialog);
                builder.setTitle("Please enter your email below");

                /**LayoutInflater inflater = LayoutInflater.from(UserProfileActivity.this);
                View custom = inflater.inflate(R.layout.custom_edit_text,null);

                ViewGroup parent = (ViewGroup) custom.getParent();

                if (parent != null)
                {
                    parent.removeView(custom);
                }**/

                final EditText emailField = new EditText(UserProfileActivity.this);
                builder.setView(emailField);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        email = emailField.getText().toString();

                        if (TextUtils.isEmpty(email))
                        {
                            Toast.makeText(UserProfileActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null)
                            {
                                usersRef.child("Users").child(currentUserID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if (snapshot.exists())
                                                {
                                                    String pass = snapshot.child("password").getValue().toString();
                                                    String email = snapshot.child("email").getValue().toString();

                                                    AuthCredential credential = EmailAuthProvider
                                                            .getCredential(email,pass);

                                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful())
                                                            {
                                                                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        sendVerification();
                                                                        EmailUpdated = true;

                                                                        textView1.setText(email);
                                                                        Toast.makeText(UserProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });
                                                            }
                                                            else
                                                            {
                                                                Exception e = task.getException();
                                                                Toast.makeText(UserProfileActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                                                            }

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
                });

                //AlertDialog alertDialog = builder.create();
                builder.show();

            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this, R.style.AlertDialog);
                builder.setTitle("Please enter your details below");

                final EditText emailField = new EditText(UserProfileActivity.this);
                //emailField.setWidth(150);
                builder.setView(emailField);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String about = emailField.getText().toString();

                        if (TextUtils.isEmpty(about))
                        {
                            Toast.makeText(UserProfileActivity.this, "Enter about", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null)
                            {
                                usersRef.child("Users").child(currentUserID)
                                        .child("about").setValue(about)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    textView3.setText(about);
                                                    Toast.makeText(UserProfileActivity.this, "About updated successfully", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


                            }

                        }
                    }
                });

                builder.show();

            }
        });

    }

    private void sendVerification() {

        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        textView2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(UserProfileActivity.this, "Verification link has already been sent to your mail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        spam.setVisibility(View.VISIBLE);
                        Toast.makeText(UserProfileActivity.this, "Verification code has been sent to your email. Please Verify to continue", Toast.LENGTH_LONG).show();

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            user.reload();
            if (EmailUpdated && email != null)
            {
                boolean emailVerified = user.isEmailVerified();
                if (emailVerified)
                {
                    usersRef.child("Users").child(currentUserID)
                            .child("email").setValue(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(UserProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }

    }

    private void InitialisedFields() {

        layout = findViewById(R.id.user_lay);
        layout1 = findViewById(R.id.email_lay);
        layout2 = findViewById(R.id.des_lay);
        updateImg = findViewById(R.id.updateImg);

        textView = findViewById(R.id.user_name);
        textView1 = findViewById(R.id.user_email);
        textView2 = findViewById(R.id.verify);
        textView3 = findViewById(R.id.about);
        spam = findViewById(R.id.spamText);

        imageView = findViewById(R.id.user_image);

    }

}