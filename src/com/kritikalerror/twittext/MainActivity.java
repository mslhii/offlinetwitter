package com.kritikalerror.twittext;


import com.kritikalerror.twittext.adapter.TabsAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager mViewPager;
	private TabsAdapter mAdapter;
	private ActionBar mActionBar;
	// Tab titles
	private String[] mTabTitles = { "Subscriptions", "Messages", "Notifications" };

    // Twitter Shortcode
    final String TWITTER_SHORTCODE = "40404";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialization
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
		mAdapter = new TabsAdapter(getSupportFragmentManager());

		mViewPager.setAdapter(mAdapter);
		mActionBar.setHomeButtonEnabled(false);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : mTabTitles) 
		{
			mActionBar.addTab(mActionBar.newTab().setText(tab_name).setTabListener(this));
		}

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mActionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose: //Send Tweet
                sendSMS(R.id.action_compose);
                return super.onOptionsItemSelected(item);
            case R.id.action_follow: //Follow user
                sendSMS(R.id.action_follow);
                return super.onOptionsItemSelected(item);
            case R.id.action_search: //Search tweets
                Toast.makeText(getApplicationContext(), "Not implemented yet, coming soon!", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            default:
                Toast.makeText(getApplicationContext(), item.getItemId(), Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean sendSMS(int designator) {
        String actionString = "";
        final Dialog smsDialog = new Dialog(MainActivity.this);
        smsDialog.setContentView(R.layout.sms_dialog);
        final EditText prepareTweet = (EditText) smsDialog.findViewById(R.id.search);
        Button saveButton = (Button) smsDialog.findViewById(R.id.saveBtn);

        switch(designator) {
            case R.id.action_compose:
                smsDialog.setTitle("Send Tweet");
                prepareTweet.setHint("What's on your mind?");
                saveButton.setText("Tweet!");
                break;
            case R.id.action_follow:
                smsDialog.setTitle("Follow Tweeter");
                prepareTweet.setHint("Who would you like to follow?");
                saveButton.setText("Follow!");
                actionString = "FOLLOW ";
                break;
            default:
                return false; //No appropriate action to take
        }

        // Need final String due to inner class
        final String paramString = actionString;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String tweetParams = paramString + prepareTweet.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(TWITTER_SHORTCODE, null, tweetParams, null, null);
                    Toast.makeText(getApplicationContext(), "Sent the request!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Request failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                smsDialog.dismiss();
            }
        });
        smsDialog.show();

        return true;
    }

}
