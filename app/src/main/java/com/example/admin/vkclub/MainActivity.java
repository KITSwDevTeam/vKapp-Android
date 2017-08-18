package com.example.admin.vkclub;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import android.os.Handler;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Boolean isFirstLaunch;
    private static Context context;

    // Duration
    private final int SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

//        PackageManager pm = getApplicationContext().getPackageManager();
//        ComponentName componentName = new ComponentName(MainActivity.this, MyFirebaseMessagingService.class);
//        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                isFirstLaunch = preferences.getBoolean("FirstLaunch", false);
                if (isFirstLaunch){
                    if (user != null) {
                        for (UserInfo profile : user.getProviderData()) {
                            // Id of the provider (ex: google.com)
                            String providerId = profile.getProviderId();
                            System.out.println("lllllllllllllllllllllllllllllllllllllllll   " + providerId);
                            if (providerId.equals("facebook.com")){
                                navigate(Dashboard.class);
                            }else {
                                // User is signed in
                                if (user.isEmailVerified()){
                                    navigate(Dashboard.class);
                                }else {
                                    navigate(LoginActivity.class);
                                }
                            }
                        }
                    } else {
                        // User is signed out
                        navigate(LoginActivity.class);
                    }
                }else {
                    editor = preferences.edit();
                    editor.putBoolean("FirstLaunch", true);
                    editor.commit();
                    Intent intent = new Intent(context, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void navigate(final Class next) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent(MainActivity.this, next);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
