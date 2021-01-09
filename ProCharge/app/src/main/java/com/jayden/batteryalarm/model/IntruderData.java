package com.jayden.batteryalarm.model;

public class IntruderData {
    int id;
    public String intruder_date;
    public String intruder_time;
    public String intruder_package;
    public String intruder_path;

    // Empty constructor
    public IntruderData() {

    }
    public IntruderData(int id, String intruder_date, String intruder_time, String intruder_package, String intruder_path) {
        this.id = id;
        this.intruder_date = intruder_date;
        this.intruder_time = intruder_time;
        this.intruder_package = intruder_package;
        this.intruder_path = intruder_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntruder_date() {
        return intruder_date;
    }

    public void setIntruder_date(String intruder_date) {
        this.intruder_date = intruder_date;
    }

    public String getIntruder_package() {
        return intruder_package;
    }

    public void setIntruder_package(String intruder_package) {
        this.intruder_package = intruder_package;
    }

    public String getIntruder_path() {
        return intruder_path;
    }

    public void setIntruder_path(String intruder_path) {
        this.intruder_path = intruder_path;
    }

    public String getIntruder_time() {
        return intruder_time;
    }

    public void setIntruder_time(String intruder_time) {
        this.intruder_time = intruder_time;
    }

    @Override
    public String toString() {
        return "IntruderData{" +
                ", intruder_date='" + intruder_date + '\'' +
                ", intruder_time='" + intruder_time + '\'' +
                ", intruder_package='" + intruder_package + '\'' +
                ", intruder_path='" + intruder_path + '\'' +
                '}';
    }
}
