package com.kritikalerror.twittext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Michael on 10/16/2014.
 *
 * Receiver class to intercept and keep all notifications from Twitter
 */
public class SMSInterceptReceiver extends BroadcastReceiver {

    final String TWITTER_SHORTCODE = "40404";

    @Override
    public void onReceive(Context context, Intent intent) {
        String smsAddress = "";

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Bundle pdusBundle = intent.getExtras();
            Object[] pdus = (Object[]) pdusBundle.get("pdus");

            // Get message
            SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
            smsAddress = messages.getOriginatingAddress();

            // Abort broadcast if it's from Twitter
            if (smsAddress.equals(TWITTER_SHORTCODE)) {
                abortBroadcast();
            }
        }
    }
}
