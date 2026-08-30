package com.pizzza.pizzzastore.ui

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.pizzza.pizzzastore.component.AppNavigation
import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.ui.base.BaseActivity
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Context

class MainActivity : BaseActivity() {

    private val viewModel : AppViewModel by viewModel()
    private val storeViewModel : StoreViewModel by viewModel()
    private val webSocketManager: WebSocketManager by inject()

    @Composable
    override fun SetScreenConfig() {
        AppNavigation(
            viewModel = viewModel,
            storeViewModel = storeViewModel
        )
    }

    override fun setDataGlobal() {
        observeSocketForRefresh()
        
        // Iniciar el servicio de notificaciones automáticamente al abrir la app
        startWebSocketService()
    }

    private fun startWebSocketService() {
        val intent = Intent(this, com.pizzza.pizzzastore.service.WebSocketService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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
