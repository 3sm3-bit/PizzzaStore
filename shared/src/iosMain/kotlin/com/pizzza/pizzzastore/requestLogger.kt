package com.pizzza.pizzzastore

import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger

actual val requestLogger: Logger = Logger.DEFAULT