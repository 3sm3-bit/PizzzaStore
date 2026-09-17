package com.pizzza.pizzzastore.di

import android.content.Context
import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.ui.login.AuthViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::GlobalUiStateManager)

    single { 
        androidContext().getSharedPreferences("pizza_prefs", Context.MODE_PRIVATE) 
    }
    
    viewModel { 
        AppViewModel(
            get(), get(), get(), get(), get()
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
            get(), get(), get()
        ) 
    }
}
