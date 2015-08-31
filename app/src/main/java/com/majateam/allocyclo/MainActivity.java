package com.majateam.allocyclo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.majateam.allocyclo.helper.BikeLocationDbHelper;
import com.majateam.allocyclo.model.Bike;
import com.majateam.allocyclo.model.Dock;
import com.majateam.allocyclo.provider.LocationProvider;
import com.majateam.allocyclo.renderer.BikeRenderer;
import com.majateam.allocyclo.service.LocationService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements LocationProvider.LocationCallback  {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationProvider mLocationProvider;
    private BikeLocationDbHelper mDbHelper;
    private List<Bike> bikes;
    private List<Dock> docks;
    private ClusterManager<ClusterItem> mClusterManager;
    private static final int BIKES = 10;
    private static final int DOCKS = 20;
    private LayoutInflater mInflater;
    private PopupWindow mDropdown = null;
    @Bind(R.id.sub_menu)
    LinearLayout mSubMenu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_base, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_menu:
                showPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void showPopup() {
        /*View menuItemView = findViewById(R.id.item_menu);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.show_bikes:
                        setClusterItems(BIKES);
                        return true;
                    case R.id.show_docks:
                        setClusterItems(DOCKS);
                        return true;
                    default:
                        return true;
                }
            }
        });
        popup.show();*/
        mSubMenu.setVisibility((mSubMenu.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.show_bikes)
    public void showBikes() {
        setClusterItems(BIKES);
        showPopup();
    }

    @OnClick(R.id.show_docks)
    public void showDocks() {
        setClusterItems(DOCKS);
        showPopup();
    }

    private PopupWindow initiatePopupWindow() {
        View menuItemView = findViewById(R.id.item_menu);
        try {

            mInflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = mInflater.inflate(R.layout.popup_window, null);

            //If you want to add any listeners to your textviews, these are two //textviews.
            //final TextView itema = (TextView) layout.findViewById(R.id.ItemA);


            //final TextView itemb = (TextView) layout.findViewById(R.id.ItemB);



            layout.measure(View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED);
            mDropdown = new PopupWindow(layout, FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,true);
            Drawable background = getResources().getDrawable(android.R.drawable.editbox_dropdown_dark_frame);
            mDropdown.setBackgroundDrawable(background);
            mDropdown.showAsDropDown(menuItemView, 5, 5);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDropdown;

    }


    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void startDemo() {
        mLocationProvider = new LocationProvider(this, this);
        mClusterManager = new ClusterManager<>(this, getMap());
        mClusterManager.setRenderer(new BikeRenderer(getApplicationContext(), getMap(), mClusterManager));
        getMap().setOnCameraChangeListener(mClusterManager);

        //Call the bikes
        LocationService locationService = new RestAdapter.Builder()
                .setEndpoint(Global.ENDPOINT)
                .build()
                .create(LocationService.class);
        mDbHelper = new BikeLocationDbHelper(this);

        locationService.listBikes(new Callback<List<Bike>>() {
            @Override
            public void success(List<Bike> returnedBikes, Response response) {
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                mDbHelper.onInsertList(db, returnedBikes);
                bikes = returnedBikes;
               /* for (Bike bike : bikes) {
                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(bike.getLat()), Double.parseDouble(bike.getLng())))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stolen_bike_location))
                    );
                }*/


                LocationService locationService = new RestAdapter.Builder()
                        .setEndpoint(Global.ENDPOINT)
                        .build()
                        .create(LocationService.class);
                locationService.listDocks(new Callback<List<Dock>>() {
                    @Override
                    public void success(List<Dock> returnedDocks, Response response) {
                        docks = returnedDocks;
                        setClusterItems(BIKES);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // you should handle errors, too
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                // you should handle errors, too
            }
        });
    }

    private void setClusterItems(int type) {
        mClusterManager.clearItems();
        if(type == BIKES)
            mClusterManager.addItems(new ArrayList<ClusterItem>(bikes));
        else
            mClusterManager.addItems(new ArrayList<ClusterItem>(docks));
        mClusterManager.cluster();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location));
        getMap().addMarker(options);
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 17.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
    }


}
