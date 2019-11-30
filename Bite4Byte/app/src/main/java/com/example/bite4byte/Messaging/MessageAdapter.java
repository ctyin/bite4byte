package com.example.bite4byte.Messaging;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bite4byte.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MessageAdapter extends ArrayAdapter<ChatBubble> {

    private Activity activity;
    private List<ChatBubble> messages;
    private String currUser;

    public MessageAdapter(Activity context, int resource, List<ChatBubble> objects, String currUser) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
        this.currUser = currUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        ChatBubble ChatBubble = getItem(position);
//        int viewType = getItemViewType(position);

        if (ChatBubble.getSender().equals(currUser)) {
            layoutResource = R.layout.sent_message;
        } else {
            layoutResource = R.layout.shape_bg_incoming_bubble;
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);

            // set the time stamp
            Date date = new Date(ChatBubble.getCreated_at());
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("EST"));
            String dateFormatted = formatter.format(date);
            TextView timeView = convertView.findViewById(R.id.text_message_time);
            timeView.setText(dateFormatted);

            if (layoutResource == R.layout.shape_bg_incoming_bubble) {
                TextView tv = convertView.findViewById(R.id.text_message_name);
                tv.setText(ChatBubble.getSender());
            }

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //set message content
        holder.msg.setText(ChatBubble.getContent());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        // think this was the bug for switching the views when they're saving correctly
        ChatBubble cb = getItem(position);
        if (cb.getSender().equals(currUser)) {
            return 0;
        } else {
            return 1;
        }
    }

    private class ViewHolder {
        private TextView msg;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.text_message_body);
        }
    }
}