package com.pizzza.pizzzastore.repository.network.exception

import kotlinx.serialization.Serializable

@Serializable
data class CompleteErrorModel(
    val code: Int? = null,
    val title: String? = null,
    val errorMessage: String? = null
)

class UiTayApiException(
    val code: Int,
    val title: String,
    val messageApi: String
) : Exception(messageApi)

class UnAuthorizedException : Exception("Sesión expirada")
class GenericException : Exception("Ocurrió un error inesperado")
class ErrorNetwork : Exception("No hay conexión a internet")

fun Throwable.toAppException(): Exception {
    val message = this.message ?: "Error desconocido"
    
    // Mapeo de errores de red comunes de Ktor/Plataforma
    if (message.contains("UnresolvedAddressException", ignoreCase = true) || 
        message.contains("ConnectException", ignoreCase = true) ||
        message.contains("socket timeout", ignoreCase = true)) {
        return ErrorNetwork()
    }

    return when (this) {
        is UiTayApiException -> this
        is UnAuthorizedException -> this
        is ErrorNetwork -> this
        else -> UiTayApiException(
            code = 0,
            title = "Error",
            messageApi = message
        )
    }
}
