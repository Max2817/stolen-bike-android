package com.majateam.spotbike;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


public abstract class BaseActivity extends AppCompatActivity {

    private GoogleMap mMap;

    protected int getLayoutId() {
        return com.majateam.spotbike.R.layout.map;
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
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(com.majateam.spotbike.R.id.map)).getMap();
        if (mMap != null) {
            startDemo();
        }
    }

    /**
     * Run the demo-specific code.
     */
    protected abstract void startDemo();

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }

}
