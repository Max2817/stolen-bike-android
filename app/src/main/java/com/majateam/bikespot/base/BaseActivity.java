package com.majateam.bikespot.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.majateam.bikespot.helper.InternetConnectionType;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String BROADCAST_EVENT_WIFI_STATE_CHANGE = "android.net.wifi.STATE_CHANGE";
    private static final String BROADCAST_EVENT_CONNECTIVITY_STATE_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction(BROADCAST_EVENT_WIFI_STATE_CHANGE);
        internetFilter.addAction(BROADCAST_EVENT_CONNECTIVITY_STATE_CHANGE);
        registerReceiver(broadcastReceiver, internetFilter);
    }

    protected abstract void setSnackBarMessage();

    protected abstract void onNetworkConnectionUpdated(Integer connectivityCode);

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSnackBarMessage();
            onNetworkConnectionUpdated(getConnectivityStatus(getApplicationContext()));
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == InternetConnectionType.TYPE_WIFI.getValue())
                return InternetConnectionType.TYPE_WIFI.getValue();

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return InternetConnectionType.TYPE_MOBILE.getValue();
        }
        return InternetConnectionType.TYPE_NOT_CONNECTED.getValue();
    }
}
