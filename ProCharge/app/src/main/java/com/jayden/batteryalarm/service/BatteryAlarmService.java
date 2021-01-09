package com.jayden.batteryalarm.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.receiver.BatteryStatusReceiver;
import com.jayden.batteryalarm.receiver.PowerConnctedReceiver;
import com.jayden.batteryalarm.templates.Constant;

public class BatteryAlarmService extends Service {
    PowerConnctedReceiver powerConnctedReceiver;
    BatteryStatusReceiver batteryStatusReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notification_set();

        //Power Connected or not receiver register
        powerConnctedReceiver = new PowerConnctedReceiver();
        IntentFilter filter = new IntentFilter();
        for (String action: PowerConnctedReceiver.POWER_ACTIONS) {
            filter.addAction(action);
        }
        registerReceiver(powerConnctedReceiver , filter);

        //Battery status receiver register
        batteryStatusReceiver = new BatteryStatusReceiver();
        IntentFilter filter_new = new IntentFilter();
        filter_new.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter_new.addAction(Intent.ACTION_BATTERY_OKAY);
        registerReceiver(batteryStatusReceiver , filter_new);

        Constant.getInstance().isCableConnected = isConnected();

        return START_STICKY;
    }

    public boolean isConnected() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = 0;
        if (intent != null) {
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    public void notification_set(){
        final String CHANNEL_ONE_NAME = "Default";
        String CHANNEL_ONE_ID = "channel-01";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(notificationChannel);
        }

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout_oreo);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                .setContentText(getApplicationContext().getResources().getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(null)
                        .bigLargeIcon(null))
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout);
        startForeground(1, notification.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(powerConnctedReceiver);
        this.unregisterReceiver(batteryStatusReceiver);
    }
}