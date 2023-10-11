package com.maxstudio.lotto.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.maxstudio.lotto.Ad.Native;
import com.maxstudio.lotto.Fragments.SuperFragment;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.R;

import java.util.List;

public class SupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private String Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Lotto6,Mach2,Mach3,Mach4,Mach5, Date,live;

    public static String day;
    private final List<DailyResultsPicker> combinedDataList;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;

    private static final int VIEW_TYPE_THIRD = 2;
    private DatabaseReference LottoRef,LottoRefs;
    private RecyclerView rec;
    private Context context;
    private boolean isDestroyed,isScrolled;
    private final String[] com;
    private boolean type;

    public SupAdapter(Context context, List<DailyResultsPicker> combinedDataList, DatabaseReference
            LottoRef, boolean isDestroyed, boolean isScrolled, String[] com,RecyclerView rec)
    {
        this.context = context;
        this.combinedDataList = combinedDataList;
        this.LottoRef = LottoRef;
        this.isDestroyed = isDestroyed;
        this.isScrolled = isScrolled;
        this.com = com;
        this.rec = rec;
    }

    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView Lotto1, Lotto2, Lotto3, Lotto4, Lotto5, day, Lotto6, Mach3,
                Mach4, Mach5, Date, live;
        LinearLayout layout;
        ImageView LottoImageMon,LottoImageTues,LottoImageWed,LottoImageThur,LottoImageFri,LottoImageSat
        ,LottoImageSun;

        public DailyResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            Lotto1 = itemView.findViewById(R.id.mon_lotto_1);
            Lotto2 = itemView.findViewById(R.id.mon_lotto_2);
            Lotto3 = itemView.findViewById(R.id.mon_lotto_3);
            Lotto4 = itemView.findViewById(R.id.mon_lotto_4);
            Lotto5 = itemView.findViewById(R.id.mon_lotto_5);
            Lotto6 = itemView.findViewById(R.id.mon_lotto_6);
            day = itemView.findViewById(R.id.day);

            Typeface typeface = Typeface.create("sans-serif",Typeface.BOLD);

            Lotto1.setTypeface(typeface);
            Lotto2.setTypeface(typeface);
            Lotto3.setTypeface(typeface);
            Lotto4.setTypeface(typeface);
            Lotto5.setTypeface(typeface);

            Date = itemView.findViewById(R.id.mon_date);

            layout = itemView.findViewById(R.id.mon_lay);
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


            SharedPreferences prefs = context.getSharedPreferences("com.maxstudio.lotto",
                    Context.MODE_PRIVATE);

            isSubs = prefs.getBoolean("service_status", false);

        }

        public void NativeAdMedium()
        {
            if (!isSubs && context != null)
            {
                natives.startLoadingAds();
            }
        }

        public void NativeAdSmall()
        {
            if (!isSubs && context != null)
            {
                natives.startLoadingAdsSmall();
            }
        }

        public void NativeAdMediumDestroy()
        {
            if (!isSubs && context != null)
            {
                natives.stopLoadingAds();
                natives.stopLoadingAdsSmall();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FIRST) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sup, parent, false);
            return new DailyResultsViewHolder(view);

        }
        else if (viewType == VIEW_TYPE_THIRD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
            return new DailyResultsViewHolder2(view);
        }
        else if (viewType == VIEW_TYPE_SECOND){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
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
            int positions = position - (position > 0 ? 1 : 0) - (position > 4 ? 1 : 0) - (position > 6 ? 1 : 0);

            if (SuperFragment.isRefreshed)
            {
                dailyResultsViewHolder.layout.setVisibility(View.GONE);
                rec.setVisibility(View.GONE);
            }

            String s = com[positions];
            LottoRef.child(s).child("Results")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.exists())
                            {
                                if (dataSnapshot.hasChild("lotto1"))
                                {

                                    dailyResultsViewHolder.layout.setVisibility(View.VISIBLE);
                                    SuperFragment.isRefreshed = false;

                                    Lotto1 = dataSnapshot.child("lotto1").getValue().toString();
                                    Lotto2 = dataSnapshot.child("lotto2").getValue().toString();
                                    Lotto3 = dataSnapshot.child("lotto3").getValue().toString();
                                    Lotto4 = dataSnapshot.child("lotto4").getValue().toString();
                                    Lotto5 = dataSnapshot.child("lotto5").getValue().toString();
                                    Lotto6 = dataSnapshot.child("lotto6").getValue().toString();

                                    Date = dataSnapshot.child("date").getValue().toString();

                                    dailyResultsViewHolder.Lotto1.setText(Lotto1);
                                    dailyResultsViewHolder.Lotto2.setText(Lotto2);
                                    dailyResultsViewHolder.Lotto3.setText(Lotto3);
                                    dailyResultsViewHolder.Lotto4.setText(Lotto4);
                                    dailyResultsViewHolder.Lotto5.setText(Lotto5);
                                    dailyResultsViewHolder.Lotto6.setText(Lotto6);

                                    dailyResultsViewHolder.Date.setText(Date);

                                    dailyResultsViewHolder.day.setText(s);

                                    if (s.equals("Friday"))
                                    {
                                        rec.requestLayout();
                                    }
                                    rec.setVisibility(View.VISIBLE);

                                    //notifyDataSetChanged();
                                    //Picasso.get().load(LottoPic).placeholder(R.drawable.ic_launcher_foreground).into(holder.LottoImage);
                                }


                            }else
                            {
                                //loadingBar.dismiss();
                                //indicator.setVisibility(android.view.View.GONE);
                                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                            }
                            //loadingBar.dismiss();
                            //indicator.setVisibility(android.view.View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            //loadingBar.dismiss();
                            //indicator.setVisibility(android.view.View.GONE);
                            Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();

                        }
                    });
            rec.requestLayout();


            //firstViewCount++;
        }
        else if (getItemViewType(position) == VIEW_TYPE_SECOND)
        {
            //Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
            DailyResultsViewHolder2 dailyResultsViewHolder2 = (DailyResultsViewHolder2) holder;
            // Bind data and UI for the second view
            // ...

            dailyResultsViewHolder2.NativeAdSmall();

            if (isDestroyed)
            {
                dailyResultsViewHolder2.NativeAdMediumDestroy();
            }

        }
        else if (getItemViewType(position) == VIEW_TYPE_THIRD)
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

        if (position == 1)
        {
            return VIEW_TYPE_SECOND;
        }
        else if (position == 5) {
            // Show the native ad after every 'adFrequency' number of data items
            return VIEW_TYPE_THIRD;
        }
        else
        {
            return VIEW_TYPE_FIRST;
        }

    }

    @Override
    public int getItemCount() {

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
