package com.majateam.bikespot;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.majateam.bikespot.helper.DateHelper;
import com.majateam.bikespot.helper.MapHelper;
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

public class MainActivity extends BaseActivity implements LocationProvider.LocationCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationProvider mLocationProvider;
    private static final String BIKES_LIST = "bikesList";
    private static final String DOCKS_LIST = "docksList";
    private ArrayList<Bike> mBikes = null;
    private ArrayList<Dock> mDocks = null;
    private ArrayList<ClusterItem> mClusterItems = null;
    private ClusterManager<ClusterItem> mClusterManager;
    private static final int BIKES = 10;
    private static final int DOCKS = 20;
    private static final String CHOICE = "choice";
    private static final String MENU_VISIBILITY = "menu_visibility";
    private int mChoice;
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private static final String CURRENT_LATITUDE = "currentLatitude";
    private static final String CURRENT_LONGITUDE = "currentLongitude";
    private List<Polyline> mPolylines;
    private static final String DESTINATION_LATITUDE = "destinationLatitude";
    private static final String DESTINATION_LONGITUDE = "destinationLongitude";
    private static final int UNSAFE_SPOT = 30;
    private static final int NEUTRAL_SPOT = 40;
    private static final String SPOT = "spot";
    private int mSpot = NEUTRAL_SPOT;
    private Marker mUserMarker = null;
    private Circle mCircle = null;
    private static final int NEUTRAL_DISTANCE = 300;
    private boolean mLocationHandledFirst = false;

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
    @Bind(R.id.bike_spot_info_layout)
    LinearLayout mBikeSpotLayout;
    @Bind(R.id.bike_spot_status)
    TextView mBikeSpotStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());

        //set default choice
        if(savedInstanceState != null) {
            mCurrentLatitude = savedInstanceState.getDouble(CURRENT_LATITUDE);
            mCurrentLongitude = savedInstanceState.getDouble(CURRENT_LONGITUDE);
            mBikes = savedInstanceState.getParcelableArrayList(BIKES_LIST);
            mDocks = savedInstanceState.getParcelableArrayList(DOCKS_LIST);
            mChoice = savedInstanceState.getInt(CHOICE);
            mSubMenu.setVisibility((savedInstanceState.getInt(MENU_VISIBILITY) == View.VISIBLE) ? View.VISIBLE : View.GONE);
            setChoice();
            mSpot = savedInstanceState.getInt(SPOT);
            setSpotStatus();
            if(mBikes == null || mDocks == null){
                startDemo();
            }

        }else{
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            mShowIcon.setImageResource(R.drawable.ic_legend_dock);
        }

        //init polylines array
        mPolylines = new ArrayList<>();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHOICE, mChoice);
        outState.putInt(MENU_VISIBILITY, mSubMenu.getVisibility());
        outState.putInt(SPOT, mSpot);
        outState.putDouble(CURRENT_LATITUDE, mCurrentLatitude);
        outState.putDouble(CURRENT_LONGITUDE, mCurrentLongitude);
        outState.putParcelableArrayList(BIKES_LIST, mBikes);
        outState.putParcelableArrayList(DOCKS_LIST, mDocks);
        if(mClusterManager != null) {
            mClusterManager.clearItems();
        }
    }

    @OnClick(R.id.card_view)
    public void showPopup() {
        mSubMenu.setVisibility((mSubMenu.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.show_choice)
    public void showChoice() {
        updateChoice();
        showPopup();
        removeDestination();
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

    private void setSpotStatus(){
        if(mSpot == UNSAFE_SPOT){
            mBikeSpotLayout.setBackgroundResource(R.color.red);
            mBikeSpotStatus.setText(R.string.bike_unsafe_spot);
            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(mCurrentLatitude, mCurrentLongitude))
                    .radius(NEUTRAL_DISTANCE)
                    .strokeColor(ContextCompat.getColor(this, R.color.transparent))
                    .fillColor(ContextCompat.getColor(this, R.color.red_transparent)); // In meters

            // Get back the mutable Circle
            if(mCircle != null){
                mCircle.remove();
            }
            mCircle = getMap().addCircle(circleOptions);
        }else{
            mBikeSpotLayout.setBackgroundResource(R.color.green);
            mBikeSpotStatus.setText(R.string.bike_neutral_spot);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mLocationProvider != null) {
            mLocationProvider.connect();
            if(mBikes == null || mBikes.size() == 0 || mDocks == null || mDocks.size() == 0){
                callData();
            }
        }else{
            startDemo();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mChoice == DOCKS && !marker.getPosition().equals(new LatLng(mCurrentLatitude, mCurrentLongitude))) {
            LatLng destPosition = marker.getPosition();
            drawDestination(destPosition, marker);
        }else if(mChoice == BIKES && !marker.getPosition().equals(new LatLng(mCurrentLatitude, mCurrentLongitude))){
            marker.showInfoWindow();
        }else{
            removeDestination();
        }
        return true;
    }

    private void drawDestination(LatLng destination, final Marker marker){
        //if it already exists a polyline we remove it

        removeDestination();
        //Then we draw the new polyline and display it

        String serverKey = "AIzaSyDNtlRTiYN4cNhjmO3Zzzghg0I7mV5i9bc";
        LatLng origin = new LatLng(mCurrentLatitude, mCurrentLongitude);

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
                        Info durationInfo = direction.getRouteList().get(0).getLegList().get(0).getDuration();
                        marker.setTitle(durationInfo.getText());
                        marker.showInfoWindow();
                        if (direction.isOK()) {
                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            PolylineOptions polylineOptions= DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 5, Color.BLACK);
                            GoogleMap map = getMap();
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            mPolylines.add(map.addPolyline(polylineOptions));
                            for (LatLng latLng : polylineOptions.getPoints()) {
                                builder.include(latLng);
                            }
                            LatLngBounds bounds = builder.build();
                            map.animateCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
                        }
                        Log.v(TAG, "direction is ok : " + direction.isOK());
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
    }

    private void removeDestination(){
        if(mPolylines.size() > 0) {
            for (Polyline line : mPolylines) {
                line.remove();
            }
            mPolylines.clear();
        }
    }

    @Override
    protected void startDemo() {
        GoogleMap map = getMap();
        mLocationProvider = new LocationProvider(this, this);
        mClusterManager = new ClusterManager<>(this, map);
        mClusterManager.setRenderer(new BikeRenderer(getApplicationContext(), map, mClusterManager));
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

        //commented code used with Android emulator, uncomment to use it
        //setFalseUserLocation();
        callData();

    }

    private void callData(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final LocationService service = retrofit.create(LocationService.class);

        Call<List<Bike>> callBikes = service.listBikes();
        callBikes.enqueue(new Callback<List<Bike>>() {

            @Override
            public void onResponse(Response<List<Bike>> response, Retrofit retrofit) {

                mBikes = new ArrayList<>(response.body());

                Call<List<Dock>> callDocks = service.listDocks();
                callDocks.enqueue(new Callback<List<Dock>>() {

                    @Override
                    public void onResponse(Response<List<Dock>> response, Retrofit retrofit) {
                        mDocks = new ArrayList<>(response.body());
                        setClusterItems(mChoice);
                        //Update location if we get the data after the location is handled
                        if (mLocationHandledFirst) {
                            mLocationProvider.callNewLocation();
                            mLocationHandledFirst = false;
                        }
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

    /**
     * False user location used only for android emulator
     */
    private void setFalseUserLocation(){
        mCurrentLatitude = 45.5486;
        mCurrentLongitude = -73.5788;
        GoogleMap map = getMap();
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        MarkerOptions options = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(com.majateam.bikespot.R.drawable.ic_user_location));
        mUserMarker = map.addMarker(options);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
    }

    private void setClusterItems(int type) {
        if(mClusterManager == null){
            GoogleMap map = getMap();
            mClusterManager = new ClusterManager<>(this, map);
            mClusterManager.setRenderer(new BikeRenderer(getApplicationContext(), map, mClusterManager));
            map.setOnCameraChangeListener(mClusterManager);
        }
        mClusterManager.clearItems();
        if(mClusterItems == null){
            mClusterItems = new ArrayList<>();
        }else{
            mClusterItems.clear();
        }
        if (type == BIKES && mBikes != null) {
            mClusterItems.addAll(mBikes);
        }else if(mDocks != null){
            mClusterItems.addAll(mDocks);
        }
        if(mClusterItems != null && mClusterItems.size() > 0){
            mClusterManager.addItems(mClusterItems);
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

        //check if there is any recent stolen bike (< 6 months) around me 300m
        if(mChoice == BIKES){
            if(mBikes != null && mBikes.size() > 0) {
                long todayMinusSixMonths = DateHelper.getDateMonthsAgo(6).getTime();
                mSpot = NEUTRAL_SPOT;
                for (Bike bike :  mBikes) {
                    if (bike.getRawDate()*1000 >= todayMinusSixMonths && MapHelper.distFrom((float) mCurrentLatitude, (float) mCurrentLongitude, Float.valueOf(bike.getLat()), Float.valueOf(bike.getLng())) <= NEUTRAL_DISTANCE) {
                        mSpot = UNSAFE_SPOT;
                        break;
                    }
                }
                setSpotStatus();
            }else{
                if(mCircle != null){
                    mCircle.remove();
                }
                mLocationHandledFirst = true;
            }
        }

    }




    @OnClick(R.id.map_user_location)
    public void showUserLocation() {
        GoogleMap map = getMap();
        if(map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        removeDestination();
    }
}
