package com.pizzza.pizzzastore.repository.utils

import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

internal inline fun <reified R : Any> String.parseJsonTo() =
    json.decodeFromString<R>(this)
