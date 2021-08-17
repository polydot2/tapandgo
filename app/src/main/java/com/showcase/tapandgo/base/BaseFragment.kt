package com.showcase.tapandgo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.showcase.tapandgo.presentation.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect


abstract class BaseFragment<VM : BaseViewModel, B : ViewDataBinding, Ui : BaseUiModel>(@LayoutRes val layoutId: Int) :
    Fragment() {

    private var uiStateJob: Job? = null
    lateinit var binding: B
    abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? MainActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiStateJob = lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it) {
                    is UiState.Init -> onInit()
                    is UiState.Loading -> onLoading()
                    is UiState.Error -> onError(it.error)
                    is UiState.Success -> onSuccess(it.uiModel as Ui)
                }
            }
        }

        viewModel.initScreen()
    }

    abstract fun onSuccess(uiModel: Ui)

    abstract fun onError(error: ApplicationError)

    open fun onLoading() {}

    abstract fun onInit()
}