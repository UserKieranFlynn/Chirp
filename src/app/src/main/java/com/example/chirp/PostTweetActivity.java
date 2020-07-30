package com.example.chirp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;


public class PostTweetActivity extends AppCompatActivity {
    String driveSharingLink = "\n" + ShareRecordingActivity.driveSharingLink; //Sharing link used to share the google drive file
    String chirpHashtag = "\n#chirpaudioupdates"; //Our designated Hashtag to find tweets by this application easily

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tweet);

        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(chirpHashtag + " " + driveSharingLink);
        builder.show();
    }

}





