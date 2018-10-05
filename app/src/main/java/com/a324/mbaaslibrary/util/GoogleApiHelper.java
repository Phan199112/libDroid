package com.a324.mbaaslibrary.util;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation;
    private String address;
    private double latitude = 0.0D;
    private double longitude = 0.0D;
    Context context;
    private static final int REQUEST_CHECK_SETTINGS = 2000;
    static GoogleApiHelper instance = null;

    private GoogleApiHelper(Context context) {
        this.context = context;
        this.buildApiClient();
    }

    public static GoogleApiHelper getInstance(Context context) {
        if(instance == null) {
            instance = new GoogleApiHelper(context);
        }

        return instance;
    }

    public void buildApiClient() {
        if(this.mGoogleApiClient == null) {
            this.mGoogleApiClient = (new GoogleApiClient.Builder(this.context)).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        this.mGoogleApiClient.connect();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000L);
        mLocationRequest.setFastestInterval(5000L);
        mLocationRequest.setPriority(100);
        com.google.android.gms.location.LocationSettingsRequest.Builder builder = (new com.google.android.gms.location.LocationSettingsRequest.Builder()).addLocationRequest(mLocationRequest);
        PendingResult result = LocationServices.SettingsApi.checkLocationSettings(this.mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            public void onResult(LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch(status.getStatusCode()) {
                    case 0:
                        GoogleApiHelper.this.getLocation();
                        break;
                    case 6:
                        try {
                            status.startResolutionForResult((Activity)GoogleApiHelper.this.context, 2000);
                        } catch (IntentSender.SendIntentException var4) {
                            ;
                        }
                    case 8502:
                }

            }
        });
    }

    public void onConnectionFailed(ConnectionResult result) {
    }

    public void onConnected(@Nullable Bundle bundle) {
        this.getLocation();
    }

    public void onConnectionSuspended(int arg0) {
        this.mGoogleApiClient.connect();
    }

    public Location getLocation() {
        try {
            if(this.mGoogleApiClient.isConnected()) {
                this.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
                if(this.mLastLocation != null) {
                    this.latitude = this.mLastLocation.getLatitude();
                    this.longitude = this.mLastLocation.getLongitude();
                    this.setAddress(this.context);
                }
            }

            return this.mLastLocation;
        } catch (SecurityException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public void setAddress(Context mContext) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            List e = geocoder.getFromLocation(this.latitude, this.longitude, 1);

            for(int i = 0; i < e.size(); ++i) {
                Address obj = (Address)e.get(i);
                this.address = obj.getAddressLine(0);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
            Toast.makeText(mContext, var6.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public String getLatLong() {
        return this.latitude + "," + this.longitude;
    }

    public String getAddress() {
        return this.address;
    }
}
