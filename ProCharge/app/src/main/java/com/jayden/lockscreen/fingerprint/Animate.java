package com.jayden.lockscreen.fingerprint;

import android.annotation.TargetApi;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;

import androidx.appcompat.widget.AppCompatImageView;

public class Animate {
    @TargetApi(Build.VERSION_CODES.M)
    public static void animate(AppCompatImageView view, AnimatedVectorDrawable scanFingerprint) {
        view.setImageDrawable(scanFingerprint);
        scanFingerprint.start();
    }
}
