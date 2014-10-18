package com.kritikalerror.twittext;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
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
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
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

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
            builder1.setTitle("Please register using Messaging app");
            builder1.setMessage("Register using the Messaging app. When you are finished, press the back button.");
            builder1.setCancelable(true);
            builder1.setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /*
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("address", SMSHelpers.TWITTER_SHORTCODE);
                            smsIntent.putExtra("sms_body","");
                            startActivity(smsIntent);
                            */
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Request failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        //Temp place to put it
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) //Greater than JB
        {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(sendIntent);
        }
        else //earlier versions
        {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", SMSHelpers.TWITTER_SHORTCODE);
            smsIntent.putExtra("sms_body","");
            startActivity(smsIntent);
        }
	}
}
