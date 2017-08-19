package com.example.admin.vkclub;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.sip.SipException;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class Receivecall extends AppCompatActivity {

    MediaPlayer player;
    Dashboard dashboard;
    ImageButton ansBtn, decBtn, toggleSpeaker, toggleMute;
    TextView callStatus, peerProfile;
    Boolean isMute = false, isSpeaker = false;

    private static Context receiveCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_receivecall);

        Receivecall.receiveCall = this;

        dashboard = (Dashboard) Dashboard.getAppContext();
        callStatus = (TextView) findViewById(R.id.call_status);
        peerProfile = (TextView) findViewById(R.id.peer);
        ansBtn = (ImageButton) findViewById(R.id.answer);
        decBtn = (ImageButton) findViewById(R.id.decline);
        toggleSpeaker = (ImageButton) findViewById(R.id.speaker);
        toggleMute = (ImageButton) findViewById(R.id.mute);

        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.start();

        toggleSpeaker.setVisibility(View.GONE);
        toggleMute.setVisibility(View.GONE);

        callStatus.setText("Calling...");

        final String callerId = dashboard.audioCall.getPeerProfile().getUserName();
        if(callerId != null){
            peerProfile.setText(callerId);
        } else{
            peerProfile.setText("Unknown");
        }

        ansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                toggleMute.setVisibility(View.VISIBLE);
                toggleSpeaker.setVisibility(View.VISIBLE);
                ansBtn.setVisibility(View.GONE);

                try {
                    dashboard.audioCall.answerCall(30);
                    dashboard.audioCall.startAudio();
                    if(dashboard.audioCall.isMuted()){
                        dashboard.audioCall.toggleMute();
                    }
                } catch (SipException e) {
                    e.printStackTrace();
                    dashboard.presentDialog("Error!", "Unable to answer. Please check with vKirirom Receptionists.");
                }
            }
        });

        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
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
                    toggleMute.setBackground(ContextCompat.getDrawable(Receivecall.this, R.drawable.call_screen_module_false));
                } else{
                    dashboard.audioCall.toggleMute();
                    toggleMute.setImageResource(R.drawable.ic_muted_white);
                    toggleMute.setBackground(ContextCompat.getDrawable(Receivecall.this, R.drawable.call_screen_module));
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
                    toggleSpeaker.setBackground(ContextCompat.getDrawable(Receivecall.this, R.drawable.call_screen_module_false));
                } else{
                    dashboard.audioCall.setSpeakerMode(false);
                    toggleSpeaker.setImageResource(R.drawable.ic_speaker_white);
                    toggleSpeaker.setBackground(ContextCompat.getDrawable(Receivecall.this, R.drawable.call_screen_module));
                }
            }
        });
    }

    public static Context getAppContext(){
        return Receivecall.receiveCall;
    }
}
