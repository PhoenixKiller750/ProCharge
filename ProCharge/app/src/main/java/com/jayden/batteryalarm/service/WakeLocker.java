package com.jayden.batteryalarm.service;

import android.content.Context;
import android.os.PowerManager;

import static android.content.Context.POWER_SERVICE;

public class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Battery:MyWakelockTag");
        wakeLock.acquire(20*60*1000L /*20 minutes*/);
    }

    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}
