package com.kritikalerror.twittext;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class SubscriptionsFragment extends Fragment {

    ListView subsView;
    SMSObjectAdapter adapter;

    final String TWITTER_SHORTCODE = "40404";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        displayMessages(rootView);

        return rootView;
	}

    public void displayMessages(View rootView)
    {
        Context viewContext = rootView.getContext();
        subsView = (ListView) rootView.findViewById(R.id.subscriptionList);

        // Set search params
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[] { "_id", "address", "date", "body" };
        String[] filter = new String[] { "%" + TWITTER_SHORTCODE + "%" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver smsRetrieve = viewContext.getContentResolver();

        // Retrieve relevant Twitter SMSes through query
        Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);
        ArrayList<SMSObject> smsArray = new ArrayList<SMSObject>();

        // Change cursor so that we get subscriptions from the results
        String tempSpliceText = "";
        boolean needsJoin = false;
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                String tempText = cursor.getString(3).trim();

                // Check for 2/2 tweets first so we can join text together
                int secondTextIDX = tempText.indexOf("2/2: ");
                if(secondTextIDX != -1) {
                    tempSpliceText = tempText.substring(secondTextIDX + 5, tempText.length());
                    needsJoin = true;
                    cursor.move(1);
                    i++;
                }

                // We only want texts that start with @xxx: format for
                String theText = cursor.getString(3).trim();
                if(needsJoin)
                {
                    theText = theText + tempSpliceText;
                    needsJoin = false;
                }
                if(cursor.getString(3).contains(": ")) {
                    smsArray.add(new SMSObject(cursor.getString(0), //id
                            cursor.getString(1), //address
                            cursor.getString(2), //date
                            theText)); //text
                }
            }
        }

        // Attached Cursor with adapter and display in listview
        adapter = new SMSObjectAdapter(viewContext, smsArray);
        subsView.setAdapter(adapter);
    }
}
