package com.jayden.batteryalarm.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.activity.AlarmRingingActivity;
import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.util.FlashlightProvider;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;
import com.jayden.lockscreen.EnterPinActivity;

import java.io.IOException;
import java.util.Objects;

public class AlarmService extends Service {

    SharedPreferencesApplication sh = new SharedPreferencesApplication();
    private MediaPlayer mp;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    Vibrator vibrator;
    VibratePattern task;
    MyTaskParams params;
    AlarmData alarm;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("SERVICE" , "Service Called ");
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("BUNDLE_EXTRA");
            if (bundle != null)
                alarm = bundle.getParcelable("ALARM_KEY");
        }
        else{
            Log.e("TAG","ELSE CAALLED INTENT NULL");
            alarm  = Constant.getInstance().alarmDatabackup;
        }

        Intent notificationIntent = null;
        if (intent != null) {
            if (Objects.requireNonNull(intent.getStringExtra("OPEN_ACTIVITY")).equalsIgnoreCase("ENTERPIN")) {
                notificationIntent = new Intent(this, EnterPinActivity.class);
                notificationIntent.putExtra("WHEN_CALL", "TherftAlarm");
            }
            else {
                notificationIntent = new Intent(this, AlarmRingingActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                final Bundle bundle = new Bundle();
                bundle.putParcelable("ALARM_KEY", alarm);
                notificationIntent.putExtra("BUNDLE_EXTRA", bundle);
                notificationIntent.putExtra("WHICH", "service");
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alarm Activate")
                .setContentText("Your Alarm is Activated")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Alarm is Playing", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
        startForeground(2, notification);

        if (intent != null && Objects.requireNonNull(intent.getStringExtra("OPEN_ACTIVITY")).equalsIgnoreCase("ENTERPIN")) {
            playSiren();
        }
        else {
            playMusic();
        }

        return START_STICKY;
    }

    private void playSiren() {
        mp = MediaPlayer.create(this, R.raw.siren);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setVolume(15, 15);
        mp.setLooping(true);
        mp.start();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void playMusic() {
        String sound = String.valueOf(R.raw.demo);

        // Register Vibrator class
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //Alarm Sound Set Slient
        int userVol;
        if (sh.getSlientSwich(this)){
            userVol = 0 ;
            // get is Alarm need to vibrate when activate
            startTask();
        }
        else {
            userVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC , userVol , AudioManager.FLAG_PLAY_SOUND);
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setVolume(userVol , userVol);
        mp.setLooping(true);
        try {
            mp.setDataSource(this, Uri.parse("android.resource://com.jayden.batteryalarm/" + sound));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();

        //Alarm flash light on or off
        FlashlightProvider flashlightProvider = new FlashlightProvider(this);
        if (sh.getFlashLight(this)) {
            flashlightProvider.turnFlashlightOn();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("SERDEST","Service on DEstroy called ::" );

        FlashlightProvider flashlightProvider = new FlashlightProvider(this);
        if (sh.getFlashLight(this)) {
            flashlightProvider.turnFlashlightOff();
        }

        if(task != null) {
            task.cancel(true);
            task = null;
        }

        if (mp != null){
            mp.release();
            mp = null ;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {

    }

    private static class MyTaskParams {
        int  dash, gap;
        MyTaskParams (int dash, int gap) {
            this.dash = dash;
            this.gap = gap;
        }
    }

    private void startTask() {
        params = new MyTaskParams(500,500);
        task = new VibratePattern();
        task.execute(params);
    }

    public Integer onVibrate (Integer dash, Integer gap) {
        long[] pattern = {
                0, dash, gap ,dash, gap
        };
        if(Build.VERSION.SDK_INT >= 26)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern,-1));
        else
            vibrator.vibrate(pattern, -1);

        return dash + gap + dash +  gap +  gap;
    }

    @SuppressLint("StaticFieldLeak")
    private class VibratePattern extends AsyncTask<MyTaskParams, Void, Integer> {
        @Override
        protected Integer doInBackground(MyTaskParams... params) {
            int span;
            span = onVibrate(params[0].dash,params[0].gap);
            return span;
        }

        @Override
        protected void onPostExecute(Integer span) {
            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isCancelled()) {
                        startTask();
                    }
                }
            }, span);
        }
    }
}