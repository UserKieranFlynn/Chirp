package com.example.chirp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import static com.example.chirp.GoogleDriveService.getGoogleDriveService;


public class DisplayChirpTweetsActivity extends AppCompatActivity {

    String ScreenName;
    String TwitterToken;
    String TwitterSecret;
    ListView lv_browse_list;
    List<Status> statuses = new ArrayList<Status>();;
    ArrayList<String> al_text = new ArrayList<>();
    TwitterAdapter obj_adapter;

    //A List to store the File ID's found in doInBackground()
    //A List to store the File ID's found in doInBackground()
    List<String> fileIDList = new ArrayList<>();
    List<Integer> positions = new ArrayList<>();

    int i = 0;
    //Used for the downloading of files from google drive
    private GoogleDriveService mDriveServiceHelper;
    private GoogleSignInAccount account;

    //Used for the playing of downloaded tweets
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;

    private void startPlaying(int position) {
        player = new MediaPlayer();
        try {
            String pos = String.valueOf(position);
            String createdFileName = "/storage/emulated/0/Android/data/com.example.chirp/cache/downloadedTweet_" + pos + ".3gpp";
            player.setDataSource(createdFileName);
            player.prepare();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
            player.start();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.FILE_NOT_FOUND_EXCEPTION),Toast.LENGTH_LONG).show();
        } catch (IOException e1) {
            Log.e(LOG_TAG, e1.toString());
        }
    }


    private void stopPlaying() {
        player.release();
        player = null;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenName = getIntent().getStringExtra("username");
        TwitterToken = getIntent().getStringExtra("token");
        TwitterSecret = getIntent().getStringExtra("secret");

        setContentView(R.layout.activity_browse_tweets);
        lv_browse_list = (ListView)findViewById(R.id.lv_browse_list);

        lv_browse_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPlaying(positions.get(position));

            }
        });


        downloadTweets();
    }



    public void downloadTweets() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadTwitterTask().execute(ScreenName);
        } else {
            Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    // download twitter timeline after first checking to see if there is a network connection
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
        String CONSUMER_KEY = getResources().getString(R.string.CONSUMER_KEY);
        String CONSUMER_SECRET = getResources().getString(R.string.CONSUMER_SECRET);

        final ProgressDialog dialog = new ProgressDialog(DisplayChirpTweetsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Loading");
            dialog.setMessage("Please wait");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... screenNames) {
            String result = null;
            //Used to get the file id from the returned results of getTwitterStream
            String fileIDFromURL;
            //Used in pattern matching below to find the file id
            String pattern = "expanded_url\":\"https:\\/\\/drive.google.com\\/file\\/d\\/";

            if (ScreenName.length() > 0) {
                result = getTwitterStream(CONSUMER_KEY, CONSUMER_SECRET, TwitterToken, TwitterSecret);
                String resultContainingFileID = result;
                //Iterate through the returned Tweets looking for the pattern defined above. Any tweets containing a Google Drive Shareable link will contain this pattern.
                //Match for the pattern, extract the File ID, add this to the List & move the starting index of the string to the end of the previous File ID (to then look for the next pattern match)
                while (resultContainingFileID.contains(pattern)) {
                    //resultContainingFileID.indexOf(pattern) + pattern.length() so that index(0) is the start of the File ID
                    resultContainingFileID = resultContainingFileID.substring(resultContainingFileID.indexOf(pattern) + pattern.length());
                    //Get the FileID
                    fileIDFromURL = resultContainingFileID.substring(0, resultContainingFileID.indexOf("\\/view?"));
                    //Add it to the list
                    fileIDList.add(fileIDFromURL);
                    //Move the starting index to the end of the previous file ID
                    resultContainingFileID = resultContainingFileID.substring(resultContainingFileID.indexOf("\\/view?"));
                }
                //call the download file method
                downloadFile(fileIDList);
            }
            return result;
        }
        //Download a file using the fileIDs garnered in the above method
        public void downloadFile(List<String> fileIDFromURLList) {
            //Used to download the above file Id from google drive
            account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            mDriveServiceHelper = new GoogleDriveService(getGoogleDriveService(getApplicationContext(), account, "Chirp"));
            for (String fileID : fileIDFromURLList) {
                //Used to name each downloaded recording differently.
                Integer index = fileIDFromURLList.indexOf(fileID);
                String createdFileName = "/storage/emulated/0/Android/data/com.example.chirp/cache/downloadedTweet_" + index + ".3gpp";
                mDriveServiceHelper.downloadFile(new java.io.File(createdFileName), fileID);
            }
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            Log.e("result", result);
            dialog.dismiss();

            try {
                JSONArray jsonArray_data = new JSONArray(result);
                al_text.clear();

                for (int i = 0; i < jsonArray_data.length(); i++) {
                    JSONObject jsonObject = jsonArray_data.getJSONObject(i);
                    System.out.println(jsonObject);
                    JSONArray jsonHashtag = jsonObject.getJSONObject("entities").getJSONArray("hashtags");
                    for (int j = 0; j < jsonHashtag.length(); j++) {
                        if ((jsonHashtag.getString(j).toLowerCase().contains("chirp"))) {
                            al_text.add(jsonObject.getJSONObject("user").getString("screen_name") + System.lineSeparator() + jsonObject.getString("full_text"));
                            positions.add(i);}
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

                obj_adapter = new TwitterAdapter(getApplicationContext(), al_text);
                lv_browse_list.setAdapter(obj_adapter);

            }
        }

        private String getTwitterStream(String key, String consumer, String token, String secret) {
            String strInitialDataSet = null;
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();

                // GET THE CONSUMER KEY AND SECRET KEY FROM THE STRINGS XML

                builder.setDebugEnabled(true)
                        .setOAuthConsumerKey(key)
                        .setOAuthConsumerSecret(consumer)
                        .setOAuthAccessToken(token)
                        .setOAuthAccessTokenSecret(secret)
                        .setJSONStoreEnabled(true).setIncludeEntitiesEnabled(true).setIncludeMyRetweetEnabled(true);
                TwitterFactory tf = new TwitterFactory(builder.build());
                Twitter twitter = tf.getInstance();

                statuses = twitter.getHomeTimeline();
                strInitialDataSet = DataObjectFactory.getRawJSON(statuses);

            } catch (Exception e) {
                // TODO: handle exception
            }
            return strInitialDataSet;
        }
    }

