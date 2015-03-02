package com.kritikalerror.twittext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Michael on 10/16/2014.
 *
 * Receiver class to intercept and keep all notifications from Twitter
 */
public class SMSInterceptReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String smsAddress = "";
        String smsBody = "";

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Bundle pdusBundle = intent.getExtras();
            Object[] pdus = (Object[]) pdusBundle.get("pdus");

            // Get message
            SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
            smsAddress = messages.getOriginatingAddress();
            smsBody = messages.getDisplayMessageBody();

            String debugMsg = "Regular senderNum: " + smsAddress + "; message: " + smsBody;
            Log.e(SMSHelpers.TAG, debugMsg);

            String mainMessage = smsBody.trim();

            if(mainMessage.matches("(.*)Followers:(.*)Following:(.*)Reply w/ WHOIS(.*)to view profile.(.*)"))
            {
                SMSHelpers.hasStatReceiver = true;
                SMSHelpers.whoisMessage = mainMessage;
                Toast.makeText(context, "Found WHOIS!", Toast.LENGTH_SHORT).show();
            }

            if(mainMessage.matches("(.*), since (.*)"))
            {
                SMSHelpers.hasWHOISReceiver = true;
                SMSHelpers.statsMessage = mainMessage;
                Toast.makeText(context, "Found STATS!", Toast.LENGTH_SHORT).show();
            }

            // Abort broadcast if it's from Twitter
            if (smsAddress.equals(SMSHelpers.TWITTER_SHORTCODE)) {
                Toast.makeText(context, debugMsg, Toast.LENGTH_SHORT).show();
                abortBroadcast();
            }
        }
    }
}
