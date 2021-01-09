package com.jayden.batteryalarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.receiver.AlarmReceiver;
import com.jayden.batteryalarm.service.AlarmService;
import com.jayden.batteryalarm.service.WakeLocker;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;

public class AlarmRingingActivity extends AppCompatActivity {
    AlarmData alarm ;
    TextView current_per ;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_ringing);

        int battery = Constant.getInstance().getCurrentBatteryStutus(AlarmRingingActivity.this);

        current_per = findViewById(R.id.current_per);
        current_per.setText(battery + "%");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(battery);

        Bundle bundleintent = getIntent().getBundleExtra("BUNDLE_EXTRA");
        if (bundleintent != null) {
            Log.e("AlarmReceiver", "bundle not null");
            alarm = bundleintent.getParcelable("ALARM_KEY");
        }
        Log.e("AlarmReceiver", "TYPE " + alarm.getAlarm_type() + " ss");

        Button stop_alarm = findViewById(R.id.stop_alarm);
        stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WakeLocker.release();
                if (alarm.getAlarm_type() != null && alarm.getAlarm_type().equals("Persentage")){
                    new SharedPreferencesApplication().setAlarmPercentage(AlarmRingingActivity.this , 0);
                }
                AlarmReceiver.cancelReminderAlarm(AlarmRingingActivity.this , alarm.getUnique_id());
                stopService(new Intent(AlarmRingingActivity.this , AlarmService.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout rel_live_ad = findViewById(R.id.rel_live_ad);
        if (new SharedPreferencesApplication().getInAppDone(AlarmRingingActivity.this)){
            rel_live_ad.setVisibility(View.INVISIBLE);
        }
        else {
            Constant.getInstance().loadBannerAd(rel_live_ad, AlarmRingingActivity.this , AlarmRingingActivity.this );
        }
    }
}