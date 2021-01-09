package com.jayden.batteryalarm.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.jayden.batteryalarm.activity.AlarmRingingActivity;
import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.service.AlarmService;
import com.jayden.batteryalarm.service.WakeLocker;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.templates.DBHelper;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class AlarmReceiver  extends BroadcastReceiver {

    static Constant constant = Constant.getInstance();
    private AlarmData alarm;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("AlarmReceiver", "intent not null");
        Bundle bundleintent = intent.getBundleExtra("BUNDLE_EXTRA");
        if (bundleintent != null) {
            Log.e("AlarmReceiver", "bundle not null");
            alarm = bundleintent.getParcelable("ALARM_KEY");
        }
        if (alarm == null) {
            Log.e("AlarmReceiver", "Alarm is null", new NullPointerException());
        } else {
            WakeLocker.acquire(context);
            Log.e("AlarmReceiver", "Alarm is Active onReceiver" + alarm.toString());
            new SharedPreferencesApplication().setAlarmAlwardySet(context , false);

            AlarmData alarmData = new AlarmData();
            SimpleDateFormat sdf = new SimpleDateFormat(" MMM dd yyyy HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            alarmData.setAlarm_states("COMPLETE");
            alarmData.setUnplagg_date_time(currentDateandTime);
            alarmData.setUnplugg_percentage(Constant.getInstance().getCurrentBatteryStutus(context));

            if (alarmData.getUnique_id() == 0) {
                new DBHelper(context).updateLastUnpluggData(Constant.lastID, alarmData, true);
            }
            else {
                new DBHelper(context).updateLastUnpluggData(alarmData.getUnique_id(), alarmData, true);
            }

            new SharedPreferencesApplication().setLastSetAlarmID(context ,  0);

            Intent intent1 = new Intent(context, AlarmRingingActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Bundle bundle = new Bundle();
            bundle.putParcelable("ALARM_KEY", alarm);
            intent1.putExtra("BUNDLE_EXTRA", bundle);
            intent1.putExtra("WHICH", "receiver");
            context.startActivity(intent1);
            constant.alarmDatabackup = alarm;

            Log.e("AlarmReceiver", "ALARM ID  IN RECEIVER : " + alarm.getUnique_id());
            Intent serviceIntent = new Intent(context, AlarmService.class);
            final Bundle bundle1 = new Bundle();
            bundle1.putParcelable("ALARM_KEY", alarm);
            serviceIntent.putExtra("BUNDLE_EXTRA", bundle1);
            serviceIntent.putExtra("OPEN_ACTIVITY" , "ALARMRING" );
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

    private static PendingIntent launchAlarmLandingPage(Context ctx, AlarmData alarm) {
        return PendingIntent.getActivity(ctx, alarm.getUnique_id(), launchIntent(ctx), FLAG_UPDATE_CURRENT
        );
    }

    private static Intent launchIntent(Context ctx) {
        return new Intent(ctx , AlarmRingingActivity.class);
    }

    public static void  setAlarm(Context context, AlarmData alarm){
        Log.e("AlarmReceiver " , " setAlarm " + alarm.toString());
        final Intent intent ;
        intent = new Intent(context, AlarmReceiver.class);
        final Bundle bundle = new Bundle();
        bundle.putParcelable("ALARM_KEY", alarm);
        intent.putExtra("BUNDLE_EXTRA", bundle);
        PendingIntent pIntent  = PendingIntent.getBroadcast(
                context,
                alarm.getUnique_id(),
                intent,
                FLAG_UPDATE_CURRENT
        );

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR , alarm.getSelected_hour());
        calendar.add(Calendar.MINUTE , alarm.getSelected_minute());
        alarm.setAlarmTime(calendar.getTimeInMillis());

        ScheduleAlarm.with(context).schedule(alarm, pIntent);
    }

    public static void cancelReminderAlarm(Context context, int alarm_id ) {
        Log.e("AlarmReceiver" , " cancelReminderAlarm  "+ alarm_id +  " cc");
        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm_id,
                intent,
                FLAG_UPDATE_CURRENT
        );

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pIntent);
        }
    }

    private static class ScheduleAlarm {

        private final Context ctx;
        private final AlarmManager am;
        private ScheduleAlarm( AlarmManager am,  Context ctx) {
            this.am = am;
            this.ctx = ctx;
        }
        static ScheduleAlarm with(Context context) {
            final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(am == null) {
                throw new IllegalStateException("AlarmManager is null");
            }
            return new ScheduleAlarm(am, context);
        }
        void schedule(AlarmData alarm, PendingIntent pi) {
            Log.e("ALARM RECEIVER " ,   " schedule  SS");
            if(SDK_INT > LOLLIPOP) {
                am.setAlarmClock(new AlarmManager.AlarmClockInfo(alarm.getAlarm_time(), launchAlarmLandingPage(ctx, alarm)), pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, alarm.getAlarm_time(), pi);
            }
        }
    }
}