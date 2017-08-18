package com.example.admin.vkclub;


import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final LatLng KIRIROM = new LatLng(11.3167, 104.0651);
//    private static final String TAG = "hello";
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    SupportMapFragment mFragment;
    Marker mCurrLocation;
    LatLng latLng;
    private static Context mapContext;
    private static Activity mapActivity;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapContext = this;
        mapActivity = this;

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Move the camera instantly to Sydney with a zoom of 15.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KIRIROM, 15));

        // Zoom in, animating the camera.
//        googleMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(KIRIROM)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
//               .bearing(90)                // Sets the orientation of the camera to east
//                . tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //set maxzoom of the map
        mMap.setMaxZoomPreference(17.0f);
        //Add GroundOverlay

//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_s);

        LatLngBounds latlngBounds = new LatLngBounds(
                new LatLng(11.3040, 104.0324),
                new LatLng(11.3430, 104.0844));

        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.vkmap_1))
                .positionFromBounds(latlngBounds);

        mMap.addGroundOverlay(groundOverlayOptions);

        //Add marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3152, 104.0676))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("The first boarding school in Cambodia, specialized in software engineering. Students are also engaged in internship projects while studying.")
                .title("Kirirom Institute of Technology"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3165, 104.0648))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Pleasant activity staff offers the information regarding various types of activities. Activity in fresh air helps you refresh your mind. Open 8:00-17:00.")
                .title("Activity Center"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3167, 104.0653))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("At the open space restaurant, Pine View Kitchen helps you enjoy a chef’s special full course of modern Khmer cuisine.")
                .title("Pine View Kitchen"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3174, 104.0649))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Customers can be welcomed to vKirirom Pine Resort. Open 8:00-20:00.Our staffs can speak English, Khmer, Japanese. Customers are provided with amenities as well.")
                .title("Reception"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3137, 104.0667))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("A tent which occupies large space which is suitable for big events, conferences and many activities. Even during rainy days, this large tent provides enjoyable indoor activity.")
                .title("Conference Tent"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3154, 104.0638))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Khmer style nature fused restaurant which serves Khmer original meals. You can also buy breads and drinks here.")
                .title("Moringa"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3181, 104.0633))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Quite elegant cottage which promises you a lot of pleasant experiences on the cool Kirirom Mountain top for couples and small families.")
                .title("Villa Jasmine"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3180, 104.0655))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Modern designed luxury room. This two-bedroom villa with a mezzanine level is suitable for big families or groups.")
                .title("Villa Suite"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3126, 104.0628))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("The most uniquely designed room derived from an earthen pipe which serves best amongst all.")
                .title("Pipe Room"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3145, 104.0646))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("High quality tent with comfortable hotel type room. Modern tent houses which have a king size bed and a nice shower room.")
                .title("Luxury Tent"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3149, 104.0644))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Khmer farmers' open-style houses which are nicely decorated with natural materials. Best rooms to experience real Khmer tradition and feel the natural fresh air.")
                .title("Khmer Cottage"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3162, 104.0654))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Enjoy many kinds of activities including football, tennis, volleyball, bubble sumo etc. Feel free to ask our activity staff for details.")
                .title("Playground Field"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3134, 104.0648))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Enjoy camping with camp fire in a large area space with high level of security provided.")
                .title("Camping Area"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3344, 104.0516))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("A beautiful lake, provides enough water supply for all the villager")
                .title("Kirirom Lake"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3348, 104.0550))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("A Village where people enjoy living on Kirirom Mountain with a perfect view.")
                .title("Village"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3330, 104.0531))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Ministry of Environment that supports the whole Kirirom environment.")
                .title("Ministry of Environment"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3165, 104.0658))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Stay in one of our specially designed bungalows and experience the invigorating fresh air and peaceful life in the pine forests of kirirom")
                .title("Bungalow"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3168, 104.0658))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Enjoy swimming with your family and friends with the surrounded pine trees and the fresh air.")
                .title("Swimming Pool"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3121, 104.0653))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Enjoy playing tennis on top of the mountain surrounded by pine trees.")
                .title("Tennis Court"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3169, 104.0647))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Big secure parking space for customers vehicle.")
                .title("Parking Area"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3139, 104.0654))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("A place for coffee lovers who enjoy having the amazing flavours with fresh air.")
                .title("Container Cafe"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3136, 104.0751))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("An outdoor party stage for big event in the top mountain of kirirom. You can enjoy lunch, BBQ and also drinks with karaoke.")
                .title("Crazy Hill"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3134, 104.0636))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("An organic farm that grows a variety of foods such as Strawberry, Pineapple, Passions etc.")
                .title("Farm"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3409, 104.0597))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Dragon Statues (snake God) whose four heads are landmark, is situated in the center of the center of the intersection.")
                .title("Dragon Statue"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3201, 104.0362))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("It is a Buddhist temple with the longest histroy in Kirirom.It makes you back to the good days in Cambodia.Please follow this good manners when worship ping the temple.")
                .title("Old Kirirom Pagoda"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3304, 104.0769))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("On the top of the stairs of gentle slope. A mural paining drawn Buddha's life inside of the building is also an sightseeing spot.")
                .title("New Kirirom Pagoda"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3111, 104.0784))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("This place us know to those in the know, and it is loved by locals. We recommend you to visit there during the rainy season.")
                .title("Otrosek Waterfall"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3291, 104.0379))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("The landscape is almost as if it is a framed picture. You can feel the magnificence of the nature while being away from the hustle and bustle of the city.")
                .title("Srash Srang Lake"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3309, 104.0606))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("The residence,which quietly stands among a pine grove,was a old fashioned cottage built of bricks.")
                .title("King's Residence"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3351, 104.0407))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("A visitor center which introduce the history of Kirirom. It is a wonderful photogenic spot.")
                .title("Visitor Center"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3133, 104.0657))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Customers can also enjoy playing football with their friends together in the resort.")
                .title("Football Court"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3129, 104.0657))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Customers can also enjoy playing volleyball with their friends together in the resort.")
                .title("Volleyball Court"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3345, 104.0553))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("It is the only elementary school established by mainly vKirirom stuff.For the bright future of children regardless of the envirionment the were grown up.")
                .title("Kirirom Elementary School"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3138, 104.0727))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("The 2km short cut road that opens up from an entrance of our resort to Conference Tent.")
                .title("Bopha Road"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3126, 104.0646))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Glamping, glamourous camping provides you a good sleep on a fluffy bed.")
                .title("Bell tent"));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(11.3158, 104.0649))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("It is a multi-purpose building which during day time can be used to enjoy wall climbing as well as a movie screen at night time.")
                .title("Climbing Theater"));


        // Add button of the map
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationService = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean locationServiceEnabled = locationService.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (locationServiceEnabled){
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(mapContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mapContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(mapActivity, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 100);
                    }
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mapContext);
                    builder.setTitle("Location provider disabled");
                    builder.setMessage("Vkclub cannot identify your current location due to location provider disabled\n" +
                            "Would you like to enable it ?");
                    builder.setCancelable(true);
                    builder.setPositiveButton(
                            "Enable",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            });

                    builder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return false;
            }
        });
//
//        buildGoogleApiClient();
//
//        mGoogleApiClient.connect();





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                AlertDialog.Builder builder = new AlertDialog.Builder(mapContext);
                builder.setTitle("Location permission denied.");
                builder.setMessage("Vkclub cannot obtain your current location without this permissions.");
                builder.setCancelable(true);
                builder.setNegativeButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    //    protected synchronized void buildGoogleApiClient() {
////        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }


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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            //place marker at current position
//            mMap.clear();
//            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//
//    }


    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        //remove previous current location marker and add new one at current position
//        if (mCurrLocation != null) {
//            mCurrLocation.remove();
//        }
//        Location location = null;
//        latLng = new LatLng(location.getLatitude(), location.getLongitude());

//        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
