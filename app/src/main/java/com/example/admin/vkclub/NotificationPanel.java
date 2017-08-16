package com.example.admin.vkclub;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 7/24/2017.
 */

public class NotificationPanel extends DialogFragment {

    Toolbar toolbar;
    private static final String TAG = "NotificationPanel";
    private ListView mListView;
    private TextView noItems;

    DataBaseHelper mDataBaseHelper;

    private static DataBaseHelper returnDbHelper;
    Dashboard dashboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_panel, container, false);
        findView(view);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setTitle("Notification");
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    private void findView(View view){
        mListView = (ListView)view.findViewById(R.id.notification_panel);
        noItems = (TextView)view.findViewById(R.id.no_item);

        mDataBaseHelper = new DataBaseHelper(getContext());
        NotificationPanel.returnDbHelper = mDataBaseHelper;
        dashboard = (Dashboard) Dashboard.getAppContext();

        noItems.bringToFront();
        noItems.setVisibility(View.GONE);
        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        // get the data and append to the list
        Cursor data = mDataBaseHelper.getNotificationData();
        final ArrayList<String> listData = new ArrayList<>();
        final String[] listDataTemp = new String[data.getCount()];

        int i = data.getCount() - 1;
        while (data.moveToNext()){
            // get the value from the database in column then add it to the arrayList
//            listData.add(data.getString(1));
            listDataTemp[i] = data.getString(1);
            i--;
        }

        for (int j=0; j<data.getCount(); j++){
            listData.add(listDataTemp[j]);
        }

        if (listData.size() == 0){
            noItems.setVisibility(View.VISIBLE);
        }

        //instantiate custom adapter
        final NotificationItem adapter = new NotificationItem(listData, getContext());
        mListView.setAdapter(adapter);

        // set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Log.d(TAG, "onItemClick: You Clicked on " + adapter.getItem(position));

//                final String message = adapterView.getItemAtPosition(position).toString();

//                Cursor data = mDataBaseHelper.getNotificationID(message); // get the id associated with that name
//                int itemID = -1;
//                while (data.moveToNext()){
//                    itemID = data.getInt(0);
//                }
//
//                if (itemID > -1){
//                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
//                    mDataBaseHelper.deleteNotificationItem(itemID, message);
//                }else {
//                    toastMessage("No ID associated with that name");
//                }
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();;
    }

    public static DataBaseHelper dbHelper(){
        return NotificationPanel.returnDbHelper;
    }

    /** The system calls this only when! creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
