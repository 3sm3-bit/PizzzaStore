package com.pizzza.pizzzastore.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

abstract class BaseActivity : ComponentActivity() {

    @Composable
    abstract fun SetScreenConfig()
    abstract  fun setDataGlobal()
    open fun getViewModel(): BaseViewModel? = null
    open fun getViewModels(): List<BaseViewModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color(0xFFA62626).toArgb())
        )

        setContent {
            // Obtenemos todos los ViewModels a observar
            val viewModels = getViewModels().ifEmpty { listOfNotNull(getViewModel()) }
            
            // Observamos el estado de carga (si alguno está cargando)
            val isLoading = viewModels.any { it.uiStateBase.loading }
            
            // Buscamos el primer ViewModel que tenga un error
            val viewModelWithError = viewModels.find { it.uiStateBase.error }
            val errorState = viewModelWithError?.uiStateBase

            Box(modifier = Modifier.fillMaxSize()) {
                SetScreenConfig()

                if (isLoading) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.3f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
                
                errorState?.let { uiState ->
                    AlertDialog(
                        onDismissRequest = { 
                            // Limpiamos el error en todos los ViewModels
                            viewModels.forEach { it.uiStateBase = it.uiStateBase.copy(error = false) }
                        },
                        title = { Text("Error") },
                        text = { Text(uiState.errorType.message ?: "Ocurrió un error inesperado") },
                        confirmButton = {
                            TextButton(onClick = { 
                                viewModels.forEach { it.uiStateBase = it.uiStateBase.copy(error = false) }
                            }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
        setDataGlobal()
    }

}