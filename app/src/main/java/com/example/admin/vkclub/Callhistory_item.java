package com.example.admin.vkclub;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin on 7/22/2017.
 */

public class Callhistory_item extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    private static final String TAG = "Callhistory_item";
    DataBaseHelper mDataBaseHelper;
    private static String[] extension;
    private static String[] username;
    private static String[] duration;
    private static String[] time;
    private static String[] status;

    public Callhistory_item(ArrayList<String> list, Context context){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "In Call History Items");
        View view = convertView;
        mDataBaseHelper = CallHistory.dbHelper();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.history_items, null);
        }

        if (position % 2 == 0){
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
            TextView tv = (TextView) view.findViewById(R.id.user_id);
            tv.setText(String.valueOf(position));
        }else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.sample));
        }

        TextView userId = (TextView) view.findViewById(R.id.user_id);
        TextView callDuration = (TextView) view.findViewById(R.id.duration);
        TextView callTime = (TextView) view.findViewById(R.id.call_time);
        ImageView callState = (ImageView) view.findViewById(R.id.call_state);

        Cursor data = mDataBaseHelper.getData();
        extension = new String[data.getCount()];
        username = new String[data.getCount()];
        duration = new String[data.getCount()];
        time = new String[data.getCount()];
        status = new String[data.getCount()];

        int i = data.getCount() - 1;
        while (data.moveToNext()){
            extension[i] = data.getString(1);
            username[i] = data.getString(2);
            duration[i] = data.getString(3);
            time[i] = data.getString(4);
            status[i] = data.getString(5);
            i--;
        }

        for (int j=0; j<data.getCount(); j++){
            if (position == j){
                userId.setText(extension[j]);
                callDuration.setText(username[j] + " (" + duration[j] + ")");
                callTime.setText(time[j]);
                if (status[j].equals("DIAL")){
                    callState.setImageResource(R.drawable.ic_outgoing_call);
                } else if(status[j].equals("RECEIVE")){
                    callState.setImageResource(R.drawable.ic_incoming_call);
                } else{
                    callState.setImageResource(R.drawable.ic_question_mark);
                }
            }
        }

        return view;
    }
}
