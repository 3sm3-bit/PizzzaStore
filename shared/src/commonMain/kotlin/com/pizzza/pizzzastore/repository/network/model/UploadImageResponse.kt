package com.pizzza.pizzzastore.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponse(
    @SerialName("ok")
    val ok: Boolean,
    @SerialName("urlImg")
    val urlImg: String? = null,
    @SerialName("fileName")
    val fileName: String? = null,
    @SerialName("msg")
    val msg: String? = null
)
