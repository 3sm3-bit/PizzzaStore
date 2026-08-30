package com.pizzza.pizzzastore.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizzza.pizzzastore.DispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

import kotlinx.coroutines.withContext

open class BaseViewModel(private val dispatchers: DispatcherProvider): ViewModel() {

    var uiStateBase by mutableStateOf(BaseUiState())

    fun execute(loading: Boolean = true, func: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io) {
            try {
                withContext(dispatchers.main) {
                    uiStateBase = uiStateBase.copy(loading = loading, error = false)
                }
                
                func()
                
                withContext(dispatchers.main) {
                    uiStateBase = uiStateBase.copy(loading = false)
                }
            } catch (ex: Exception) {
                withContext(dispatchers.main) {
                    uiStateBase = uiStateBase.copy(error = true, errorType = ex, loading = false)
                }
            }
        }
    }

    fun executeAlter(loading: Boolean = true,func:suspend ()->Unit){
        viewModelScope.launch(dispatchers.io){
            try {
                uiStateBase = uiStateBase.copy(loading = loading)
                delay(1000.milliseconds)
                uiStateBase = uiStateBase.copy(loading = false)
                func()
            }catch (ex:Exception){
                uiStateBase = uiStateBase.copy(error = true, errorType = ex, loading = false)
            }
        }
    }
}
