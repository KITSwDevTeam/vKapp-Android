package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

/**
 * Created by admin on 7/24/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private static Context context;
    DataBaseHelper mDataBaseHelper;

    Calendar calendar;
    String currentDateTime;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        calendar = Calendar.getInstance();
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }else {
            Log.d(TAG, "Message data payload size: " + remoteMessage.getData().size());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        context = this;

        Intent in = new Intent(this, Dashboard.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, in, PendingIntent.FLAG_ONE_SHOT);
        mDataBaseHelper = new DataBaseHelper(getApplicationContext());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (remoteMessage.getNotification().getTitle() != null){
            notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            mDataBaseHelper.addNotificationData(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), sendTime());
        }else {
            notificationBuilder.setContentTitle("New Notification");
            mDataBaseHelper.addNotificationData(remoteMessage.getNotification().getBody(), "New Notification", sendTime());
        }
        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setVibrate(new long[0]);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

//        Intent intent = new Intent(this, DialogActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("message", remoteMessage.getNotification().getBody());
//        intent.putExtra("title", remoteMessage.getNotification().getTitle());
//        startActivity(intent);
    }

    private String sendTime(){
        int am_pm = calendar.get(Calendar.AM_PM);
        if (am_pm == Calendar.AM)
            currentDateTime = String.format("%04d", calendar.get(Calendar.YEAR)) + "/"
                    + String.format("%02d", calendar.get(Calendar.MONTH)) + "/"
                    + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + " "
                    + String.format("%02d", calendar.get(Calendar.HOUR)) + ":"
                    + String.format("%02d", calendar.get(Calendar.MINUTE)) + " AM";
        else
            currentDateTime = String.format("%04d", calendar.get(Calendar.YEAR)) + "/"
                    + String.format("%02d", calendar.get(Calendar.MONTH)) + "/"
                    + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + " "
                    + String.format("%02d", calendar.get(Calendar.HOUR)) + ":"
                    + String.format("%02d", calendar.get(Calendar.MINUTE)) + " PM";
        return currentDateTime;
    }
}
