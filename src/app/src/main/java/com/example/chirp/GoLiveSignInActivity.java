package com.example.chirp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import androidx.appcompat.app.AppCompatActivity;

public class GoLiveSignInActivity extends AppCompatActivity {

    TwitterLoginButton loginButton;
    Button btnSubmit;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.CONSUMER_KEY), getResources().getString(R.string.CONSUMER_SECRET));
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_twitter_go_live_sign_in);

        name = (EditText) findViewById(R.id.txtName);



        /* Twitter */
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                /*
                  This provides TwitterSession as a result
                  This will execute when the authentication is successful
                 */
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                //Calling login method and passing twitter session
                login(session);
            }

            @Override
            public void failure(TwitterException exception) {
                //Displaying Toast message
                Toast.makeText(GoLiveSignInActivity.this, "Authentication failed!", Toast.LENGTH_LONG).show();
            }
        });
        btnSubmit = (Button) findViewById(R.id.btnSend);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    Toast.makeText(GoLiveSignInActivity.this, "Please write a name or sign into your Twitter.", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println(name.getText().toString());
                    Intent intent = new Intent(GoLiveSignInActivity.this, GoLiveGetAccountsActivity.class);
                    intent.putExtra("username", name.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void login(TwitterSession session) {
        String username = session.getUserName();
        Intent intent = new Intent(GoLiveSignInActivity.this, GoLiveGetAccountsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

}