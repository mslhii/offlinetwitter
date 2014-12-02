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
                    /*
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(SMSHelpers.TWITTER_SHORTCODE, null, "START", null, null);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Request failed, please try again later!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    */

                    Intent smsIntent;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) //Greater than JB
                    {
                        /*
                        smsIntent = new Intent(Intent.ACTION_SENDTO,
                                Uri.parse("smsto:" + Uri.encode(SMSHelpers.TWITTER_SHORTCODE)));
                        smsIntent.putExtra("sms_body", "");
                        */
                        smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.putExtra("address", SMSHelpers.TWITTER_SHORTCODE);
                        smsIntent.putExtra("sms_body", "START");
                        smsIntent.setData(Uri.parse("smsto:" + SMSHelpers.TWITTER_SHORTCODE));
                        startActivityForResult(smsIntent, BACK_RESULT);
                    }
                    else
                    {
                        smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", SMSHelpers.TWITTER_SHORTCODE);
                        smsIntent.putExtra("sms_body", "START");
                        startActivityForResult(smsIntent, BACK_RESULT);
                    }

                    dialog.cancel();
                }
            });


        AlertDialog alert = builder.create();
        alert.show();
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
        // Populate SMS Inbox with fake data
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Your phone is activated! Reply w/ HELP to check out all the things you can do with Twitter text messaging. Reply w/ STO");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "For the best experience, download Twitter for your phone! Visit twitter.com/download");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Your phone is already set up to use Twitter. Reply w/ FOLLOW username to get their Tweets on your phone. ex: FOLLOW TWIT");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "You're now following @Gizmodo. Their tweets will be sent to you. Send OFF @Gizmodo to stop.");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Notifications are now on. Reply w/OFF to turn them off. Reply w/SET for additional notification settings help.");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Followers: 1,133,542 Following: 79 Reply w/ WHOIS @GIZMODO to view profile.");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Gizmodo, since Mar 2007. Bio: Technologies that change the way we live, work, love, play, think, and feel. Web: http://t");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "@CNET: Korean messaging app KakaoTalk tries to rebuild image after privacy flap http://t.co/w3H8o7lYO4/s/hEwf http://t.c");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "@Gizmodo: Samsung Galaxy Note 4 Review: The best at being big: http://t.co/t2o0DJniT9/s/Q-q0 http://t.co/TPclSKyBka/s/rA");
        SMSHelpers._addMessageToSent(LoginActivity.this, "RT CNET");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "1/2: @comex: RT @a_greenberg: Kickstarter for Anonabox launched this morning w/ $7.5k goal. They've raised $85k. https:/");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "2/2: /t.co/0IcP3WG4HG/s/4fm2");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "@Gizmodo: How to photograph the Northern Lights, from @IndefiniteWild http://t.co/IjUKkYCKoe/s/alIG http://t.co/PcnxciY");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "@comex: RT @supersat: @bl4sty @psifertex We need a deterministic CVE number to NSA-like name generator. 5326? That's Sas");
        SMSHelpers._addMessageToSent(LoginActivity.this, "D comex so when is the next ios jb released??");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "1/2: @CNET: RT @BridgetCarey: Google battles Amazon! Skype battles Snapchat! And Facebook... well, there's egg people. h");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "2/2: /J079BnCQxJ/s/mG1X");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Followers: 802,507 Following: 280 Reply w/ WHOIS @CNET to view profile.");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "1/2: CNET, since Apr 2009. Bio: CNET is the premier destination for tech product reviews, news, price comparisons, free");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "2/2: . Location: San Francisco Web: http://t.co/NajCuIAUyl/s/t6aD");
        SMSHelpers._addMessageToInbox(LoginActivity.this,
                "Direct from @ListCraigs66: again To reply, type 'DM @ListCraigs66 [your message]' m.twitter.com/messages");
        SMSHelpers._addMessageToSent(LoginActivity.this, "Test Tweet!");
        SMSHelpers._addMessageToSent(LoginActivity.this, "D ListCraigs66 test reply!");

        Toast.makeText(getApplicationContext(), "Populated fake SMS into inbox!", Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }
}
