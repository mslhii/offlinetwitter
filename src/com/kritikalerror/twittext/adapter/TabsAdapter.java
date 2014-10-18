package com.kritikalerror.twittext.adapter;

import com.kritikalerror.twittext.MessagesFragment;
import com.kritikalerror.twittext.NotificationsFragment;
import com.kritikalerror.twittext.SubscriptionsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAdapter extends FragmentPagerAdapter {

	public TabsAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new SubscriptionsFragment();
		case 1:
			// Games fragment activity
			return new MessagesFragment();
		case 2:
			// Movies fragment activity
			return new NotificationsFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}