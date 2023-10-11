package com.maxstudio.lotto.Utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.maxstudio.lotto.Activities.ChatActivity;
import com.maxstudio.lotto.Activities.HomeActivity;
import com.maxstudio.lotto.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static int v = 0;
    public int f = 0;
    private int d = 0;
    public static String c = "";
    private int b = 0;
    private Bitmap bitmap = null;
    private Bitmap bitmap2 = null;
    private RemoteViews remoteViews,remoteView;
    List<String> list = new ArrayList<>();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0)
        {
            String title = data.get("title");
            String body = data.get("body");
            String name = data.get("groupName");
            String id = data.get("group_id");
            String count = data.get("groupCount");
            String admin = data.get("admin");
            String image = data.get("groupImage");
            String msgId = data.get("mesId");
            String type = data.get("types");
            String pImg = data.get("profImage");
            v++;

            if (!list.contains(name))
            {
                d = list.size();
                list.add(name);

            }
            else
            {
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).equals(name))
                    {
                        d = i;
                    }
                }

            }

            showNotification(title, body, d,name,id,count,admin,image,msgId,type,pImg);


        }
        else
        {
            b++;
             if (remoteMessage.getNotification() != null) {

                 String title = remoteMessage.getNotification().getTitle();
                 String body = remoteMessage.getNotification().getBody();

                 showNotification2(title, body,b);

             }
        }


    }

    public void showNotification(String title, String body,int v,String name,
                                 String id, String count, String admin,
                                 String image,String msgId,String type,String pImg) {
        // Create a notification channel for Android 8.0 (Oreo) and above

        String channelId = "my_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("groupName",name);
        intent.putExtra("groupImage",image);
        intent.putExtra("groupKey",id);
        intent.putExtra("msgId",msgId);
        intent.putExtra("count",count);
        intent.putExtra("admin",admin);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,v,intent,
                PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

        String content;

        try
        {
            bitmap = Picasso.get().load(pImg).get();
            bitmap2 = Picasso.get().load(image).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Bitmap circularBitmap = getCircularBitmap(bitmap);
        Bitmap largeIcon = getCircularBitmap(bitmap2);

        if (type.equals("image"))
        {
            remoteViews = new RemoteViews(getPackageName(),R.layout.not_layout_one_img);
            remoteViews.setImageViewBitmap(R.id.img,circularBitmap);
            remoteViews.setTextViewText(R.id.user,title);
            content = "Photo";

            remoteView = new RemoteViews(getPackageName(),R.layout.not_layout_one_img);
            remoteView.setImageViewBitmap(R.id.img,circularBitmap);
            remoteView.setTextViewText(R.id.user,title);
        }
        else
        {

            remoteViews = new RemoteViews(getPackageName(),R.layout.not_layout_one);
            remoteViews.setImageViewBitmap(R.id.img,circularBitmap);
            remoteViews.setTextViewText(R.id.user,title);
            remoteViews.setTextViewText(R.id.text,body);
            content = body;

            remoteView = new RemoteViews(getPackageName(),R.layout.not_layout_one_exp);
            remoteView.setImageViewBitmap(R.id.img,circularBitmap);
            remoteView.setTextViewText(R.id.user,title);
            remoteView.setTextViewText(R.id.text,body);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.not_icon_1)
                .setContentTitle(name)
                .setContentText(content)
                .setAutoCancel(true)
                .setGroup("Group")
                .setSubText(name)
                .setContent(remoteViews)
                .setCustomBigContentView(remoteView)
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#EB464C"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        // Set vibration and sound for the notification
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(v, builder.build());

        // Vibrate the device
        vibrateDevice();
        summaryNotification();
    }

    private Bitmap getCircularBitmap(Bitmap bitmap)
    {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas canvas = new Canvas(circleBitmap);
        canvas.drawCircle(bitmap.getWidth()/2f, bitmap.getHeight()/2f, bitmap.getWidth()/2f, paint);
        return circleBitmap;
    }

    

    public void showNotification2(String title, String body,int b) {
        // Create a notification channel for Android 8.0 (Oreo) and above

        String channelId = "my_channel_id_2";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "My Channel 2",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id_2")
                .setSmallIcon(R.drawable.not_icon_1)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setGroup("Groups")
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#EB464C"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(body));

        // Set vibration and sound for the notification
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(b, builder.build());

        // Vibrate the device
        vibrateDevice();
        summaryNotification2();
    }

    public void summaryNotification() {
        // Create a notification channel for Android 8.0 (Oreo) and above

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String group = "";

        if (list.size() > 1)
        {
            group = "groups";
        }
        else
        {
            group = "group";
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.not_icon_1)
                .setAutoCancel(true)
                .setGroup("Group")
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#EB464C"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setSummaryText(list.size() + " " + group ));

        // Set vibration and sound for the notification
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(-1, builder.build());

        // Vibrate the device
        //vibrateDevice();
    }

    public void summaryNotification2() {
        // Create a notification channel for Android 8.0 (Oreo) and above

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id_2")
                .setSmallIcon(R.drawable.not_icon_1)
                .setAutoCancel(true)
                .setGroup("Groups")
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#EB464C"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Set vibration and sound for the notification
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(-2, builder.build());

        // Vibrate the device
        //vibrateDevice();
    }

    private void vibrateDevice() {
        Vibrator vibrator = this.getVibratorInstance();
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                long[] timing = {0,150,100,150,100};
                VibrationEffect vibrationEffect = VibrationEffect.
                        createWaveform(timing, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(vibrationEffect);
            }
            else
            {
                vibrator.vibrate(150);
            }

        }
    }

    private Vibrator getVibratorInstance()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            VibratorManager vbManager = (VibratorManager)
                    this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (vbManager != null)
            {
                return vbManager.getDefaultVibrator();
            }
        }
        else
        {
            Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null)
            {
                return vibrator;
            }
        }
        return null;
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
