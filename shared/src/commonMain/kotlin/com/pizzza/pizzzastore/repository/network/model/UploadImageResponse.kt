package com.pizzza.pizzzastore.repository.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponse(
    val ok: Boolean,
    val urlImg: String? = null,
    val fileName: String? = null,
    val msg: String? = null
)
