package com.maxstudio.lotto.Fragments;

import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.CallLog;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.maxstudio.lotto.R;

import java.util.List;


public class PlayFragment extends Fragment {

    private RelativeLayout lay1, lay2, lay3, lay4, lay5, lay6, lay7, lay8;
    private TextView directText1, directText2, directText3, directText4, directText5, permText, permText2, banker;
    private EditText editText, editText1;
    private String ussd;
    private View View;

    private Button button;
    private boolean checked, checked1, checked2, checked3, checked4, checked5, checked6, checked7;


    public PlayFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_play, container, false);


        InitializedFields();

        return View;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void InitializedFields() {

        lay1 = View.findViewById(R.id.layout_d1);
        lay2 = View.findViewById(R.id.layout_d2);
        lay3 = View.findViewById(R.id.layout_d3);
        lay4 = View.findViewById(R.id.layout_d4);
        lay5 = View.findViewById(R.id.layout_d5);
        lay6 = View.findViewById(R.id.layout_P2);
        lay7 = View.findViewById(R.id.layout_P3);
        lay8 = View.findViewById(R.id.layout_B);

        directText1 = View.findViewById(R.id.text1);
        directText2 = View.findViewById(R.id.text2);
        directText3 = View.findViewById(R.id.text3);
        directText4 = View.findViewById(R.id.text4);
        directText5 = View.findViewById(R.id.text5);
        permText = View.findViewById(R.id.text6);
        permText2 = View.findViewById(R.id.text7);
        banker = View.findViewById(R.id.text8);

        button = View.findViewById(R.id.button);

        editText = View.findViewById(R.id.num);
        editText1 = View.findViewById(R.id.amount_editText);


        lay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = true;
                checked6 = false;
                checked5 = false;
                checked4 = false;
                checked3 = false;
                checked2 = false;
                checked1 = false;

                lay1.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);


                directText1.setTextColor(Color.parseColor("#ffffff"));
                directText2.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = false;
                checked6 = false;
                checked5 = false;
                checked4 = false;
                checked3 = false;
                checked2 = false;
                checked1 = true;

                lay2.setBackgroundResource(R.drawable.layout_background2);

                lay1.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);


                directText2.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = false;
                checked6 = false;
                checked5 = false;
                checked4 = false;
                checked3 = false;
                checked2 = true;
                checked1 = false;

                lay3.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay1.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);

                directText3.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText2.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = false;
                checked6 = false;
                checked5 = false;
                checked4 = false;
                checked3 = true;
                checked2 = false;
                checked1 = false;

                lay4.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay1.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);

                directText4.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText2.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = false;
                checked6 = false;
                checked5 = false;
                checked4 = true;
                checked3 = false;
                checked2 = false;
                checked1 = false;

                lay5.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay1.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);

                directText5.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText2.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = false;
                checked6 = false;
                checked5 = true;
                checked4 = false;
                checked3 = false;
                checked2 = false;
                checked1 = false;
                lay6.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay1.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);

                permText.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                directText2.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = false;
                checked = false;
                checked6 = true;
                checked5 = false;
                checked4 = false;
                checked3 = false;
                checked2 = false;
                checked1 = false;

                lay7.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay1.setBackgroundResource(R.drawable.layout_background3);
                lay8.setBackgroundResource(R.drawable.layout_background3);

                permText2.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                directText2.setTextColor(Color.parseColor("#292525"));
                banker.setTextColor(Color.parseColor("#292525"));
            }
        });

        lay8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checked7 = true;
                checked = false;
                checked6 = false;
                checked5 = false;
                checked4 = false;
                checked3 = false;
                checked2 = false;
                checked1 = false;

                lay8.setBackgroundResource(R.drawable.layout_background2);

                lay2.setBackgroundResource(R.drawable.layout_background3);
                lay3.setBackgroundResource(R.drawable.layout_background3);
                lay4.setBackgroundResource(R.drawable.layout_background3);
                lay5.setBackgroundResource(R.drawable.layout_background3);
                lay6.setBackgroundResource(R.drawable.layout_background3);
                lay7.setBackgroundResource(R.drawable.layout_background3);
                lay1.setBackgroundResource(R.drawable.layout_background3);

                banker.setTextColor(Color.parseColor("#ffffff"));
                directText1.setTextColor(Color.parseColor("#292525"));
                directText3.setTextColor(Color.parseColor("#292525"));
                directText4.setTextColor(Color.parseColor("#292525"));
                directText5.setTextColor(Color.parseColor("#292525"));
                permText.setTextColor(Color.parseColor("#292525"));
                permText2.setTextColor(Color.parseColor("#292525"));
                directText2.setTextColor(Color.parseColor("#292525"));
            }

        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                if (checked) {
                    OnButtonClicked();
                    //DeleteCallLog();

                }
                if (checked1) {
                    OnButtonClicked1();

                }
                if (checked2) {
                    OnButtonClicked2();

                }
                if (checked3) {
                    OnButtonClicked3();

                }
                if (checked4) {
                    OnButtonClicked4();

                }
                if (checked5) {
                    OnButtonClicked5();

                }
                if (checked6) {
                    OnButtonClicked6();

                }
                if (checked7) {
                    OnButtonClicked7();

                }

            }
        });

    }

    private void OnButtonClicked() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();



        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        } else {

         Intent callIntent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         ussd = "*959*1*1*"+ directNo + "*" + amount + "*1" + Uri.encode("#");
         callIntent.setData(Uri.parse("tel:" + ussd));

         if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
         if (ActivityCompat.checkSelfPermission(getContext(),
         Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
         {

         startActivity(callIntent);

            }
         }
        }

    }

    private void OnButtonClicked1() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*2*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }

    }

    private void OnButtonClicked2() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*3*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }
    }

    private void OnButtonClicked3() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*4*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }
    }

    private void OnButtonClicked4() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*5*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }
    }

    private void OnButtonClicked5() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*6*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }
    }

    private void OnButtonClicked6() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*7*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }
    }

    private void OnButtonClicked7() {

        String directNo = editText.getText().toString();
        String amount = editText1.getText().toString();

        if (directNo.equals("") || amount.equals("")) {
            Toast.makeText(getContext(), "Enter number(s) value or amount", Toast.LENGTH_SHORT).show();
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            ussd = "*959*1*8*" + directNo + "*" + amount + "*1" + Uri.encode("#");
            callIntent.setData(Uri.parse("tel:" + ussd));

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                {

                    startActivity(callIntent);

                }
            }

        }
    }

    private void DeleteCallLog()
    {
        String query = "NUMBER:" + ussd;
        getContext().getContentResolver().delete(CallLog.Calls.CONTENT_URI, query,null);
    }
}