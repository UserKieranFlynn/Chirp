package com.example.chirp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TwitterDetailsActivity extends AppCompatActivity {
    TextView name;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitterprofile);

        user=getIntent().getStringExtra("username");
        name=(TextView)findViewById(R.id.nametextView);
        name.setText(user);

    }
}