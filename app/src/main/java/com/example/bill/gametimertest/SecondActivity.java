package com.example.bill.gametimertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private TextView timerText;
    private TextView activityName;
    private Button switchActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        timerText = (TextView) findViewById(R.id.gameTimerTextSecond);
        activityName = (TextView) findViewById(R.id.activityNameSecond);
        activityName.setText(TAG);
        switchActivityButton = (Button) findViewById(R.id.switchToMainActivityButton);
        switchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SecondActivity.this, MainActivity.class);
                SecondActivity.this.startActivity(myIntent);
            }
        });
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            Log.i(TAG, "Countdown seconds remaining: " +  millisUntilFinished / 1000);
            long secondsRemaining = millisUntilFinished/1000;
            long minutes = 0;
            long seconds = 0;
            if (secondsRemaining >= 60)
            {
                minutes = secondsRemaining / 60;
                seconds = (secondsRemaining % 60);
            }
            if (seconds < 10) {
                timerText.setText(Long.toString(minutes) + ":0" + Long.toString(seconds));
            }
            else {
                timerText.setText(Long.toString(minutes) + ":" + Long.toString(seconds));
            }
        }
    }
}
