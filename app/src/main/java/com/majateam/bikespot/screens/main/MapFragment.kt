package com.majateam.bikespot.screens.main

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.majateam.bikespot.R
import com.majateam.bikespot.R.string
import com.majateam.bikespot.model.Bike
import com.majateam.bikespot.model.Dock
import com.majateam.bikespot.provider.LocationProvider
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import java.util.*

/**
 * Stolen Bike Created by nmartino on 2017-04-17.
 */
class MapFragment : Fragment() {
    var mSubMenu: LinearLayout? = null
    var mDisplayChoice: TextView? = null
    var mShowChoice: TextView? = null
    var mChoiceIcon: ImageView? = null
    var mShowIcon: ImageView? = null
    var mBikeSpotStatusLayout: LinearLayout? = null
    var mBikeSpotStatus: TextView? = null
    var mBikeSpotTitle: TextView? = null
    var mBikeSpotDescription: TextView? = null
    var mBikeSpotBrand: TextView? = null
    var mLocateUser: FloatingActionButton? = null
    var mEmptyContainer: LinearLayout? = null
    var mEmptyIcon: ImageView? = null
    var mEmptyTitle: TextView? = null
    var mEmptySubtitle: TextView? = null
    var mContainer: FrameLayout? = null
    var mCoordinatorLayout: CoordinatorLayout? = null
    private val mLocationProvider: LocationProvider? = null
    var mBikes: ArrayList<Bike>? = null
    var mDocks: ArrayList<Dock>? = null
    var mChoice = 0
    var mCurrentLatitude = 0.0
    var mCurrentLongitude = 0.0
    var mMenuVisibilityState = 0
    var mSpot = LOADING_SPOT
    private val mLocationHandledFirst = false
    var mSnackBar: Snackbar? = null
    private val mInternetConnected = true
    private val mConnectivityStatus: Int? = null
    private var mFirstTime: Boolean? = null
    var mapView: MapView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initEmptyContainer();
        //mSubMenu.setVisibility((mMenuVisibilityState == View.VISIBLE) ? View.VISIBLE : View.GONE);
        //set default choice
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        activity?.let { Mapbox.getInstance(it, getString(string.mapbox_access_token)) }
        if (savedInstanceState == null) {
            mChoice = BIKES
            //mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            //mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            //mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            //mShowIcon.setImageResource(R.drawable.ic_legend_dock);
            mBikes = ArrayList()
            mDocks = ArrayList()
        }

        //init polylines array
        //mPolylines = ArrayList()
        //First time the app is launched
        mFirstTime = true
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync { mapboxMap ->
            mapboxMap.addMarker(MarkerOptions()
                .position(LatLng(48.13863, 11.57603))
                .title("Hello World!")
                .snippet("Welcome to my marker."))
        }
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
        /*if (getConnectivityStatus(this) != TYPE_NOT_CONNECTED) {
            mContainer.setVisibility(View.VISIBLE);
            mEmptyContainer.setVisibility(View.GONE);
            setSpotStatus();
            if (mLocationProvider != null) {
                mLocationProvider.connect();
                if (mBikes == null || mBikes.size() == 0 || mDocks == null || mDocks.size() == 0) {
                    callData();
                }
            }
            if (!mFirstTime && getMap() != null && mUserMarker != null) {
                mUserMarker.remove();
                mUserMarker = null;
            } else {
                mFirstTime = false;
            }
        } else {
            mContainer.setVisibility(View.GONE);
            mEmptyContainer.setVisibility(View.VISIBLE);
        }*/
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
        /*if (mClusterManager != null) {
            mClusterManager.clearItems();
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    fun showPopup() {
        mSubMenu!!.visibility = if (mSubMenu!!.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    /*@OnClick(R.id.show_choice)
    public void showChoice() {
        updateChoice();
        showPopup();
        removeDestination();
    }*/ /*private void updateChoice() {
        if (mChoice == BIKES) {
            mChoice = DOCKS;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_bikes);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.docks);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_dock);
            mShowIcon.setImageResource(R.drawable.ic_legend_stolen);
        } else {
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            mShowIcon.setImageResource(R.drawable.ic_legend_dock);
        }
        setClusterItems(mChoice);
    }

    private void setChoice() {
        if (mChoice == BIKES) {
            mChoice = BIKES;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_docks);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.bikes);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_stolen);
            mShowIcon.setImageResource(R.drawable.ic_legend_dock);
        } else {
            mChoice = DOCKS;
            mShowChoice.setText(com.majateam.bikespot.R.string.show_bikes);
            mDisplayChoice.setText(com.majateam.bikespot.R.string.docks);
            mChoiceIcon.setImageResource(R.drawable.ic_legend_dock);
            mShowIcon.setImageResource(R.drawable.ic_legend_stolen);
        }
        setClusterItems(mChoice);
    }

    private void setSpotStatus() {
        // Get back the mutable Circle
        if (mBikeSpotTitle.getVisibility() != View.VISIBLE) {
            if (mSpot == UNSAFE_SPOT) {
                mBikeSpotStatusLayout.setBackgroundResource(R.color.red);
                mBikeSpotStatus.setText(R.string.bike_unsafe_spot);
            } else if (mSpot == NEUTRAL_SPOT) {
                mBikeSpotStatusLayout.setBackgroundResource(R.color.green);
                mBikeSpotStatus.setText(R.string.bike_neutral_spot);
            } else {
                mBikeSpotStatusLayout.setBackgroundResource(R.color.grey);
                mBikeSpotStatus.setText(R.string.loading);
            }
        }
        if (mSpot == UNSAFE_SPOT) {
            // Instantiates a new CircleOptions object and defines the center and radius
            if (mCircle == null) {
                // Instantiates a new CircleOptions object and defines the center and radius
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(mCurrentLatitude, mCurrentLongitude))
                        .radius(NEUTRAL_DISTANCE)
                        .strokeColor(ContextCompat.getColor(this, R.color.transparent))
                        .fillColor(ContextCompat.getColor(this, R.color.red_transparent)); // In meters
                mCircle = getMap().addCircle(circleOptions);
            } else {
                mCircle.setCenter(new LatLng(mCurrentLatitude, mCurrentLongitude));
            }
        } else {
            if (mCircle != null) {
                mCircle.remove();
            }
        }
    }

    private void initEmptyContainer() {
        mEmptyIcon.setImageResource(R.drawable.ic_wifi_black_48dp);
        mEmptyTitle.setText(R.string.empty_no_internet_connection);
        mEmptySubtitle.setText(getString(R.string.empty_please_connect));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mChoice == DOCKS && !marker.getPosition().equals(new LatLng(mCurrentLatitude, mCurrentLongitude))) {
            LatLng destPosition = marker.getPosition();
            drawDestination(destPosition, marker);
        } else if (mChoice == BIKES && !marker.getPosition().equals(new LatLng(mCurrentLatitude, mCurrentLongitude))) {
            marker.showInfoWindow();
            if (mBikeRenderer != null) {
                HashMap<String, ClusterItem> clusterItemMarkerMap = mBikeRenderer.getmMarkerClusterItemMap();
                Bike bike = (Bike) clusterItemMarkerMap.get(marker.getId());
                if (bike != null) {
                    mBikeSpotTitle.setVisibility(View.VISIBLE);
                    mBikeSpotTitle.setText(bike.getTitle());
                    mBikeSpotDescription.setVisibility(View.VISIBLE);
                    mBikeSpotDescription.setText(bike.getDescription());
                    mBikeSpotBrand.setVisibility(View.VISIBLE);
                    String brand = getString(R.string.brand) + " : " + bike.getBrand();
                    mBikeSpotBrand.setText(brand);
                    mBikeSpotStatus.setVisibility(View.GONE);
                    mLocateUser.setVisibility(View.GONE);
                    mBikeSpotStatusLayout.setBackgroundResource(R.color.green);
                }
            }
        } else {
            removeDestination();
            hideMarkerInfo();
        }
        return true;
    }

    private void drawDestination(LatLng destination, final Marker marker) {
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
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        //String status = direction.getStatus();
                        Info durationInfo = direction.getRouteList().get(0).getLegList().get(0).getDuration();
                        marker.setTitle(durationInfo.getText());
                        marker.showInfoWindow();
                        if (direction.isOK()) {
                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 5, Color.BLACK);
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

    private void removeDestination() {
        if (mPolylines.size() > 0) {
            for (Polyline line : mPolylines) {
                line.remove();
            }
            mPolylines.clear();
        }
    }

    @Override
    protected void startApp() {
        GoogleMap map = getMap();
        if (mLocationProvider == null)
            mLocationProvider = new LocationProvider(this, this);
        if (mClusterManager == null)
            mClusterManager = new ClusterManager<>(this, map);
        if (mBikeRenderer == null)
            mBikeRenderer = new BikeRenderer(getApplicationContext(), map, mClusterManager);
        mClusterManager.setRenderer(mBikeRenderer);
        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

        //commented code used with Android emulator, uncomment to use it
        //setFalseUserLocation();
        callData();

    }

    private void callData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final LocationService service = retrofit.create(LocationService.class);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Call<List<Bike>> callBikes = service.listBikes(Global.BIKES_URL, String.valueOf(cal.getTime().getTime() / 1000));
        callBikes.enqueue(new Callback<List<Bike>>() {

            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.body() != null) {
                    mBikes.clear();
                    mBikes.addAll(response.body());
                }

                Call<List<Dock>> callDocks = service.listDocks(Global.DOCKS_URL);
                callDocks.enqueue(new Callback<List<Dock>>() {

                    @Override
                    public void onResponse(Call<List<Dock>> call, Response<List<Dock>> response) {
                        if (response.body() != null) {
                            mDocks.clear();
                            mDocks.addAll(response.body());
                        }
                        setClusterItems(mChoice);
                        //Update location if we get the data after the location is handled
                        if (mLocationHandledFirst) {
                            mLocationProvider.callNewLocation();
                            mLocationHandledFirst = false;
                        }
                        setChoice();
                    }

                    @Override
                    public void onFailure(Call<List<Dock>> call, Throwable throwable) {
                        Log.d("Dock call failed %s", throwable.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable throwable) {
                Log.d("Bikes call failed %s", throwable.getMessage());
            }
        });
    }

    // False user location used only for android emulator
    private void setFalseUserLocation() {
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
        if (mClusterManager == null) {
            GoogleMap map = getMap();
            mClusterManager = new ClusterManager<>(this, map);
            mBikeRenderer = new BikeRenderer(getApplicationContext(), map, mClusterManager);
            mClusterManager.setRenderer(mBikeRenderer);
            map.setOnCameraIdleListener(mClusterManager);
        }
        mClusterManager.clearItems();
        if (mClusterItems == null) {
            mClusterItems = new ArrayList<>();
        } else {
            mClusterItems.clear();
        }
        if (type == BIKES && mBikes != null) {
            mClusterItems.addAll(mBikes);
        } else if (mDocks != null) {
            mClusterItems.addAll(mDocks);
        }
        if (mClusterItems != null && mClusterItems.size() > 0) {
            mClusterManager.addItems(mClusterItems);
        }
        mClusterManager.cluster();
    }

    public void handleNewLocation(Location location) {
        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        GoogleMap map = getMap();
        if (mUserMarker == null) {
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(com.majateam.bikespot.R.drawable.ic_user_location));
            mUserMarker = map.addMarker(options);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
        } else {
            mUserMarker.setPosition(latLng);
        }

        //check if there is any recent stolen bike (< 6 months) around me 300m
        if (mChoice == BIKES) {
            if (mBikes != null && mBikes.size() > 0) {
                long todayMinusSixMonths = DateHelper.getDateMonthsAgo(6).getTime();
                mSpot = NEUTRAL_SPOT;
                for (Bike bike : mBikes) {
                    if (bike.getRawDate() * 1000 >= todayMinusSixMonths && MapHelper.distFrom((float) mCurrentLatitude, (float) mCurrentLongitude, Float.valueOf(bike.getLat()), Float.valueOf(bike.getLng())) <= NEUTRAL_DISTANCE) {
                        mSpot = UNSAFE_SPOT;
                        break;
                    }
                }
                setSpotStatus();
            } else {
                if (mCircle != null) {
                    mCircle.remove();
                }
                mLocationHandledFirst = true;
            }
        }

    }

    @OnClick(R.id.map_user_location)
    public void showUserLocation() {
        GoogleMap map = getMap();
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 15.0f));
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (mLocationProvider != null) {
            mLocationProvider.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        removeDestination();
        hideMarkerInfo();
    }

    private void hideMarkerInfo() {
        if (mBikeSpotTitle.getVisibility() == View.VISIBLE) {
            mBikeSpotTitle.setVisibility(View.GONE);
            mBikeSpotDescription.setVisibility(View.GONE);
            mBikeSpotBrand.setVisibility(View.GONE);
            mBikeSpotStatus.setVisibility(View.VISIBLE);
            mLocateUser.setVisibility(View.VISIBLE);
            setSpotStatus();
        }
    }

    @Override
    protected void setSnackBarMessage() {
        String internetStatus;
        int connectionStatusCode = getConnectivityStatus(this);
        if (connectionStatusCode != TYPE_NOT_CONNECTED) {
            internetStatus = getString(R.string.internet_connected);
        } else {
            internetStatus = getString(R.string.lost_internet_connection);
        }
        mSnackBar = Snackbar
                .make(mCoordinatorLayout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSnackBar.dismiss();
                    }
                });
        // Changing message text color
        mSnackBar.setActionTextColor(Color.BLACK);
        // Changing action button text color
        View sbView = mSnackBar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        if (connectionStatusCode == TYPE_NOT_CONNECTED) {
            if (mInternetConnected) {
                mSnackBar.show();
                mInternetConnected = false;
            }
        } else {
            if (!mInternetConnected) {
                mInternetConnected = true;
                mSnackBar.show();
            }
        }
    }

    @Override
    protected void onNetworkConnectionUpdated(Integer connectivityCode) {
        if (mConnectivityStatus != null && mConnectivityStatus == TYPE_NOT_CONNECTED && connectivityCode != TYPE_NOT_CONNECTED && mEmptyContainer.getVisibility() == View.VISIBLE) {
            //refresh if we are now connected
            onResume();
        }
        mConnectivityStatus = connectivityCode;
    }*/
    protected val layout: Int
        protected get() = R.layout.map

    companion object {
        private const val BIKES = 10
        private const val DOCKS = 20
        private const val UNSAFE_SPOT = 30
        private const val NEUTRAL_SPOT = 40
        private const val LOADING_SPOT = 50
        private const val NEUTRAL_DISTANCE = 300
    }
}