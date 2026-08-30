package com.pizzza.pizzzastore.application

import android.app.Application
import com.pizzza.pizzzastore.di.initKoin
import com.pizzza.pizzzastore.di.viewModelModule
import com.pizzza.pizzzastore.utils.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PizzaApplication: Application()  {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        initKoin {
            androidContext(this@PizzaApplication)
            androidLogger()
            modules(viewModelModule)
        }
    }
}
