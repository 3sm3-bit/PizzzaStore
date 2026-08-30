package com.pizzza.pizzzastore.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.NetworkCapabilities
import com.pizzza.pizzzastore.repository.utils.ConnectivityManager

class AndroidConnectivityManager(private val context: Context) : ConnectivityManager {
    @SuppressLint("MissingPermission")
    override fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager?
        val capabilities = connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
