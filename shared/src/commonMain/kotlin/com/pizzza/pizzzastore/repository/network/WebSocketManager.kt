package com.pizzza.pizzzastore.repository.network

import com.pizzza.pizzzastore.shared.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class WebSocketNotification(
    val titulo: String,
    val mensaje: String,
    val ordenGeneralId: String? = null,
    val cliente: String? = null,
    val phone: String? = null,
    val reception: String? = null,
    val address: String? = null,
    val priceTotal: String? = null,
    val symbol: String? = null,
    val date: String? = null,
    val branchId: String? = null
)

@Serializable
data class IdentifyMessage(
    val type: String = "IDENTIFY",
    val branchId: String
)

class WebSocketManager(private val client: HttpClient) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var session: WebSocketSession? = null
    private var connectionJob: kotlinx.coroutines.Job? = null

    private val _notifications = MutableSharedFlow<WebSocketNotification>()
    val notifications = _notifications.asSharedFlow()

    private val _isConnected = MutableSharedFlow<Boolean>(replay = 1)
    val isConnected = _isConnected.asSharedFlow()

    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true // FORZAR a enviar "type": "IDENTIFY"
    }

    private var currentBranchId: String? = null

    fun connect(branchId: String) {
        currentBranchId = branchId
        connectionJob?.cancel()
        connectionJob = scope.launch {
            var attempt = 0
            while (true) {
                try {
                    _isConnected.emit(false)
                    // La URL base ya contiene /pizzzeria, el servidor WS suele estar en la raíz del host
                    val baseUrl = BuildConfig.BASE_URL_SERVICE
                    val wsUrl = if (baseUrl.contains("ngrok")) {
                        baseUrl.substringBefore("/pizzzeria").replace("https://", "wss://").replace("http://", "ws://")
                    } else {
                        baseUrl.replace("https://", "wss://").replace("http://", "ws://")
                    }
                    
                    println("🍕 WS - Intentando conectar a: $wsUrl (Intento $attempt)")
                    
                    val wsClient = HttpClient {
                        install(io.ktor.client.plugins.websocket.WebSockets) {
                            pingIntervalMillis = 30_000 // Enviar Ping cada 30 segundos
                        }
                    }

                    wsClient.webSocket(urlString = wsUrl) {
                        session = this
                        println("🍕 WS - ¡CONECTADO EXITOSAMENTE!")
                        _isConnected.emit(true)
                        attempt = 0 // Resetear intentos al conectar
                        
                        // Enviar identificación
                        val identify = IdentifyMessage(branchId = branchId)
                        val jsonStr = json.encodeToString(IdentifyMessage.serializer(), identify)
                        println("🍕 WS - Enviando IDENTIFY: $jsonStr")
                        send(jsonStr)

                        incoming.consumeEach { frame ->
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                println("🍕 WS - Mensaje Recibido: $text")
                                try {
                                    val notification = json.decodeFromString<WebSocketNotification>(text)
                                    println("🍕 WS - Notificación procesada: ${notification.titulo}")
                                    _notifications.emit(notification)
                                } catch (e: Exception) {
                                    println("🍕 WS - Info/Control: $text")
                                }
                            }
                        }
                        println("🍕 WS - El canal de entrada se ha cerrado.")
                    }
                    wsClient.close()
                } catch (e: Exception) {
                    println("🍕 WS - ERROR: ${e.message}")
                }
                
                _isConnected.emit(false)
                attempt++
                val delayTime = (2000L * attempt).coerceAtMost(30000L) // Backoff Exponencial simple (2s, 4s, 6s... hasta 30s)
                println("🍕 WS - Reintentando conexión en ${delayTime/1000}s...")
                delay(delayTime)
            }
        }
    }

    fun reconnect() {
        currentBranchId?.let { connect(it) }
    }

    fun close() {
        connectionJob?.cancel()
        scope.launch {
            session?.close()
        }
    }
}
