package com.pizzza.pizzzastore.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.utils.AudioQueueManager
import com.pizzza.pizzzastore.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class WebSocketService : Service() {

    private val webSocketManager: WebSocketManager by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var audioQueueManager: AudioQueueManager

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "websocket_service_channel"
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        audioQueueManager = AudioQueueManager(this)
        createServiceNotificationChannel()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                createNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                } else {
                    0
                }
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        
        setupWebSocket()
    }

    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Servicio de Pedidos",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene la conexión para recibir pedidos en tiempo real"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pizzería activa")
            .setContentText("Esperando nuevos pedidos...")
            .setSmallIcon(com.pizzza.pizzzastore.R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    private fun setupWebSocket() {
        // Conectar al socket
        webSocketManager.connect(branchId = "1")

        // Escuchar notificaciones
        webSocketManager.notifications
            .onEach { notification ->
                println("🍕 Service - Pedido Recibido: ${notification.titulo}")
                
                // Reproducir audio
                audioQueueManager.enqueueAudio()

                // Mostrar notificación visual del pedido
                notificationHelper.showOrderNotification(
                    title = notification.titulo,
                    message = notification.mensaje
                )
            }
            .launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        println("🍕 WebSocketService - Servicio detenido por el usuario")
        webSocketManager.close()
        audioQueueManager.release()
        super.onDestroy()
    }
}
