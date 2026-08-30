package com.pizzza.pizzzastore.repository.network

import com.pizzza.pizzzastore.repository.network.exception.toAppException
import kotlinx.coroutines.CancellationException

suspend inline fun <T, R> apiCall(
    crossinline call: suspend () -> T,
    transform: (T) -> R
): R {
    return try {
        val result = call()
        transform(result)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        throw e.toAppException()
    }
}

suspend inline fun <T> apiCall(
    crossinline call: suspend () -> T
): T = apiCall(call) { it }
