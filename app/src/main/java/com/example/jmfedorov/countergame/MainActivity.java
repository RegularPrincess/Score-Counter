package com.example.jmfedorov.countergame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jmfedorov.countergame.utils.SpeechResultProcessor;
import com.example.jmfedorov.countergame.utils.Timer;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int vibroLength = 100;
    private static final int REQUEST_MICROPHONE = 112;

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
    private float mSensorValue;
    private SensorManager mSensorManager;
    private float mSensorMaximum;

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

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (sensor != null) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorMaximum = sensor.getMaximumRange();
        }

        checkPermission();
        Speech.init(this, getPackageName());
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
                postPunch(counter, textView);
                break;
            default: break;
        }
        recognize();
        return counter;
    }

    private void postPunch(int counter, TextView textView){
        vibrator.vibrate(vibroLength);
        checkZero(counter);
        textView.setText(String.valueOf(counter));
    }


    private void resetAll() {
        int baseValue = defineBaseValue();
        oneCounter = baseValue;
        twoCounter = baseValue;
        one.setText(String.valueOf(oneCounter));
        two.setText(String.valueOf(twoCounter));
        timer.reset();
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
        if(counter <= 0){
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

    private void checkPermission() {
        int permissMicrophone = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permissMicrophone != PackageManager.PERMISSION_GRANTED) {
            makeRequestMicrophone();
        }
    }

    protected void makeRequestMicrophone() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_MICROPHONE);

    }



    private void recognize(){
        try {
            // you must have android.permission.RECORD_AUDIO granted at this point
            Speech.getInstance().startListening(new SpeechDelegate() {
                @Override
                public void onStartOfSpeech() {
                    Log.i("speech", "speech recognition is now active");
                }

                @Override
                public void onSpeechRmsChanged(float value) {
                    Log.d("speech", "rms is now: " + value);
                }

                @Override
                public void onSpeechPartialResults(List<String> results) {
                    StringBuilder str = new StringBuilder();
                    for (String res : results) {
                        str.append(res).append(" ");
                    }

                    Log.i("speech", "partial result: " + str.toString().trim());
                }

                @Override
                public void onSpeechResult(String result) {
                    int who = SpeechResultProcessor.getWho(result);
                    int howmany = 0;
                    try {
                        howmany = SpeechResultProcessor.getHowmany(result);
                    } catch (Exception e){
                        return;
                    }

                    switch (who){
                        case SpeechResultProcessor.RED:
                            twoCounter += howmany;
                            postPunch(twoCounter, two);
                            break;
                        case SpeechResultProcessor.BLUE:
                            oneCounter += howmany;
                            postPunch(oneCounter, one);
                            break;
                        case SpeechResultProcessor.NIL:
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Не распозналось")
                                    .setMessage(result);
                            AlertDialog alert = builder.create();
                            alert.show();
                            Log.i("speech", "result: " + result);
                    }
                }
            });
        } catch (SpeechRecognitionNotAvailable exc) {
            Log.e("speech", "Speech recognition is not available on this device!");
        } catch (GoogleVoiceTypingDisabledException exc) {
            Log.e("speech", "Google voice typing must be enabled!");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mSensorValue = event.values[0];
        if (mSensorValue < mSensorMaximum) {
            vibrator.vibrate(vibroLength);
            recognize();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().shutdown();
    }
}
