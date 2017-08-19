package com.example.admin.vkclub;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.sip.SipException;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class Calling extends AppCompatActivity {

    public static MediaPlayer player;
    Dashboard dashboard;
    ImageButton ansBtn, decBtn, toggleSpeaker, toggleMute;
    TextView callStatus, peerProfile;
    Boolean isMute = false, isSpeaker = false;
    public static Activity activity;
    public static Context context;

    private static Calling callingActivity;
    int hours = 0, mins = 0, secs = 0;

    private static final String TAG = "MainActivity";
    private static String callDuration;
    private static boolean answerFlag = false;

    DataBaseHelper mDatabaseHelper;
    Calendar calendar;
    String callerId;
    String callerName;
    String currentDateTime;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        calendar = Calendar.getInstance();
        callingActivity = this;
        activity = this;
        Calling.context = this;
        dashboard = (Dashboard) Dashboard.getAppContext();
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        callStatus = (TextView) findViewById(R.id.call_status);
        peerProfile = (TextView) findViewById(R.id.peer);
        ansBtn = (ImageButton) findViewById(R.id.answer);
        decBtn = (ImageButton) findViewById(R.id.decline);
        toggleSpeaker = (ImageButton) findViewById(R.id.speaker);
        toggleMute = (ImageButton) findViewById(R.id.mute);

        if(getIntent().getExtras().getString("STATE").equals("RECEIVING")){
            player.start();
            toggleSpeaker.setVisibility(View.GONE);
            toggleMute.setVisibility(View.GONE);

            callerId = dashboard.audioCall.getPeerProfile().getUserName();
            callerName = dashboard.audioCall.getPeerProfile().getDisplayName();
            if(callerId != null){
                peerProfile.setText(callerId);
            } else{
                peerProfile.setText("Unknown");
            }
        } else if(getIntent().getExtras().getString("STATE").equals("DAILING")){
            ansBtn.setVisibility(View.GONE);
            peerProfile.setText(getIntent().getExtras().getString("CALLEE"));
        }

        callStatus.setText("Calling...");
        System.out.println(getIntent().getExtras().getString("STATE"));

        ansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerFlag = true;
                player.stop();
                toggleMute.setVisibility(View.VISIBLE);
                toggleSpeaker.setVisibility(View.VISIBLE);
                ansBtn.setVisibility(View.GONE);

                try {
                    dashboard.audioCall.answerCall(30);
                } catch (SipException e) {
                    e.printStackTrace();
                    dashboard.presentDialog("Error!", "Unable to answer. Please check with vKirirom Receptionists.");
                }
            }
        });

        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getExtras().getString("STATE").equals("RECEIVING")){
                    player.stop();
                    if (callerName == null && callDuration != null){
                        addData(callerId, callerId, callDuration, callTime(), "RECEIVE");
                    }else if (callerName != null && callDuration == null){
                        addData(callerId, callerName, "00:00:00", callTime(), "RECEIVE");
                    }else {
                        addData(callerId, callerName, callDuration, callTime(), "RECEIVE");
                    }
                }

                if (getIntent().getExtras().getString("STATE").equals("DAILING")){
                    if (callDuration == null){
                        addData(getIntent().getExtras().getString("CALLEE"), getIntent().getExtras().getString("CALLEE"), "00:00:00", callTime(), "DIAL");
                    }else {
                        addData(getIntent().getExtras().getString("CALLEE"), getIntent().getExtras().getString("CALLEE"), callDuration, callTime(), "DIAL");
                    }
                }

                callStatus.setText("Hanging up...");
                finish();
                try {
                    dashboard.audioCall.endCall();
                } catch (SipException e) {
                    e.printStackTrace();
                }
            }
        });

        toggleMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMute = !isMute;
                if(isMute){
                    dashboard.audioCall.toggleMute();
                    toggleMute.setImageResource(R.drawable.ic_muted_black);
                    toggleMute.setBackground(ContextCompat.getDrawable(Calling.this, R.drawable.call_screen_module_false));
                } else{
                    dashboard.audioCall.toggleMute();
                    toggleMute.setImageResource(R.drawable.ic_muted_white);
                    toggleMute.setBackground(ContextCompat.getDrawable(Calling.this, R.drawable.call_screen_module));
                }
            }
        });

        toggleSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeaker = !isSpeaker;
                if(isSpeaker){
                    dashboard.audioCall.setSpeakerMode(true);
                    toggleSpeaker.setImageResource(R.drawable.ic_speaker_black);
                    toggleSpeaker.setBackground(ContextCompat.getDrawable(Calling.this, R.drawable.call_screen_module_false));
                } else{
                    dashboard.audioCall.setSpeakerMode(false);
                    toggleSpeaker.setImageResource(R.drawable.ic_speaker_white);
                    toggleSpeaker.setBackground(ContextCompat.getDrawable(Calling.this, R.drawable.call_screen_module));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("ON RESUME CALLING");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("ON PAUSE");
        player.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("ON START");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("ON STOP");
    }

    public static Context getAppContext(){
        return Calling.context;
    }

    public static Calling getInstance(){
        return callingActivity;
    }

    public void updateCallDuation(){
        Calling.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (secs < 59){
                    secs++;
                }else {
                    secs = 0;
                    if (mins < 59){
                        mins++;
                    }else {
                        mins = 0;
                        hours++;
                    }
                }
                callDuration = String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
                callStatus.setText(callDuration);
            }
        });
    }

    public void addData(String extension, String username, String duration, String time, String callStatus){
        mDatabaseHelper = new DataBaseHelper(getApplicationContext());
        boolean insertData = mDatabaseHelper.addData(extension, username, duration, time, callStatus);
        if (insertData){
            toastMessage("Data Successfully Inserted!");
        }else {
            toastMessage("Something went wrong");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String callTime(){
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

    public static Activity getActivity(){
        return Calling.activity;
    }
}
