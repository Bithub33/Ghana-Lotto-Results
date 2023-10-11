package com.maxstudio.lotto.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.maxstudio.lotto.Activities.ChatActivity;
import com.maxstudio.lotto.Utils.DiffUtilCallBack;
import com.maxstudio.lotto.Models.Messages;
import com.maxstudio.lotto.R;
import com.maxstudio.lotto.Interfaces.RecyclerViewInterface;
import com.maxstudio.lotto.Activities.ZoomActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
   private final List<Messages> userMessageList;
   private final List<Messages> userMessageList2 = new ArrayList<>();
   public static final ArrayList<String> del_list = new ArrayList<>();
   public static final ArrayList<String> image_del_list = new ArrayList<>();
   public static final ArrayList<Integer> pos_list = new ArrayList<>();
   private HashMap<Integer, Integer> clickCountMap = new HashMap<>();
   public static final ArrayList<String> copy_list = new ArrayList<>();
   private String[] copyString;
   private FirebaseAuth mAuth;
   public String user,remove,message;
   private static int  num = 0;
   private RelativeLayout layout;
   private RelativeLayout layout2;
   private RelativeLayout copy;
   private RelativeLayout delete;
   private final TextView count;
   private static final int VIEW_TYPE_FIRST = 0;
   private static final int VIEW_TYPE_SECOND = 1;
   private StorageReference mStorage;
   private DatabaseReference usersRef,groupRef,userRef;
   private final boolean isLongPressed = false;
   private final boolean isBeingClicked = true;
   private boolean isLongClicked = false;
   private boolean isImage= false;
   private static boolean isCopy = false;
    private static boolean isDel = false;
   private boolean istext= false;
   public boolean isVisible= false;
   private SharedPreferences prefs;
   private MediaPlayer mp_send,mp_rec;
   private View.OnLongClickListener onLongClickListener;
   private int Counter = 0;
   private int ct = 0;
   private final RecyclerViewInterface recyclerViewInterface;


    public MessageAdapter(List<Messages> userMessageList,
                          RecyclerViewInterface recyclerViewInterface, RelativeLayout layout,
                          RelativeLayout layout2, RelativeLayout copy, RelativeLayout delete,
                          TextView count,MediaPlayer mp_send,MediaPlayer mp_rec)
   {
       this.userMessageList = userMessageList;
       this.layout = layout;
       this.layout2 = layout2;
       this.copy = copy;
       this.delete = delete;
       this.count = count;
       this.recyclerViewInterface = recyclerViewInterface;
       this.mp_send = mp_send;
       this.mp_rec = mp_rec;
   }


    public static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMsgText, receiverMsgText, receiverMsgTime, senderMsgTime,
                receiverMsgDate, senderMsgDate, senderName, msgDelivered,copyText,delText,copyText2,delText2;
        public ShapeableImageView senderMsgImage, receiverMsgImage;
        public CircleImageView senderImage;
        public LinearLayout sendLay, recLay;
        private SharedPreferences prefs;

        MediaPlayer mp_send,mp_rec;
        public RelativeLayout MsgLay,relativeLayout,relativeLayout2,relativeLayout3,relativeLayout4;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            MsgLay = itemView.findViewById(R.id.msg_lay);
            senderMsgText = itemView.findViewById(R.id.sender_message);
            receiverMsgText = itemView.findViewById(R.id.custom_receiver_dialog);
            senderMsgImage = itemView.findViewById(R.id.custom_image_left);
            receiverMsgImage = itemView.findViewById(R.id.custom_image_right);
            senderMsgTime = itemView.findViewById(R.id.sender_time);
            receiverMsgTime = itemView.findViewById(R.id.receiver_time);
            senderMsgDate = itemView.findViewById(R.id.sender_date);
            receiverMsgDate = itemView.findViewById(R.id.receiver_date);
            msgDelivered = itemView.findViewById(R.id.msg_delivered);

            copyText = itemView.findViewById(R.id.copy);
            delText = itemView.findViewById(R.id.delete);
            copyText2 = itemView.findViewById(R.id.copy2);
            delText2 = itemView.findViewById(R.id.delete2);

            senderImage = itemView.findViewById(R.id.sender_image);
            sendLay = itemView.findViewById(R.id.send_lay);
            recLay = itemView.findViewById(R.id.rec_lay);
            senderName = itemView.findViewById(R.id.sender_name);
            relativeLayout = itemView.findViewById(R.id.copy_del_lay);
            relativeLayout2 = itemView.findViewById(R.id.copy_del_lay2);
            relativeLayout3 = itemView.findViewById(R.id.rec_pad);
            relativeLayout4 = itemView.findViewById(R.id.send_pad);
            

            if ((isCopy || ChatActivity.back) || isDel)
            {
                num = 0;
                //isCopy = false;
                //isDel = false;
                image_del_list.clear();
                pos_list.clear();
                copy_list.clear();
                del_list.clear();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MsgLay.setForeground(null);

                    isCopy = false;
                    isDel = false;
                }

            }


        }
    }

    public class MessageViewHolder2 extends RecyclerView.ViewHolder
    {

        RelativeLayout layout;
        public MessageViewHolder2(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.msg_del);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        if (viewType == VIEW_TYPE_FIRST)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_message_layout, parent, false);
            return new MessageViewHolder(view);
        }
        else
        {
            View views = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_layout, parent, false);
            return new MessageViewHolder2(views);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (getItemViewType(position) == VIEW_TYPE_FIRST)
        {
            String messageSenderId = mAuth.getCurrentUser().getUid();
            Messages messages = userMessageList.get(position-1);

            String fromUserId = messages.getFrom();
            String GroupId = messages.getGroupId();
            final String fromMessageType = messages.getType();

            usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(messageSenderId);

            groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId);

            userRef.keepSynced(true);
            usersRef.keepSynced(true);
            groupRef.keepSynced(true);

            MessageViewHolder messageViewHolder = (MessageViewHolder) holder;

            messageViewHolder.receiverMsgText.setVisibility(View.GONE);
            messageViewHolder.senderMsgText.setVisibility(View.GONE);
            messageViewHolder.senderMsgImage.setVisibility(View.GONE);
            messageViewHolder.receiverMsgImage.setVisibility(View.GONE);
            messageViewHolder.receiverMsgTime.setVisibility(View.GONE);
            messageViewHolder.senderMsgTime.setVisibility(View.GONE);
            messageViewHolder.receiverMsgDate.setVisibility(View.GONE);
            messageViewHolder.senderMsgDate.setVisibility(View.GONE);
            messageViewHolder.msgDelivered.setVisibility(View.GONE);

            messageViewHolder.sendLay.setVisibility(View.GONE);
            messageViewHolder.recLay.setVisibility(View.GONE);
            //holder.sendLay.setVisibility(View.GONE);


            ColorDrawable colorDrawable = new ColorDrawable(R.drawable.foreground);

            ColorDrawable colorDrawables = new ColorDrawable(R.drawable.foreground_pressed);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog);

                    builder.setTitle("Delete Message");
                    builder.setMessage("Confirm action to delete message");
                    //builder.setIcon(R.drawable.ic_baseline_error_24);
                    builder.setCancelable(false);

                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //holder.relativeLayout.setVisibility(View.GONE);

                            dialog.cancel();
                        }
                    });

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            for (String del : del_list)
                            {
                                groupRef.child("Messages").child(del).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    del_list.remove(del);
                                                    num = num - 1;
                                                    count.setText(String.valueOf(num));

                                                }

                                            }
                                        });
                            }

                            if (recyclerViewInterface != null)
                            {
                                messageViewHolder.relativeLayout.setVisibility(View.GONE);
                                for (int pos : pos_list)
                                {
                                    if (pos != RecyclerView.NO_POSITION)
                                    {
                                        recyclerViewInterface.delete(pos);
                                    }

                                }

                            }

                            if (isImage)
                            {
                                for (String msg : image_del_list)
                                {
                                    if (msg != null)
                                    {
                                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                        StorageReference storageReference = firebaseStorage.
                                                getReferenceFromUrl(msg);
                                        storageReference.delete();
                                        //image_del_list.remove(msg);
                                    }

                                }
                            }

                            //num = 0;
                            layout2.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            isDel = true;
                            isLongClicked = false;

                            Toast.makeText(view.getContext(), "Message deleted", Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.show();

                }
            });

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    StringBuilder stringBuilder = new StringBuilder();
                    for (String text : copy_list) {
                        stringBuilder.append(text).append("\n"); // Use "\n" to separate each text

                    }
                    String allTexts = stringBuilder.toString().trim(); // Remove trailing "\n"

                    // Copy the concatenated text to the clipboard
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        ClipData clip = ClipData.newPlainText("Copied Text", allTexts);
                        clipboard.setPrimaryClip(clip);

                    }

                    /**for (int pos : pos_list)
                    {
                        if (pos != RecyclerView.NO_POSITION)
                        {

                        }
                    }**/

                    num = 0;
                    layout2.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    pos_list.clear();
                    isLongClicked = false;
                    isCopy = true;
                    notifyDataSetChanged();


                    Toast.makeText(view.getContext(), "Message copied", Toast.LENGTH_SHORT).show();


                }
            });

            if (fromMessageType.equals("text"))
            {

                if (fromUserId.equals(messageSenderId))
                {

                    messageViewHolder.sendLay.setVisibility(View.GONE);

                    messageViewHolder.recLay.setVisibility(View.VISIBLE);

                    messageViewHolder.receiverMsgText.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMsgTime.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMsgDate.setVisibility(View.VISIBLE);

                    //holder.receiverMsgText.setBackgroundResource(R.drawable.sender_messages_layout);
                    messageViewHolder.receiverMsgText.setText(messages.getMessage());
                    messageViewHolder.receiverMsgTime.setText(messages.getTime());
                    messageViewHolder.receiverMsgDate.setText(messages.getDate());

                    if (position == userMessageList.size())
                    {
                        String pos = messages.getMessageId();
                        groupRef.child("Messages").child(pos).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists() && snapshot.hasChild("status"))
                                {
                                    String del = snapshot.child("status").getValue().toString();
                                    if (del.equals("Del"))
                                    {

                                        if (ChatActivity.sent)
                                        {
                                            mp_send.start();
                                            ChatActivity.sent = false;
                                        }
                                        messageViewHolder.msgDelivered.setVisibility(View.VISIBLE);
                                        messageViewHolder.msgDelivered.setText(del);

                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    onLongClickListener = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                if (!isLongClicked)
                                {
                                    remove = messages.getMessageId();
                                    message = messages.getMessage();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();
                                    //Toast.makeText(v.getContext(), ""+pos, Toast.LENGTH_SHORT).show();

                                    layout.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                    del_list.clear();
                                    del_list.add(remove);
                                    copy_list.clear();
                                    copy_list.add(message);
                                    isLongClicked = true;
                                    istext = true;
                                    num = 0;
                                    num = num + 1;
                                    clickCountMap.put(pos, num);
                                    pos_list.clear();
                                    pos_list.add(pos);
                                    count.setVisibility(View.VISIBLE);
                                    count.setText(String.valueOf(num));
                                    messageViewHolder.MsgLay.setForeground(colorDrawables);


                                    if (!isLongClicked)
                                    {
                                        messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);
                                    }

                                    if (copy.getVisibility() == View.GONE)
                                    {
                                        copy.setVisibility(View.VISIBLE);
                                    }
                                }

                            }
                            else
                            {
                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Delete Message",
                                                "Copy Message"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                builder.setTitle("Choose an option");
                                //builder.setMessage("Confirm action to delete message");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                            builder.setTitle("Delete Message");
                                            builder.setMessage("Confirm action to delete message");
                                            //builder.setIcon(R.drawable.ic_baseline_error_24);
                                            builder.setCancelable(false);

                                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //holder.relativeLayout.setVisibility(View.GONE);

                                                    dialog.cancel();
                                                }
                                            });

                                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String remove = messages.getMessageId();

                                                    groupRef.child("Messages").child(remove).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful())
                                                                    {
                                                                        messageViewHolder.relativeLayout.setVisibility(View.GONE);

                                                                        if (recyclerViewInterface != null)
                                                                        {
                                                                            int pos = holder.getBindingAdapterPosition();

                                                                            if (pos != RecyclerView.NO_POSITION)
                                                                            {
                                                                                recyclerViewInterface.delete(pos);
                                                                            }
                                                                        }
                                                                    }

                                                                    //holder.sendLay.setVisibility(View.GONE);
                                                                    //notifyDataSetChanged();

                                                                }
                                                            });

                                                }
                                            });
                                            builder.show();

                                        }
                                        if (which == 1)
                                        {
                                            String text = messages.getMessage();

                                            ClipboardManager cm = (ClipboardManager)
                                                    v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clipData = ClipData.newPlainText(text,text);
                                            cm.setPrimaryClip(clipData);
                                        }

                                    }
                                });

                                builder.show();
                            }

                            return true;
                        }
                    };

                    messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);

                    messageViewHolder.MsgLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Counter = Counter + 1;
                            if (isLongClicked)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    messageViewHolder.MsgLay.setForeground(colorDrawable);

                                    String msgId = messages.getMessageId();
                                    String message = messages.getMessage();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();

                                    //message = messages.getMessage();

                                    if (pos_list.contains(pos)) {
                                        // Get the current click count for this position
                                        int clickCount = clickCountMap.get(pos);
                                        // Increment the click count
                                        clickCount++;

                                        messageViewHolder.MsgLay.setForeground(null);
                                        del_list.remove(msgId);
                                        copy_list.remove(message);
                                        if(num > 0)
                                        {
                                            num = num - 1;
                                        }
                                        pos_list.remove(pos);

                                        count.setText(String.valueOf(num));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            clickCountMap.remove(pos, num);
                                        }

                                    } else {
                                        // If the position is not in the map, add it with a click count of 1
                                        clickCountMap.put(pos, 1);
                                        pos_list.add(pos);
                                        messageViewHolder.MsgLay.setForeground(colorDrawable);
                                        del_list.add(msgId);
                                        copy_list.add(message);
                                        num = num + 1;
                                        count.setText(String.valueOf(num));
                                    }


                                    if(count.getText().equals("0"))
                                    {
                                        layout.setVisibility(View.VISIBLE);
                                        layout2.setVisibility(View.GONE);
                                        count.setVisibility(View.GONE);
                                        isLongClicked = false;
                                        clickCountMap.clear();
                                        pos_list.clear();
                                        copy_list.clear();
                                        del_list.clear();
                                    }

                                }

                            }

                        }
                    });


                }
                else
                {

                    messageViewHolder.recLay.setVisibility(View.GONE);

                    messageViewHolder.sendLay.setVisibility(View.VISIBLE);

                    messageViewHolder.senderMsgTime.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMsgDate.setVisibility(View.VISIBLE);
                    messageViewHolder.senderImage.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMsgText.setVisibility(View.VISIBLE);

                    //holder.receiverMsgText.setBackgroundResource(R.drawable.receiver_msg_layout);
                    messageViewHolder.senderMsgText.setText(messages.getMessage());
                    messageViewHolder.senderMsgTime.setText(messages.getTime());
                    messageViewHolder.senderMsgDate.setText(messages.getDate());

                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                if (snapshot.hasChild("image"))
                                {
                                    String image = snapshot.child("image").getValue().toString();

                                    Picasso.get().load(image).fit().centerCrop().placeholder(R.drawable.profile_image)
                                            .into((messageViewHolder.senderImage));

                                }

                                if (snapshot.exists() && snapshot.hasChild("username"))
                                {
                                    String name = snapshot.child("username").getValue().toString();

                                    messageViewHolder.senderName.setText(name);

                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (position == userMessageList.size() &&
                            userMessageList.size() > ChatActivity.list2.size())
                    {
                        mp_rec.start();
                        userRef.child("Groups").child(GroupId).setValue("");
                    }

                    onLongClickListener = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                if (!isLongClicked)
                                {
                                    remove = messages.getMessageId();
                                    message = messages.getMessage();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();

                                    layout.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                    del_list.clear();
                                    del_list.add(remove);
                                    copy_list.clear();
                                    copy_list.add(message);
                                    pos_list.clear();
                                    pos_list.add(pos);
                                    isLongClicked = true;
                                    istext = true;
                                    num = 0;
                                    num = num + 1;
                                    clickCountMap.put(pos, num);
                                    count.setVisibility(View.VISIBLE);
                                    count.setText(String.valueOf(num));
                                    messageViewHolder.MsgLay.setForeground(colorDrawables);

                                    if (!isLongClicked)
                                    {
                                        messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);
                                    }

                                    if (copy.getVisibility() == View.GONE)
                                    {
                                        copy.setVisibility(View.VISIBLE);
                                    }
                                }


                            }
                            else
                            {
                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Delete Message",
                                                "Copy Message"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                builder.setTitle("Choose an option");
                                //builder.setMessage("Confirm action to delete message");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                            builder.setTitle("Delete Message");
                                            builder.setMessage("Confirm action to delete message");
                                            //builder.setIcon(R.drawable.ic_baseline_error_24);
                                            builder.setCancelable(false);

                                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //holder.relativeLayout.setVisibility(View.GONE);

                                                    dialog.cancel();
                                                }
                                            });

                                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String remove = messages.getMessageId();

                                                    groupRef.child("Messages").child(remove).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful())
                                                                    {
                                                                        messageViewHolder.relativeLayout.setVisibility(View.GONE);

                                                                        if (recyclerViewInterface != null)
                                                                        {
                                                                            int pos = holder.getBindingAdapterPosition();

                                                                            if (pos != RecyclerView.NO_POSITION)
                                                                            {
                                                                                recyclerViewInterface.delete(pos);
                                                                            }
                                                                        }
                                                                    }

                                                                    //holder.sendLay.setVisibility(View.GONE);
                                                                    //notifyDataSetChanged();

                                                                }
                                                            });

                                                }
                                            });
                                            builder.show();

                                        }
                                        if (which == 1)
                                        {
                                            String text = messages.getMessage();

                                            ClipboardManager cm = (ClipboardManager)
                                                    v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clipData = ClipData.newPlainText(text,text);
                                            cm.setPrimaryClip(clipData);
                                        }

                                    }
                                });

                                builder.show();
                            }
                            return true;
                        }
                    };

                    messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);

                    messageViewHolder.MsgLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Counter = Counter + 1;
                            if (isLongClicked)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    messageViewHolder.MsgLay.setForeground(colorDrawable);

                                    String msgId = messages.getMessageId();
                                    String message = messages.getMessage();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();


                                    //message = messages.getMessage();

                                    if (pos_list.contains(pos)) {
                                        // Get the current click count for this position
                                        int clickCount = clickCountMap.get(pos);
                                        // Increment the click count
                                        clickCount++;

                                        messageViewHolder.MsgLay.setForeground(null);
                                        del_list.remove(msgId);
                                        copy_list.remove(message);
                                        if(num > 0)
                                        {
                                            num = num - 1;
                                        }
                                        pos_list.remove(pos);
                                        count.setText(String.valueOf(num));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            clickCountMap.remove(pos, num);
                                        }

                                    } else {
                                        // If the position is not in the map, add it with a click count of 1

                                        clickCountMap.put(pos, 1);
                                        messageViewHolder.MsgLay.setForeground(colorDrawable);
                                        del_list.add(msgId);
                                        copy_list.add(message);
                                        pos_list.add(pos);
                                        num = num + 1;
                                        count.setText(String.valueOf(num));
                                    }

                                    if(count.getText().equals("0"))
                                    {
                                        layout.setVisibility(View.VISIBLE);
                                        layout2.setVisibility(View.GONE);
                                        count.setVisibility(View.GONE);
                                        clickCountMap.clear();
                                        pos_list.clear();
                                        copy_list.clear();
                                        del_list.clear();
                                        isLongClicked = false;
                                    }

                                }

                            }

                        }
                    });

                }

            }

            if (fromMessageType.equals("image")) {

                messageViewHolder.relativeLayout3.setPadding(10,10,10,10);
                messageViewHolder.relativeLayout4.setPadding(10,10,10,10);

                if (fromUserId.equals(messageSenderId)) {

                    messageViewHolder.sendLay.setVisibility(View.GONE);

                    messageViewHolder.recLay.setVisibility(View.VISIBLE);

                    messageViewHolder.receiverMsgImage.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMsgTime.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMsgDate.setVisibility(View.VISIBLE);

                    //holder.receiverMsgText.setBackgroundResource(R.drawable.sender_messages_layout);
                    String img = messages.getMessage();
                    //Picasso.get().setIndicatorsEnabled(true);

                    Picasso.get().load(img).fit().centerCrop()
                            .into((messageViewHolder.receiverMsgImage));

                    messageViewHolder.receiverMsgTime.setText(messages.getTime());
                    messageViewHolder.receiverMsgDate.setText(messages.getDate());

                    onLongClickListener = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                if (!isLongClicked)
                                {
                                    remove = messages.getMessageId();
                                    message = messages.getMessage();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();

                                    layout.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                    del_list.clear();
                                    del_list.add(remove);
                                    image_del_list.clear();
                                    image_del_list.add(message);
                                    pos_list.clear();
                                    pos_list.add(pos);
                                    isLongClicked = true;
                                    isImage = true;
                                    num = 0;
                                    num = num + 1;
                                    clickCountMap.put(pos, num);
                                    count.setVisibility(View.VISIBLE);
                                    count.setText(String.valueOf(num));
                                    messageViewHolder.MsgLay.setForeground(colorDrawables);

                                    if (!isLongClicked)
                                    {
                                        messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);
                                    }

                                    copy.setVisibility(View.GONE);
                                }

                            }
                            else
                            {
                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Delete Message",
                                                "Copy Message"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                builder.setTitle("Choose an option");
                                //builder.setMessage("Confirm action to delete message");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                            builder.setTitle("Delete Message");
                                            builder.setMessage("Confirm action to delete message");
                                            //builder.setIcon(R.drawable.ic_baseline_error_24);
                                            builder.setCancelable(false);

                                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //holder.relativeLayout.setVisibility(View.GONE);

                                                    dialog.cancel();
                                                }
                                            });

                                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String remove = messages.getMessageId();

                                                    groupRef.child("Messages").child(remove).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful())
                                                                    {
                                                                        messageViewHolder.relativeLayout.setVisibility(View.GONE);

                                                                        if (recyclerViewInterface != null)
                                                                        {
                                                                            int pos = holder.getBindingAdapterPosition();

                                                                            if (pos != RecyclerView.NO_POSITION)
                                                                            {
                                                                                recyclerViewInterface.delete(pos);
                                                                            }
                                                                        }
                                                                    }

                                                                    //holder.sendLay.setVisibility(View.GONE);
                                                                    //notifyDataSetChanged();

                                                                }
                                                            });

                                                }
                                            });
                                            builder.show();

                                        }
                                        if (which == 1)
                                        {
                                            String text = messages.getMessage();

                                            ClipboardManager cm = (ClipboardManager)
                                                    v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clipData = ClipData.newPlainText(text,text);
                                            cm.setPrimaryClip(clipData);
                                        }

                                    }
                                });

                                builder.show();
                            }

                            return true;
                        }
                    };

                    messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);

                    messageViewHolder.MsgLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Counter = Counter + 1;
                            if (isLongClicked)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    messageViewHolder.MsgLay.setForeground(colorDrawable);

                                    String msgId = messages.getMessageId();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();
                                    String msg = messages.getMessage();

                                    copy.setVisibility(View.GONE);

                                    if (pos_list.contains(pos)) {
                                        // Get the current click count for this position
                                        int clickCount = clickCountMap.get(pos);
                                        // Increment the click count
                                        clickCount++;

                                        messageViewHolder.MsgLay.setForeground(null);
                                        del_list.remove(msgId);
                                        pos_list.remove(pos);
                                        image_del_list.remove(msg);
                                        if(num > 0)
                                        {
                                            num = num - 1;
                                        }
                                        isImage = false;

                                        count.setText(String.valueOf(num));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            clickCountMap.remove(pos, num);
                                        }

                                        if (image_del_list.isEmpty() && istext)
                                        {
                                            copy.setVisibility(View.VISIBLE);
                                        }

                                    } else {
                                        // If the position is not in the map, add it with a click count of 1
                                        clickCountMap.put(pos, 1);
                                        messageViewHolder.MsgLay.setForeground(colorDrawable);
                                        del_list.add(msgId);
                                        pos_list.add(pos);
                                        isImage = true;
                                        image_del_list.add(msg);
                                        num = num + 1;
                                        count.setText(String.valueOf(num));
                                    }


                                    if(count.getText().equals("0"))
                                    {
                                        layout.setVisibility(View.VISIBLE);
                                        layout2.setVisibility(View.GONE);
                                        count.setVisibility(View.GONE);
                                        del_list.clear();
                                        pos_list.clear();
                                        image_del_list.clear();
                                        isLongClicked = false;
                                        isImage = false;
                                        //clickCountMap.clear();
                                    }

                                }

                            }
                            else
                            {
                                Intent RequestIntent = new Intent(view.getContext(), ZoomActivity.class);
                                RequestIntent.putExtra("imageId", img);
                                RequestIntent.putExtra("title", messages.getYou());
                                RequestIntent.putExtra("date", messages.getDate());
                                RequestIntent.putExtra("time", messages.getTime());
                                view.getContext().startActivity(RequestIntent);
                            }

                        }
                    });


                    if (position == userMessageList.size()) {
                        String pos = messages.getMessageId();
                        groupRef.child("Messages").child(pos).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists() && snapshot.hasChild("status")) {
                                    String del = snapshot.child("status").getValue().toString();

                                    if (del.equals("Del"))
                                    {
                                        if (ChatActivity.sent)
                                        {
                                            mp_send.start();
                                            ChatActivity.sent = false;
                                        }
                                        messageViewHolder.msgDelivered.setVisibility(View.VISIBLE);
                                        messageViewHolder.msgDelivered.setText(del);

                                    }

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }


                }
                else {


                    messageViewHolder.recLay.setVisibility(View.GONE);

                    messageViewHolder.sendLay.setVisibility(View.VISIBLE);

                    messageViewHolder.senderMsgTime.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMsgDate.setVisibility(View.VISIBLE);
                    messageViewHolder.senderImage.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMsgImage.setVisibility(View.VISIBLE);

                    //holder.receiverMsgText.setBackgroundResource(R.drawable.receiver_msg_layout);
                    String img = messages.getMessage();
                    //Picasso.get().setIndicatorsEnabled(true);

                    Picasso.get().load(img).fit().centerCrop()
                            .into((messageViewHolder.senderMsgImage));
                    /**Picasso.get().load(img).fetch(new Callback() {
                    @Override
                    public void onSuccess() {

                    Picasso.get().load(img).into((holder.senderMsgImage));
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                    });**/

                    messageViewHolder.senderMsgTime.setText(messages.getTime());

                    messageViewHolder.senderMsgDate.setText(messages.getDate());

                    onLongClickListener = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                if (!isLongClicked)
                                {
                                    remove = messages.getMessageId();
                                    message = messages.getMessage();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();

                                    layout.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                    del_list.clear();
                                    del_list.add(remove);
                                    image_del_list.clear();
                                    image_del_list.add(message);
                                    pos_list.clear();
                                    pos_list.add(pos);
                                    isLongClicked = true;
                                    isImage = true;
                                    num = 0;
                                    num = num + 1;
                                    clickCountMap.put(pos, num);
                                    count.setVisibility(View.VISIBLE);
                                    count.setText(String.valueOf(num));
                                    messageViewHolder.MsgLay.setForeground(colorDrawables);

                                    if (!isLongClicked)
                                    {
                                        messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);
                                    }

                                    copy.setVisibility(View.GONE);
                                }

                            }
                            else
                            {
                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Delete Message",
                                                "Copy Message"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                builder.setTitle("Choose an option");
                                //builder.setMessage("Confirm action to delete message");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialog);

                                            builder.setTitle("Delete Message");
                                            builder.setMessage("Confirm action to delete message");
                                            //builder.setIcon(R.drawable.ic_baseline_error_24);
                                            builder.setCancelable(false);

                                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //holder.relativeLayout.setVisibility(View.GONE);

                                                    dialog.cancel();
                                                }
                                            });

                                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String remove = messages.getMessageId();

                                                    groupRef.child("Messages").child(remove).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful())
                                                                    {
                                                                        messageViewHolder.relativeLayout.setVisibility(View.GONE);

                                                                        if (recyclerViewInterface != null)
                                                                        {
                                                                            int pos = holder.getBindingAdapterPosition();

                                                                            if (pos != RecyclerView.NO_POSITION)
                                                                            {
                                                                                recyclerViewInterface.delete(pos);
                                                                            }
                                                                        }
                                                                    }

                                                                    //holder.sendLay.setVisibility(View.GONE);
                                                                    //notifyDataSetChanged();

                                                                }
                                                            });

                                                }
                                            });
                                            builder.show();

                                        }
                                        if (which == 1)
                                        {
                                            String text = messages.getMessage();

                                            ClipboardManager cm = (ClipboardManager)
                                                    v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clipData = ClipData.newPlainText(text,text);
                                            cm.setPrimaryClip(clipData);
                                        }

                                    }
                                });

                                builder.show();
                            }

                            return true;
                        }
                    };

                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                if (snapshot.hasChild("image")) {
                                    String image = snapshot.child("image").getValue().toString();

                                    Picasso.get().load(image).fetch(new Callback() {
                                        @Override
                                        public void onSuccess() {

                                            Picasso.get().load(image).fit().centerCrop().placeholder(R.drawable.profile_image)
                                                    .into((messageViewHolder.senderImage));
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });

                                }
                            }

                            if (snapshot.exists() && snapshot.hasChild("username")) {
                                String name = snapshot.child("username").getValue().toString();

                                messageViewHolder.senderName.setText(name);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    messageViewHolder.MsgLay.setOnLongClickListener(onLongClickListener);

                    messageViewHolder.MsgLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Counter = Counter + 1;
                            if (isLongClicked)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    messageViewHolder.MsgLay.setForeground(colorDrawable);

                                    String msgId = messages.getMessageId();
                                    Integer pos = messageViewHolder.getBindingAdapterPosition();
                                    String msg = messages.getMessage();

                                    copy.setVisibility(View.GONE);

                                    if (pos_list.contains(pos)) {
                                        // Get the current click count for this position
                                        int clickCount = clickCountMap.get(pos);
                                        // Increment the click count
                                        clickCount++;

                                        messageViewHolder.MsgLay.setForeground(null);
                                        del_list.remove(msgId);
                                        pos_list.remove(pos);
                                        image_del_list.remove(msg);
                                        if(num > 0)
                                        {
                                            num = num - 1;
                                        }
                                        isImage = false;

                                        count.setText(String.valueOf(num));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            clickCountMap.remove(pos, num);
                                        }

                                        if (image_del_list.isEmpty() && istext)
                                        {
                                            copy.setVisibility(View.VISIBLE);
                                        }

                                    } else {
                                        // If the position is not in the map, add it with a click count of 1
                                        clickCountMap.put(pos, 1);
                                        messageViewHolder.MsgLay.setForeground(colorDrawable);
                                        del_list.add(msgId);
                                        image_del_list.add(msg);
                                        pos_list.add(pos);
                                        isImage = true;
                                        num = num + 1;
                                        count.setText(String.valueOf(num));
                                    }


                                    if(count.getText().equals("0"))
                                    {
                                        layout.setVisibility(View.VISIBLE);
                                        layout2.setVisibility(View.GONE);
                                        count.setVisibility(View.GONE);
                                        pos_list.clear();
                                        isLongClicked = false;
                                        isImage = false;
                                        //clickCountMap.clear();
                                        del_list.clear();
                                        image_del_list.clear();
                                    }

                                }

                            }
                            else
                            {
                                Intent RequestIntent = new Intent(view.getContext(), ZoomActivity.class);
                                RequestIntent.putExtra("imageId", img);
                                RequestIntent.putExtra("title", messages.getUsername());
                                RequestIntent.putExtra("date", messages.getDate());
                                RequestIntent.putExtra("time", messages.getTime());
                                view.getContext().startActivity(RequestIntent);
                            }

                        }
                    });

                    if (position == userMessageList.size() &&
                            userMessageList.size() > ChatActivity.list2.size())
                    {
                        mp_rec.start();
                        userRef.child("Groups").child(GroupId).setValue("");
                    }

                }
            }

            if(count.getText().equals("0"))
            {
                layout.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                count.setVisibility(View.GONE);
                del_list.clear();
                pos_list.clear();
                copy_list.clear();
                image_del_list.clear();
                isLongClicked = false;
                isImage = false;
                istext = false;
                //clickCountMap.clear();

            }



        }
        else
        {
            MessageViewHolder2 messageViewHolder2 = (MessageViewHolder2) holder;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    messageViewHolder2.layout.setVisibility(View.VISIBLE);

                }
            },2000);

        }
    }

    @Override
    public int getItemCount() {

       return userMessageList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
        {
            isVisible = true;
            return VIEW_TYPE_SECOND;
        }
        else
        {

            return VIEW_TYPE_FIRST;
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
