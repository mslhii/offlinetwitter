package com.kritikalerror.twittext;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Michael on 10/16/2014.
 *
 * Has helper functions for other classes to use
 */
public class SMSHelpers {

    public static final String TWITTER_SHORTCODE = "40404";
    public static final int REPLY = 100;
    public static final int RETWEET = 200;
    public static final int FAVORITE = 300;
    public static final int REGISTER_USERNAME = 400;
    public static final int START = 10;
    public static final int STOP = 20;
    public static final int ON = 30;
    public static final int OFF = 40;

    public static final String TAG = "TwitText";
    public static String userName = "User";

    public static boolean sendHiddenSMS(final Context context, String actionString) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SMSHelpers.TWITTER_SHORTCODE, null, actionString, null, null);
            Toast.makeText(context, "Sent the request!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context,
                    "Request failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean sendHiddenSMS(final Context context, String actionString, String actionParams) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SMSHelpers.TWITTER_SHORTCODE, null, actionString + actionParams, null, null);
            Toast.makeText(context, "Sent the request!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context,
                    "Request failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean sendDialogSMS(final Context context, Activity activity, int designator) {
        String actionString = "";
        final Dialog smsDialog = new Dialog(activity);
        smsDialog.setContentView(R.layout.sms_dialog);
        final EditText prepareTweet = (EditText) smsDialog.findViewById(R.id.search);
        Button sendButton = (Button) smsDialog.findViewById(R.id.saveBtn);

        switch(designator) {
            case R.id.action_compose:
                smsDialog.setTitle("Send Tweet");
                prepareTweet.setHint("What's happening?");
                sendButton.setText("Tweet");
                break;
            case R.id.action_follow:
                smsDialog.setTitle("Follow Tweeter");
                prepareTweet.setHint("Who would you like to follow?");
                sendButton.setText("Follow");
                actionString = "FOLLOW ";
                break;
            case R.id.action_message:
                smsDialog.setTitle("Send Direct Message");
                prepareTweet.setHint("<message recipient> <body>");
                sendButton.setText("Send");
                actionString = "D ";
                break;
            case REPLY:
                smsDialog.setTitle("Reply Tweeter");
                prepareTweet.setHint("What's happening?");
                sendButton.setText("Reply");
                actionString = "@ ";
                break;
            case REGISTER_USERNAME:
                smsDialog.setTitle("Enter Username");
                prepareTweet.setHint("Username");
                sendButton.setText("Send");
                break;
            default:
                return false; //No appropriate action to take
        }

        // Need final String due to inner class
        final String paramString = actionString;

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String tweetParams = paramString + prepareTweet.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(TWITTER_SHORTCODE, null, tweetParams, null, null);

                    //TODO: may not be necessary
                    SMSHelpers._addMessageToSent(context, tweetParams);
                    Toast.makeText(context, "Sent the request!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context,
                            "Request failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                smsDialog.dismiss();
            }
        });
        smsDialog.show();

        return true;
    }

    public static boolean sendMessageSMS(final Context context, Activity activity, String sender) {
        String actionString = "";
        final Dialog smsDialog = new Dialog(activity);
        smsDialog.setContentView(R.layout.sms_dialog);
        final EditText prepareTweet = (EditText) smsDialog.findViewById(R.id.search);
        Button sendButton = (Button) smsDialog.findViewById(R.id.saveBtn);

        smsDialog.setTitle("Send Direct Message");
        prepareTweet.setHint("Start a new message");
        sendButton.setText("Send");
        actionString = "D " + sender + " ";

        // Need final String due to inner class
        final String paramString = actionString;

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String tweetParams = paramString + prepareTweet.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(TWITTER_SHORTCODE, null, tweetParams, null, null);
                    Toast.makeText(context, "Sent the request!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context,
                            "Request failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                smsDialog.dismiss();
            }
        });
        smsDialog.show();

        return true;
    }

    public static boolean isSMSTweet(String message)
    {
        if(!(message.matches("D (.*)")) &&
                !(message.toLowerCase().matches("start")) &&
                !(message.toLowerCase().matches("retweet (.*)")) &&
                !(message.toLowerCase().matches("rt (.*)")) &&
                !(message.toLowerCase().matches("suggest")) &&
                !(message.toLowerCase().matches("on (.*)")) &&
                !(message.toLowerCase().matches("off (.*)")) &&
                !(message.toLowerCase().matches("follow (.*)")) &&
                !(message.toLowerCase().matches("unfollow (.*)")) &&
                !(message.toLowerCase().matches("f (.*)")) &&
                !(message.toLowerCase().matches("leave (.*)")) &&
                !(message.toLowerCase().matches("stop")) &&
                !(message.toLowerCase().matches("quit")) &&
                !(message.toLowerCase().matches("unsubscribe")) &&
                !(message.toLowerCase().matches("cancel")) &&
                !(message.toLowerCase().matches("end")) &&
                !(message.toLowerCase().matches("arret")) &&
                !(message.toLowerCase().matches("l (.*)")) &&
                !(message.toLowerCase().matches("set location (.*)")) &&
                !(message.toLowerCase().matches("set bio (.*)")) &&
                !(message.toLowerCase().matches("set profile (.*)")) &&
                !(message.toLowerCase().matches("set language (.*)")) &&
                !(message.toLowerCase().matches("set name (.*)")) &&
                !(message.toLowerCase().matches("set url (.*)")) &&
                !(message.toLowerCase().matches("whois (.*)")) &&
                !(message.toLowerCase().matches("w (.*)")) &&
                !(message.toLowerCase().matches("get (.*)")) &&
                !(message.toLowerCase().matches("g (.*)")) &&
                !(message.toLowerCase().matches("fav (.*)")) &&
                !(message.toLowerCase().matches("fave (.*)")) &&
                !(message.toLowerCase().matches("favorite (.*)")) &&
                !(message.toLowerCase().matches("favourite (.*)")) &&
                !(message.toLowerCase().matches("stats (.*)")) &&
                !(message.toLowerCase().matches("sug")) &&
                !(message.toLowerCase().matches("s")) &&
                !(message.toLowerCase().matches("wtf")) &&
                !(message.toLowerCase().matches("help")) &&
                !(message.toLowerCase().matches("info")) &&
                !(message.toLowerCase().matches("aide")) &&
                !(message.toLowerCase().matches("set discover on")) &&
                !(message.toLowerCase().matches("set discover off")) &&
                !(message.toLowerCase().matches("block (.*)")) &&
                !(message.toLowerCase().matches("blk (.*)")) &&
                !(message.toLowerCase().matches("report (.*)")) &&
                !(message.toLowerCase().matches("rep (.*)")) &&
                !(message.toLowerCase().matches("unblock (.*)")) &&
                !(message.toLowerCase().matches("unblk (.*)")) &&
                !(message.toLowerCase().matches("(.*), since (.*). Bio:(.*)")) &&
                !(message.toLowerCase().matches("(.*)Followers:(.*)Following:(.*)Reply w/ WHOIS(.*)to view profile.(.*)")))
        {
            return true;
        }

        return false;
    }

    public static void _addMessageToInbox(Context context, String message)
    {
        ContentValues values = new ContentValues();
        values.put("address", SMSHelpers.TWITTER_SHORTCODE);
        values.put("body", message);
        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
    }

    public static void _addMessageToSent(Context context, String message)
    {
        ContentValues values = new ContentValues();
        values.put("address", SMSHelpers.TWITTER_SHORTCODE);
        values.put("body", message);
        context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }
}
