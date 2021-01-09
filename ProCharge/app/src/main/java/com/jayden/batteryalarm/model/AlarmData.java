package com.jayden.batteryalarm.model;

import android.os.Parcel;
import android.os.Parcelable;


public class AlarmData implements Parcelable {

    private int unique_id , target_percentage , current_percentage , selected_hour , selected_minute , unplugg_percentage ;
    private String alarm_type , alarm_states , current_date_time , unplagg_date_time;
    private long alarm_time ;

    public AlarmData() {
    }

    /*public AlarmData(Parcel in) {
        id = in.readInt();
        type  = in.readString();
        time = in.readLong();
    }*/

    public static final Creator<AlarmData> CREATOR = new Creator<AlarmData>() {
        @Override
        public AlarmData createFromParcel(Parcel in) {
            return new AlarmData();
        }

        @Override
        public AlarmData[] newArray(int size) {
            return new AlarmData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(unique_id);
        parcel.writeString(alarm_type);
        parcel.writeString(alarm_states);
        parcel.writeString(current_date_time);
        parcel.writeString(unplagg_date_time);
        parcel.writeInt(target_percentage);
        parcel.writeInt(current_percentage);
        parcel.writeInt(selected_hour);
        parcel.writeInt(selected_minute);
        parcel.writeInt(unplugg_percentage);
        parcel.writeLong(alarm_time);
    }

    public int getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(int unique_id) {
        this.unique_id = unique_id;
    }

    public int getTarget_percentage() {
        return target_percentage;
    }

    public void setTarget_percentage(int target_percentage) {
        this.target_percentage = target_percentage;
    }

    public int getCurrent_percentage() {
        return current_percentage;
    }

    public void setCurrent_percentage(int current_percentage) {
        this.current_percentage = current_percentage;
    }

    public int getSelected_hour() {
        return selected_hour;
    }

    public void setSelected_hour(int selected_hour) {
        this.selected_hour = selected_hour;
    }

    public int getSelected_minute() {
        return selected_minute;
    }

    public void setSelected_minute(int selected_minute) {
        this.selected_minute = selected_minute;
    }

    public int getUnplugg_percentage() {
        return unplugg_percentage;
    }

    public void setUnplugg_percentage(int unplugg_percentage) {
        this.unplugg_percentage = unplugg_percentage;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public String getAlarm_states() {
        return alarm_states;
    }

    public void setAlarm_states(String alarm_states) {
        this.alarm_states = alarm_states;
    }

    public String getCurrent_date_time() {
        return current_date_time;
    }

    public void setCurrent_date_time(String current_date_time) {
        this.current_date_time = current_date_time;
    }

    public String getUnplagg_date_time() {
        return unplagg_date_time;
    }

    public void setUnplagg_date_time(String unplagg_date_time) {
        this.unplagg_date_time = unplagg_date_time;
    }

    public void setAlarmTime(long alarm_time) {
        this.alarm_time = alarm_time;
    }

    public long getAlarm_time() {
        return alarm_time;
    }

    @Override
    public String toString() {
        return "AlarmData{" +
                "unique_id=" + unique_id +
                ", target_percentage=" + target_percentage +
                ", current_percentage=" + current_percentage +
                ", selected_hour=" + selected_hour +
                ", selected_minute=" + selected_minute +
                ", unplugg_percentage=" + unplugg_percentage +
                ", alarm_type='" + alarm_type + '\'' +
                ", alarm_states='" + alarm_states + '\'' +
                ", current_date_time='" + current_date_time + '\'' +
                ", unplagg_date_time='" + unplagg_date_time + '\'' +
                '}';
    }


}