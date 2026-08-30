package com.pizzza.pizzzastore.di

import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.repository.network.WebSocketNotification
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object KoinHelper : KoinComponent {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun getDataUseCase(): DataUseCase = get()
    fun getWebSocketManager(): WebSocketManager = get()

    // Método para que Swift pueda suscribirse a las notificaciones fácilmente
    fun observeNotifications(callback: (WebSocketNotification) -> Unit) {
        getWebSocketManager().notifications
            .onEach { callback(it) }
            .launchIn(scope)
    }
}
