package com.showcase.tapandgo.presentation.details

import androidx.fragment.app.viewModels
import com.showcase.tapandgo.R
import com.showcase.tapandgo.base.BaseFragment
import com.showcase.tapandgo.databinding.FragmentMapBinding
import com.showcase.tapandgo.presentation.map.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : BaseFragment<MapViewModel, FragmentMapBinding>(R.layout.fragment_map) {
    override val viewModel: MapViewModel by viewModels()
    override fun onInit() {

    }
}
