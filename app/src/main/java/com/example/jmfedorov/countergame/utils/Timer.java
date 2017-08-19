package com.example.jmfedorov.countergame.utils;

import android.os.CountDownTimer;
import android.widget.TextView;


public class Timer extends CountDownTimer {
    private static final int millisecondInSecond = 1000;
    private static final long infinity  = 1000000000;
    private Time time;
    private TextView timerView;

    public Timer(TextView timerView) {
        super(infinity, millisecondInSecond);
        time = new Time();
        this.timerView = timerView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        time.secondIncrement();
        renderTime();
    }

    @Override
    public void onFinish() {
        //Ничего не происходит, отчет продолжается
    }

    public String getTime(){
        return time.toString();
    }

    public void renderTime(){
        timerView.setText(time.toString());
    }

    public void reset(){
        time.reset();
    }
}
