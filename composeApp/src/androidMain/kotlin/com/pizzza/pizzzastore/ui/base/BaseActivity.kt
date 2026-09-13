package com.pizzza.pizzzastore.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzastore.repository.network.exception.UiTayApiException
import com.valu.uitaycompose.loading.UiProgress
import com.valu.uitaycompose.modal.UiTayDialog
import com.valu.uitaycompose.model.UiTayDialogModel
import com.valu.uitaycompose.utils.tay_red_600
import org.koin.android.ext.android.inject
import  com.pizzza.pizzzastore.R

abstract class BaseActivity : ComponentActivity() {

    val globalUiStateManager: GlobalUiStateManager by inject()
    open fun getViewModel(): BaseViewModel? = null

    @Composable
    fun RenderGenericDialog(
        image: Int,
        title: String,
        subTitle: String,
        onResult: (Boolean) -> Unit
    ) {
        UiTayDialog(
            model = UiTayDialogModel(
                image = image,
                title = title,
                subTitle = subTitle,
                isCancel = false
            ),
            onDismissRequest = { result: Boolean ->
                onResult(result)
            }
        )
    }

    @Composable
    abstract fun SetScreenConfig()
    abstract fun setDataGlobal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color(0xFFA62626).toArgb())
        )

        setContent {
                CompositionLocalProvider(
                    LocalGlobalUiStateManager provides globalUiStateManager
                ) {
                    val uiState by globalUiStateManager.uiState.collectAsStateWithLifecycle()

                    Box(modifier = Modifier.fillMaxSize()) {
                        SetScreenConfig()

                        if (uiState.loading) {
                            UiProgress(colorProgress = tay_red_600)
                        }

                        if (uiState.error) {
                            val errorInfo = uiState.errorType.mapperError()
                            RenderGenericDialog(
                                image = errorInfo.first,
                                title = errorInfo.second,
                                subTitle = errorInfo.third
                            ) { dialogResult ->
                                globalUiStateManager.updateUiState { current ->
                                    current.copy(
                                        error = false,
                                        popUpGeneric = true,
                                        popUpGenericValue = dialogResult
                                    )
                                }
                            }
                        }
                    }

            }
        }
        setDataGlobal()
    }
}

fun Throwable.mapperError(): Triple<Int, String, String> {
    return when (this) {
        is UiTayApiException -> {
            Triple(
                R.drawable.ic_pizzza,
                this.title.ifEmpty { "Error" },
                this.messageApi.ifEmpty { "Ocurrió un error inesperado" }
            )
        }
        else -> {
            Triple(
                R.drawable.ic_pizzza,
                "Error",
                this.message ?: "Ocurrió un error inesperado"
            )
        }
    }
}