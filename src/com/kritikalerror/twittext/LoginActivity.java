package com.kritikalerror.twittext;


import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class LoginActivity extends Activity {
    private BroadcastReceiver mIntentReceiver;
    private ProgressDialog mWaitingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final Button launchMain = (Button) findViewById(R.id.login); 
	    launchMain.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {                 
	        	Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
	        	//myIntent.putExtra("key", value); //implement login stuff later
	        	startActivity(myIntent);
	        }
	    });
	    
	    final Button registerButton = (Button) findViewById(R.id.register); 
	    registerButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) { 
	        	register();
	        }
	    });

        // This doesn't always work, testing only
        final Button populateButton = (Button) findViewById(R.id.populate);
        populateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //TODO: test only!!
                // Populate SMS Inbox with fake data
                ContentValues values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "@CNET: Korean messaging app KakaoTalk tries to rebuild image after privacy flap http://t.co/w3H8o7lYO4/s/hEwf http://t.c");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "@Gizmodo: Samsung Galaxy Note 4 Review: The best at being big: http://t.co/t2o0DJniT9/s/Q-q0 http://t.co/TPclSKyBka/s/rA");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "1/2: @comex: RT @a_greenberg: Kickstarter for Anonabox launched this morning w/ $7.5k goal. They've raised $85k. https:/");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "2/2: /t.co/0IcP3WG4HG/s/4fm2");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "@Gizmodo: How to photograph the Northern Lights, from @IndefiniteWild http://t.co/IjUKkYCKoe/s/alIG http://t.co/PcnxciY");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "@comex: RT @supersat: @bl4sty @psifertex We need a deterministic CVE number to NSA-like name generator. 5326? That's Sas");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "1/2: @CNET: RT @BridgetCarey: Google battles Amazon! Skype battles Snapchat! And Facebook... well, there's egg people. h");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SMSHelpers.TWITTER_SHORTCODE);
                values.put("body", "2/2: /J079BnCQxJ/s/mG1X");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

                Toast.makeText(getApplicationContext(), "Populated fake SMS into inbox!", Toast.LENGTH_LONG).show();
            }
        });
	}

    /**
     * Register is a function that handles user registration with Twitter
     */
	private void register() {
        //Kick things off by sending the START sms
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SMSHelpers.TWITTER_SHORTCODE, null, "START", null, null);
            Toast.makeText(getApplicationContext(), "Sent the request!",
                    Toast.LENGTH_LONG).show();

            mWaitingDialog = new ProgressDialog(this);
            mWaitingDialog.setCancelable(true);
            mWaitingDialog.setCanceledOnTouchOutside(false);
            mWaitingDialog.setTitle("Waiting");
            mWaitingDialog.setMessage("Please wait for Twitter's response.");
            mWaitingDialog.isIndeterminate();
            mWaitingDialog.show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Request failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        IntentFilter intentFilter = new IntentFilter("SMSInterceptReceiver.intent.MAIN");

        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

                //Process the sms format and extract body and phoneNumber
                msg = msg.replace("\n", "");
                String body = msg.substring(msg.lastIndexOf(":")+1, msg.length());
                String pNumber = msg.substring(0,msg.lastIndexOf(":"));

                //Add it to the list or do whatever you wish to
                Toast.makeText(getApplicationContext(), pNumber + ": " + body, Toast.LENGTH_LONG).show();

                // If text received is the welcome text from Twitter, proceed with name registration
                mWaitingDialog.dismiss();
                mWaitingDialog.cancel();

                // After registration, take user to the main screen
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);
	}
}
