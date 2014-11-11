package com.kritikalerror.twittext;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;

import com.kritikalerror.twittext.db.LoginDBHandler;

public class LoginActivity extends Activity {

    LoginDBHandler mLoginDBHandler;

    private final int BACK_RESULT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        // Disable action bar extras
        ActionBar ab = getActionBar();
        ab.hide();

        // Get instance of Login DB
        mLoginDBHandler = new LoginDBHandler(this);
        mLoginDBHandler.open();

		final Button launchMain = (Button) findViewById(R.id.login); 
	    launchMain.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
                logIn();
            }
	    });
	    
	    final Button registerButton = (Button) findViewById(R.id.register); 
	    registerButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
                signUp();
                //register();
	        }
	    });

	}

    /**
     * Register is a function that handles user registration with Twitter
     */
	private void register() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) //Greater than JB
        {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(SMSHelpers.TWITTER_SHORTCODE, null, "START", null, null);

                final Uri inboxURI = Uri.parse("content://sms/inbox");
                final String[] reqCols = new String[] { "_id", "address", "date", "body" };
                String[] filter = new String[] { "%" + SMSHelpers.TWITTER_SHORTCODE + "%" };
                boolean hasResponse = false;
                while(!hasResponse)
                {
                    ContentResolver smsRetrieve = getContentResolver();
                    Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);

                    if (cursor.moveToFirst()) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);

                            if(cursor.getString(3).contains("Welcome to Twitter!"))
                            {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                builder1.setMessage("Do you have a Twitter account already?");
                                builder1.setCancelable(true);
                                builder1.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                                SMSHelpers.sendHiddenSMS(getApplicationContext(),
                                                        "YES",
                                                        "");
                                            }
                                        });
                                builder1.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                                SMSHelpers.sendDialogSMS(getApplicationContext(),
                                                        LoginActivity.this,
                                                        SMSHelpers.REGISTER_USERNAME);
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                Toast.makeText(LoginActivity.this,
                                        "Got response!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Request failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else //earlier versions
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Please register using Messaging app");
            builder.setMessage("Register using the Messaging app. Follow the instructions that Twitter sends you. When you are finished, press the back button.");
            builder.setCancelable(true);
            builder.setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
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

                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
	}

    private void signUp()
    {
        final Dialog signUpDialog = new Dialog(LoginActivity.this);

        signUpDialog.setContentView(R.layout.signup_dialog);
        signUpDialog.setTitle("Sign Up");

        final EditText editTextUserName =(EditText) signUpDialog.findViewById(R.id.editTextUserName);
        final EditText editTextPassword = (EditText) signUpDialog.findViewById(R.id.editTextPassword);
        final EditText editTextConfirmPassword = (EditText) signUpDialog.findViewById(R.id.editTextConfirmPassword);

        final Button btnCreateAccount = (Button) signUpDialog.findViewById(R.id.buttonCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();
                String confirmPassword=editTextConfirmPassword.getText().toString();

                if(userName.equals("")||password.equals("")||confirmPassword.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Field empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!password.equals(confirmPassword))
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    // Save the Data in Database
                    mLoginDBHandler.insertEntry(userName, password);
                    Toast.makeText(getApplicationContext(), "Account successfully created!", Toast.LENGTH_LONG).show();

                    register();
                }
            }
        });

        signUpDialog.show();
    }

    private void logIn()
    {
        final Dialog loginDialog = new Dialog(LoginActivity.this);

        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setTitle("Login");

        final EditText editTextUserName = (EditText) loginDialog.findViewById(R.id.editTextUserNameToLogin);
        final EditText editTextPassword = (EditText) loginDialog.findViewById(R.id.editTextPasswordToLogin);

        final Button btnSignIn = (Button) loginDialog.findViewById(R.id.buttonSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String userName = editTextUserName.getText().toString();
                String password = editTextPassword.getText().toString();

                String storedPassword = mLoginDBHandler.getSingleEntry(userName);

                if(password.equals(storedPassword))
                {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                    loginDialog.dismiss();

                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(myIntent);
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Username and Password do not match!", Toast.LENGTH_LONG).show();
                }

            }
        });

        loginDialog.show();
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
    protected void onDestroy() {
        super.onDestroy();
        mLoginDBHandler.close();
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
        SMSHelpers._addMessage(LoginActivity.this,
                "@CNET: Korean messaging app KakaoTalk tries to rebuild image after privacy flap http://t.co/w3H8o7lYO4/s/hEwf http://t.c");
        SMSHelpers._addMessage(LoginActivity.this,
                "@Gizmodo: Samsung Galaxy Note 4 Review: The best at being big: http://t.co/t2o0DJniT9/s/Q-q0 http://t.co/TPclSKyBka/s/rA");
        SMSHelpers._addMessage(LoginActivity.this,
                "1/2: @comex: RT @a_greenberg: Kickstarter for Anonabox launched this morning w/ $7.5k goal. They've raised $85k. https:/");
        SMSHelpers._addMessage(LoginActivity.this,
                "2/2: /t.co/0IcP3WG4HG/s/4fm2");
        SMSHelpers._addMessage(LoginActivity.this,
                "@Gizmodo: How to photograph the Northern Lights, from @IndefiniteWild http://t.co/IjUKkYCKoe/s/alIG http://t.co/PcnxciY");
        SMSHelpers._addMessage(LoginActivity.this,
                "@comex: RT @supersat: @bl4sty @psifertex We need a deterministic CVE number to NSA-like name generator. 5326? That's Sas");
        SMSHelpers._addMessage(LoginActivity.this,
                "1/2: @CNET: RT @BridgetCarey: Google battles Amazon! Skype battles Snapchat! And Facebook... well, there's egg people. h");
        SMSHelpers._addMessage(LoginActivity.this,
                "2/2: /J079BnCQxJ/s/mG1X");
        SMSHelpers._addMessage(LoginActivity.this,
                "Followers: 802,507 Following: 280 Reply w/ WHOIS @CNET to view profile.");
        SMSHelpers._addMessage(LoginActivity.this,
                "1/2: CNET, since Apr 2009. Bio: CNET is the premier destination for tech product reviews, news, price comparisons, free");
        SMSHelpers._addMessage(LoginActivity.this,
                "2/2: . Location: San Francisco Web: http://t.co/NajCuIAUyl/s/t6aD");
        SMSHelpers._addMessage(LoginActivity.this,
                "Direct from @ListCraigs66: again To reply, type 'DM @ListCraigs66 [your message]' m.twitter.com/messages");

        Toast.makeText(getApplicationContext(), "Populated fake SMS into inbox!", Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }
}


/*
Your phone is activated! Reply w/ HELP to check out all the things you can do with Twitter text messaging. Reply w/ STO
For the best experience, download Twitter for your phone! Visit twitter.com/download
Your phone is already set up to use Twitter. Reply w/ FOLLOW username to get their Tweets on your phone. ex: FOLLOW TWIT

You're now following @Gizmodo. Their tweets will be sent to you. Send OFF @Gizmodo to stop.
Notifications are now on. Reply w/OFF to turn them off. Reply w/SET for additional notification settings help.
 */