package com.example.chirp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;

import static com.example.chirp.GoogleDriveService.getGoogleDriveService;


public class UsersOwnTweetsActivity extends AppCompatActivity {

    String ScreenName;
    ListView lv_user_list;
    ArrayList<String> al_text = new ArrayList<>();
    TwitterAdapter obj_adapter;

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
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
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
        setContentView(R.layout.activity_user_tweets);
        lv_user_list = (ListView)findViewById(R.id.lv_user_list);

        lv_user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPlaying(positions.get(position));
            }
        });

        downloadTweets();
    }

    // download twitter timeline after first checking to see if there is a network connection
    public void downloadTweets() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadTwitterTask().execute(ScreenName);
        } else {
            Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    // Uses an AsyncTask to download a Twitter user's timeline
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
        String CONSUMER_KEY = getResources().getString(R.string.CONSUMER_KEY);
        String CONSUMER_SECRET = getResources().getString(R.string.CONSUMER_SECRET);
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json";
        final ProgressDialog dialog = new ProgressDialog(UsersOwnTweetsActivity.this);

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
                result = getTwitterStream(ScreenName);
                //Result value is used in onPostExecute(), so a copy is made to be manipulated in the below for loop
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

        //Download a file using the fileID garnered in the above method
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
        @Override
        protected void onPostExecute(String result) {
            Log.e("result", result);
            dialog.dismiss();
            try {
                JSONArray jsonArray_data = new JSONArray(result);
                al_text.clear();

                for (int i = 0; i < jsonArray_data.length(); i++) {
                    JSONObject jsonObject = jsonArray_data.getJSONObject(i);
                    JSONArray jsonHashtag = jsonObject.getJSONObject("entities").getJSONArray("hashtags");
                    for (int j = 0; j < jsonHashtag.length(); j++) {
                        if ((jsonHashtag.getString(j).toLowerCase().contains("chirpaudioupdates"))) {
                            al_text.add(jsonObject.getJSONObject("user").getString("screen_name") + System.lineSeparator() + jsonObject.getString("text"));
                            positions.add(i);}
                    }
                    System.out.println("al_text: " + al_text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // send the tweets to the adapter for rendering
            obj_adapter = new TwitterAdapter(getApplicationContext(), al_text);
            lv_user_list.setAdapter(obj_adapter);
        }

        // convert a JSON authentication object into an Authenticated object
        private TwitterAuthentication jsonToAuthenticated(String rawAuthorization) {
            TwitterAuthentication auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, TwitterAuthentication.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return auth;
        }

        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }

        private String getTwitterStream(String screenName) {
            String results = null;
            try {
                // Step 1: Encode consumer key and secret
                String base64Encoded = createAndEncodeKeys();

                // Step 2: Obtain a bearer token
                String rawAuthorization = obtainBearerToken(base64Encoded);
                //Use the above authorization value to convert to a TwitterAuthentication value
                TwitterAuthentication auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(TwitterStreamURL + "?count=10&screen_name=" + ScreenName);
                    System.out.println(httpGet);

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    System.out.println("auth.access_token ========== " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");

                    // update the results with the body of the response
                    results = getResponseBody(httpGet);
                }

            } catch (IllegalStateException ex1) {
                System.out.println("IllegalStateException");
            }

            return results;
        }

        //Used to create & encode the Consumer keys used in the httpPost request. Called from within the getTwitterStream() method.
        private String createAndEncodeKeys() {
            String base64Encoded = null;
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

            } catch (UnsupportedEncodingException ex) {
                System.out.println("UnsupportedEncodingException");
            }
            return base64Encoded;

        }

        //Used to obtain the Bearer token for the GET request. Called from within the getTwitterStream() method.
        private String obtainBearerToken(String base64Encoded){
            String rawAuthorization = null;
            try {
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                rawAuthorization = getResponseBody(httpPost);
            } catch (UnsupportedEncodingException ex) {
                System.out.println("UnsupportedEncodingException");
            }
            return rawAuthorization;
        }
    }
}
