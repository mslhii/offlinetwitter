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

import java.util.ArrayList;

/**
 * Created by Michael on 10/24/2014.
 */
public class ViewMessageActivity extends Activity  {

    private ListView threadView;
    private SMSObjectAdapter adapter;

    private final String TWITTER_SHORTCODE = "40404";
    private final String TAG = "ViewMessageActivity";
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

            if (cursor.getString(3).matches("(.*)Direct from @(.*): (.*)") ||
                    cursor.getString(3).matches("(.*)D " + cursor.getString(1) + " (.*)")) {
                smsArray.add(new SMSObject(cursor.getString(0), //id
                        cursor.getString(1), //address
                        cursor.getString(2), //date
                        cursor.getString(3).trim())); //original text
            }
        }
    }

    // Attached Cursor with adapter and display in listview
    adapter = new SMSObjectAdapter(ViewMessageActivity.this, smsArray);
    threadView.setAdapter(adapter);

    // Make ListView items clickable
    threadView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                final SMSObject selectedSMS = (SMSObject) parent.getItemAtPosition(position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(ViewMessageActivity.this);
                builder.setTitle("Message Options")
                        .setItems(new CharSequence[]{"Reply", "Delete"},
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        switch (which) {
                                            case 0:
                                                SMSHelpers.sendMessageSMS(getApplicationContext(), ViewMessageActivity.this, selectedSMS.address);
                                                break;
                                            case 1:
                                                SMSHelpers.deleteSMS(ViewMessageActivity.this, selectedSMS);
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
                SMSHelpers.sendMessageSMS(getApplicationContext(), ViewMessageActivity.this, mAddress);
                break;
            case R.id.action_populatemessage:
                SMSHelpers._addMessageToInbox(ViewMessageActivity.this,
                        "Direct from @ListCraigs66: again To reply, type 'DM @ListCraigs66 [your message]' m.twitter.com/messages");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
