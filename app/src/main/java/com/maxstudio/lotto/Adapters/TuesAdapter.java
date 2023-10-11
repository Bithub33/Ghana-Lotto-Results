package com.maxstudio.lotto.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Fragments.TuesdayHistoryFragment;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.R;

import java.util.ArrayList;
import java.util.List;

public class
TuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView ResultsRecyclerList;
    private String Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Mach1,Mach2,Mach3,Mach4,Mach5, Date;

    private DatabaseReference LottoRef;
    private ProgressDialog loadingBar;
    private ProgressBar indicator,indicator2;

    private View View;

    private AdView mAdView;
    private boolean isSubs;
    private FrameLayout adContainerView;

    public static String day;
    private Context context;

    private boolean isDestroyed,isScrolled,isLoading;
    private AdLoader adLoader4;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;
    private List<DailyResultsPicker> combinedDataList;

    FirebaseRecyclerAdapter<DailyResultsPicker, RecyclerView.ViewHolder> adapter;

    public TuesAdapter(Context context, List<DailyResultsPicker> combinedDataList, boolean isScrolled,
                       ProgressBar indicator,DatabaseReference LottoRef,
                       String day,boolean isDestroyed,boolean isLoading,ProgressBar indicator2)
    {
        this.context = context;
        this.combinedDataList = combinedDataList;
        this.isScrolled = isScrolled;
        this.indicator = indicator;
        this.LottoRef = LottoRef;
        this.day = day;
        this.isDestroyed = isDestroyed;
        this.isLoading = isLoading;
        this.indicator2 = indicator2;
    }

    public void addAll(List<DailyResultsPicker> list)
    {
        combinedDataList.addAll(list);
        notifyItemRangeChanged(combinedDataList.size(),list.size());
    }

    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView Lotto1, Lotto2, Lotto3, Lotto4, Lotto5, Mach1, Mach2, Mach3, Mach4, Mach5, Date;
        LinearLayout layout;
        ImageView LottoImage;

        public DailyResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            Lotto1 = itemView.findViewById(R.id.lotto_1);
            Lotto2 = itemView.findViewById(R.id.lotto_2);
            Lotto3 = itemView.findViewById(R.id.lotto_3);
            Lotto4 = itemView.findViewById(R.id.lotto_4);
            Lotto5 = itemView.findViewById(R.id.lotto_5);
            Mach1 = itemView.findViewById(R.id.mach_1);
            Mach2 = itemView.findViewById(R.id.mach_2);
            Mach3 = itemView.findViewById(R.id.mach_3);
            Mach4 = itemView.findViewById(R.id.mach_4);
            Mach5 = itemView.findViewById(R.id.mach_5);

            Typeface typeface = Typeface.create("sans-serif",Typeface.BOLD);

            Lotto1.setTypeface(typeface);
            Lotto2.setTypeface(typeface);
            Lotto3.setTypeface(typeface);
            Lotto4.setTypeface(typeface);
            Lotto5.setTypeface(typeface);
            Mach1.setTypeface(typeface);
            Mach2.setTypeface(typeface);
            Mach3.setTypeface(typeface);
            Mach4.setTypeface(typeface);
            Mach5.setTypeface(typeface);


            if (day.equals("Tuesday"))
            {
                LottoImage = itemView.findViewById(R.id.tuesday_logo);
            }
            if (day.equals("Wednesday"))
            {
                LottoImage = itemView.findViewById(R.id.midweek_logo);
            }if (day.equals("Thursday"))
            {
                LottoImage = itemView.findViewById(R.id.thursday_logo);
            }if (day.equals("Friday"))
            {
                LottoImage = itemView.findViewById(R.id.friday_logo);
            }if (day.equals("Saturday"))
            {
                LottoImage = itemView.findViewById(R.id.saturday_logo);
            }if (day.equals("Sunday"))
            {
                LottoImage = itemView.findViewById(R.id.sunday_logo);
            }if (day.equals("Monday"))
            {
                LottoImage = itemView.findViewById(R.id.monday_logo);
            }

            Date = itemView.findViewById(R.id.result_date);

            layout = itemView.findViewById(R.id.results_layout2);
            //LottoImage = itemView.findViewById(R.id.lotto_image);
        }
    }

    public class DailyResultsViewHolder2 extends RecyclerView.ViewHolder {

        TemplateView template2;
        boolean isSubs;
        String[] adUnitIds2,adUnitIds,adUnitIds3;
        int currentAdIndex2;

        Native natives;
        public DailyResultsViewHolder2(@NonNull View itemView) {
            super(itemView);

            adUnitIds = new String[]{
                    itemView.getContext().getString(R.string.Native_add_id),
                    itemView.getContext().getString(R.string.Native_add_id_1_2),
                    itemView.getContext().getString(R.string.Native_add_id_1_3),
                    itemView.getContext().getString(R.string.Native_add_id_1_4)};

            adUnitIds2 = new String[]{
                    itemView.getContext().getString(R.string.Native_add_medium_id),
                    itemView.getContext().getString(R.string.Native_add_medium_id_2),
                    itemView.getContext().getString(R.string.Native_add_medium_id_3),
                    itemView.getContext().getString(R.string.Native_add_medium_id_4)};

            adUnitIds3 = new String[]{
                    itemView.getContext().getString(R.string.Native_add_small_id),
                    itemView.getContext().getString(R.string.Native_add_small_id_2),
                    itemView.getContext().getString(R.string.Native_add_small_id_3),
                    itemView.getContext().getString(R.string.Native_add_small_id_4)};

            TemplateView templateView = itemView.findViewById(R.id.my_template_1);
            TemplateView templateView2 = itemView.findViewById(R.id.my_template_2);
            TemplateView templateView3 = itemView.findViewById(R.id.my_template_3);

            natives = new Native(context, templateView,templateView2,
                    templateView3, adUnitIds,adUnitIds2,adUnitIds3,isScrolled,isDestroyed);


            SharedPreferences prefs = itemView.getContext().getSharedPreferences("com.maxstudio.lotto",
                    Context.MODE_PRIVATE);

            isSubs = prefs.getBoolean("service_status", false);

        }

        public void NativeAdMedium()
        {
            if (!isSubs && context != null)
            {
                natives.startLoadingAdsMedium();
            }
        }

        public void NativeAdMediumDestroy()
        {
            if (!isSubs && context != null)
            {
                natives.stopLoadingAdsMedium();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_FIRST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_display2, parent, false);
            return new DailyResultsViewHolder(view);
        } else if (viewType == VIEW_TYPE_SECOND){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_add_layout, parent, false);
            return new DailyResultsViewHolder2(view);
        }
        else {
            throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_FIRST) {
            //Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();

            DailyResultsViewHolder dailyResultsViewHolder = (DailyResultsViewHolder) holder;

            // Bind data and UI for the first view
            int positions = position - (position > 0 ? 1 : 0) - (position > 5 ? 1 : 0);
            // ...
            DailyResultsPicker dailyResultsPicker = combinedDataList.get(positions);
            String WeekDays = dailyResultsPicker.getKey();

            assert WeekDays != null;
            LottoRef.child(WeekDays).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        dailyResultsViewHolder.LottoImage.setVisibility(android.view.View.VISIBLE);

                        dailyResultsViewHolder.layout.setVisibility(android.view.View.VISIBLE);
                        //isLoading = false;

                        indicator.setVisibility(android.view.View.GONE);

                        Lotto1 = dataSnapshot.child("lotto1").getValue().toString();
                        Lotto2 = dataSnapshot.child("lotto2").getValue().toString();
                        Lotto3 = dataSnapshot.child("lotto3").getValue().toString();
                        Lotto4 = dataSnapshot.child("lotto4").getValue().toString();
                        Lotto5 = dataSnapshot.child("lotto5").getValue().toString();
                        Mach1 = dataSnapshot.child("mach1").getValue().toString();
                        Mach2 = dataSnapshot.child("mach2").getValue().toString();
                        Mach3 = dataSnapshot.child("mach3").getValue().toString();
                        Mach4 = dataSnapshot.child("mach4").getValue().toString();
                        Mach5 = dataSnapshot.child("mach5").getValue().toString();

                        Date = dataSnapshot.child("date").getValue().toString();

                        dailyResultsViewHolder.Lotto1.setText(Lotto1);
                        dailyResultsViewHolder.Lotto2.setText(Lotto2);
                        dailyResultsViewHolder.Lotto3.setText(Lotto3);
                        dailyResultsViewHolder.Lotto4.setText(Lotto4);
                        dailyResultsViewHolder.Lotto5.setText(Lotto5);
                        dailyResultsViewHolder.Mach1.setText(Mach1);
                        dailyResultsViewHolder.Mach2.setText(Mach2);
                        dailyResultsViewHolder.Mach3.setText(Mach3);
                        dailyResultsViewHolder.Mach4.setText(Mach4);
                        dailyResultsViewHolder.Mach5.setText(Mach5);
                        dailyResultsViewHolder.Date.setText(Date);


                    }else
                    {
                        //loadingBar.dismiss();
                        indicator.setVisibility(android.view.View.GONE);
                        Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    //loadingBar.dismiss();
                    indicator.setVisibility(android.view.View.GONE);
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();

                }
            });

        }
        else if (getItemViewType(position) == VIEW_TYPE_SECOND)
        {
            //Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
            DailyResultsViewHolder2 dailyResultsViewHolder2 = (DailyResultsViewHolder2) holder;
            // Bind data and UI for the second view
            // ...
            dailyResultsViewHolder2.NativeAdMedium();

            if (isDestroyed)
            {
                dailyResultsViewHolder2.NativeAdMediumDestroy();
            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 5)
        {
            return VIEW_TYPE_SECOND;
        }
        else
        {
            return VIEW_TYPE_FIRST;
        }
    }

    @Override
    public int getItemCount() {

        //int nativeAdCount = Math.min((combinedDataList.size() + 2) / 3, 20 / 3);

        int co = combinedDataList.size();

        if (combinedDataList.size() >= 5) {
            co += 2;
        }
        // If the original item count is greater than or equal to 1, add 1 for the native ad at position 0
        else if (combinedDataList.size() >= 1) {
            co += 1;
        }

        return co; // + (combinedDataList.size() / 3);
    }
}
