package com.showcase.tapandgo.presentation.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.showcase.tapandgo.data.repository.dto.Station

class MarkerClusterItem(
    var latLng: LatLng,
    var station: Station
) : ClusterItem {

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }
}