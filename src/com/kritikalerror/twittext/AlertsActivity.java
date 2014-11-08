package com.kritikalerror.twittext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kritikalerror.twittext.R;

import java.util.ArrayList;

public class AlertsActivity extends Activity {

    private ListView threadView;
    private SMSObjectAdapter adapter;

    private final String TWITTER_SHORTCODE = "40404";
    private final String TAG = "AlertsActivity";
    private String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmessages);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        threadView = (ListView) findViewById(R.id.messagesList);

        mAddress = getIntent().getExtras().getString("address");

        // Set search params
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "date", "body"};
        String[] filter = new String[]{"%" + TWITTER_SHORTCODE + "%"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver smsRetrieve = getContentResolver();

        // Retrieve relevant Twitter SMSes through query
        Cursor cursor = smsRetrieve.query(inboxURI, reqCols, "address LIKE ?", filter, null);

        ArrayList<SMSObject> smsArray = new ArrayList<SMSObject>();

        // Change cursor so that we get subscriptions from the results
        if (cursor.moveToFirst()) {
            //String str = cursor.getString(cursor.getColumnIndex("text"));
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                if (cursor.getString(3).matches("(.*)Sorry, no suggestions for who to follow right now. Check back later!(.*)") ||
                        cursor.getString(3).matches("(.*)D " + cursor.getString(1) + " (.*)")) {
                    smsArray.add(new SMSObject(cursor.getString(0), //id
                            cursor.getString(1), //address
                            cursor.getString(2), //date
                            cursor.getString(3).trim())); //original text
                }
            }
        }

        // Attached Cursor with adapter and display in listview
        adapter = new SMSObjectAdapter(AlertsActivity.this, smsArray);
        threadView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                SMSHelpers.sendMessageSMS(getApplicationContext(), AlertsActivity.this, mAddress);
                break;
            case R.id.action_populatemessage:
                SMSHelpers._addMessage(AlertsActivity.this,
                        "Sorry, no suggestions for who to follow right now. Check back later!");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}