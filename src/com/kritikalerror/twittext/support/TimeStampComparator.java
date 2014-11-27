package com.kritikalerror.twittext.support;

import com.kritikalerror.twittext.SMSObject;

import java.util.Comparator;

/**
 * Created by Michael on 11/27/2014.
 */
public class TimeStampComparator implements Comparator<SMSObject> {
    @Override
    public int compare(SMSObject arg0, SMSObject arg1) {
        return (int) (Long.parseLong(arg1.timestamp) - Long.parseLong(arg0.timestamp));
    }
}
