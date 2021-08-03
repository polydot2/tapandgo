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

    fun getMapPreview(): Uri {
        return Uri.Builder().scheme("https")
            .authority("maps.googleapis.com")
            .appendPath("maps")
            .appendPath("api")
            .appendPath("staticmap")
            .appendQueryParameter("size", "512x512")
            .appendQueryParameter("maptype", "roadmap")
            .appendQueryParameter("key", BuildConfig.PLACES_API_KEY)
            .build()
    }

    fun getGoogleMapIntent(arguments: DestinationFragmentArgs): Intent {
        val packageName = "com.google.android.apps.maps"
        val jsonURL = "https://maps.google.com/maps?"
        val sBuf = StringBuffer(jsonURL)
        sBuf.append("saddr=")
        sBuf.append(arguments.from.latitude)
        sBuf.append(',')
        sBuf.append(arguments.from.longitude)
        sBuf.append("&daddr=")
        sBuf.append(arguments.stationDeparture.position.latitude)
        sBuf.append(',')
        sBuf.append(arguments.stationDeparture.position.longitude)
        sBuf.append("+to:")
        sBuf.append(arguments.stationsArrival.position.latitude)
        sBuf.append(',')
        sBuf.append(arguments.stationsArrival.position.longitude)
        sBuf.append("+to:")
        sBuf.append(arguments.to.latitude)
        sBuf.append(',')
        sBuf.append(arguments.to.longitude)
        sBuf.append("&mode=BIKE");
        sBuf.append("&key=")
        sBuf.append(BuildConfig.PLACES_API_KEY)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sBuf.toString()))
        return intent.setPackage(packageName)
    }
}