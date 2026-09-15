package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.ui.login.AuthViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::GlobalUiStateManager)
    
    viewModel { 
        AppViewModel(
            get(), get(), get(), get()
        ) 
    }
    
    viewModel { 
        StoreViewModel(
            get(),
            get()
        ) 
    }

    viewModel { 
        AuthViewModel(
            get(), get()
        ) 
    }
}
