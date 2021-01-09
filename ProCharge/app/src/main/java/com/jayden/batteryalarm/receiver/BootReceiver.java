package com.jayden.batteryalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jayden.batteryalarm.service.BatteryAlarmService;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BOOT_COMPLETED)){
                context.startService(new Intent(context , BatteryAlarmService.class));
            }
        }
    }
}