package com.pizzza.pizzzastore.usecases.di

import com.pizzza.pizzzastore.usecases.DataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCasesModule = module {
    factoryOf(::DataUseCase)
}