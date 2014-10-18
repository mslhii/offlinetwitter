package com.kritikalerror.twittext;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Michael on 10/16/2014.
 */
public class SMSObject {

    // Member variables
    private String _id;
    public String shortcode;
    public String address;
    public String date;
    public String text;

    // Lazy instantiation constructor
    public SMSObject()
    {
        _id = "0";
        shortcode = "0";
        address = "0";
        date = "0";
        text = "";
    }

    public SMSObject(String id, String address, String date, String text)
    {
        String tempText = text.trim();

        // Slice out the 1/2 from the text (2/2 is ignored)
        int firstTextIDX = tempText.indexOf("1/2: ");
        if(firstTextIDX != -1)
        {
            tempText = tempText.substring(firstTextIDX + 5, tempText.length());
        }

        // Get the name of the user who wrote that tweet
        int index = tempText.indexOf(": ");
        Log.e("TEST", String.valueOf(index));

        this._id = id;
        this.shortcode = address;
        this.address = tempText.substring(0, index);
        this.date = convertDate(date);
        this.text = tempText.substring(index + 2, tempText.length());
    }

    private String convertDate(String inDate) {
        Date finalDate = new Date(Long.parseLong(inDate));
        return new SimpleDateFormat("MM/dd/yyyy, hh:mm a").format(finalDate);
    }
}