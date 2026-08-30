package com.pizzza.pizzzastore

import android.util.Log
import io.ktor.client.plugins.logging.Logger

actual val requestLogger: Logger = object : Logger {
    override fun log(message: String) {
        if (message.contains("BODY") || message.contains("{") || message.contains("[")) {
            Log.d("NETWORK_LOG_DATA", message)
        } else {
            Log.d("NETWORK_LOG", message)
        }
    }
}