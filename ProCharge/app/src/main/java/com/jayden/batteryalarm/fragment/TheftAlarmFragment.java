package com.jayden.batteryalarm.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.activity.ActivateAlarmActivity;
import com.jayden.batteryalarm.dialog.SettingDialog;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;

public class TheftAlarmFragment extends Fragment {
    Constant constant = Constant.getInstance();
    TextView current_per_frg;
    ProgressBar progressBar;
    View rootView;
    SharedPreferencesApplication sh = new SharedPreferencesApplication();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_theft_alarm , container , false);

        //checkPermission();

        current_per_frg = rootView.findViewById(R.id.txt_per);
        current_per_frg.setText(String.valueOf(constant.getCurrentBatteryStutus(requireContext())));

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setProgress(constant.getCurrentBatteryStutus(requireContext()));

        ImageView img_settings = rootView.findViewById(R.id.img_settings);
        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingDialog settingDialog = new SettingDialog(requireContext(), R.style.AppTheme ,getActivity());
                settingDialog.show();
                settingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        adShowCode();
                    }
                });
            }
        });

        Button activate_alarm = rootView.findViewById(R.id.activate_alarm);
        activate_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActivateAlarm();
            }
        });

        return rootView;
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    private void setActivateAlarm() {
        Log.e("USERPIN" , sh.getUserPin(requireContext()) + " ");
        if (sh.getUserPin(requireContext()).equalsIgnoreCase("")){
            Intent intent = new Intent(getContext(), com.jayden.lockscreen.EnterPinActivity.class);
            intent.putExtra("WHEN_CALL", "THEFTALARMSET");
            requireContext().startActivity(intent);
        }
        else {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("isCableConnected", constant.isCableConnected + " ");
                if (constant.isCableConnected) {
                    constant.isSetTherftAlarm = true;
                    sh.setTheftAlarmOcured(requireContext() , true);

                    Intent activateAlarm = new Intent(getContext(), ActivateAlarmActivity.class);
                    startActivity(activateAlarm);
                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext(), R.style.alert_dialog);
                    builder.setTitle("Alert ");
                    builder.setMessage("Please Connect to charging");
                    builder.setPositiveButton(requireContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //Creating dialog box
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();

                    Button btn1 = dialog.findViewById(android.R.id.button1);
                    btn1.setTextColor(requireContext().getResources().getColor(R.color.colorPrimary));
                }
            } else {
                checkPermission();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adShowCode();
    }

    private void adShowCode(){
        RelativeLayout rel_live_ad = rootView.findViewById(R.id.rel_live_ad);
        if (sh.getInAppDone(requireContext())){
            rel_live_ad.setVisibility(View.INVISIBLE);
        }


    }
}