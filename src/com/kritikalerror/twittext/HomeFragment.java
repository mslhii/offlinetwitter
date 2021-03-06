package com.kritikalerror.twittext;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kritikalerror.twittext.support.SwipeRefreshLayout;
import com.kritikalerror.twittext.support.TimeStampComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    private ListView subsView;
    private SMSObjectAdapter adapter;
    private Handler handler;
    private View rootView;

    private Context mContext;
    private Comparator<SMSObject> timeComparator = new TimeStampComparator();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        handler = new Handler();

		rootView = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        subsView = (ListView) rootView.findViewById(R.id.subscriptionList);
        mContext = rootView.getContext();
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeHomeContainer);

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
                swipeContainer.setRefreshing(false);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void displayMessages()
    {
        final Context viewContext = mContext;

        // Set search params
        final Uri inboxURI = Uri.parse("content://sms/inbox");
        final Uri sentURI = Uri.parse("content://sms/sent");
        final String[] reqCols = new String[] { "_id", "address", "date", "body" };
        String[] filter = new String[] { "%" + SMSHelpers.TWITTER_SHORTCODE + "%" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver smsRetrieve = viewContext.getContentResolver();

        // Retrieve relevant Twitter SMSes through query
        Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);
        Cursor sentCursor = smsRetrieve.query(sentURI, reqCols, "address LIKE ?", filter, null);
        ArrayList<SMSObject> smsArray = new ArrayList<SMSObject>();

        // Change cursor so that we get subscriptions from the results
        String tempSpliceText = "";
        String tempText = "";
        boolean needsJoin = false;
        String theText = "";
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                tempText = cursor.getString(3).trim();

                // Check for 2/2 tweets first so we can join text together
                int secondTextIDX = tempText.indexOf("2/2: ");
                if(secondTextIDX != -1) {
                    tempSpliceText = tempText.substring(secondTextIDX + 5, tempText.length());
                    needsJoin = true;
                    cursor.move(1);
                    i++;
                }

                // We only want texts that start with @xxx: format for
                theText = cursor.getString(3).trim();
                if(needsJoin)
                {
                    theText = theText + tempSpliceText;
                    needsJoin = false;
                }

                // Do not want any other messages from Twitter, needs to match @xxx: format
                if(cursor.getString(3).matches("(.*)@(.*): (.*)") &&
                        !(cursor.getString(3).matches("(.*)Direct from @(.*): (.*)"))) {
                    smsArray.add(new SMSObject(cursor.getString(0), //id
                            cursor.getString(1), //address
                            cursor.getString(2), //date
                            theText, //text
                            cursor.getString(3).trim())); //original text
                }
            }
        }

        if (sentCursor.moveToFirst()) {
            for (int i = 0; i < sentCursor.getCount(); i++) {
                sentCursor.moveToPosition(i);

                // Do not want any other messages we send to Twitter
                if(SMSHelpers.isSMSTweet(sentCursor.getString(3).trim())) {
                    smsArray.add(new SMSObject(sentCursor.getString(0), //id
                            Integer.valueOf(sentCursor.getString(1)), //address
                            sentCursor.getString(2), //date
                            sentCursor.getString(3).trim())); //text
                }
            }
        }

        // Sort ArrayList by timestamp
        Collections.sort(smsArray, timeComparator);

        // Attached Cursor with adapter and display in listview
        adapter = new SMSObjectAdapter(viewContext, smsArray);
        subsView.setAdapter(adapter);

        // Make ListView items clickable
        subsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                final SMSObject selectedSMS = (SMSObject) parent.getItemAtPosition(position);
                String[] filter = new String[] { "%" + SMSHelpers.TWITTER_SHORTCODE + "%", "%" + selectedSMS.address + "%" };

                // Check to see if Tweet is latest or not
                ContentResolver smsTemp = viewContext.getContentResolver();
                Cursor tempCursor = smsTemp.query(inboxURI, reqCols, "address LIKE ? AND body LIKE ?", filter, null);
                tempCursor.moveToFirst();

                // Determine if the selected text matches what we are looking for
                if((tempCursor.getString(3).trim().contains(selectedSMS.original)) &&
                        !(tempCursor.getString(3).trim().matches("(.*)Followers:(.*)Following:(.*)Reply w/ WHOIS(.*)to view profile.(.*)")) &&
                        !(tempCursor.getString(3).trim().matches("(.*), since(.*).(.*)")))
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Tweet Options")
                            .setItems(new CharSequence[]{"View Profile", "Reply", "Retweet", "Favorite", "Report", "Delete"},
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // The 'which' argument contains the index position
                                            // of the selected item
                                            switch (which) {
                                                case 0:
                                                    Intent profileIntent = new Intent(getActivity(), OtherProfileActivity.class);
                                                    profileIntent.putExtra("address", selectedSMS.address);
                                                    startActivity(profileIntent);
                                                    break;
                                                case 1:
                                                    SMSHelpers.sendDialogSMS(viewContext, getActivity(), SMSHelpers.REPLY);
                                                    break;
                                                case 2:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "RETWEET " + selectedSMS.address);
                                                    break;
                                                case 3:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "FAVORITE " + selectedSMS.address);
                                                    break;
                                                case 4:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "UNFOLLOW " + selectedSMS.address);
                                                    break;
                                                case 8:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "LEAVE " + selectedSMS.address);
                                                    break;
                                                case 6:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "BLOCK " + selectedSMS.address);
                                                    break;
                                                case 7:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "REPORT " + selectedSMS.address);
                                                    break;
                                                case 5:
                                                    SMSHelpers.deleteSMS(viewContext, selectedSMS);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if(selectedSMS.address.equals(SMSHelpers.userName))
                {
                    Cursor userCursor = smsTemp.query(sentURI, reqCols, "address LIKE ? AND body LIKE ?", filter, null);
                    userCursor.moveToFirst();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Tweet Options")
                            .setItems(new CharSequence[]{"Delete"},
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // The 'which' argument contains the index position
                                            // of the selected item
                                            switch (which) {
                                                case 0:
                                                    SMSHelpers.deleteSMS(viewContext, selectedSMS);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Tweet Options")
                            .setItems(new CharSequence[]{"View Profile", "Reply", "Report", "Delete"},
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // The 'which' argument contains the index position
                                            // of the selected item
                                            switch (which) {
                                                case 0:
                                                    Intent profileIntent = new Intent(getActivity(), OtherProfileActivity.class);
                                                    profileIntent.putExtra("address", selectedSMS.address);
                                                    startActivity(profileIntent);
                                                    break;
                                                case 1:
                                                    SMSHelpers.sendDialogSMS(viewContext, getActivity(), SMSHelpers.REPLY);
                                                    break;
                                                case 2:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "UNFOLLOW " + selectedSMS.address);
                                                    break;
                                                case 6:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "LEAVE " + selectedSMS.address);
                                                    break;
                                                case 4:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "BLOCK " + selectedSMS.address);
                                                    break;
                                                case 5:
                                                    SMSHelpers.sendHiddenSMS(viewContext, "REPORT " + selectedSMS.address);
                                                    break;
                                                case 3:
                                                    SMSHelpers.deleteSMS(viewContext, selectedSMS);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }
}

/*
 40404: 1/2: You have retweeted @CNET's latest tweet: Ebola outbreaks, privacy concerns & acts of violence drove http://t.co/EQX 11:03 PM
40404: 2/2: f the... 11:03 PM
 */