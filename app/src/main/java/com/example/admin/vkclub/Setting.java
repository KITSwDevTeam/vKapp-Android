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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    private TextView mHelp;
    private Switch msetting, mNotification;
    SharedPreferences preference,prefs;
    SharedPreferences.Editor editor,editors;
    Boolean isFirstLaunch,istrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        msetting = (Switch) findViewById(R.id.settingbtn);
        mNotification = (Switch) findViewById(R.id.locationbtn);
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
        preference = PreferenceManager.getDefaultSharedPreferences(Setting.this);
        isFirstLaunch = preference.getBoolean("noti_setting", false);
        Log.i("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^",isFirstLaunch.toString());
        if (isFirstLaunch){
//            if(istrue = prefs.getBoolean("isTrue",true)){
//                msetting.setChecked(true);
//            }else if(istrue = prefs.getBoolean("isTrue",false)){
//                msetting.setChecked(false);
//            }else {
//
//            }
            prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
            istrue = prefs.getBoolean("isTrue",false);
            Log.i("IIIIIIIIIIIIIIIIIIIIIIIIII",istrue.toString());
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
                        Log.i("+++++++++++++++++++++++++++++++++",msetting.toString());

                        prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
                        editors = prefs.edit();
                        editors.putBoolean("isTrue", true);
                        editors.commit();

                        PackageManager pm = getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(Setting.this, MyFirebaseMessagingService.class);
                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    }else {
                        msetting.setChecked(false);
                        Log.i("------------------------------------",msetting.toString());

                        prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
                        editors = prefs.edit();
                        editors.putBoolean("isTrue", false);
                        editors.commit();

                        PackageManager pm = getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(Setting.this, MyFirebaseMessagingService.class);
                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    }
                }
            });
        }else {
            editor = preference.edit();
            editor.putBoolean("noti_setting", true);
            editor.commit();

            msetting.setChecked(true);
            prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
            editors = prefs.edit();
            editors.putBoolean("isTrue", true);
            editors.commit();
            msetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b==true){

                        prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
                        editors = prefs.edit();
                        editors.putBoolean("isTrue",true);
                        Log.i("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV",editor.toString());
                        editors.commit();
                        PackageManager pm = getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(Setting.this, MyFirebaseMessagingService.class);
                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    }else {
                        prefs = PreferenceManager.getDefaultSharedPreferences(Setting.this);
                        editors = prefs.edit();
                        editors.putBoolean("isTrue",false);
                        Log.i("PPPPPPPPPPPPPPPPPPPPPPPPPPP",editor.toString());
                        editors.commit();
                        PackageManager pm = getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(Setting.this, MyFirebaseMessagingService.class);
                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    }
                }
            });


            Log.i("*********************",msetting.toString());
            PackageManager pm = getApplicationContext().getPackageManager();
            ComponentName componentName = new ComponentName(Setting.this, MyFirebaseMessagingService.class);
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        }


        //Help Alert
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentDialog("Help","Notification: Turn OFF/ON all incoming alert notification including Digital News Content as well as Chat Messaging.");
            }
        });

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
