package com.pizzza.pizzzastore.repository.di

import androidx.room.RoomDatabase
import com.pizzza.pizzzastore.repository.db.manager.AppDataBase
import com.pizzza.pizzzastore.repository.network.exception.CompleteErrorModel
import com.pizzza.pizzzastore.repository.network.exception.UiTayApiException
import com.pizzza.pizzzastore.repository.network.exception.UnAuthorizedException
import com.pizzza.pizzzastore.repository.network.manager.InstantSerializer
import com.pizzza.pizzzastore.repository.network.KmmService
import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.requestLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

val jsonLenient = Json { ignoreUnknownKeys = true }

val networkModule = module {
    // Cliente para peticiones REST (con ContentNegotiation y validación)
    single(named("httpClient")) {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        serializersModule = SerializersModule {
                            contextual(Instant::class, InstantSerializer)
                        }
                    },
                )
            }

            defaultRequest {
                headers.append("ngrok-skip-browser-warning", "true")
            }

            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        val statusCode = response.status.value
                        val errorText = try { response.bodyAsText() } catch (e: Exception) { "" }
                        
                        when (statusCode) {
                            //401 -> throw UnAuthorizedException()
                            in 400..599 -> {
                                val errorModel = try {
                                    jsonLenient.decodeFromString<CompleteErrorModel>(errorText)
                                } catch (e: Exception) {
                                    null
                                }
                                throw UiTayApiException(
                                    code = statusCode,
                                    title = errorModel?.title ?: "Error $statusCode",
                                    messageApi = errorModel?.errorMessage ?: errorText.takeIf { it.isNotBlank() } ?: "Ocurrió un error inesperado"
                                )
                            }
                        }
                    }
                }
            }

            install(Logging) {
                logger = requestLogger
                level = LogLevel.ALL
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 60_000
                requestTimeoutMillis = 60_000
            }
        }
    }

    // Cliente dedicado para WebSockets (sin ContentNegotiation global para evitar conflictos)
    single(named("wsClient")) {
        HttpClient {
            install(WebSockets)
            install(Logging) {
                logger = requestLogger
                level = LogLevel.ALL
            }
        }
    }

    single { KmmService(get(named("httpClient"))) }
    single { WebSocketManager(get(named("wsClient"))) }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDataBase> {
    // Esta función debe ser implementada en cada plataforma (expect/actual)
    // O usar un factory de Koin que ya esté configurado.
    throw Exception("Use platform specific builder")
}
