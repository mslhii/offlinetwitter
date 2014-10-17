package com.kritikalerror.twittext;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SubscriptionsFragment extends Fragment {

    ListView subsView;
    SMSObjectAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        displayMessages(rootView);

        return rootView;
	}

    public void displayMessages(View rootView)
    {
        final Context viewContext = rootView.getContext();
        subsView = (ListView) rootView.findViewById(R.id.subscriptionList);

        // Set search params
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[] { "_id", "address", "date", "body" };
        String[] filter = new String[] { "%" + SMSHelpers.TWITTER_SHORTCODE + "%" };

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

        // Make ListView items clickable
        subsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                final SMSObject selectedSMS = (SMSObject) parent.getItemAtPosition(position);
                //Toast.makeText(viewContext, "You selected: " + selectedSMS.address + ", " + selectedSMS.text, Toast.LENGTH_LONG).show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tweet Options")
                        .setItems(new CharSequence[] {"Reply", "Retweet", "Favorite", "Unfollow", "Block", "Report"},
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch(which) {
                                    case 0:
                                        SMSHelpers.sendDialogSMS(viewContext, getActivity(), SMSHelpers.REPLY);
                                        break;
                                    case 1:
                                        Toast.makeText(viewContext, "Not implemented yet, coming soon!", Toast.LENGTH_LONG).show();
                                        //SMSHelpers.sendDialogSMS(viewContext, getActivity(), SMSHelpers.RETWEET);
                                        break;
                                    case 2:
                                        Toast.makeText(viewContext, "Not implemented yet, coming soon!", Toast.LENGTH_LONG).show();
                                        //SMSHelpers.sendDialogSMS(viewContext, getActivity(), SMSHelpers.FAVORITE);
                                        break;
                                    case 3:
                                        SMSHelpers.sendHiddenSMS(viewContext, "UNFOLLOW " + selectedSMS.address);
                                        break;
                                    case 4:
                                        SMSHelpers.sendHiddenSMS(viewContext, "BLOCK " + selectedSMS.address);
                                        break;
                                    case 5:
                                        SMSHelpers.sendHiddenSMS(viewContext, "REPORT " + selectedSMS.address);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
