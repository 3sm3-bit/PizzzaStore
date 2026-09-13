package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.ui.login.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { GlobalUiStateManager() }
    viewModel { AppViewModel(get(), get(), get(),
        get(),get()) }
    viewModel { StoreViewModel(get(), get()) }
    viewModel { BaseViewModel(get()) }
    viewModel { AuthViewModel(get(),get()) }
}
