package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    private TextView mHelp;
    private Switch msetting;
    SharedPreferences prefs,shareprefs;
    SharedPreferences.Editor editor;
    Boolean isFirstLaunch,istrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        msetting = (Switch) findViewById(R.id.settingbtn);
        mHelp = (TextView) findViewById(R.id.help);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Setting");

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        //set switch on as defaults
        prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
        shareprefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
        isFirstLaunch = shareprefs.getBoolean("noti_setting", false);
        if (isFirstLaunch){
            istrue = prefs.getBoolean("isTrue",false);
            if(istrue==true){
                msetting.setChecked(true);
            }else {
                msetting.setChecked(false);
            }
            msetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(msetting.isChecked()){
                        msetting.setChecked(true);
                        sharepreference(true);
                        toggleNotification( PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                    }else {
                        msetting.setChecked(false);
                        sharepreference(false);
                        toggleNotification(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                    }
                }
            });
        }else {
            editor = shareprefs.edit();
            editor.putBoolean("noti_setting", true);
            editor.commit();

            msetting.setChecked(true);
            sharepreference(true);
            toggleNotification(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
            msetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b==true){
                        msetting.setChecked(true);
                        sharepreference(true);
                        toggleNotification(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                    }else {
                        msetting.setChecked(false);
                        sharepreference(false);
                        toggleNotification(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                    }
                }
            });
        }

        //Help Alert
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentDialog("Help","Notification: Turn OFF/ON all incoming alert notification including Digital News Content as well as Chat Messaging.");
            }
        });

    }

    public void toggleNotification(int state){
        PackageManager pm = getApplicationContext().getPackageManager();
        ComponentName componentName = new ComponentName(Setting.this, MyFirebaseMessagingService.class);
        pm.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP);
    }

    public void sharepreference(boolean onOff){
        editor = prefs.edit();
        editor.putBoolean("isTrue",onOff);
        editor.commit();
    }

    public void presentDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
