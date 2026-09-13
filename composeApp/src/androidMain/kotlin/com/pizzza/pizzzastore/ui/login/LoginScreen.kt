package com.pizzza.pizzzastore.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzastore.R
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_grey_800
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textM14
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToClientHome: () -> Unit
) {
    val uiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val isButtonEnabled = uiState.user.length > 2 && uiState.pass.length > 2

    Scaffold(
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(painter = painterResource(R.drawable.ic_logo_pizzzeria),
                contentDescription = "logo_ic",
                modifier = Modifier.width(350.dp).height(100.dp))

            UiTayEditLayout(
                value = uiState.user,
                onValueChange = { viewModel.onUserChange(it) },
                hint = "Usuario o email",
                model = UiEditLayoutModel(
                    uiStrokeActiveColor = tay_red_600,
                    uiTextColor = tay_red_600,
                    uiTextActiveColor = tay_red_600,
                    uiTitleActiveColor = tay_red_600,
                    uiTextFont = textM14,
                    uiTitleFont = textM14
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            UiTayEditLayout(
                value = uiState.pass,
                onValueChange = { viewModel.onPassChange(it) },
                hint = "Contraseña",
                isPassword = true,
                model = UiEditLayoutModel(
                    uiStrokeActiveColor = tay_red_600,
                    uiTextColor = tay_red_600,
                    uiTextActiveColor = tay_red_600,
                    uiTitleActiveColor= tay_red_600,
                    uiIconColor = tay_grey_800,
                    uiIconActiveColor=  tay_red_600,
                    uiTextFont = textM14,
                    uiTitleFont = textM14
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            UiTayButton(
                uiTayText = "Iniciar Sesión",
                uiTayEnable = isButtonEnabled,
                uiTayClick = {
                    viewModel.login {
                        onNavigateToClientHome()
                    }
                },
                uiTayBtnModifier = UiTayButtonModel(
                    uTBgColor = tay_red_600,
                    uTStrokeColor = tay_red_600,
                    uTBgSelectedColor = tay_red_600,
                    uTStrokeSelectedColor = tay_red_600,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}