package com.majateam.allocyclo.renderer;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.majateam.allocyclo.R;
import com.majateam.allocyclo.model.Bike;

/**
 * Draws profile photos inside markers (using IconGenerator).
 * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
 */
public class BikeRenderer extends DefaultClusterRenderer<ClusterItem> {

    public BikeRenderer(Context context, GoogleMap map, ClusterManager<ClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterItem clusterItem, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        if(clusterItem instanceof Bike)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stolen_bike_location));
        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dock_location));
        super.onBeforeClusterItemRendered(clusterItem, markerOptions);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}