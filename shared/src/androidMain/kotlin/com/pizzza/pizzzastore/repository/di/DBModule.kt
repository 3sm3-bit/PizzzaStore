package com.pizzza.pizzzastore.repository.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.pizzza.pizzzastore.repository.utils.ConnectivityManager
import com.pizzza.pizzzastore.repository.db.manager.AppDataBase
import com.pizzza.pizzzastore.repository.AndroidConnectivityManager
import org.koin.dsl.module

actual val dbModule = module {
    single<ConnectivityManager> { AndroidConnectivityManager(get()) }
    
    single<AppDataBase> {
        val context: Context = get()
        val dbFile = context.getDatabasePath("pizzza_app.db")
        Room.databaseBuilder<AppDataBase>(
            context = context,
            name = dbFile.absolutePath
        )
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
