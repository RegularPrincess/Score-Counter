package com.example.jmfedorov.countergame;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jmfedorov.countergame.utils.Timer;

public class MainActivity extends AppCompatActivity {
    private static final int vibroLength = 100;

    private TextView one;
    private TextView two;
    private TextView timeView;
    private EditText baseValue;
    private ImageButton resetButton;
    private int oneCounter;
    private int twoCounter;
    private int width;
    private Timer timer;
    private Vibrator vibrator;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        one = (TextView)findViewById(R.id.one);
        two = (TextView)findViewById(R.id.two);
        timeView = (TextView)findViewById(R.id.tTime);
        baseValue = (EditText)findViewById(R.id.baseValue);
        resetButton = (ImageButton)findViewById(R.id.resetButton);
        width = getDisplayWidth();
        timer = new Timer(timeView);
        timer.start();
        vibrator = (Vibrator) getSystemService (VIBRATOR_SERVICE);
        resetAll();

        one.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                oneCounter = hendleClick(event, one, oneCounter);
                return true;
            }
        });

        two.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                twoCounter = hendleClick(event, two, twoCounter);
                return true;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });
    }

    private int hendleClick(MotionEvent event, TextView textView, int counter){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // получаем координаты касания
                float mX = event.getX();
                if(mX > width/2){
                    counter++;
                } else {
                    counter--;
                }
                vibrator.vibrate(vibroLength);
                checkZero(counter);
                textView.setText(String.valueOf(counter));
                break;
            default: break;
        }
        return counter;
    }


    private void resetAll() {
        int baseValue = defineBaseValue();
        oneCounter = baseValue;
        twoCounter = baseValue;
        one.setText(String.valueOf(oneCounter));
        two.setText(String.valueOf(twoCounter));
        timer.reset();
        vibrator.vibrate(vibroLength*4);
    }

    private int defineBaseValue() {
        return Integer.decode(baseValue.getText().toString());
    }

    private int getDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private void checkZero(int counter){
        if(counter == 0){
            playTrack();
        }
    }

    private void playTrack() {
        new Thread(){
            public void run() {
                mp = MediaPlayer.create(MainActivity.this, R.raw.track);
                mp.start();
            }
        }.start();
    }
}
