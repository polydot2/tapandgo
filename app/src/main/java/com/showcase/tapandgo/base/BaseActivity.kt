package com.showcase.tapandgo.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    AppCompatActivity() {

    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialisation of the binding
        binding = DataBindingUtil.setContentView(this, layoutId)

        // call the init function in the derived class
        init(savedInstanceState)
    }

    protected abstract fun init(savedInstanceState: Bundle?)

}
