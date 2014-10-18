package com.kritikalerror.twittext;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ActivityFragment extends Fragment {

    ListView subsView;
    SimpleCursorAdapter adapter;

    final String TWITTER_SHORTCODE = "40404";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        displayMessages(rootView);
		
		return rootView;
	}

    public void displayMessages(View rootView)
    {
        Context viewContext = rootView.getContext();
        subsView = (ListView) rootView.findViewById(R.id.notificationList);

        // Set search params
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[] { "_id", "address", "date", "body" };
        String[] filter = new String[] { "%" + TWITTER_SHORTCODE + "%" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver smsRetrieve = viewContext.getContentResolver();

        // Retrieve relevant Twitter SMSes through query
        Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);

        // Change cursor so that we get subscriptions from the results
        if (cursor.moveToFirst()) {
            //String str = cursor.getString(cursor.getColumnIndex("text"));
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                Log.e("TEST", "id: " + cursor.getString(0));
                Log.e("TEST", "address: " + cursor.getString(1));
                Log.e("TEST", "date: " + cursor.getString(2));
                Log.e("TEST", "text: " + cursor.getString(3));
            }
        }

        // Attached Cursor with adapter and display in listview
        adapter = new SimpleCursorAdapter(viewContext, R.layout.row_layout, cursor,
                new String[] { "body", "address", "date" }, new int[] { R.id.text, R.id.name, R.id.date });
        subsView.setAdapter(adapter);
    }

}
