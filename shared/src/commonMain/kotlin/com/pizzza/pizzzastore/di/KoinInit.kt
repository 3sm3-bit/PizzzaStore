package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.repository.di.networkModule
import com.pizzza.pizzzastore.repository.di.dbModule
import com.pizzza.pizzzastore.repository.di.repositoryModule
import com.pizzza.pizzzastore.usecases.di.useCasesModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            dispatcherModule,
            repositoryModule,
            networkModule,
            dbModule,
            useCasesModule
        )
    }

