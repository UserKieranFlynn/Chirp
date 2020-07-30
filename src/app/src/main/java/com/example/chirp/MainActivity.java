package com.example.chirp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TypefaceProvider.registerDefaultIconSets();
    }

    public void recordTweet(View view) {
        //Go to RecordAudioActivity
        Intent intent = new Intent(this, RecordAudioActivity.class);
        startActivity(intent);
    }

    public void goLive(View view) {
        //Go to GoLiveSignInActivity
        Intent intent = new Intent(this, GoLiveSignInActivity.class);
        startActivity(intent);
    }

    public void browseChirp(View view) {
        //Go to BrowseSignInActivity
        Intent intent = new Intent(this, BrowseSignInActivity.class);
        startActivity(intent);
    }

    public void userTweets(View view) {
        //Go to UserSignInActivity
        Intent intent = new Intent(this, UserSignInActivity.class);
        startActivity(intent);
    }
}

