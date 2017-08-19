package com.example.jmfedorov.countergame.utils;


import java.util.*;
import java.util.Timer;

public class Time {
    private int second;
    private int minute;
    private int hour;

    public Time() {
        second = 0;
        minute = 0;
        hour = 0;
    }

    public void reset(){
        second = 0;
        minute = 0;
        hour = 0;
    }

    public void secondIncrement(){
        second++;
        if(second == 60){
            minuteIncrement();
            second = 0;
        }
    }

    public void minuteIncrement(){
        minute++;
        if(minute == 60){
            hour++;
            minute = 0;
        }
    }

    @Override
    public String toString() {
        return formatTime(hour) + ":" + formatTime(minute) + ":" + formatTime(second);
    }

    private String formatTime(int t){
        if(t < 10){
            return "0" + t;
        } else {
            return String.valueOf(t);
        }
    }
}
