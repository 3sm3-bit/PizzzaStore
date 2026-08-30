package com.pizzza.pizzzastore.repository.di

import com.pizzza.pizzzastore.repository.network.DataNetwork
import com.pizzza.pizzzastore.usecases.network.IDataNetwork
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::DataNetwork) { bind<IDataNetwork>() }
}
