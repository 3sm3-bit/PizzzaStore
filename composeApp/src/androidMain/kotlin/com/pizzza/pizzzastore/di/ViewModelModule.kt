package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AppViewModel(get(), get()) }
    viewModel { StoreViewModel(get(), get()) }
    viewModel { BaseViewModel(get()) }
}
