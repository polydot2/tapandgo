package com.showcase.tapandgo.presentation.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerClusterRenderer<T : ClusterItem?>(
    context: Context?,
    googleMap: GoogleMap?,
    clusterManager: ClusterManager<T>?
) : DefaultClusterRenderer<T>(context, googleMap, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
        return cluster.size >= 2
    }

    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions?) {
        val markerDescriptor = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_ORANGE
        )
        markerOptions?.icon(markerDescriptor)
    }
}
