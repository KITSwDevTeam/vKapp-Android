package com.example.admin.vkclub;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class Services extends AppCompatActivity {
    Button mReservation, mReception, mActivity, mPineviewkitchen, mMessageservice, mHousekeeping;
    Dashboard dashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        dashboard = (Dashboard) Dashboard.getAppContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Services");

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        mReservation = (Button) findViewById(R.id.reservation);
        mReception = (Button) findViewById(R.id.reception);
        mActivity = (Button) findViewById(R.id.activitybtn);
        mPineviewkitchen = (Button) findViewById(R.id.pineviewkitchen);
        mMessageservice = (Button) findViewById(R.id.messageservice);
        mHousekeeping = (Button) findViewById(R.id.housekeeping);

        mReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://vkirirom.com/en/reservation.php"));
                startActivity(intent);
            }
        });
        mReception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Services.this, Calling.class);
                in.putExtra("STATE", "DAILING");
                in.putExtra("CALLEE", "100235");
                startActivity(in);
                dashboard.initiateCall("100235");
            }
        });
        mActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Services.this, Calling.class);
                in.putExtra("STATE", "DAILING");
                in.putExtra("CALLEE", "100236");
                startActivity(in);
                dashboard.initiateCall("100236");
            }
        });
        mPineviewkitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Services.this, Calling.class);
                in.putExtra("STATE", "DAILING");
                in.putExtra("CALLEE", "100237");
                startActivity(in);
                dashboard.initiateCall("100237");
            }
        });
        mMessageservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Services.this, Calling.class);
                in.putExtra("STATE", "DAILING");
                in.putExtra("CALLEE", "100238");
                startActivity(in);
                dashboard.initiateCall("100238");
            }
        });
        mHousekeeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Services.this, Calling.class);
                in.putExtra("STATE", "DAILING");
                in.putExtra("CALLEE", "100239");
                startActivity(in);
                dashboard.initiateCall("100239");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
