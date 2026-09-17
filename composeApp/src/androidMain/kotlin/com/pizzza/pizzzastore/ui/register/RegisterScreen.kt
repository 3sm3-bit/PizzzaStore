package com.pizzza.pizzzastore.ui.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.login.AuthViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.*
import com.valu.uitaycompose.utils.*
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = koinViewModel(),
    storeViewModel: StoreViewModel = koinViewModel(),
    addressFromMap: String? = null,
    latFromMap: String? = null,
    lngFromMap: String? = null,
    onNavigateToAddressSelection: (String?, String?, String?) -> Unit,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val storeUiState = storeViewModel.storeUiState
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        storeViewModel.getBranchesList()
    }

    // Efecto reactivo inteligente para auto-asignar la sucursal por defecto al rol DRIVER o STORE
    LaunchedEffect(storeUiState.branches, uiState.rol) {
        val currentRole = uiState.rol.uppercase()
        if (currentRole == "DRIVER" || currentRole == "STORE") {
            if (storeUiState.branches.isEmpty()) {
                // Si no hay sucursales, asignamos "1" por defecto para estos roles
                viewModel.onRegisterFieldChange(area = "1")
            } else if (storeUiState.branches.size == 1) {
                // Si hay una sola, la auto-seleccionamos
                viewModel.onRegisterFieldChange(area = storeUiState.branches.first().identifier)
            } else if (uiState.isEditMode && uiState.area.isNotBlank() && uiState.area != "0") {
                // Al editar con múltiples sucursales, validamos que el área actual exista en el listado para mantenerla seleccionada
                val exist = storeUiState.branches.any { it.identifier == uiState.area }
                if (!exist) {
                    viewModel.onRegisterFieldChange(area = storeUiState.branches.first().identifier)
                }
            } else if (uiState.area == "0") {
                // Si venía de otro rol y ahora es DRIVER/STORE, seleccionamos la primera por defecto
                viewModel.onRegisterFieldChange(area = storeUiState.branches.first().identifier)
            }
        } else {
            // Para cualquier otro rol (ADMIN, CLIENTE, etc), el área por defecto es "0"
            if (uiState.area != "0") {
                viewModel.onRegisterFieldChange(area = "0")
            }
        }
    }
    
    LaunchedEffect(addressFromMap, latFromMap, lngFromMap) {
        if (addressFromMap != null || latFromMap != null || lngFromMap != null) {
            viewModel.onRegisterFieldChange(
                address = addressFromMap ?: uiState.address,
                latitude = latFromMap ?: uiState.latitude,
                longitude = lngFromMap ?: uiState.longitude
            )
        }
    }
    
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()
    val isPhoneValid = uiState.phone.length == 10 && uiState.phone.all { it.isDigit() }

    val isButtonEnabled = uiState.nameUser.isNotBlank() &&
            uiState.names.isNotBlank() &&
            uiState.lastName.isNotBlank() &&
            isEmailValid &&
            (uiState.isEditMode || uiState.pass.isNotBlank()) &&
            isPhoneValid &&
            uiState.address.isNotBlank() &&
            uiState.address != "Selecciona dirección en el mapa"

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = if (uiState.isEditMode) "Editar Usuario" else "Registrar Usuario",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(tay_red_50)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack()
                    }
                }
            }
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    UiTayEditLayout(
                        value = uiState.nameUser,
                        onValueChange = { viewModel.onRegisterFieldChange(nameUser = it) },
                        hint = "Nombre de usuario",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.names,
                        onValueChange = { viewModel.onRegisterFieldChange(names = it) },
                        hint = "Nombres",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.lastName,
                        onValueChange = { viewModel.onRegisterFieldChange(lastName = it) },
                        hint = "Apellidos",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.email,
                        onValueChange = { viewModel.onRegisterFieldChange(email = it) },
                        hint = "Correo electrónico",
                        keyboardType = KeyboardType.Email,
                        isError = uiState.email.isNotBlank() && !isEmailValid,
                        errorMessage = "Formato de correo inválido",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                if (!uiState.isEditMode) {
                    item {
                        UiTayEditLayout(
                            value = uiState.pass,
                            onValueChange = { viewModel.onRegisterFieldChange(pass = it) },
                            hint = "Contraseña",
                            isPassword = true,
                            imeAction = ImeAction.Next,
                            model = UiEditLayoutModel(
                                uiStrokeActiveColor = tay_red_600,
                                uiTextColor = tay_red_600,
                                uiTextActiveColor = tay_red_600,
                                uiTitleActiveColor = tay_red_600,
                                uiIconColor = tay_grey_800,
                                uiIconActiveColor = tay_red_600,
                                uiTextFont = textM14,
                                uiTitleFont = textM14
                            )
                        )
                    }
                }
                item {
                    UiTayEditLayout(
                        value = uiState.phone,
                        onValueChange = { viewModel.onRegisterFieldChange(phone = it) },
                        hint = "Celular",
                        keyboardType = KeyboardType.Number,
                        maxLength = 10,
                        isError = uiState.phone.isNotBlank() && !isPhoneValid,
                        errorMessage = "Debe ser de 10 dígitos",
                        imeAction = ImeAction.Done,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Rol de Usuario",
                            style = textM14,
                            color = tay_red_600,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val roles = listOf("CLIENTE", "DRIVER", "STORE", "ADMIN")
                        roles.chunked(2).forEach { rowRoles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                rowRoles.forEach { role ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.onRegisterFieldChange(rol = role) }
                                    ) {
                                        RadioButton(
                                            selected = uiState.rol == role,
                                            onClick = { viewModel.onRegisterFieldChange(rol = role) },
                                            colors = RadioButtonDefaults.colors(selectedColor = tay_red_600)
                                        )
                                        Text(text = role, style = textM12, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Mostrar la sección interactiva de sucursales únicamente si el rol es DRIVER o STORE
                // y hay 2 o más opciones disponibles en la lista
                val isBranchRequiredRole = uiState.rol.uppercase() == "DRIVER" || uiState.rol.uppercase() == "STORE"
                if (isBranchRequiredRole && storeUiState.branches.size >= 2) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Seleccionar Sucursal",
                                style = textM14,
                                color = tay_red_600,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(storeUiState.branches) { branch ->
                                    val isSelected = uiState.area == branch.identifier
                                    Surface(
                                        onClick = { viewModel.onRegisterFieldChange(area = branch.identifier) },
                                        color = if (isSelected) tay_red_600 else Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isSelected) tay_red_600 else tay_grey_400),
                                        modifier = Modifier.width(120.dp).height(40.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                                        ) {
                                            Text(
                                                text = branch.nameBranch,
                                                style = textM12,
                                                color = if (isSelected) Color.White else Color.Black,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    Surface(
                        onClick = { onNavigateToAddressSelection(uiState.latitude, uiState.longitude, uiState.address) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, tay_green_600),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (uiState.address.isBlank()) "Selecciona dirección en el mapa" else uiState.address,
                                style = textM12,
                                color = if (uiState.address.isBlank()) Color.Gray else tay_red_600,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = tay_green_600
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                UiTayButton(
                    uiTayText = if (uiState.isEditMode) "Guardar Cambios" else "Registrarse",
                    uiTayEnable = isButtonEnabled,
                    uiTayClick = {
                        viewModel.register { _ ->
                            Toast.makeText(context, if (uiState.isEditMode) "Usuario actualizado" else "Registro exitoso", Toast.LENGTH_LONG).show()
                            onRegisterSuccess()
                        }
                    },
                    uiTayBtnModifier = UiTayButtonModel(
                        uTBgColor = tay_red_600,
                        uTStrokeColor = tay_red_600,
                        uTBgSelectedColor = tay_red_600,
                        uTStrokeSelectedColor = tay_red_600,
                    )
                )
            }
        }
    }
}
