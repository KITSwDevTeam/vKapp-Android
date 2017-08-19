package com.example.admin.vkclub;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by admin on 6/19/2017.
 */

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        toastMessage("Start GPS_Location service....................................");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("latitude", location.getLatitude());
                i.putExtra("longitude", location.getLongitude());
                sendBroadcast(i);
                Log.v(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent("location_update");
                i.putExtra("latitude", "provider_disabled");
                i.putExtra("longitude", "provider_disabled");
                sendBroadcast(i);
                Log.v("unavailable", "unavailable");
                toastMessage("Your location setting is disabled. Please enable them under setting to share your current location with Vkclub." +
                        "\nThank you for using Vkclub.");

            }

        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

            if (locationManager == null){
                Intent i = new Intent("location_update");
                i.putExtra("latitude", "unavailable");
                i.putExtra("longitude", "unavailable");
                sendBroadcast(i);
                Log.v("unavailable", "unavailable");
            }
        }else {
            Intent i = new Intent("location_update");
            i.putExtra("latitude", "permission_denied");
            i.putExtra("longitude", "permission_denied");
            sendBroadcast(i);
            Log.v("permission_denied", "permission_denied");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }

    private void toastMessage(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
