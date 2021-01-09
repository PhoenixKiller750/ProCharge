package com.jayden.batteryalarm.util;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesApplication {

    public static final String MYPREFERENCES = "MyPreference" ;

    public String GETSTARTED = "GETSTARTED";
    public String SETSLIENT = "SETSLIENT";
    public String FLASHLIGHT = "FLASHLIGHT";
    public String SETALARMMTH = "SETALARMMTH";
    public String SETALARPERSENTAG ="SETALARPERSENTAG";
    public String KEYUSERPIN= "KEYUSERPIN";
    public String ALARMALWARDYSET = "ALARMALWARDYSET";
    public String FINGUREPRINTALLOW = "FINGUREPRINTALLOW";
    public String INTRUDERCOUNT = "INTRUDERCOUNT";
    public String INAPPDONE = "INAPPDONE";
    public String THEFTRUNNING = "THEFTRUNNING";
    public String LASTSETALARMID = "LASTSETALARMID";


    //TODO Set and Get get Started  or not
    public boolean getGetStarted(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(GETSTARTED)) {
            return sharedPreferences.getBoolean(GETSTARTED, false);
        }
        return false;
    }

    public void setGetStarted(Context context , Boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(GETSTARTED, set);
        editor.apply();
    }

    //TODO Switch is slient
    public void setSlientSwich(Context context, boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SETSLIENT, set);
        editor.apply();
    }

    public boolean getSlientSwich(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(SETSLIENT)) {
            return sharedPreferences.getBoolean(SETSLIENT, false);
        }
        return false;
    }

    //TODO Flash Light
    public void setFlashLight(Context context, boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FLASHLIGHT, set);
        editor.apply();
    }

    public boolean getFlashLight(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(FLASHLIGHT)) {
            return sharedPreferences.getBoolean(FLASHLIGHT, false);
        }
        return false;
    }

    //TODO setAlarm Method
    public void setAlarmMethod(Context context, String set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETALARMMTH, set);
        editor.apply();
    }

    public String getAlarmMethod(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(SETALARMMTH)){
            return sharedPreferences.getString(SETALARMMTH , "SET_PERCNT");
        }
        return "SET_PERCNT";
    }

    //TODO set and  get Alarm Persentage time
    public void setAlarmPercentage(Context context, int set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SETALARPERSENTAG, set);
        editor.apply();
    }

    public int getAlarmPercentage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(SETALARPERSENTAG)){
            return sharedPreferences.getInt(SETALARPERSENTAG , 0);
        }
        return 0;
    }

    //TODO Alarm Alwardy Set or not
    public void setAlarmAlwardySet(Context context, boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALARMALWARDYSET , set);
        editor.apply();
    }

    public boolean getAlarmAlwardySet(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(ALARMALWARDYSET)) {
            return sharedPreferences.getBoolean(ALARMALWARDYSET , false);
        }
        return false;
    }

    //TODO Set User Pin
    public void setUserPin(Context context, String set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEYUSERPIN , set);
        editor.apply();
    }

    public String getUserPin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(KEYUSERPIN)) {
            return sharedPreferences.getString(KEYUSERPIN , "");
        }
        return "";
    }

    //TODO User Fingure print allow or not
    public void setUserFingurePrint(Context context, boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FINGUREPRINTALLOW , set);
        editor.apply();
    }

    public boolean getUserFingurePrint(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(FINGUREPRINTALLOW)) {
            return sharedPreferences.getBoolean(FINGUREPRINTALLOW , false);
        }
        return false;
    }

    //TODO Intruder count
    public void setIntruderimagecount(Context context, int set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INTRUDERCOUNT , set);
        editor.apply();
    }

    public int getIntruderimagecount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(INTRUDERCOUNT)) {
            return sharedPreferences.getInt(INTRUDERCOUNT , 1);
        }
        return 1;
    }

    //TODO Thefrt Alarm Running or not
    public boolean getTheftAlarmOcured(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(THEFTRUNNING)) {
            return sharedPreferences.getBoolean(THEFTRUNNING , true);
        }
        return true;
    }

    public void setTheftAlarmOcured(Context context, boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEFTRUNNING , set);
        editor.apply();
    }

    //TODO InApp Done or not
    public boolean getInAppDone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(INAPPDONE)) {
            return sharedPreferences.getBoolean(INAPPDONE , false);
        }
        return false;
    }

    public void setInAppDone(Context context, boolean set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(INAPPDONE , set);
        editor.apply();
    }

    //TODO InApp Done or not
    public int getLastSetAlarmID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(LASTSETALARMID)) {
            return sharedPreferences.getInt(LASTSETALARMID , 0);
        }
        return 0;
    }

    public void setLastSetAlarmID(Context context, int set) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LASTSETALARMID , set);
        editor.apply();
    }

}