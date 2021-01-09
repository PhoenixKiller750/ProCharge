package com.jayden.batteryalarm.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.templates.Constant;

import java.util.ArrayList;
import java.util.List;

public class PerPickerCustomDialog extends Dialog {
    Context context;
    public static int selectdValue;
    public static boolean selectvaluedone = false ;
    NumberPicker numberPicker;

    public PerPickerCustomDialog(@NonNull Context context, int dialogCustomTheme, Activity activity) {
        super(context , dialogCustomTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_per_picker);

        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(20);
        numberPicker.setMinValue(1);
        final List<String> displayedValues = new ArrayList<>();
        for (int i = 5; i  <  110; i += 5 ) {
            displayedValues.add(String.format("%02d", i));
        }
        numberPicker.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));

        setPreValueSelected(context , numberPicker);

        selectdValue = numberPicker.getValue() * 5;
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                selectdValue = numberPicker.getValue() * 5;
            }
        });

        TextView ok_btn = findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectvaluedone = true ;
                dismiss();
            }
        });

        TextView cancle_btn = findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectvaluedone = false ;
                dismiss();
            }
        });
    }

    public static void setPreValueSelected(Context context , NumberPicker numberPicker)
    {
        int current_per = Constant.getInstance().getCurrentBatteryStutus(context);
        if (current_per > 0  && current_per < 5) {
            numberPicker.setValue(1);
            selectdValue = 5;
        }else if (current_per >= 5 && current_per < 10) {
            numberPicker.setValue(2);
            selectdValue = 10;
        }else if (current_per >= 10 && current_per < 15) {
            numberPicker.setValue(3);
            selectdValue = 15;
        }else if (current_per >= 15 && current_per < 20) {
            numberPicker.setValue(4);
            selectdValue = 20;
        }else if (current_per >= 20 && current_per < 25) {
            numberPicker.setValue(5);
            selectdValue = 25;
        }else if (current_per >= 25 && current_per < 30) {
            numberPicker.setValue(6);
            selectdValue = 30;
        }else if (current_per >= 30 && current_per < 35) {
            numberPicker.setValue(7);
            selectdValue = 35;
        }else if (current_per >= 35 && current_per < 40) {
            numberPicker.setValue(8);
            selectdValue = 40;
        }else if (current_per >= 40 && current_per < 45) {
            numberPicker.setValue(9);
            selectdValue = 45;
        }else if (current_per >= 45 && current_per < 50) {
            numberPicker.setValue(10);
            selectdValue = 50;
        }else if (current_per > 50  && current_per < 55) {
            numberPicker.setValue(11);
            selectdValue = 55;
        }else if (current_per >= 55 && current_per < 60) {
            numberPicker.setValue(12);
            selectdValue = 60;
        }else if (current_per >= 60 && current_per < 65) {
            numberPicker.setValue(13);
            selectdValue = 65;
        }else if (current_per >= 65 && current_per < 70) {
            numberPicker.setValue(14);
            selectdValue = 70;
        }else if (current_per >= 70 && current_per < 75) {
            numberPicker.setValue(15);
            selectdValue = 75;
        }else if (current_per >= 75 && current_per < 80) {
            numberPicker.setValue(16);
            selectdValue = 80;
        }else if (current_per >= 80 && current_per < 85) {
            numberPicker.setValue(17);
            selectdValue = 85;
        }else if (current_per >= 85 && current_per < 90) {
            numberPicker.setValue(18);
            selectdValue = 90;
        }else if (current_per >= 90 && current_per < 95) {
            numberPicker.setValue(19);
            selectdValue = 95;
        }else if (current_per >= 95 && current_per < 100) {
            numberPicker.setValue(20);
            selectdValue = 100;
        } else {
            numberPicker.setValue(20);
            selectdValue = 100;
        }
    }
}