package com.showcase.tapandgo.presentation.map.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.showcase.tapandgo.R
import com.showcase.tapandgo.data.repository.dto.Station
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

class StationBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    //private var binding: FragmentStationDetailBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var dateFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())

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

    fun show(station: Station?) {
        station?.let {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            findViewById<TextView>(R.id.name).text = it.name
            findViewById<TextView>(R.id.main_stand).text = context.getString(
                R.string.stand,
                it.mainStands.availabilities.stands
            )
            findViewById<TextView>(R.id.bikes).text = context.getString(
                R.string.bike,
                it.mainStands.availabilities.bikes
            )
            findViewById<TextView>(R.id.update).text = context.getString(
                R.string.last_update, dateFormatter.format(
                    ZonedDateTime.parse(it.lastUpdate)
                )
            )
        }
    }

    fun hide() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}