package com.showcase.tapandgo.presentation.destination

import android.content.Intent
import android.net.Uri
import com.showcase.tapandgo.BuildConfig
import com.showcase.tapandgo.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DestinationViewModel @Inject constructor(
) : BaseViewModel() {

    fun getGoogleMapIntent(arguments: DestinationFragmentArgs): Intent {
        val packageName = "com.google.android.apps.maps"
        val base = "https://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s+to:%s,%s+to:%s,%s&mode=BIKE&key=%s"
        val path = String.format(
            base,
            arguments.from.latitude,
            arguments.from.longitude,
            arguments.stationDeparture.position.latitude,
            arguments.stationDeparture.position.longitude,
            arguments.stationsArrival.position.latitude,
            arguments.stationsArrival.position.longitude,
            arguments.to.latitude,
            arguments.to.longitude,
            BuildConfig.PLACES_API_KEY
        )

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(path))
        return intent.setPackage(packageName)
    }
}