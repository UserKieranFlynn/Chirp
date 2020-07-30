package com.example.chirp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GoLiveOptionsActivity extends AppCompatActivity {

    String ScreenName;

    private TextView currentTime = null;
    private com.beardedhen.androidbootstrap.BootstrapEditText hoursToListenFor = null;

    private com.beardedhen.androidbootstrap.BootstrapButton btnSubmit = null;
    private com.beardedhen.androidbootstrap.BootstrapEditText hashtag = null;

    private String userEnteredHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_live);

        //Use the displayCurrentTime function to get the current time in HH:mm:ss format
        currentTime = findViewById(R.id.currentTime);
        currentTime.setText("The current time is: "+ displayCurrentTime());

        ScreenName = getIntent().getStringExtra("username");

        hoursToListenFor = findViewById(R.id.hoursToListenFor);
        hashtag = findViewById(R.id.hashtag);
        btnSubmit = findViewById(R.id.btnSend);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEnteredHours = hoursToListenFor.getText().toString();
                userEnteredHours = listenUntilTime(userEnteredHours);

                if (hashtag.getText().toString().isEmpty()) {
                    Toast.makeText(GoLiveOptionsActivity.this, "Please input a hashtag.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(GoLiveOptionsActivity.this, GoLiveTweetsActivity.class);
                    intent.putExtra("time", userEnteredHours);
                    intent.putExtra("hashtag", hashtag.getText().toString());
                    intent.putExtra("username", ScreenName);
                    startActivity(intent);
                }
            }
        });
    }

    //Function to display the current time
    public String displayCurrentTime() {
        String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        return currentTimeString;
    }

    //Function to add the user entered number of hours to the current time
    public String listenUntilTime(String userEnteredHours) {
        Integer userEnteredHoursInt = Integer.parseInt(userEnteredHours);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, +userEnteredHoursInt);
        String listenUntilTimeString = new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
        return listenUntilTimeString;
    }
}

