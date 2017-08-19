package com.example.admin.vkclub;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin on 7/24/2017.
 */

public class NotificationItem extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    private static final String TAG = "Notification Items";
    DataBaseHelper mDataBaseHelper;

    private static String[] message;
    private static String[] title;
    private static String[] sendTime;

    public NotificationItem(ArrayList<String> list, Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "In Call History Items");
        View view = convertView;
        mDataBaseHelper = NotificationPanel.dbHelper();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.notification_items, null);
        }

        if (position % 2 == 0){
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        }else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.darker_white));
        }

        TextView notificaitonMessage = (TextView) view.findViewById(R.id.notification_message);
        TextView notificationTitle = (TextView) view.findViewById(R.id.notification_title);
//        TextView notificationTime = (TextView) view.findViewById(R.id.notification_time);

        Cursor data = mDataBaseHelper.getNotificationData();
        message = new String[data.getCount()];
        title = new String[data.getCount()];
        sendTime = new String[data.getCount()];

        int i = data.getCount() - 1;
        while (data.moveToNext()){
            message[i] = data.getString(1);
            title[i] = data.getString(2);
            sendTime[i] = data.getString(3);
            i--;
        }

        for (int j=0; j<data.getCount(); j++){
            if (position == j){
                notificaitonMessage.setText(message[j]);
                notificationTitle.setText(title[j]);
//                notificationTime.setText(sendTime[j]);
            }
        }

        return view;
    }
}
