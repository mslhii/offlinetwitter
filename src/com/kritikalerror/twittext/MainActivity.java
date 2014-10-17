package com.kritikalerror.twittext;


import com.kritikalerror.twittext.adapter.TabsAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager mViewPager;
	private TabsAdapter mAdapter;
	private ActionBar mActionBar;
	// Tab titles
	private String[] mTabTitles = { "Home", "Discover", "Activity" };

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
        menu.findItem(R.id.action_search).getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose: //Send Tweet
                SMSHelpers.sendDialogSMS(getApplicationContext(), MainActivity.this, R.id.action_compose);
                return super.onOptionsItemSelected(item);
            case R.id.action_alert: //Send Tweet
                Toast.makeText(getApplicationContext(), "Not implemented yet, coming soon!", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            case R.id.action_message: //Send Tweet
                Toast.makeText(getApplicationContext(), "Not implemented yet, coming soon!", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            case R.id.action_follow: //Follow user
                SMSHelpers.sendDialogSMS(getApplicationContext(), MainActivity.this, R.id.action_follow);
                return super.onOptionsItemSelected(item);
            case R.id.action_search: //Search tweets and/or users
                Toast.makeText(getApplicationContext(), "Not implemented yet, coming soon!", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            case R.id.action_exit: //Stops all subscriptions and exits to Login Page
                SMSHelpers.sendHiddenSMS(getApplicationContext(), "OFF");
                return super.onOptionsItemSelected(item);
            default:
                Toast.makeText(getApplicationContext(), item.getItemId(), Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }
}
