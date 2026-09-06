package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.printer.BluetoothPrinterManager
import org.koin.dsl.module

val printerModule = module {
    single { BluetoothPrinterManager(get()) }
}
