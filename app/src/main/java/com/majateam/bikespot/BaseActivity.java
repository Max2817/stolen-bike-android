package com.majateam.bikespot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


public abstract class BaseActivity extends AppCompatActivity {

    public final static int TYPE_WIFI = 10;
    public final static int TYPE_MOBILE = 11;
    public final static int TYPE_NOT_CONNECTED = 12;

    private GoogleMap mMap;

    protected int getLayoutId() {
        return com.majateam.bikespot.R.layout.map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
        if(getConnectivityStatus(this) != TYPE_NOT_CONNECTED) {
            if (mMap != null) {
                startApp();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(com.majateam.bikespot.R.id.map)).getMap();
    }

    /**
     * Run the demo-specific code.
     */
    protected abstract void startApp();

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    protected abstract void setSnackbarMessage();
    protected abstract void onNetworkConnectionUpdated(Integer connectivityCode);
    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSnackbarMessage();
            onNetworkConnectionUpdated(getConnectivityStatus(getApplicationContext()));
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

}
