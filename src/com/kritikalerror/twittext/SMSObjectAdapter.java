package com.kritikalerror.twittext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Michael on 10/16/2014.
 *
 * Custom Adapter to display tweets in a ListView container for each Fragment
 */
public class SMSObjectAdapter extends ArrayAdapter<SMSObject> {

    public SMSObjectAdapter(Context context, ArrayList<SMSObject> SMSArray) {
        super(context, 0, SMSArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SMSObject sms = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvDate = (TextView) convertView.findViewById(R.id.date);
        TextView tvText = (TextView) convertView.findViewById(R.id.text);
        // Populate the data into the template view using the data object
        tvName.setText(sms.address);
        tvDate.setText(String.valueOf(sms.date));
        tvText.setText(sms.text);
        // Return the completed view to render on screen
        return convertView;
    }
}
