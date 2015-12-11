package com.majateam.bikespot;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
    private double mCurrentLatitude;
    private double mCurrentLongitude;

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
            mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            mShowIcon.setImageResource(R.drawable.ic_legend_dock);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHOICE, mChoice);
        outState.putInt(MENU_VISIBILITY, mSubMenu.getVisibility());
    }


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
            mChoiceIcon.setImageResource(R.drawable.ic_legend_dock);
            mShowIcon.setImageResource(R.drawable.ic_legend_stolen);
        }else{
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            mShowIcon.setImageResource(R.drawable.ic_legend_dock);
        }
        setClusterItems(mChoice);
    }

    private void setChoice(){
        if(mChoice == BIKES){
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            mShowIcon.setImageResource(R.drawable.ic_legend_dock);
        }else{
            mChoice = DOCKS;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_bikes);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.docks);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_dock);
            mShowIcon.setImageResource(R.drawable.ic_legend_stolen);
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

        mCurrentLatitude = 45.5486;
        mCurrentLongitude = -73.5788;
        GoogleMap map = getMap();
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        MarkerOptions options = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(com.majateam.bikespot.R.drawable.ic_user_location));
        mUserMarker = map.addMarker(options);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final LocationService service = retrofit.create(LocationService.class);

        Call<List<Bike>> callBikes = service.listBikes();
        callBikes.enqueue(new Callback<List<Bike>>() {

            @Override
            public void onResponse(Response<List<Bike>> response, Retrofit retrofit) {

                bikes = response.body();

                Call<List<Dock>> callDocks = service.listDocks();
                callDocks.enqueue(new Callback<List<Dock>>() {

                    @Override
                    public void onResponse(Response<List<Dock>> response, Retrofit retrofit) {
                        docks = response.body();
                        setClusterItems(mChoice);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // you should handle errors, too
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
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
        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        GoogleMap map = getMap();
        if(mUserMarker == null){
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(com.majateam.bikespot.R.drawable.ic_user_location));
            mUserMarker = map.addMarker(options);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
        }else{
            mUserMarker.setPosition(latLng);
        }


    }
    //Direction server key AIzaSyDNtlRTiYN4cNhjmO3Zzzghg0I7mV5i9bc

    @OnClick(R.id.map_user_location)
    public void showUserLocation() {
        //GoogleMap map = getMap();
        /*if(map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
        }*/
        String serverKey = "AIzaSyDNtlRTiYN4cNhjmO3Zzzghg0I7mV5i9bc";
        LatLng origin = new LatLng(45.5486, -73.5788);
        LatLng destination = new LatLng(45.5231079, -73.589279);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //for (Marker marker : markers) {
        builder.include(origin);
        builder.include(destination);
        //}
        LatLngBounds bounds = builder.build();
        int padding = 30; // offset from edges of the map in pixels
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.BICYCLING)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        // Do something here
                        //String status = direction.getStatus();
                        if(direction.isOK()){
                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(MainActivity.this, stepList, 5, Color.RED, 3, Color.BLUE);
                            GoogleMap map = getMap();
                            for (PolylineOptions polylineOption : polylineOptionList) {
                                map.addPolyline(polylineOption);
                            }
                            map.animateCamera(cu);
                        }
                        Log.v(TAG, "direction is ok : " + direction.isOK());
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });

    }


}
