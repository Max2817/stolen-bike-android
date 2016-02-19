package com.majateam.bikespot.renderer;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.majateam.bikespot.R;
import com.majateam.bikespot.model.Bike;

import java.util.HashMap;

/**
 * Draws profile photos inside markers (using IconGenerator).
 * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
 */
public class BikeRenderer extends DefaultClusterRenderer<ClusterItem> {

    private Context mContext;
    private HashMap<String, ClusterItem> mMarkerClusterItemMap;

    public BikeRenderer(Context context, GoogleMap map, ClusterManager<ClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mMarkerClusterItemMap = new HashMap<>();
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterItem clusterItem, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        if(clusterItem instanceof Bike)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stolen_bike_location)).title(mContext.getString(R.string.stolen_on) + " " + ((Bike) clusterItem).getDate());
        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dock_location));
        super.onBeforeClusterItemRendered(clusterItem, markerOptions);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected void onClusterRendered(Cluster<ClusterItem> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

    @Override
    protected void onClusterItemRendered(ClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        mMarkerClusterItemMap.put(marker.getId(), clusterItem);
    }

    public HashMap<String, ClusterItem> getmMarkerClusterItemMap() {
        return mMarkerClusterItemMap;
    }
}