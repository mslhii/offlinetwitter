package com.kritikalerror.twittext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Michael on 10/20/2014.
 */
public class OtherProfileActivity extends Activity {

    private TextView mUserName;
    private TextView mRealName;
    private TextView mSinceWhen;
    private TextView mFollowers;
    private TextView mBio;
    private TextView mLocation;
    private TextView mWeb;

    protected ProgressDialog mDownloadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherprofile);

        mUserName = (TextView) findViewById(R.id.profileusername);
        mRealName = (TextView) findViewById(R.id.profilename);
        mSinceWhen = (TextView) findViewById(R.id.whois);
        mFollowers = (TextView) findViewById(R.id.follow);
        mBio = (TextView) findViewById(R.id.otherbio);
        mLocation = (TextView) findViewById(R.id.otherloc);
        mWeb = (TextView) findViewById(R.id.otherweb);

        // Retrieve address from past activity
        Intent intent = getIntent();
        final String profileAddress = intent.getStringExtra("address");

        // Check database for existing user info, if not then send SMS query
        new GetProfileInfo(getApplicationContext(), profileAddress).execute();

        // RT button
        final Button reTweetButton = (Button) findViewById(R.id.retweet);
        reTweetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SMSHelpers.sendHiddenSMS(getApplicationContext(), "RETWEET " + profileAddress);
            }
        });

        // Favorite button
        final Button favoriteButton = (Button) findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SMSHelpers.sendHiddenSMS(getApplicationContext(), "FAVORITE " + profileAddress);
            }
        });

        // Leave button
        final Button leaveButton = (Button) findViewById(R.id.leave);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SMSHelpers.sendHiddenSMS(getApplicationContext(), "LEAVE " + profileAddress);
            }
        });
    }

    private class GetProfileInfo extends AsyncTask<Void, Void, String[]> {
        private Context context;
        private String address;

        public GetProfileInfo(Context context, String address) {
            this.context = context;
            this.address = address;
        }

        protected String[] doInBackground(Void... params) {
            String[] result = new String[2]; //1st is whois, 2nd is stats info

            // Set search params
            final Uri inboxURI = Uri.parse("content://sms/inbox");
            final String[] reqCols = new String[] { "_id", "address", "date", "body" };
            String[] filter = new String[] { "%" + SMSHelpers.TWITTER_SHORTCODE + "%" };

            // We need to keep looping and refreshing until we get the SMSes
            boolean hasWHOIS = false;
            boolean hasStats = false;
            while(!(hasWHOIS && hasStats)) {
                // Get Content Resolver object, which will deal with Content Provider
                ContentResolver smsRetrieve = context.getContentResolver();

                // Retrieve relevant Twitter SMSes through query
                Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);

                // Check to see if SMSes we have are from the correct Tweeter
                String temp = "";
                String shortAddr = address.replace("@", "");
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);

                        temp = cursor.getString(3);

                        if(temp.contains("Followers: "))
                        {
                            result[1] = temp;
                            hasStats = true;
                        }

                        if(temp.contains(shortAddr + ", since"))
                        {
                            result[0] = temp;
                            hasWHOIS = true;
                        }
                    }
                }
            }

            //temp comment to aid in parsing
            //40404: 2/2: . Location: San Francisco Web: http://t.co/NajCuIAUyl/s/t6aD
            //40404: 1/2: CNET, since Apr 2009. Bio: CNET is the premier destination for tech product reviews, news, price comparisons, free
            //40404: Followers: 802,507 Following: 280 Reply w/ WHOIS @CNET to view profile.

            return result;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            mDownloadProgress = new ProgressDialog(OtherProfileActivity.this);
            mDownloadProgress.setCancelable(true); //temp for now
            mDownloadProgress.setCanceledOnTouchOutside(false);
            mDownloadProgress.setTitle("Retrieving Profile Info");
            mDownloadProgress.setMessage("Please Wait...");
            mDownloadProgress.isIndeterminate();
            mDownloadProgress.show();

            // Request user profile data
            SMSHelpers.sendHiddenSMS(context, "WHOIS " + address);
            SMSHelpers.sendHiddenSMS(context, "STATS " + address);
        }

        @Override
        protected void onPostExecute(String[] sms) {
            super.onPostExecute(sms);

            mDownloadProgress.cancel();
        }
    }
}
