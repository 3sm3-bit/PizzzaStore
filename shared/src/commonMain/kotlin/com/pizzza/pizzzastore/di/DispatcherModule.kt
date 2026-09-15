package com.pizzza.pizzzastore.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule = module {
    single<CoroutineDispatcher>(named("IO")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("Default")) { Dispatchers.Default }
    single<CoroutineDispatcher>(named("Main")) { Dispatchers.Main }
}