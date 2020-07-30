package com.example.chirp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import static com.example.chirp.GoogleDriveService.getGoogleDriveService;


public class ShareRecordingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleDriveService mDriveServiceHelper;
    private GoogleSignInAccount account;
    private static final String TAG = "GoogleDriveUpload";
    private TextView email = null;

    private com.beardedhen.androidbootstrap.BootstrapButton uploadFile;

    public static String driveSharingLink;

    private String fileName = RecordAudioActivity.fileName;
    private static final int REQUEST_PERMISSION = 200;

    private boolean permissionAccepted = false;
    static String file_id = null;

    private String [] permissions = {Manifest.permission.INTERNET};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION:
                permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionAccepted) finish();
    }

    public void postTweet(View view) {
        //Go to PostTweetActivity
        Intent intent = new Intent(this, PostTweetActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_recording);
        initView();

        //Disable the button until the shareable link has been generated
        com.beardedhen.androidbootstrap.BootstrapButton tweetButton = findViewById(R.id.post_tweet);
        tweetButton.setEnabled(false);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);

        /**Upload a File onClickListener
         * If this is done successfully it will generate the Shareable Link for the file & supply it to the tweet Composer in PostTweetActivity */
        uploadFile.setOnClickListener(view -> {
            if (mDriveServiceHelper == null) {
                System.out.println("mDriveServiceHelper == null");
                return;
            }
            mDriveServiceHelper.uploadFile(new java.io.File(fileName), "audio/3gpp", null)
                    .addOnSuccessListener(GoogleDriveFiles -> {
                        Gson gson = new Gson();
                        Log.d(TAG, "onSuccess: " + gson.toJson(GoogleDriveFiles));

                        //Get the shareable Drive link
                        mDriveServiceHelper.GetShareableLink()
                                .addOnSuccessListener(s -> {
                                    //Set the driveSharingLink variable equal to the url generated from the GetShareableLink method
                                    driveSharingLink = s;
                                    //Enable the Share on Twitter button now that the link is not equal to null
                                    tweetButton.setEnabled(true);

                                    //Debugging
                                    Log.d(TAG, "onSuccess: " + s);

                                    //Provide success feedback to user
                                    Context ctx = getApplicationContext();
                                    CharSequence successMessage = "File successfully uploaded";
                                    int duration = Toast.LENGTH_SHORT;



                                    Toast toast = Toast.makeText(ctx, successMessage, duration);
                                    toast.show();
                                })
                                .addOnFailureListener(e -> {
                                    //Debugging
                                    Log.d(TAG, "onFailure: " + e.getMessage());

                                    //Provide failure feedback to user
                                    Context ctx = getApplicationContext();
                                    CharSequence failureMessage = "File upload failed. Please restart the application and try again.";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(ctx, failureMessage, duration);
                                    toast.show();
                                });
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account == null) {
            signIn();
        }
        else {
            email.setText(account.getEmail());
            mDriveServiceHelper = new GoogleDriveService(getGoogleDriveService(getApplicationContext(), account, "Chirp"));
        }
    }

    //Sign into Google Drive
    private void signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(getApplicationContext(), signInOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);

    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());
                        email.setText(googleSignInAccount.getEmail());
                        mDriveServiceHelper = new GoogleDriveService(getGoogleDriveService(getApplicationContext(), googleSignInAccount, "appName"));

                        Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to sign in.", e);
                    }
                });
    }

    //Initialise view elements
    private void initView() {
        uploadFile = findViewById(R.id.upload_file);
        email = findViewById(R.id.email);
    }
}