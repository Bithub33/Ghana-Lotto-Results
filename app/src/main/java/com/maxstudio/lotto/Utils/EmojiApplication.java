package com.maxstudio.lotto.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
//import com.vanniktech.emoji.EmojiManager;
//import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class EmojiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //EmojiManager.install(new GoogleEmojiProvider());
    }

}
