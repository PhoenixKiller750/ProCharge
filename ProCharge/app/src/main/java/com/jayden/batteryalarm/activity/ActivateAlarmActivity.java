package com.jayden.batteryalarm.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;

public class ActivateAlarmActivity extends AppCompatActivity {

    TextView current_per ;
    ProgressBar progressBar;
    Constant constant = Constant.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activate);

        int battery = constant.getCurrentBatteryStutus(ActivateAlarmActivity.this);

        current_per = findViewById(R.id.current_per);
        current_per.setText(battery + "%");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(battery);

    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout rel_live_ad = findViewById(R.id.rel_live_ad);
        if (new SharedPreferencesApplication().getInAppDone(ActivateAlarmActivity.this)){
            rel_live_ad.setVisibility(View.INVISIBLE);
        }
        else {
            Constant.getInstance().loadBannerAd(rel_live_ad, ActivateAlarmActivity.this , ActivateAlarmActivity.this );
        }
    }
}