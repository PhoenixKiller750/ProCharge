package com.jayden.batteryalarm.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.adapter.LogHistoryAdapter;
import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.templates.DBHelper;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;

import java.util.List;

public class HistoryLogDialog extends Dialog {

    List<AlarmData> alarmDataArrayList;
    Context context;
    Activity activity ;

    public HistoryLogDialog(Context context, int themeResId, Activity activity) {
        super(context ,themeResId );
        this.context = context ;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_history_log);

        Log.e("hfkj" , "HistoryLogDialog  ");

        RelativeLayout rel_back = findViewById(R.id.rel_back);
        rel_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        alarmDataArrayList = new DBHelper(context).getAllAlarmData();

        RecyclerView recycle_history = findViewById(R.id.recycle_history);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recycle_history.setLayoutManager(mLayoutManager);
        recycle_history.setItemAnimator(new DefaultItemAnimator());
        LogHistoryAdapter logHistoryAdapter = new  LogHistoryAdapter(context , alarmDataArrayList);
        recycle_history.setAdapter(logHistoryAdapter);

        logHistoryAdapter.notifyDataSetChanged();

        Log.e("hfkj" , "HistoryLogDialogs ffsgfsg   ");

    }

    @Override
    protected void onStart() {
        super.onStart();
        RelativeLayout rel_live_ad = findViewById(R.id.rel_live_ad);
        RelativeLayout rel_inapp = findViewById(R.id.rel_inapp);
        if (new SharedPreferencesApplication().getInAppDone(context)){
            rel_live_ad.setVisibility(View.INVISIBLE);
            rel_inapp.setVisibility(View.GONE);
        }
    }
}
