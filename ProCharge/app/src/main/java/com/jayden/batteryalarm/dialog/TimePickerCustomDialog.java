package com.jayden.batteryalarm.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.jayden.batteryalarm.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TimePickerCustomDialog extends Dialog {
    Context context;
    public static int selectedhour = 0 , selectedmin = 1 ;
    public static boolean selectvaluedonetime = false ;

    public TimePickerCustomDialog(Context context, int dialogCustomTheme, @NonNull Activity activity) {
        super(context , dialogCustomTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_picker);

        TimePicker mTimePicker = findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(0);
        mTimePicker.setCurrentMinute(1);
        setIntervalTimePicker(mTimePicker);

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedhour = timePicker.getHour();
                    selectedmin = timePicker.getMinute() * 5;
                }
                else {
                    selectedhour = timePicker.getCurrentHour();
                    selectedmin = timePicker.getCurrentMinute() * 5;
                }
            }
        });

        TextView ok_btn = findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectvaluedonetime = true ;
                dismiss();
            }
        });

        TextView cancle_btn = findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectvaluedonetime = false ;
                dismiss();
            }
        });
    }

    private void setIntervalTimePicker(TimePicker timePicker) {
        final int TIME_PICKER_INTERVAL = 5;
        final int HOUR_PICKER_INTERVAL = 8;
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");

            Field field = classForid.getField("minute");
            NumberPicker minuteSpinner = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));
            minuteSpinner.setWrapSelectorWheel(true);

            NumberPicker hourSpinner = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "hour", "id", "android"));
            hourSpinner.setMinValue(0);
            hourSpinner.setMaxValue((32 / HOUR_PICKER_INTERVAL) - 1);


            List<String> mDisplayedValuesHr = new ArrayList<>();
            for (int j = 0; j <= 24; j += HOUR_PICKER_INTERVAL) {
                if (j == 0)
                    mDisplayedValuesHr.add(String.format("%02d", j));
                else
                    mDisplayedValuesHr.add(String.format("%02d", j / HOUR_PICKER_INTERVAL));
            }
            hourSpinner.setDisplayedValues(mDisplayedValuesHr.toArray(new String[mDisplayedValuesHr.size()]));
            hourSpinner.setWrapSelectorWheel(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
