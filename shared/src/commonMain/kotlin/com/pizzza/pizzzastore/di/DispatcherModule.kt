package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.DefaultDispatcherProvider
import com.pizzza.pizzzastore.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dispatcherModule = module {
    singleOf(::DefaultDispatcherProvider) { bind<DispatcherProvider>() }
}