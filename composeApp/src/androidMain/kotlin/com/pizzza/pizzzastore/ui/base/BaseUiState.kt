package com.pizzza.pizzzastore.ui.base

import androidx.compose.ui.graphics.Color
import com.valu.uitaycompose.utils.tay_red_600

data class BaseUiState(
    var popUpGenericValue: Boolean = false,
    var popUpGeneric: Boolean = false,
    var loading: Boolean = false,
    var shimmer: Boolean = false,
    var error: Boolean = false,
    var statusBarColor: Color = tay_red_600,
    var errorType: Throwable = Throwable()
)