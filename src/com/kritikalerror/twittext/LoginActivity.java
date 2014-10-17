package com.kritikalerror.twittext;


import java.util.ArrayList;

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
	private final String SHORTCODE = "40404";
	
	
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
	        	//myIntent.putExtra("key", value); //implement login stuff later
	        	startActivity(myIntent);
	        }
	    });

        final Button populateButton = (Button) findViewById(R.id.populate);
        populateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //TODO: test only!!
                // Populate SMS Inbox with fake data
                ContentValues values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "@CNET: Korean messaging app KakaoTalk tries to rebuild image after privacy flap http://t.co/w3H8o7lYO4/s/hEwf http://t.c");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "@Gizmodo: Samsung Galaxy Note 4 Review: The best at being big: http://t.co/t2o0DJniT9/s/Q-q0 http://t.co/TPclSKyBka/s/rA");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "1/2: @comex: RT @a_greenberg: Kickstarter for Anonabox launched this morning w/ $7.5k goal. They've raised $85k. https:/");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "2/2: /t.co/0IcP3WG4HG/s/4fm2");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "@Gizmodo: How to photograph the Northern Lights, from @IndefiniteWild http://t.co/IjUKkYCKoe/s/alIG http://t.co/PcnxciY");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "@comex: RT @supersat: @bl4sty @psifertex We need a deterministic CVE number to NSA-like name generator. 5326? That's Sas");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "1/2: @CNET: RT @BridgetCarey: Google battles Amazon! Skype battles Snapchat! And Facebook... well, there's egg people. h");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                values = new ContentValues();
                values.put("address", SHORTCODE);
                values.put("body", "2/2: /J079BnCQxJ/s/mG1X");
                getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

                Toast.makeText(getApplicationContext(), "Populated fake SMS into inbox!", Toast.LENGTH_LONG).show();
            }
        });
	}
	
	private void register() {
		SmsManager smsManager = SmsManager.getDefault();
	    ArrayList<String> parts = smsManager.divideMessage("START"); 
	    //smsManager.sendMultipartTextMessage(SHORTCODE, null, parts, null, null);
	    ContentValues values = new ContentValues(); 
	    values.put("address", SHORTCODE);         
	    values.put("body", "START"); 
	    getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	    
	    // TEST ONLY! REMOVE LATER
	    unitRegisterTest();
	    
	    Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
	    cursor.moveToFirst();

	    String msgData = "";
	    do {
	       for(int idx = 0; idx < cursor.getColumnCount(); idx++)
	       {
	           //msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
	    	   if (cursor.getColumnName(idx) == SHORTCODE)
	    	   {
	    		   msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
	    	   }
	       }
	    }
	    while(cursor.moveToNext());
	    
	    Log.w("test", msgData);
	    Toast.makeText(getApplicationContext(), msgData, Toast.LENGTH_LONG).show();
	}
	
	private void unitRegisterTest() {
		// TEST ONLY! REMOVE LATER
		ContentValues values = new ContentValues(); 
	    values.put("address", SHORTCODE);         
	    values.put("body", "Your phone is activated! Reply w/ HELP to check out all the things you can do with Twitter text messaging. Reply w/ STO"); 
	    getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	    values = new ContentValues(); 
	    values.put("address", SHORTCODE);         
	    values.put("body", "For the best experience, download Twitter for your phone! Visit twitter.com/download"); 
	    getContentResolver().insert(Uri.parse("content://sms/draft"), values);
	}
}
