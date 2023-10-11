package com.maxstudio.lotto.Fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.maxstudio.lotto.Ad.MyApplication;
import com.maxstudio.lotto.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminFragment extends Fragment {

    private View View;

    private Button button, button2, button3, button4, liveBut, send, sureBut;
    private RelativeLayout layout, layout2, layout3, layout4,layout5,layout6, signIn, input;
    private EditText date, day, days, lotto1, lotto2, lotto3, lotto4, lotto5, mach1, mach2,
            mach3, mach4, mach5, event, order, email, password, live;
    private ProgressDialog loadingBar;
    private String ResultsRef, dayString;

    private DatabaseReference LottoRef;
    private FirebaseAuth mAuth;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_admin, container, false);

        LottoRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        InitialisedFields();
        Java8DateExample();
        MyApplication.isAdFullScreen = true;

        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .commit();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendResults();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPrediction();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToday();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        liveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveEntry();
            }
        });

        sureBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SureEntry();
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundResource(android.R.color.holo_blue_dark);
                layout2.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout6.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout5.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout3.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout4.setBackgroundColor(Color.parseColor("#A8ECE5"));

                button2.setVisibility(android.view.View.VISIBLE);
                event.setVisibility(android.view.View.VISIBLE);
                order.setVisibility(android.view.View.GONE);
                button.setVisibility(android.view.View.GONE);
                button3.setVisibility(android.view.View.GONE);
                button4.setVisibility(android.view.View.GONE);
                liveBut.setVisibility(android.view.View.GONE);
                sureBut.setVisibility(android.view.View.GONE);
                days.setVisibility(android.view.View.VISIBLE);
                email.setVisibility(android.view.View.VISIBLE);
                password.setVisibility(android.view.View.VISIBLE);
                send.setVisibility(android.view.View.VISIBLE);

                lotto4.setVisibility(android.view.View.VISIBLE);
                lotto5.setVisibility(android.view.View.VISIBLE);
                mach1.setVisibility(android.view.View.VISIBLE);
                mach2.setVisibility(android.view.View.VISIBLE);
                mach3.setVisibility(android.view.View.VISIBLE);
                mach4.setVisibility(android.view.View.VISIBLE);
                mach5.setVisibility(android.view.View.VISIBLE);
                days.setVisibility(android.view.View.VISIBLE);

                days.setHint("days...");
                day.setHint("days...");
                day.setText("");
                event.setHint("yest...");

                SetDay();

            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout2.setBackgroundResource(android.R.color.holo_blue_dark);
                layout.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout6.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout5.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout3.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout4.setBackgroundColor(Color.parseColor("#A8ECE5"));

                event.setVisibility(android.view.View.VISIBLE);
                order.setVisibility(android.view.View.VISIBLE);
                button2.setVisibility(android.view.View.GONE);
                button3.setVisibility(android.view.View.GONE);
                button.setVisibility(android.view.View.VISIBLE);
                button4.setVisibility(android.view.View.GONE);
                liveBut.setVisibility(android.view.View.GONE);
                sureBut.setVisibility(android.view.View.GONE);
                days.setVisibility(android.view.View.GONE);
                email.setVisibility(android.view.View.GONE);
                password.setVisibility(android.view.View.GONE);
                send.setVisibility(android.view.View.GONE);

                lotto4.setVisibility(android.view.View.VISIBLE);
                lotto5.setVisibility(android.view.View.VISIBLE);
                mach1.setVisibility(android.view.View.VISIBLE);
                mach2.setVisibility(android.view.View.VISIBLE);
                mach3.setVisibility(android.view.View.VISIBLE);
                mach4.setVisibility(android.view.View.VISIBLE);
                mach5.setVisibility(android.view.View.VISIBLE);
                //days.setVisibility(android.view.View.VISIBLE);

                days.setHint("days...");
                day.setHint("days...");
                day.setText("");
                event.setHint("event");

            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout3.setBackgroundResource(android.R.color.holo_blue_dark);
                layout2.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout6.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout5.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout4.setBackgroundColor(Color.parseColor("#A8ECE5"));

                button3.setVisibility(android.view.View.VISIBLE);
                event.setVisibility(android.view.View.GONE);
                order.setVisibility(android.view.View.GONE);
                button.setVisibility(android.view.View.GONE);
                liveBut.setVisibility(android.view.View.GONE);
                sureBut.setVisibility(android.view.View.GONE);
                button2.setVisibility(android.view.View.GONE);
                button4.setVisibility(android.view.View.GONE);
                days.setVisibility(android.view.View.GONE);

                lotto4.setVisibility(android.view.View.VISIBLE);
                lotto5.setVisibility(android.view.View.VISIBLE);
                mach1.setVisibility(android.view.View.VISIBLE);
                mach2.setVisibility(android.view.View.VISIBLE);
                mach3.setVisibility(android.view.View.VISIBLE);
                mach4.setVisibility(android.view.View.VISIBLE);
                mach5.setVisibility(android.view.View.VISIBLE);

                day.setHint("days...");
                //day.setText("");

            }
        });

        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout4.setBackgroundResource(android.R.color.holo_blue_dark);
                layout3.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout6.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout2.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout5.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout.setBackgroundColor(Color.parseColor("#A8ECE5"));

                button4.setVisibility(android.view.View.VISIBLE);
                event.setVisibility(android.view.View.VISIBLE);
                order.setVisibility(android.view.View.GONE);
                button.setVisibility(android.view.View.GONE);
                button2.setVisibility(android.view.View.GONE);
                liveBut.setVisibility(android.view.View.GONE);
                sureBut.setVisibility(android.view.View.GONE);
                button3.setVisibility(android.view.View.GONE);
                days.setVisibility(android.view.View.VISIBLE);

                lotto4.setVisibility(android.view.View.VISIBLE);
                lotto5.setVisibility(android.view.View.VISIBLE);
                mach1.setVisibility(android.view.View.VISIBLE);
                mach2.setVisibility(android.view.View.VISIBLE);
                mach3.setVisibility(android.view.View.VISIBLE);
                mach4.setVisibility(android.view.View.VISIBLE);
                mach5.setVisibility(android.view.View.VISIBLE);
                days.setVisibility(android.view.View.VISIBLE);

                days.setHint("future date");
                day.setHint("draw type");
                day.setText("");
                days.setText("");
                event.setText("");
                event.setHint("event");

                SetDrawType();

            }
        });

        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout5.setBackgroundResource(android.R.color.holo_blue_dark);
                layout3.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout6.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout2.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout4.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout.setBackgroundColor(Color.parseColor("#A8ECE5"));

                liveBut.setVisibility(android.view.View.VISIBLE);
                button4.setVisibility(android.view.View.GONE);
                event.setVisibility(android.view.View.GONE);
                sureBut.setVisibility(android.view.View.GONE);
                order.setVisibility(android.view.View.GONE);
                button.setVisibility(android.view.View.GONE);
                button2.setVisibility(android.view.View.GONE);
                button3.setVisibility(android.view.View.GONE);
                days.setVisibility(android.view.View.VISIBLE);

                lotto4.setVisibility(android.view.View.VISIBLE);
                lotto5.setVisibility(android.view.View.VISIBLE);
                mach1.setVisibility(android.view.View.VISIBLE);
                mach2.setVisibility(android.view.View.GONE);
                mach3.setVisibility(android.view.View.GONE);
                mach4.setVisibility(android.view.View.GONE);
                mach5.setVisibility(android.view.View.GONE);

                day.setHint("day");
                days.setHint("draw type");


                //SetDrawType();

            }
        });

        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout6.setBackgroundResource(android.R.color.holo_blue_dark);
                layout3.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout2.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout4.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout5.setBackgroundColor(Color.parseColor("#A8ECE5"));
                layout.setBackgroundColor(Color.parseColor("#A8ECE5"));

                sureBut.setVisibility(android.view.View.VISIBLE);
                button4.setVisibility(android.view.View.GONE);
                liveBut.setVisibility(android.view.View.GONE);
                event.setVisibility(android.view.View.GONE);
                order.setVisibility(android.view.View.GONE);
                button.setVisibility(android.view.View.GONE);
                button2.setVisibility(android.view.View.GONE);
                button3.setVisibility(android.view.View.GONE);

                lotto4.setVisibility(android.view.View.GONE);
                lotto5.setVisibility(android.view.View.GONE);
                mach1.setVisibility(android.view.View.GONE);
                mach2.setVisibility(android.view.View.GONE);
                mach3.setVisibility(android.view.View.GONE);
                mach4.setVisibility(android.view.View.GONE);
                mach5.setVisibility(android.view.View.GONE);
                days.setVisibility(android.view.View.GONE);

                day.setHint("day");


                //SetDrawType();

            }
        });

        return View;
    }

    private void InitialisedFields() {

        date = View.findViewById(R.id.date);
        day = View.findViewById(R.id.day);
        days = View.findViewById(R.id.days);
        lotto1 = View.findViewById(R.id.lotto1);
        lotto2 = View.findViewById(R.id.lotto2);
        lotto3 = View.findViewById(R.id.lotto3);
        lotto4 = View.findViewById(R.id.lotto4);
        lotto5 = View.findViewById(R.id.lotto5);
        mach1 = View.findViewById(R.id.mach1);
        mach2 = View.findViewById(R.id.mach2);
        mach3 = View.findViewById(R.id.mach3);
        mach4 = View.findViewById(R.id.mach4);
        mach5 = View.findViewById(R.id.mach5);
        event = View.findViewById(R.id.event);
        order = View.findViewById(R.id.order);
        email = View.findViewById(R.id.email);
        password = View.findViewById(R.id.password);


        button = View.findViewById(R.id.history_button);
        button2 = View.findViewById(R.id.input_button);
        button3 = View.findViewById(R.id.predict_button);
        button4 = View.findViewById(R.id.today_button);
        liveBut = View.findViewById(R.id.live_button);
        sureBut = View.findViewById(R.id.sure_button);

        send = View.findViewById(R.id.send);
        layout = View.findViewById(R.id.today);
        layout2 = View.findViewById(R.id.history);
        layout3 = View.findViewById(R.id.predict);
        layout4 = View.findViewById(R.id.today_lay);
        layout5 = View.findViewById(R.id.other_lay);
        layout6 = View.findViewById(R.id.sure_lay);
        signIn = View.findViewById(R.id.SignUp);
        input = View.findViewById(R.id.input);

        loadingBar = new ProgressDialog(getContext());

    }

    private void SignIn() {

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getContext(), "*Email is required", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(getContext(), "*Password is required", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                signIn.setVisibility(android.view.View.GONE);
                                input.setVisibility(android.view.View.VISIBLE);

                                Toast.makeText(getContext(), "Signed In", Toast.LENGTH_SHORT).show();

                            } else {
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void SendMessage() {

        String lottoText1 = lotto1.getText().toString();
        String lottoText2 = lotto2.getText().toString();
        String lottoText3 = lotto3.getText().toString();
        String lottoText4 = lotto4.getText().toString();
        String lottoText5 = lotto5.getText().toString();

        String machText1 = mach1.getText().toString();
        String machText2 = mach2.getText().toString();
        String machText3 = mach3.getText().toString();
        String machText4 = mach4.getText().toString();
        String machText5 = mach5.getText().toString();

        String eventText = event.getText().toString();
        String orderText = order.getText().toString();


        String dateText = date.getText().toString();
        String dayText = day.getText().toString();

        if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                TextUtils.isEmpty(lottoText4) || TextUtils.isEmpty(lottoText5) || TextUtils.isEmpty(machText1) || TextUtils.isEmpty(machText2) ||
                TextUtils.isEmpty(machText3) || TextUtils.isEmpty(machText4) || TextUtils.isEmpty(machText5) ||
                TextUtils.isEmpty(dateText) || TextUtils.isEmpty(dayText) || TextUtils.isEmpty(eventText) || TextUtils.isEmpty(eventText)) {
            Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

        } else {
            String HistoryRef = "Results History" + "/" + dayText + "/" + orderText;


            Map<String, String> todayTextBody = new HashMap<String, String>();
            todayTextBody.put("date", dateText);
            todayTextBody.put(dayText, dayText);
            todayTextBody.put("lotto1", lottoText1);
            todayTextBody.put("lotto2", lottoText2);
            todayTextBody.put("lotto3", lottoText3);
            todayTextBody.put("lotto4", lottoText4);
            todayTextBody.put("lotto5", lottoText5);
            todayTextBody.put("mach1", machText1);
            todayTextBody.put("mach2", machText2);
            todayTextBody.put("mach3", machText3);
            todayTextBody.put("mach4", machText4);
            todayTextBody.put("mach5", machText5);
            todayTextBody.put("event", eventText);


            Map<String, Object> historyBodyDetails = new HashMap<>();
            historyBodyDetails.put(HistoryRef, todayTextBody);

            //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
            //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            LottoRef.updateChildren(historyBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }

    }

    private void SendResults() {

        String lottoText1 = lotto1.getText().toString();
        String lottoText2 = lotto2.getText().toString();
        String lottoText3 = lotto3.getText().toString();
        String lottoText4 = lotto4.getText().toString();
        String lottoText5 = lotto5.getText().toString();

        String machText1 = mach1.getText().toString();
        String machText2 = mach2.getText().toString();
        String machText3 = mach3.getText().toString();
        String machText4 = mach4.getText().toString();
        String machText5 = mach5.getText().toString();


        String dateText = date.getText().toString();
        String dayText = day.getText().toString();
        String daysText = days.getText().toString();
        //String liveText = event.getText().toString();

        if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                TextUtils.isEmpty(lottoText4) || TextUtils.isEmpty(lottoText5) || TextUtils.isEmpty(machText1) || TextUtils.isEmpty(machText2) ||
                TextUtils.isEmpty(machText3) || TextUtils.isEmpty(machText4) || TextUtils.isEmpty(machText5) ||
                TextUtils.isEmpty(dateText) || TextUtils.isEmpty(dayText) || TextUtils.isEmpty(daysText)) {
            Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

        } else {
            if (dayText.equals("Sunday")) {
                ResultsRef = "Days(Sunday)" + "/" + dayText + "/" + "Results";

            } else {
                ResultsRef = "Days" + "/" + dayText + "/" + "Results";

            }

            Map<String, String> todayTextBody = new HashMap<String, String>();
            todayTextBody.put("date", dateText);
            todayTextBody.put(daysText, daysText);
            todayTextBody.put("lotto1", lottoText1);
            todayTextBody.put("lotto2", lottoText2);
            todayTextBody.put("lotto3", lottoText3);
            todayTextBody.put("lotto4", lottoText4);
            todayTextBody.put("lotto5", lottoText5);
            todayTextBody.put("mach1", machText1);
            todayTextBody.put("mach2", machText2);
            todayTextBody.put("mach3", machText3);
            todayTextBody.put("mach4", machText4);
            todayTextBody.put("mach5", machText5);
            todayTextBody.put("live", "yes");

            Map<String, Object> historyBodyDetails = new HashMap<>();
            historyBodyDetails.put(ResultsRef, todayTextBody);

            //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
            //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            LottoRef.updateChildren(historyBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                    }

                }

            });

        }

        String yesterdayText = event.getText().toString();

        if (TextUtils.isEmpty(yesterdayText)) {
            Toast.makeText(getContext(), "Field is required", Toast.LENGTH_SHORT).show();
        } else {
            if (yesterdayText.equals("Sunday")) {
                LottoRef.child("Days(Sunday)").child(yesterdayText).child("Results")
                        .child("live").setValue("no")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getContext(), "live value set to 'no'", Toast.LENGTH_SHORT).show();

                            }
                        });
            } else {
                LottoRef.child("Days").child(yesterdayText).child("Results")
                        .child("live").setValue("no")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getContext(), "live value set to 'no'", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        }


    }

    private void SendPrediction() {

        String lottoText1 = lotto1.getText().toString();
        String lottoText2 = lotto2.getText().toString();
        String lottoText3 = lotto3.getText().toString();
        String lottoText4 = lotto4.getText().toString();
        String lottoText5 = lotto5.getText().toString();

        String machText1 = mach1.getText().toString();
        String machText2 = mach2.getText().toString();
        String machText3 = mach3.getText().toString();
        String machText4 = mach4.getText().toString();
        String machText5 = mach5.getText().toString();


        String dateText = date.getText().toString();
        String dayText = day.getText().toString();
        //String daysText = days.getText().toString();

        if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                TextUtils.isEmpty(lottoText4) || TextUtils.isEmpty(lottoText5) || TextUtils.isEmpty(machText1) || TextUtils.isEmpty(machText2) ||
                TextUtils.isEmpty(machText3) || TextUtils.isEmpty(machText4) || TextUtils.isEmpty(machText5) ||
                TextUtils.isEmpty(dateText) || TextUtils.isEmpty(dayText)) {
            Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

        } else {

            String ResultsRef = "Today's Prediction" + "/" + "Results";


            Map<String, String> todayTextBody = new HashMap<String, String>();
            todayTextBody.put("date", dateText);
            todayTextBody.put(dayText, dayText);
            todayTextBody.put("lotto1", lottoText1);
            todayTextBody.put("lotto2", lottoText2);
            todayTextBody.put("lotto3", lottoText3);
            todayTextBody.put("lotto4", lottoText4);
            todayTextBody.put("lotto5", lottoText5);
            todayTextBody.put("mach1", machText1);
            todayTextBody.put("mach2", machText2);
            todayTextBody.put("mach3", machText3);
            todayTextBody.put("mach4", machText4);
            todayTextBody.put("mach5", machText5);

            Map<String, Object> historyBodyDetails = new HashMap<>();
            historyBodyDetails.put(ResultsRef, todayTextBody);

            //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
            //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            LottoRef.updateChildren(historyBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }

    }

    private void SendToday() {

        String lottoText1 = lotto1.getText().toString();
        String lottoText2 = lotto2.getText().toString();
        String lottoText3 = lotto3.getText().toString();
        String lottoText4 = lotto4.getText().toString();
        String lottoText5 = lotto5.getText().toString();

        String machText1 = mach1.getText().toString();
        String machText2 = mach2.getText().toString();
        String machText3 = mach3.getText().toString();
        String machText4 = mach4.getText().toString();
        String machText5 = mach5.getText().toString();

        String eventText = event.getText().toString();
        String futureText = days.getText().toString();

        String dateText = date.getText().toString();
        String drawType = day.getText().toString();

        if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                TextUtils.isEmpty(lottoText4) || TextUtils.isEmpty(lottoText5) || TextUtils.isEmpty(machText1) || TextUtils.isEmpty(machText2) ||
                TextUtils.isEmpty(machText3) || TextUtils.isEmpty(machText4) || TextUtils.isEmpty(machText5) ||
                TextUtils.isEmpty(dateText) || TextUtils.isEmpty(drawType) || TextUtils.isEmpty(eventText) || TextUtils.isEmpty(eventText)) {
            Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

        } else {
            String todayRef = "Today" + "/" + "Results";


            Map<String, String> todayTextBody = new HashMap<String, String>();
            todayTextBody.put("date", dateText);
            todayTextBody.put("draw_type", drawType);
            todayTextBody.put("lotto1", lottoText1);
            todayTextBody.put("lotto2", lottoText2);
            todayTextBody.put("lotto3", lottoText3);
            todayTextBody.put("lotto4", lottoText4);
            todayTextBody.put("lotto5", lottoText5);
            todayTextBody.put("mach1", machText1);
            todayTextBody.put("mach2", machText2);
            todayTextBody.put("mach3", machText3);
            todayTextBody.put("mach4", machText4);
            todayTextBody.put("mach5", machText5);
            todayTextBody.put("event", eventText);
            todayTextBody.put("future_date", futureText);


            Map<String, Object> todayBodyDetails = new HashMap<>();
            todayBodyDetails.put(todayRef, todayTextBody);

            //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
            //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            LottoRef.updateChildren(todayBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }


    }

    private void Java8DateExample(){

        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.now();
        }
        DayOfWeek dayOfWeek = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfWeek = localDate.getDayOfWeek();
        }
        dayString = dayOfWeek.toString();

        //Toast.makeText(getContext(), ""+ dayString, Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 5);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);

        if (calendar.after(startTime) && calendar.before(endTime)) {
            // current time is between 12am and 5am

            LottoRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            String key = ds.getKey();

                            assert key != null;
                            LottoRef.child("Groups").child(key).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.exists() && snapshot.hasChild("Messages"))
                                            {
                                                //Toast.makeText(getContext(), "Deleted message", Toast.LENGTH_SHORT).show();

                                                LottoRef.child("Groups").child(key).child("Messages").
                                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                if (snapshot.exists())
                                                                {
                                                                    for (DataSnapshot ds : snapshot.getChildren())
                                                                    {
                                                                        String msgKey = ds.getKey();

                                                                        assert msgKey != null;
                                                                        LottoRef.child("Groups").child(key).child("Messages").
                                                                                child(msgKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                if (snapshot.exists() && snapshot.hasChild("date"))
                                                                                {

                                                                                    try {

                                                                                        SimpleDateFormat dates = new SimpleDateFormat("MMM dd yyyy");
                                                                                        //Dates to compare
                                                                                        Date CurrentDate = new Date();
                                                                                        String p_Date = snapshot.child("date").getValue().toString();
                                                                                        String previousDate = p_Date.replace(",", "");
                                                                                        java.util.Date date2 = dates.parse(previousDate);

                                                                                        //java.util.Date date1 = dates.parse(CurrentDate);

                                                                                        //Setting dates

                                                                                        //Comparing dates
                                                                                        assert date2 != null;
                                                                                        long difference = Math.abs(CurrentDate.getTime() - date2.getTime());
                                                                                        long differenceDates = difference / (24 * 60 * 60 * 1000);

                                                                                        //Convert long to String
                                                                                        String dayDifference = Long.toString(differenceDates);
                                                                                        int dif = Integer.parseInt(dayDifference);

                                                                                        //Toast.makeText(getContext(), "Deleted message:" + previousDate, Toast.LENGTH_SHORT).show();

                                                                                        Log.e("HERE","HERE: " + dayDifference);

                                                                                        if(dif >= 30)
                                                                                        {
                                                                                            String remKey = snapshot.getKey();
                                                                                            String type = snapshot.child("type").getValue().toString();

                                                                                            if (type.equals("text"))
                                                                                            {
                                                                                                assert remKey != null;
                                                                                                LottoRef.child("Groups").child(key).child("Messages").
                                                                                                        child(msgKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if (task.isSuccessful())
                                                                                                        {
                                                                                                            Toast.makeText(getContext(), "Deleted message", Toast.LENGTH_SHORT).show();
                                                                                                        }

                                                                                                    }
                                                                                                });
                                                                                            }else if (type.equals("image"))
                                                                                            {
                                                                                                LottoRef.child("Groups").child(key).child("Messages").
                                                                                                        child(msgKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if (task.isSuccessful())
                                                                                                        {
                                                                                                            Toast.makeText(getContext(), "Deleted message", Toast.LENGTH_SHORT).show();

                                                                                                            String msg = snapshot.child("message").getValue().toString();
                                                                                                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                                                                            StorageReference storageReference = firebaseStorage.
                                                                                                                    getReferenceFromUrl(msg);
                                                                                                            storageReference.delete();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                        }

                                                                                    }catch (Exception e)
                                                                                    {
                                                                                        Log.e("DIDN'T WORK", "exception " + e); }
                                                                                }


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    }
                                                                }


                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void SetDay(){

        if (dayString.equals("MONDAY"))
        {
            day.setText("Monday");
            days.setText("monday");
            event.setText("Sunday");
        }

        if (dayString.equals("TUESDAY"))
        {
            day.setText("NTuesday");
            days.setText("tuesday");
            event.setText("Monday");
        }

        if (dayString.equals("WEDNESDAY"))
        {
            day.setText("oWednesday");
            days.setText("wednesday");
            event.setText("NTuesday");
        }

        if (dayString.equals("THURSDAY"))
        {
            day.setText("pThursday");
            days.setText("thursday");
            event.setText("oWednesday");
        }

        if (dayString.equals("FRIDAY"))
        {
            day.setText("qFriday");
            days.setText("friday");
            event.setText("pThursday");
        }

        if (dayString.equals("SATURDAY"))
        {
            day.setText("rSaturday");
            days.setText("saturday");
            event.setText("qFriday");
        }

        if (dayString.equals("SUNDAY"))
        {
            day.setText("Sunday");
            days.setText("sunday");
            event.setText("rSaturday");
        }
    }

    private void SetDrawType(){

        if (dayString.equals("MONDAY"))
        {
            day.setText("Monday Special");
        }

        if (dayString.equals("TUESDAY"))
        {
            day.setText("Lucky Tuesday");
        }

        if (dayString.equals("WEDNESDAY"))
        {
            day.setText("Midweek");
        }

        if (dayString.equals("THURSDAY"))
        {
            day.setText("Fortune Thursday");
        }

        if (dayString.equals("FRIDAY"))
        {
            day.setText("Friday Bonanza");
        }

        if (dayString.equals("SATURDAY"))
        {
            day.setText("National Weekly");
        }

        if (dayString.equals("SUNDAY"))
        {
            day.setText("Sunday Aseda");
        }
    }

    private void liveEntry() {

        String lottoText1 = lotto1.getText().toString();
        String lottoText2 = lotto2.getText().toString();
        String lottoText3 = lotto3.getText().toString();
        String lottoText4 = lotto4.getText().toString();
        String lottoText5 = lotto5.getText().toString();
        String lottoText6 = mach1.getText().toString();


        String dateText = date.getText().toString();
        String Day = day.getText().toString();
        String drawType = days.getText().toString();

        if (drawType.equals("Super")) {
            if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                    TextUtils.isEmpty(lottoText4) || TextUtils.isEmpty(lottoText5) || TextUtils.isEmpty(lottoText6) ||
                    TextUtils.isEmpty(dateText) || TextUtils.isEmpty(drawType) || TextUtils.isEmpty(Day)) {
                Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

            } else {
                String todayRef = "Other Games" + "/" + drawType + "/" + Day + "/" + "Results";


                Map<String, String> todayTextBody = new HashMap<String, String>();
                todayTextBody.put("day", Day);
                todayTextBody.put("date", dateText);
                todayTextBody.put("draw_type", drawType);
                todayTextBody.put("lotto1", lottoText1);
                todayTextBody.put("lotto2", lottoText2);
                todayTextBody.put("lotto3", lottoText3);
                todayTextBody.put("lotto4", lottoText4);
                todayTextBody.put("lotto5", lottoText5);
                todayTextBody.put("lotto6", lottoText6);


                Map<String, Object> todayBodyDetails = new HashMap<>();
                todayBodyDetails.put(todayRef, todayTextBody);

                //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
                //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                LottoRef.updateChildren(todayBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                        }

                    }

                });
            }
        }
        else{
            if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                    TextUtils.isEmpty(lottoText4) || TextUtils.isEmpty(lottoText5) ||
                    TextUtils.isEmpty(dateText) || TextUtils.isEmpty(drawType) || TextUtils.isEmpty(Day)) {
                Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

            } else {
                String todayRef = "Other Games"+ "/" + drawType +  "/"  + Day + "/" + "Results";


                Map<String, String> todayTextBody = new HashMap<String, String>();
                todayTextBody.put("day", Day);
                todayTextBody.put("date", dateText);
                todayTextBody.put("draw_type", drawType);
                todayTextBody.put("lotto1", lottoText1);
                todayTextBody.put("lotto2", lottoText2);
                todayTextBody.put("lotto3", lottoText3);
                todayTextBody.put("lotto4", lottoText4);
                todayTextBody.put("lotto5", lottoText5);


                Map<String, Object> todayBodyDetails = new HashMap<>();
                todayBodyDetails.put(todayRef, todayTextBody);

                //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
                //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                LottoRef.updateChildren(todayBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                        }

                    }

                });
            }
        }
    }

    private void SureEntry() {

        String lottoText1 = lotto1.getText().toString();
        String lottoText2 = lotto2.getText().toString();
        String lottoText3 = lotto3.getText().toString();


        String dateText = date.getText().toString();
        String Day = day.getText().toString();
        //String drawType = days.getText().toString();

        if (TextUtils.isEmpty(lottoText1) || TextUtils.isEmpty(lottoText2) || TextUtils.isEmpty(lottoText3) ||
                TextUtils.isEmpty(dateText) || TextUtils.isEmpty(Day)) {
            Toast.makeText(getContext(), "Empty field is required ", Toast.LENGTH_SHORT).show();

        } else {
            String todayRef = "Sure Banker";


            Map<String, String> todayTextBody = new HashMap<String, String>();
            todayTextBody.put(Day, Day);
            todayTextBody.put("date", dateText);
            todayTextBody.put("2sure1", lottoText1);
            todayTextBody.put("2sure2", lottoText2);
            todayTextBody.put("banker", lottoText3);


            Map<String, Object> todayBodyDetails = new HashMap<>();
            todayBodyDetails.put(todayRef, todayTextBody);

            //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
            //userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            LottoRef.updateChildren(todayBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getContext(), "*" + message, Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }
    }

}