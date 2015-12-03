package com.majateam.bikespot;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.majateam.bikespot.helper.BikeLocationDbHelper;
import com.majateam.bikespot.model.Bike;
import com.majateam.bikespot.model.Dock;
import com.majateam.bikespot.provider.LocationProvider;
import com.majateam.bikespot.renderer.BikeRenderer;
import com.majateam.bikespot.service.LocationService;

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

public class MainActivity extends BaseActivity implements LocationProvider.LocationCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationProvider mLocationProvider;
    private BikeLocationDbHelper mDbHelper;
    private List<Bike> bikes;
    private List<Dock> docks;
    private ClusterManager<ClusterItem> mClusterManager;
    private static final int BIKES = 10;
    private static final int DOCKS = 20;
    private static final String CHOICE = "choice";
    private static final String MENU_VISIBILITY = "menu_visibility";
    private LayoutInflater mInflater;
    private PopupWindow mDropdown = null;
    private int mChoice;

    private Marker mUserMarker = null;
    @Bind(R.id.sub_menu)
    LinearLayout mSubMenu;
    @Bind(R.id.display_choice)
    TextView mDisplayChoice;
    @Bind(R.id.show_choice_txt)
    TextView mShowChoice;
    @Bind(R.id.choice_icon)
    ImageView mChoiceIcon;
    @Bind(R.id.show_icon)
    ImageView mShowIcon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        /*Toolbar toolbar = (Toolbar) findViewById(com.majateam.bikespot.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        //set default choice

        if(savedInstanceState != null) {
            mChoice = savedInstanceState.getInt(CHOICE);
            mSubMenu.setVisibility((savedInstanceState.getInt(MENU_VISIBILITY) == View.VISIBLE) ? View.VISIBLE : View.GONE);
            setChoice();
        }else{
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_stolen_bike_location);
            mShowIcon.setImageResource(R.drawable.ic_dock_location);
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.majateam.bikespot.R.menu.menu_base, menu);
        return true;
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHOICE, mChoice);
        outState.putInt(MENU_VISIBILITY, mSubMenu.getVisibility());
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case com.majateam.bikespot.R.id.item_menu:
                showPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @OnClick(R.id.menu_icon)
    public void showPopup() {
        mSubMenu.setVisibility((mSubMenu.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    @OnClick(com.majateam.bikespot.R.id.show_choice)
    public void showChoice() {
        updateChoice();
        showPopup();
    }

    private void updateChoice(){
        if(mChoice == BIKES){
            mChoice = DOCKS;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_bikes);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.docks);
            mChoiceIcon.setImageResource(R.drawable.ic_dock_location);
            mShowIcon.setImageResource(R.drawable.ic_stolen_bike_location);
        }else{
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_stolen_bike_location);
            mShowIcon.setImageResource(R.drawable.ic_dock_location);
        }
        setClusterItems(mChoice);
    }

    private void setChoice(){
        if(mChoice == BIKES){
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_stolen_bike_location);
            mShowIcon.setImageResource(R.drawable.ic_dock_location);
        }else{
            mChoice = DOCKS;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_bikes);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.docks);
            mChoiceIcon.setImageResource(R.drawable.ic_dock_location);
            mShowIcon.setImageResource(R.drawable.ic_stolen_bike_location);
        }
        setClusterItems(mChoice);
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
                //SQLiteDatabase db = mDbHelper.getWritableDatabase();
                //mDbHelper.onInsertList(db, returnedBikes);
                bikes = returnedBikes;


                LocationService locationService = new RestAdapter.Builder()
                        .setEndpoint(Global.ENDPOINT)
                        .build()
                        .create(LocationService.class);
                locationService.listDocks(new Callback<List<Dock>>() {
                    @Override
                    public void success(List<Dock> returnedDocks, Response response) {
                        docks = returnedDocks;
                        setClusterItems(mChoice);
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
        if (type == BIKES && bikes != null) {
            mClusterManager.addItems(new ArrayList<ClusterItem>(bikes));
        }else if(docks != null){
            mClusterManager.addItems(new ArrayList<ClusterItem>(docks));
        }
        mClusterManager.cluster();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    public void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        GoogleMap map = getMap();
        if(mUserMarker == null){
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(com.majateam.bikespot.R.drawable.ic_user_location));
            mUserMarker = map.addMarker(options);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15.0f));
        }else{
            mUserMarker.setPosition(latLng);
        }


    }


}
