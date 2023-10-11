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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class GroupInfoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private LinearLayout layout,layout1;
    private TextView textView,textView1;
    private RelativeLayout updateImg;
    private CircleImageView imageView;
    private String currentUserID,userRef,groupRef,groupId;

    private static final int GalleryPicker = 1;

    private Toolbar mToolBar;

    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private DatabaseReference usersRef,rootRef;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isSubs;
    private Uri selImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        getWindow().setStatusBarColor(Color.parseColor("#CC2C2C"));

        groupId = getIntent().getExtras().get("groupId").toString();

        rootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").push().getKey();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Group_image");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        InitialisedFields();

        profileUpdateFunction();

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

        mToolBar = findViewById(R.id.pro_toolbar);
        setSupportActionBar(mToolBar);
        setTitle("Group Info");
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

    private void profileUpdateFunction() {

        rootRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    String admin = snapshot.child("admin").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();

                    textView.setText(name);

                    if (admin.equals(currentUserID))
                    {
                        updateImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                imagePickerLauncher.launch(intent);

                                MyApplication.isAdFullScreen = true;

                            }
                        });

                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this, R.style.AlertDialog);
                                builder.setTitle("Please enter your group name");

                                final EditText groupNameField = new EditText(GroupInfoActivity.this);
                                builder.setView(groupNameField);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String groupName = groupNameField.getText().toString();

                                        if (TextUtils.isEmpty(groupName))
                                        {
                                            Toast.makeText(GroupInfoActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            rootRef.child(groupId).child("name").setValue(groupName);
                                        }
                                    }
                                });

                                builder.show();

                            }
                        });

                        layout1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this, R.style.AlertDialog);
                                builder.setTitle("Please enter your group details");

                                final EditText groupNameField = new EditText(GroupInfoActivity.this);
                                //groupNameField.setHint("Edit username");
                                builder.setView(groupNameField);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String groupName = groupNameField.getText().toString();

                                        if (TextUtils.isEmpty(groupName))
                                        {
                                            Toast.makeText(GroupInfoActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            rootRef.child(groupId).child("about").setValue(groupName);
                                        }
                                    }
                                });

                                builder.show();

                            }
                        });
                    }

                    if (snapshot.hasChild("about"))
                    {
                        String about = snapshot.child("about").getValue().toString();

                        textView1.setText(about);
                    }

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

                }


            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

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
            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();

            final StorageReference filePath = UserProfileImageRef.child(groupId + ".jpg");

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

                                    rootRef.child(groupId).child("image")
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

                                                        Toast.makeText(GroupInfoActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        String message = task.getException().toString();
                                                        Toast.makeText(GroupInfoActivity.this, "*" + message,Toast.LENGTH_LONG).show();
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

    private void InitialisedFields() {

        layout = findViewById(R.id.user_lay);
        layout1 = findViewById(R.id.email_lay);
        updateImg = findViewById(R.id.updateImg);

        textView = findViewById(R.id.user_name);
        textView1 = findViewById(R.id.user_email);

        imageView = findViewById(R.id.user_image);

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
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
                byte[] fileInBytes = baos.toByteArray();

                final StorageReference filePath = UserProfileImageRef.child(groupId + ".jpg");

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

                                        rootRef.child(groupId).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            Picasso.get().load(downloadUrl).fetch(new Callback() {
                                                                @Override
                                                                public void onSuccess() {

                                                                    Picasso.get().load(downloadUrl).placeholder(R.drawable.profile_image).into(imageView);
                                                                }

                                                                @Override
                                                                public void onError(Exception e) {

                                                                }
                                                            });

                                                            Toast.makeText(GroupInfoActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            String message = task.getException().toString();
                                                            Toast.makeText(GroupInfoActivity.this, "*" + message,Toast.LENGTH_LONG).show();
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
            Toast.makeText(GroupInfoActivity.this, "unsuccessful", Toast.LENGTH_LONG).show();
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
}