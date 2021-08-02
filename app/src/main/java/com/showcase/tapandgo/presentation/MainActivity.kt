package com.showcase.tapandgo.presentation

import android.os.Bundle
import com.showcase.tapandgo.R
import com.showcase.tapandgo.base.BaseActivity
import com.showcase.tapandgo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun init(savedInstanceState: Bundle?) {
    }
}