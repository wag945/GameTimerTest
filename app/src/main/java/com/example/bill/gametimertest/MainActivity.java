package com.example.bill.gametimertest;

import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView timerText;
    private TextView activityName;
    private Button switchActivityButton;
    private DatabaseReference gamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerText = (TextView) findViewById(R.id.gameTimerText);
        activityName = (TextView) findViewById(R.id.activityName);
        activityName.setText(TAG);
        switchActivityButton = (Button) findViewById(R.id.switchToSecondActivityButton);
        switchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, SecondActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        gamesRef = FirebaseDatabase.getInstance().getReference();
        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot gameSnapshot: dataSnapshot.getChildren()) {
                    String gameTimer = gameSnapshot.child("gameTimer").getValue(String.class);
                    if (gameTimer != null) {
                        Log.d("MainActivity", "gameTimer: " + gameTimer);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,
                        "onCanceled error",
                        Toast.LENGTH_LONG).show();
            }
        });
        startService(new Intent(this, BroadcastService.class));
        Log.i(TAG, "Started service");
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
        stopService(new Intent(this, BroadcastService.class));
        Log.i(TAG, "Stopped service");
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
                seconds = secondsRemaining % 60;
            }

            String timerTextStr= "";

            if (seconds < 10) {
                timerTextStr = Long.toString(minutes)+":0"+Long.toString(seconds);
                timerText.setText(timerTextStr);
            }
            else {
                timerTextStr = Long.toString(minutes)+":"+Long.toString(seconds);
                timerText.setText(timerTextStr);
            }

            gamesRef.child("gameTimer").setValue(timerTextStr);
        }
    }
}
