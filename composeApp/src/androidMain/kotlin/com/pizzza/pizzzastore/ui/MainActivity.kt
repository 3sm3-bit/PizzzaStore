package com.pizzza.pizzzastore.ui

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pizzza.pizzzastore.component.AppNavigation
import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.ui.base.BaseActivity
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Context

class MainActivity : BaseActivity() {

    private val viewModel : AppViewModel by viewModel()
    private val storeViewModel : StoreViewModel by viewModel()
    private val webSocketManager: WebSocketManager by inject()

    private val prefs by lazy { getSharedPreferences("pizza_prefs", Context.MODE_PRIVATE) }

    @Composable
    override fun SetScreenConfig() {
        AppNavigation(
            viewModel = viewModel,
            storeViewModel = storeViewModel
        )
    }

    override fun setDataGlobal() {
        // 1. Cargar el estado guardado y aplicarlo al ViewModel sin disparar efectos aún
        val isEnabled = prefs.getBoolean("notifications_enabled", false)
        viewModel.setNotificationsEnabled(isEnabled)

        observeSocketForRefresh()
        
        // El sync se maneja ahora en la SplashScreen
        
        // 3. Iniciar la observación del Switch
        // Esto manejará tanto el estado inicial como cualquier cambio posterior
        observeNotificationToggle()
    }

    private fun observeNotificationToggle() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                snapshotFlow { viewModel.orderUiState.notificationsEnabled }
                    .collectLatest { enabled ->
                        // Guardar en persistencia cada vez que cambie
                        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
                        
                        if (enabled) {
                            println("🍕 MainActivity - Switch ACTIVADO: Iniciando servicio")
                            startWebSocketService()
                        } else {
                            println("🍕 MainActivity - Switch DESACTIVADO: Deteniendo servicio")
                            stopWebSocketService()
                        }
                    }
            }
        }
    }

    private fun startWebSocketService() {
        val intent = Intent(this, com.pizzza.pizzzastore.service.WebSocketService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopWebSocketService() {
        val intent = Intent(this, com.pizzza.pizzzastore.service.WebSocketService::class.java)
        stopService(intent)
    }

    private fun observeSocketForRefresh() {
        webSocketManager.notifications
            .onEach {
                println("🍕 MainActivity - Notificación recibida para refrescar lista")
                viewModel.getGeneralOrderList()
            }
            .launchIn(lifecycleScope)
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun getViewModels(): List<BaseViewModel> = listOf(
        viewModel,
        storeViewModel
    )
}
