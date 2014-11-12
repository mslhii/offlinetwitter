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
import android.widget.Toast;

/**
 * Created by Michael on 10/20/2014.
 */
public class OtherProfileActivity extends Activity {

    private TextView mUserName;
    private TextView mRealName;
    private TextView mSinceWhen;
    private TextView mFollowers;
    private TextView mBio;

    protected ProgressDialog mDownloadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherprofile);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        mUserName = (TextView) findViewById(R.id.profileusername);
        mRealName = (TextView) findViewById(R.id.profilename);
        mSinceWhen = (TextView) findViewById(R.id.whois);
        mFollowers = (TextView) findViewById(R.id.follow);
        mBio = (TextView) findViewById(R.id.otherbio);

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

                // Fixes nullpointerexception?
                if(cursor == null)
                {
                    break;
                }

                // Check to see if SMSes we have are from the correct Tweeter
                String temp = "";
                String shortAddr = address.replace("@", "");
                String tempText = "";
                String theText = "";
                boolean needsJoin = false;
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);

                        temp = cursor.getString(3).trim();

                        if(temp.matches("(.*)Followers:(.*)Following:(.*)Reply w/ WHOIS(.*)to view profile.(.*)")
                                && temp.contains(shortAddr))
                        {
                            result[1] = temp.trim();
                            hasStats = true;
                        }

                        int secondTextIDX = temp.indexOf("2/2: ");
                        if(secondTextIDX != -1) {
                            tempText = temp.substring(secondTextIDX + 5, temp.length());
                            needsJoin = true;
                            cursor.move(1);
                            i++;
                            temp = cursor.getString(3).trim();
                        }

                        if(temp.contains(shortAddr + ", since"))
                        {
                            if(needsJoin)
                            {
                                theText = cursor.getString(3).trim();
                                theText = theText + tempText;
                                needsJoin = false;
                                result[0] = theText.trim();
                            }
                            else {
                                result[0] = temp.trim();
                            }
                            hasWHOIS = true;
                        }
                    }
                }
            }
            // Reset the two flags to make sure we can get future texts?
            hasWHOIS = false;
            hasStats = false;

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

            String[] whoisString = sms[1].split("Reply");

            mFollowers.setText(whoisString[0]);
            mUserName.setText(whoisString[1].substring(whoisString[1].indexOf("@"), whoisString[1].indexOf(" to")));

            // Get Profile Name first
            int prefixIndex = sms[0].indexOf("1/2: ");
            if(prefixIndex == -1) {
                mRealName.setText(sms[0].substring(0, sms[0].indexOf(",")));
            }
            else
            {
                mRealName.setText(sms[0].substring(prefixIndex + 5, sms[0].indexOf(",")));
            }

            // Get Since When
            mSinceWhen.setText(sms[0].substring(sms[0].indexOf(",") + 2, sms[0].indexOf(".")));

            // Get Bio/Web/Location if any
            int bioIndex = sms[0].indexOf("Bio: ");
            if(bioIndex != -1)
            {
                mBio.setText(sms[0].substring(bioIndex, sms[0].length()));
            }

            mDownloadProgress.cancel();
        }
    }
}
