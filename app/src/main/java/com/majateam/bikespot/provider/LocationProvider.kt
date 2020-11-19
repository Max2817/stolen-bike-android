package com.majateam.bikespot.provider

import android.content.Context
import android.location.Location

/**
 * Created by nmartino on 12/17/14.
 */
class LocationProvider(context: Context?, callback: LocationCallback?) /*public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity)mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        (MainActivity)mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {

            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                mLocationCallback.handleNewLocation(location);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    public void callNewLocation(){
        onConnected(null);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!mResolvingError) {
            // Already attempting to resolve an error.
            if (connectionResult.hasResolution() && mContext instanceof Activity) {
                try {
                    mResolvingError = true;
                    Activity activity = (Activity)mContext;
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    // Log the error
                    e.printStackTrace();
                    mGoogleApiClient.connect();
                }
            } else {
                //showErrorDialog(result.getErrorCode());
                mResolvingError = true;
                Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }*/ {
    interface LocationCallback {
        fun handleNewLocation(location: Location?)
    }

    private val mLocationCallback: LocationCallback? = null
    private val mContext: Context? = null
    private val mResolvingError: Boolean? = null

    companion object {
        val TAG = LocationProvider::class.java.simpleName
        const val REQUEST_LOCATION = 100

        /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
        private const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    }
}