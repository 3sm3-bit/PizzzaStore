package com.pizzza.pizzzastore.ui

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import com.pizzza.pizzzastore.component.AppNavigation
import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.ui.base.BaseActivity
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.valu.uitaycompose.utils.permission.rememberUiTayPermissionManager
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.repeatOnLifecycle

class MainActivity : BaseActivity() {

    private val viewModel : AppViewModel by viewModel()
    private val storeViewModel : StoreViewModel by viewModel()
    private val webSocketManager: WebSocketManager by inject()
    
    private val prefs by lazy { getSharedPreferences("pizza_prefs", MODE_PRIVATE) }

    @Composable
    override fun SetScreenConfig() {
        val permissionManager = rememberUiTayPermissionManager()
        
        LaunchedEffect(Unit) {
            val permissions = mutableListOf<String>()
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.add(android.Manifest.permission.BLUETOOTH_CONNECT)
                permissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
            } else {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            permissionManager.requestPermissions(permissions.toTypedArray()) {
                Log.d("Permissions", "Permisos procesados")
                // Intentar conectar impresora ahora que tenemos permisos
                viewModel.reconnectPrinter()
            }
        }

        AppNavigation(
            viewModel = viewModel,
            storeViewModel = storeViewModel
        )
    }

    override fun setDataGlobal() {
        // Cargar el ID de sucursal guardado (default "1")
        val savedBranchId = prefs.getString("selected_branch_id", "1") ?: "1"
        viewModel.setInitialSelectedBranchId(savedBranchId)

        observeSocketForRefresh()
        
        // Iniciar el servicio de notificaciones automáticamente al abrir la app
        startWebSocketService()
        
        // Observar cambios en el ID de sucursal para guardarlos y reiniciar el servicio
        observeBranchIdChanges()
    }

    private fun observeBranchIdChanges() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                snapshotFlow { viewModel.orderUiState.selectedBranchId }
                    .collectLatest { branchId ->
                        val currentSaved = prefs.getString("selected_branch_id", "1")
                        if (branchId != currentSaved) {
                            println("🍕 MainActivity - Cambio de sucursal detectado: $branchId. Guardando y reiniciando servicio.")
                            prefs.edit().putString("selected_branch_id", branchId).apply()
                            
                            // Reiniciar el servicio para que tome el nuevo branchId
                            stopWebSocketService()
                            startWebSocketService()
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
