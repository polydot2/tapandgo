package com.showcase.tapandgo.presentation.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.showcase.tapandgo.R
import com.showcase.tapandgo.data.repository.dto.Station

class StationBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    //private var binding: FragmentStationDetailBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    init {
        inflate(context, R.layout.fragment_station_detail, this)
        //binding = FragmentStationDetailBinding.bind(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bottomSheetBehavior = BottomSheetBehavior.from(this)
        //binding = FragmentStationDetailBinding.bind(this)
        hide()
    }

    fun show(station: Station) {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        findViewById<TextView>(R.id.name).text = station.name
        findViewById<TextView>(R.id.main_stand).text =
            context.getString(R.string.stand, station.mainStands.availabilities.stands)
        findViewById<TextView>(R.id.bikes).text =
            context.getString(R.string.bike, station.mainStands.availabilities.bikes)
    }

    fun hide() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}