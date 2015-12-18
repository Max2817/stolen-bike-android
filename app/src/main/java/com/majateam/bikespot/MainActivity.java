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
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
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

public class MainActivity extends BaseActivity implements LocationProvider.LocationCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationProvider mLocationProvider;
    private List<Bike> bikes;
    private List<Dock> docks;
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
    private static final int UNSAFE_SPOT = 30;
    private static final int NEUTRAL_SPOT = 40;
    private static final String SPOT = "spot";
    private int mSpot = NEUTRAL_SPOT;
    private Marker mUserMarker = null;
    private Circle mCircle = null;
    private static final int NEUTRAL_DISTANCE = 300;

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
            mChoice = savedInstanceState.getInt(CHOICE);
            mSubMenu.setVisibility((savedInstanceState.getInt(MENU_VISIBILITY) == View.VISIBLE) ? View.VISIBLE : View.GONE);
            setChoice();
            mSpot = savedInstanceState.getInt(SPOT);
            setSpotStatus();
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
    }


    @OnClick(R.id.menu_icon)
    public void showPopup() {
        mSubMenu.setVisibility((mSubMenu.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    @OnClick(com.majateam.bikespot.R.id.show_choice)
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
                    .strokeColor(ContextCompat.getColor(this, R.color.red))
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
        setClusterItems(mChoice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mChoice == DOCKS) {
            LatLng destPosition = marker.getPosition();
            drawDestination(destPosition, marker);
        }
        return false;
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
                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(MainActivity.this, stepList, 5, Color.BLACK, 3, Color.BLUE);
                            GoogleMap map = getMap();
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (PolylineOptions polylineOption : polylineOptionList) {
                                mPolylines.add(map.addPolyline(polylineOption));
                                for (LatLng latLng : polylineOption.getPoints()) {
                                    builder.include(latLng);
                                }
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 0; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            map.animateCamera(cu);
                            VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
                            LatLngBounds mapLatLngBound = visibleRegion.latLngBounds;

                            map.animateCamera(CameraUpdateFactory.newLatLng(mapLatLngBound.getCenter()));
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
        mClusterManager = new ClusterManager<>(this, getMap());
        mClusterManager.setRenderer(new BikeRenderer(getApplicationContext(), getMap(), mClusterManager));
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(this);

        //commented code used with Android emulator, uncomment to use it
        //setFalseUserLocation();

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

        //check if there is any recent stolen bike (< 6 months) around me 300m
        if(mChoice == BIKES){
            if(bikes != null && bikes.size() > 0) {
                mSpot = NEUTRAL_SPOT;
                for (Bike bike :  bikes) {
                    if (MapHelper.distFrom((float) mCurrentLatitude, (float) mCurrentLongitude, Float.valueOf(bike.getLat()), Float.valueOf(bike.getLng())) <= NEUTRAL_DISTANCE) {
                        mSpot = UNSAFE_SPOT;
                        break;
                    }
                }
                setSpotStatus();
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


}
