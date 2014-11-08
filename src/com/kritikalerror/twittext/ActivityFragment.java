package com.kritikalerror.twittext;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.kritikalerror.twittext.support.SwipeRefreshLayout;

import java.util.ArrayList;

public class ActivityFragment extends Fragment {

    private ListView subsView;
    private SMSObjectAdapter adapter;

    private SwipeRefreshLayout swipeContainer;
    private Handler handler;

    private View rootView;

    private Context viewContext;

    final String TWITTER_SHORTCODE = "40404";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        handler = new Handler();

		rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        viewContext = rootView.getContext();
        subsView = (ListView) rootView.findViewById(R.id.notificationList);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeNotificationsContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                handler.post(refreshing);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        subsView.setOnScrollListener(new ListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(subsView != null && subsView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = subsView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = subsView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeContainer.setEnabled(enable);
            }
        });

        displayMessages();
		
		return rootView;
	}

    private final Runnable refreshing = new Runnable(){
        public void run(){
            try {
                displayMessages();
                //Toast.makeText(mContext, "Refreshed!", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void displayMessages()
    {
        // Set search params
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "date", "body"};
        String[] filter = new String[]{"%" + TWITTER_SHORTCODE + "%"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver smsRetrieve = viewContext.getContentResolver();

        // Retrieve relevant Twitter SMSes through query
        Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);

        ArrayList<SMSObject> smsArray = new ArrayList<SMSObject>();

        // Change cursor so that we get subscriptions from the results
        if (cursor.moveToFirst()) {
            //String str = cursor.getString(cursor.getColumnIndex("text"));
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                if (cursor.getString(3).contains("Sorry, no suggestions for who to follow right now. Check back later!")) {
                    smsArray.add(new SMSObject(cursor.getString(0), //id
                            cursor.getString(2), //date
                            cursor.getString(3).trim())); //original text
                }
            }
        }

        // Attached Cursor with adapter and display in listview
        adapter = new SMSObjectAdapter(viewContext, smsArray);
        subsView.setAdapter(adapter);
    }

}