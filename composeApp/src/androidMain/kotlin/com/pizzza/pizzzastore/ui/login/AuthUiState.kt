package com.pizzza.pizzzastore.ui.login

data class AuthUiState(
    val user: String = "",
    val pass: String = "",
    val isLoginSuccessful: Boolean = false,
    val error: String? = null,
    val nameUser: String = "",
    val names: String = "",
    val lastName: String = "",
    val document: String = "11111111",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val rol: String = "CLIENTE",
    val area: String = "1",
    val longitude: String = "",
    val latitude: String = ""
)
