package com.jayden.batteryalarm.receiver;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.activity.MainActivity;
import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.service.AlarmService;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.templates.DBHelper;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;
import com.jayden.lockscreen.EnterPinActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PowerConnctedReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "DISCONNECTEDCHANNEL";
    Constant constant = Constant.getInstance();

    DBHelper dbHelper;
    SharedPreferencesApplication sh = new SharedPreferencesApplication();
    public static final String[] POWER_ACTIONS = {
            Intent.ACTION_POWER_CONNECTED ,
            Intent.ACTION_POWER_DISCONNECTED ,
            Intent.ACTION_BATTERY_CHANGED
    };

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DBHelper(context);
        if (intent != null) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            // Are we charging / charged?
            assert batteryStatus != null;
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

            if (isCharging) {
                Log.e("RECEIVER ", "CONNECTED isCharging : Constant.lastID " + Constant.lastID);
                constant.isCableConnected = true;
                String set_alarm = dbHelper.getPerticularAlarmType(String.valueOf(Constant.lastID));
                if (sh.getAlarmAlwardySet(context)) {
                    if (set_alarm != null) {
                        if (set_alarm.equalsIgnoreCase("TIME")) {
                            Log.e("RECEIVER ", set_alarm + " SET ALS");
                            AlarmData alarm = new DBHelper(context).getPerticularAlarmData(String.valueOf(Constant.lastID));
                            Log.e("RECEIVER ", alarm.getCurrent_date_time() + " SET ALS");
                            SimpleDateFormat format = new SimpleDateFormat(" MMM dd yyyy HH:mm:ss", Locale.getDefault());
                            try {
                                Date date1 = format.parse(alarm.getCurrent_date_time());
                                String currentDateandTime = format.format(new Date());
                                Date date2 = format.parse(currentDateandTime);
                                assert date2 != null;
                                assert date1 != null;
                                long millis = date2.getTime() - date1.getTime();

                                long minutes = (millis / (1000 * 60)) % 60;
                                long hours = millis / (1000 * 60 * 60);

                                long difference_hours = alarm.getSelected_hour() - hours;
                                long difference_min = alarm.getSelected_minute() - minutes;

                                if (difference_hours > 0 && difference_min > 0) {
                                    alarm.setSelected_minute((int) difference_min);
                                    alarm.setSelected_hour((int) difference_hours);
                                }
                                Log.e("RECEIVER ", "hours  : " + hours + " minutes : " + minutes);
                                Log.e("RECEIVER ", "difference_hours  : " + difference_hours + " difference_min : " + difference_min);
                                AlarmReceiver.setAlarm(context, alarm);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.e("CATCh", e.getMessage() + " ");
                            }
                        }
                    }
                }
            } else {
                Log.e("RECEIVER ", "DISCONNECTED");
                constant.isCableConnected = false;
                if (Constant.lastID != 0) {
                    String set_alarm = dbHelper.getPerticularAlarmType(String.valueOf(Constant.lastID));
                    if (sh.getAlarmAlwardySet(context)) {
                        Log.e("BATTERY", set_alarm + " xx");
                        if (set_alarm.equalsIgnoreCase("PERCENTAGE")) {
                            showNotification(context, context.getResources().getString(R.string.app_name), "Your Selected percentage not occurs");
                        } else if (set_alarm.equalsIgnoreCase("TIME")) {
                            showNotification(context, context.getResources().getString(R.string.app_name), "Your Selected time not occurs");
                            AlarmReceiver.cancelReminderAlarm(context, Constant.lastID);
                        }
                        AlarmData alarmData = new AlarmData();
                        SimpleDateFormat sdf = new SimpleDateFormat(" MMM dd yyyy HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        alarmData.setUnplagg_date_time(currentDateandTime);
                        alarmData.setUnplugg_percentage(constant.getCurrentBatteryStutus(context));
                        new DBHelper(context).updateLastUnpluggData(Constant.lastID, alarmData, false);
                    }
                }
                Log.e("AVT", "constant.isSetTherftAlarm " + constant.isSetTherftAlarm);
                if (constant.isSetTherftAlarm) {
                    if (sh.getTheftAlarmOcured(context)) {
                        sh.setTheftAlarmOcured(context, false);
                        Log.e("AVT", "constant.isSetTherftAlarm is true ");

                        Intent intentEnter = new Intent(context, EnterPinActivity.class);
                        intentEnter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentEnter.putExtra("WHEN_CALL", "TherftAlarm");
                        context.startActivity(intentEnter);

                        Intent serviceIntent = new Intent(context, AlarmService.class);
                        Bundle bundle = new Bundle();
                        serviceIntent.putExtra("BUNDLE_EXTRA", bundle);
                        serviceIntent.putExtra("OPEN_ACTIVITY", "ENTERPIN");
                        ContextCompat.startForegroundService(context, serviceIntent);
                    }
                }
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Alarm is Playing", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void showNotification(Context context, String title , String message) {
        if (constant.isNotificationShow) {
            constant.isNotificationShow = false;
            createNotificationChannel(context);
            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(123456, builder.build());
        }
    }


    /*if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_POWER_CONNECTED)) {
                Log.e("RECEIVER " , "CONNECTED " + Constant.lastID);
                constant.isCableConnected = true;
                Toast.makeText(context, " CONNECTED ", Toast.LENGTH_SHORT).show();
                String set_alarm = dbHelper.getPerticularAlarmType(String.valueOf(Constant.lastID));
                if (sh.getAlarmAlwardySet(context)){
                    if (set_alarm.equalsIgnoreCase("TIME")) {
                        Log.e("RECEIVER " , set_alarm  + " SET ALS");
                        AlarmData alarm = new DBHelper(context).getPerticularAlarmData(String.valueOf(Constant.lastID));
                        Log.e("RECEIVER " , alarm.getCurrent_date_time() + " SET ALS");
                        SimpleDateFormat format = new SimpleDateFormat(" MMM dd yyyy HH:mm:ss", Locale.getDefault());
                        try {
                            Date date1 = format.parse(alarm.getCurrent_date_time());
                            String currentDateandTime = format.format(new Date());
                            Date date2 = format.parse(currentDateandTime);
                            assert date2 != null;
                            assert date1 != null;
                            long millis = date2.getTime() - date1.getTime();

                            long minutes = (millis / (1000 * 60)) % 60;
                            long hours = millis / (1000 * 60 * 60);

                            long difference_hours = alarm.getSelected_hour() - hours;
                            long difference_min = alarm.getSelected_minute() - minutes ;

                            if (difference_hours > 0 && difference_min > 0 ){
                                alarm.setSelected_minute((int) difference_min);
                                alarm.setSelected_hour((int) difference_hours);
                            }
                            Log.e("RECEIVER " , "hours  : " + hours + " minutes : " + minutes );
                            Log.e("RECEIVER " , "difference_hours  : " + difference_hours + " difference_min : " + difference_min );
                            AlarmReceiver.setAlarm(context , alarm);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("CATCh" , e.getMessage() + " ");
                        }
                    }
                }
            }
            else if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_POWER_DISCONNECTED)) {
                Log.e("RECEIVER " , "DISCONNECTED");
                constant.isCableConnected = false;
                Toast.makeText(context, " DISCONNECTED  ", Toast.LENGTH_SHORT).show();
                String set_alarm = dbHelper.getPerticularAlarmType(String.valueOf(Constant.lastID));
                if (Constant.lastID != 0 ) {
                    if (sh.getAlarmAlwardySet(context)) {
                        Log.e("BATTERY", set_alarm + " xx");
                        if (set_alarm.equalsIgnoreCase("PERCENTAGE")) {
                            showNotification(context , context.getResources().getString(R.string.app_name) , "Your Selected percentage not occurs");
                        } else if (set_alarm.equalsIgnoreCase("TIME")) {
                            showNotification(context , context.getResources().getString(R.string.app_name) , "Your Selected time not occurs");
                            AlarmReceiver.cancelReminderAlarm(context, Constant.lastID);
                        }
                        AlarmData alarmData = new AlarmData();
                        SimpleDateFormat sdf = new SimpleDateFormat(" MMM dd yyyy HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        alarmData.setUnplagg_date_time(currentDateandTime);
                        alarmData.setUnplugg_percentage(constant.getCurrentBatteryStutus(context));
                        new DBHelper(context).updateLastUnpluggData(Constant.lastID, alarmData , false);
                    }
                }
                Log.e("AVT" , "constant.isSetTherftAlarm " + constant.isSetTherftAlarm  );
                if (constant.isSetTherftAlarm){
                    if (sh.getTheftAlarmOcured(context)) {
                        sh.setTheftAlarmOcured(context, false);
                        Log.e("AVT", "constant.isSetTherftAlarm is true ");

                        Intent serviceIntent = new Intent(context, AlarmService.class);
                        Bundle bundle = new Bundle();
                        serviceIntent.putExtra("BUNDLE_EXTRA", bundle);
                        serviceIntent.putExtra("OPEN_ACTIVITY", "ENTERPIN");
                        ContextCompat.startForegroundService(context, serviceIntent);

                        Intent intentEnter = new Intent(context, EnterPinActivity.class);
                        intentEnter.putExtra("WHEN_CALL", "TherftAlarm");
                        context.startActivity(intentEnter);
                    }
                }
            }*/
}