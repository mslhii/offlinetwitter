package com.kritikalerror.twittext;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Michael on 10/18/2014.
 *
 * Uses SharedPreferences to store user info in a database
 * Also sends the profile to Twitter by SMS for updating
 */
public class ProfileActivity extends Activity {
    private ImageButton mProfilePic;
    private EditText mRealName;
    private EditText mUserName;
    private EditText mLocation;
    private EditText mWebsite;
    private EditText mBio;

    private final String REAL_NAME_KEY = "realNameKey";
    private final String USER_NAME_KEY = "userNameKey";
    private final String LOCATION_KEY = "locationKey";
    private final String WEBSITE_KEY = "websiteKey";
    private final String BIO_KEY = "bioKey";

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        mSharedPreferences = getSharedPreferences("TwitTextProfileSettings", Context.MODE_PRIVATE);

        // Get View elements
        mProfilePic = (ImageButton) findViewById(R.id.profileImage);
        mRealName = (EditText) findViewById(R.id.realname);
        mUserName = (EditText) findViewById(R.id.username);
        mLocation = (EditText) findViewById(R.id.location);
        mWebsite = (EditText) findViewById(R.id.website);
        mBio = (EditText) findViewById(R.id.bio);

        // Populate View elements if it has data
        if (mSharedPreferences.contains(REAL_NAME_KEY))
        {
            mRealName.setText(mSharedPreferences.getString(REAL_NAME_KEY, ""));
        }
        if (mSharedPreferences.contains(USER_NAME_KEY))
        {
            mUserName.setText(mSharedPreferences.getString(USER_NAME_KEY, ""));
        }
        if (mSharedPreferences.contains(LOCATION_KEY))
        {
            mLocation.setText(mSharedPreferences.getString(LOCATION_KEY, ""));
        }
        if (mSharedPreferences.contains(WEBSITE_KEY))
        {
            mWebsite.setText(mSharedPreferences.getString(WEBSITE_KEY, ""));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveProfile();
        Toast.makeText(getApplicationContext(), "Saved your profile!",Toast.LENGTH_LONG).show();
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void saveProfile()
    {
        String realNameResults = mRealName.getText().toString();
        String userNameResults = mUserName.getText().toString();
        String locationResults = mLocation.getText().toString();
        String websiteResults = mWebsite.getText().toString();
        String bioResults = mBio.getText().toString();

        // Send texts only if they are different from what we have
        if(!realNameResults.equals(mSharedPreferences.getString(REAL_NAME_KEY, "")))
        {
            SMSHelpers.sendHiddenSMS(getApplicationContext(), "SET NAME ", mRealName.getText().toString());
        }
        if(!userNameResults.equals(mSharedPreferences.getString(USER_NAME_KEY, "")))
        {
            SMSHelpers.sendHiddenSMS(getApplicationContext(), "SET USERNAME ", mUserName.getText().toString());
        }
        if(!locationResults.equals(mSharedPreferences.getString(LOCATION_KEY, "")))
        {
            SMSHelpers.sendHiddenSMS(getApplicationContext(), "SET LOCATION ", mLocation.getText().toString());
        }
        if(!websiteResults.equals(mSharedPreferences.getString(WEBSITE_KEY, "")))
        {
            SMSHelpers.sendHiddenSMS(getApplicationContext(), "SET URL ", mWebsite.getText().toString());
        }
        if(!bioResults.equals(mSharedPreferences.getString(BIO_KEY, "")))
        {
            SMSHelpers.sendHiddenSMS(getApplicationContext(), "SET BIO ", mBio.getText().toString());
        }

        // Store username in static variable for future access
        // without having to go into SharedPreferences
        SMSHelpers.userName = userNameResults;

        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(REAL_NAME_KEY, realNameResults);
        edit.putString(USER_NAME_KEY, userNameResults);
        edit.putString(LOCATION_KEY, locationResults);
        edit.putString(WEBSITE_KEY, websiteResults);
        edit.putString(BIO_KEY, bioResults);
        edit.commit();
    }
}
