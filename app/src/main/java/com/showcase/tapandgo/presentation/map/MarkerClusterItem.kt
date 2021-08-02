package com.showcase.tapandgo.presentation.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MarkerClusterItem(
    var latLng: LatLng,
    var name: String
) : ClusterItem {

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return ""
    }
}