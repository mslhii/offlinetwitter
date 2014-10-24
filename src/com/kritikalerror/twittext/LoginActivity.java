package com.kritikalerror.twittext;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;

public class LoginActivity extends Activity {

    private final int BACK_RESULT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        // Disable action bar extras
        ActionBar ab = getActionBar();
        ab.hide();

		final Button launchMain = (Button) findViewById(R.id.login); 
	    launchMain.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {                 
	        	Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
	        	//myIntent.putExtra("key", value); //implement login stuff later
	        	startActivity(myIntent);
                finish();
	        }
	    });
	    
	    final Button registerButton = (Button) findViewById(R.id.register); 
	    registerButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
                register();
	        }
	    });

	}

    /**
     * Register is a function that handles user registration with Twitter
     */
	private void register() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Please register using Messaging app");
        builder.setMessage("Register using the Messaging app. Follow the instructions that Twitter sends you. When you are finished, press the back button.");
        builder.setCancelable(true);
        builder.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) //Greater than JB
                        {
                            //String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(LoginActivity.this);

                            Toast.makeText(getApplicationContext(), "KitKat redirecting...", Toast.LENGTH_LONG).show();

                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(SMSHelpers.TWITTER_SHORTCODE)));
                            sendIntent.setType("text/plain");
                            sendIntent.putExtra("sms_body", "START");
                            startActivityForResult(sendIntent, BACK_RESULT);
                        } else //earlier versions
                        {
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(SMSHelpers.TWITTER_SHORTCODE, null, "START", null, null);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Request failed, please try again later!",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("address", SMSHelpers.TWITTER_SHORTCODE);
                            smsIntent.putExtra("sms_body", "");
                            startActivityForResult(smsIntent, BACK_RESULT);
                        }
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // If our requestcode matches, start mainactivity
        if(requestCode == BACK_RESULT)
        {
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, 0, 0, "Populate");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        return super.onOptionsItemSelected(item);
    }
}
