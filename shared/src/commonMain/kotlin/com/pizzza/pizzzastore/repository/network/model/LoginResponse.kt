package com.pizzza.pizzzastore.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("userValid")
    val userValid: UserResponse,
    @SerialName("token")
    val token: String
)
