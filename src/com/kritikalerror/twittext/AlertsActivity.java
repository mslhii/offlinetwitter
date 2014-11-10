package com.kritikalerror.twittext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
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
    private SimpleCursorAdapter adapter;

    private final String TWITTER_SHORTCODE = "40404";
    private final String TAG = "AlertsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmessages);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        threadView = (ListView) findViewById(R.id.messagesList);

        // Set search params
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "date", "body"};
        String[] filter = new String[]{"%" + TWITTER_SHORTCODE + "%"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver smsRetrieve = getContentResolver();

        // Retrieve relevant Twitter SMSes through query
        Cursor cursor = smsRetrieve.query(inboxURI, reqCols, null, null, null);

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
        adapter = new SimpleCursorAdapter(AlertsActivity.this, R.layout.row_layout, cursor,
                new String[] { "body", "address", "date" }, new int[] { R.id.text, R.id.name, R.id.date });
        threadView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Populate");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SMSHelpers._addMessage(AlertsActivity.this,
                "Sorry, no suggestions for who to follow right now. Check back later!");
        return super.onOptionsItemSelected(item);
    }

}