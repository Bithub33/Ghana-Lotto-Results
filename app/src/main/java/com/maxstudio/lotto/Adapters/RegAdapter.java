package com.maxstudio.lotto.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
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
import com.maxstudio.lotto.Fragments.RegularFragment;
import com.maxstudio.lotto.Models.DailyResultsPicker;
import com.maxstudio.lotto.R;

import java.util.ArrayList;
import java.util.List;

public class RegAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private String Lotto1,Lotto2,Lotto3,Lotto4,Lotto5,Mach1,Mach2,Mach3,Mach4,Mach5, Date,live;

    public static String day;
    private List<DailyResultsPicker> combinedDataList;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_SECOND = 1;
    private static final int VIEW_TYPE_THIRD = 2;
    private static final int VIEW_TYPE_FOURTH = 3;
    private DatabaseReference DataRef,LottoRefs;
    private Context context;
    public static boolean ready;
    private RecyclerView rec;
    private boolean isDestroyed,isScrolled;

    public RegAdapter(Context context,List<DailyResultsPicker> combinedDataList,DatabaseReference
            DataRef,boolean isDestroyed,boolean isScrolled,RecyclerView rec)
    {
        this.context = context;
        this.combinedDataList = combinedDataList;
        this.DataRef = DataRef;
        this.isDestroyed = isDestroyed;
        this.isScrolled = isScrolled;
        this.rec = rec;

    }

    public static class DailyResultsViewHolder extends RecyclerView.ViewHolder {
        TextView Lotto1, Lotto2, Lotto3, Lotto4, Lotto5, Mach1, Mach2, Mach3,
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
            Mach1 = itemView.findViewById(R.id.mon_mach_1);
            Mach2 = itemView.findViewById(R.id.mon_mach_2);
            Mach3 = itemView.findViewById(R.id.mon_mach_3);
            Mach4 = itemView.findViewById(R.id.mon_mach_4);
            Mach5 = itemView.findViewById(R.id.mon_mach_5);

            LottoImageMon = itemView.findViewById(R.id.mon_logo);
            LottoImageTues = itemView.findViewById(R.id.tuesday_logo);
            LottoImageWed = itemView.findViewById(R.id.wednesday_logo);
            LottoImageThur = itemView.findViewById(R.id.thursday_logo);
            LottoImageFri = itemView.findViewById(R.id.friday_logo);
            LottoImageSat = itemView.findViewById(R.id.saturday_logo);
            LottoImageSun = itemView.findViewById(R.id.sunday_logo);

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

        public void NativeAdSmall()
        {
            if (!isSubs && context != null)
            {
                natives.startLoadingAds();
            }
        }

        public void NativeAdSmall2()
        {
            if (!isSubs && context != null)
            {
                natives.startLoadingAdsSmall();
            }
        }

        public void NativeAdDestroy()
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reg_layout, parent, false);
            return new DailyResultsViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_SECOND){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
            return new DailyResultsViewHolder2(view);
        }
        else if (viewType == VIEW_TYPE_THIRD){
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
            int positions = position - (position > 0 ? 1 : 0) -  (position > 7 ? 1 : 0);
            // ...
            DailyResultsPicker dailyResultsPicker = combinedDataList.get(positions);
            String WeekDays = dailyResultsPicker.getKey();

            if (RegularFragment.isRefreshed)
            {
                rec.setVisibility(View.GONE);
                dailyResultsViewHolder.layout.setVisibility(View.GONE);
                dailyResultsViewHolder.LottoImageMon.setVisibility(View.GONE);
                dailyResultsViewHolder.LottoImageTues.setVisibility(android.view.View.GONE);
                dailyResultsViewHolder.LottoImageWed.setVisibility(android.view.View.GONE);
                dailyResultsViewHolder.LottoImageThur.setVisibility(android.view.View.GONE);
                dailyResultsViewHolder.LottoImageFri.setVisibility(android.view.View.GONE);
                dailyResultsViewHolder.LottoImageSat.setVisibility(android.view.View.GONE);
                dailyResultsViewHolder.LottoImageSun.setVisibility(android.view.View.GONE);

            }

            assert WeekDays != null;
            DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        ready = true;
                        if (dataSnapshot.child("Days").child(WeekDays).child("Results").hasChild("lotto1"))
                        {
                            live = dataSnapshot.child("Days").child(WeekDays).child("Results")
                                    .child("live").getValue().toString();

                            RegularFragment.isRefreshed = false;

                            if (live.equals("yes"))
                            {
                                dailyResultsViewHolder.layout.setVisibility(View.GONE);
                            }else
                            {
                                dailyResultsViewHolder.layout.setVisibility(View.VISIBLE);

                            }

                            if ((WeekDays).equals("Monday"))
                            {
                                dailyResultsViewHolder.LottoImageMon.setVisibility(android.view.View.VISIBLE);
                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageMon.setVisibility(View.GONE);
                            }
                            if ((WeekDays).equals("NTuesday"))
                            {
                                dailyResultsViewHolder.LottoImageTues.setVisibility(android.view.View.VISIBLE);

                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageTues.setVisibility(View.GONE);
                            }
                            if ((WeekDays).equals("oWednesday"))
                            {
                                dailyResultsViewHolder.LottoImageWed.setVisibility(android.view.View.VISIBLE);
                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageWed.setVisibility(View.GONE);
                            }
                            if ((WeekDays).equals("pThursday"))
                            {
                                dailyResultsViewHolder.LottoImageThur.setVisibility(android.view.View.VISIBLE);
                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageThur.setVisibility(View.GONE);
                            }
                            if ((WeekDays).equals("qFriday"))
                            {
                                dailyResultsViewHolder.LottoImageFri.setVisibility(android.view.View.VISIBLE);
                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageFri.setVisibility(View.GONE);
                            }
                            if ((WeekDays).equals("rSaturday"))
                            {
                                dailyResultsViewHolder.LottoImageSat.setVisibility(android.view.View.VISIBLE);
                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageSat.setVisibility(View.GONE);
                            }


                            Lotto1 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("lotto1").getValue().toString();
                            Lotto2 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("lotto2").getValue().toString();
                            Lotto3 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("lotto3").getValue().toString();
                            Lotto4 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("lotto4").getValue().toString();
                            Lotto5 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("lotto5").getValue().toString();
                            Mach1 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("mach1").getValue().toString();
                            Mach2 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("mach2").getValue().toString();
                            Mach3 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("mach3").getValue().toString();
                            Mach4 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("mach4").getValue().toString();
                            Mach5 = dataSnapshot.child("Days").child(WeekDays).child("Results").child("mach5").getValue().toString();

                            Date = dataSnapshot.child("Days").child(WeekDays).child("Results").child("date").getValue().toString();

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


                        }

                        if (dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").hasChild("lotto1"))
                        {
                            live = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("live").getValue().toString();


                            if ((WeekDays).equals("Sunday"))
                            {
                                dailyResultsViewHolder.LottoImageSun.setVisibility(android.view.View.VISIBLE);
                            }
                            else
                            {
                                dailyResultsViewHolder.LottoImageSun.setVisibility(View.GONE);
                            }

                            Lotto1 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("lotto1").getValue().toString();
                            Lotto2 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("lotto2").getValue().toString();
                            Lotto3 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("lotto3").getValue().toString();
                            Lotto4 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("lotto4").getValue().toString();
                            Lotto5 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("lotto5").getValue().toString();
                            Mach1 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("mach1").getValue().toString();
                            Mach2 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("mach2").getValue().toString();
                            Mach3 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("mach3").getValue().toString();
                            Mach4 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("mach4").getValue().toString();
                            Mach5 = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("mach5").getValue().toString();

                            Date = dataSnapshot.child("Days(Sunday)").child(WeekDays).child("Results").child("date").getValue().toString();

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

                            //dailyResultsViewHolder.layout.requestLayout();

                            if (live.equals("yes"))
                            {
                                dailyResultsViewHolder.layout.setVisibility(View.GONE);
                            }else
                            {
                                rec.setVisibility(View.VISIBLE);
                                dailyResultsViewHolder.layout.setVisibility(View.VISIBLE);


                            }
                            rec.setVisibility(View.VISIBLE);

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
                dailyResultsViewHolder2.NativeAdDestroy();
            }

        }
        else if (getItemViewType(position) == VIEW_TYPE_THIRD)
        {
            //Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
            DailyResultsViewHolder2 dailyResultsViewHolder2 = (DailyResultsViewHolder2) holder;
            // Bind data and UI for the second view
            // ...

            dailyResultsViewHolder2.NativeAdSmall2();


        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1)
        {
            return VIEW_TYPE_SECOND;
        }
        else if (position == 7) {
            // Show the native ad after every 'adFrequency' number of data items
            return VIEW_TYPE_THIRD;
        }
        /**else if (position == 4) {
            // Show the native ad after every 'adFrequency' number of data items
            return VIEW_TYPE_THIRD;
        }**/
        else
        {
            return VIEW_TYPE_FIRST;
        }
    }

    @Override
    public int getItemCount() {

        int co = combinedDataList.size();

        if (combinedDataList.size() >= 7) {
            co += 2;
        }
        // If the original item count is greater than or equal to 1, add 1 for the native ad at position 0
        else if (combinedDataList.size() >= 1) {
            co += 1;
        }

        return co; // + (combinedDataList.size() / 3);
    }
}
