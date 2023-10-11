package com.maxstudio.lotto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.jsibbold.zoomage.ZoomageView;
import com.maxstudio.lotto.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ZoomActivity extends AppCompatActivity {

    private ZoomageView zoomageView;
    private Toolbar mToolbar;
    private TextView textView,textView2,textView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        String img = getIntent().getExtras().get("imageId").toString();
        String title = getIntent().getExtras().get("title").toString();
        String date = getIntent().getExtras().get("date").toString();
        String time = getIntent().getExtras().get("time").toString();

        getWindow().setStatusBarColor(Color.parseColor("#A81616"));

        zoomageView = findViewById(R.id.zoom);
        textView = findViewById(R.id.title);
        textView2 = findViewById(R.id.date);
        textView3 = findViewById(R.id.time);

        textView.setText(title);
        textView2.setText(date);
        textView3.setText(time);


        Picasso.get().load(img).fetch(new Callback() {
            @Override
            public void onSuccess() {

                Picasso.get().load(img).into((zoomageView));
            }

            @Override
            public void onError(Exception e) {

            }
        });

        mToolbar = findViewById(R.id.zoom_bar);
        setSupportActionBar(mToolbar);
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
}