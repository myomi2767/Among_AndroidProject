package com.example.among.calendar;

public class Events {
    String EVENT;
    String TIME;
    String DATE;
    String MONTH;
    String YEAR;

    public Events(String EVENT, String TIME, String DATE, String MONTH, String YEAR) {
        this.EVENT = EVENT;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
    }

    public String getEVENT() {
        return EVENT;
    }

    public String getTIME() {
        return TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setEVENT(String EVENT) {
        this.EVENT = EVENT;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }
}
