package com.example.yuravyrovoy.trylocation;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    final private static int MY_PERMISSIONS_REQUEST_GET_LOCATION = 124;
    private boolean mLocationPermitted;
    LocationListener mNetworkListener;
    LocationListener mGPSListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationPermitted = false;

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            setMessage("Retrieving permission.\r\nPlease wait...");
            requestGetLocationPermission();

        } else {

            mLocationPermitted = true;
            setMessage("We have the location permission (1)");
        }
    }

    private void requestGetLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);


/*
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == false) {

                mLocationPermitted = false;
                setMessage("No location permission. User postponed");
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_GET_LOCATION);

            }
*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mLocationPermitted = true;
                    setMessage("We have the location permission (2)");

                } else {

                    mLocationPermitted = false;
                    setMessage("No location permission.");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onPause (){
        super.onPause();

        setMessage("Location request is canceled");
        setNetworkMessage("");
        setGPSkMessage("");
        removeLocationListeners();
    }

    private void setLocationListener() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        mGPSListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewGPSLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        mNetworkListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewNetworkLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                                != PackageManager.PERMISSION_GRANTED))
        {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGPSListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkListener);
    }

    private void makeUseOfNewGPSLocation(Location location){

        setMessage("GPS location achieved");
        setGPSkMessage("GPS: " + location.toString());
    }

    private void makeUseOfNewNetworkLocation(Location location){
        setMessage("Network location achieved");
        setNetworkMessage("Network: " + location.toString());
    }

    public void onClickRequest(View v) {

        if(mLocationPermitted == true){
            setMessage("Location requested");
            setNetworkMessage("Network location: Waiting...");
            setGPSkMessage("GPS location: Waiting...");

            setLocationListener();
        }
        else{
            setMessage("Location request is not permitted");
        }
    }

    public void onClickStop(View v) {

        setMessage("Location request is canceled");
        removeLocationListeners();
    }

    private void removeLocationListeners(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(mGPSListener);
        locationManager.removeUpdates(mNetworkListener);
    }

    private void setMessage(String sMessage){
        TextView textView = (TextView) findViewById(R.id.textMessage);

        if(textView != null) {
            textView.setText(sMessage);
        }
    }

    private void setNetworkMessage(String sMessage){
        TextView textNetworkMessage = (TextView)findViewById(R.id.text_network);

        if(textNetworkMessage != null) {
            textNetworkMessage.setText(sMessage);
        }
    }

    private void setGPSkMessage(String sMessage){

        TextView textGPSMessage = (TextView)findViewById(R.id.text_gps);

        if(textGPSMessage != null) {
            textGPSMessage.setText(sMessage);
        }

    }

}

