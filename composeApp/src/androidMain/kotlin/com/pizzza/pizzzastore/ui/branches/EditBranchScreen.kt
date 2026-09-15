package com.pizzza.pizzzastore.ui.branches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBranchScreen(
    viewModel: StoreViewModel,
    addressFromMap: String? = null,
    latFromMap: String? = null,
    lngFromMap: String? = null,
    onNavigateToAddressSelection: (String?, String?, String?) -> Unit,
    onBack: () -> Unit
) {
    val branch = viewModel.storeUiState.selectedBranch
    val isEditMode = branch != null

    var name by remember { mutableStateOf(branch?.nameBranch ?: "") }
    var identifier by remember { mutableStateOf(branch?.identifier ?: "") }
    var description by remember { mutableStateOf(branch?.description ?: "") }
    var address by remember { mutableStateOf(branch?.address ?: "") }
    var phone by remember { mutableStateOf(branch?.phone ?: "") }
    var latitude by remember { mutableStateOf(branch?.latitude ?: "") }
    var longitude by remember { mutableStateOf(branch?.longitude ?: "") }

    LaunchedEffect(addressFromMap, latFromMap, lngFromMap) {
        if (addressFromMap != null) address = addressFromMap
        if (latFromMap != null) latitude = latFromMap
        if (lngFromMap != null) longitude = lngFromMap
    }

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = if (isEditMode) "Editar Sucursal" else "Crear Sucursal",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(tay_red_50)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack.invoke()
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UiTayEditLayout(
                value = name,
                onValueChange = { name = it },
                hint = "Nombre de Sucursal",
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

            UiTayEditLayout(
                value = identifier,
                onValueChange = { identifier = it },
                hint = "Identificador",
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

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Selector de Dirección
            Column {
                Text(
                    text = "Dirección / Ubicación",
                    style = textB14,
                    color = tay_red_600,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Surface(
                    onClick = { onNavigateToAddressSelection(latitude, longitude, address) },
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
                            text = if (address.isBlank()) "Selecciona dirección en el mapa" else address,
                            style = textM12,
                            color = if (address.isBlank()) Color.Gray else tay_red_600,
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

            UiTayEditLayout(
                value = phone,
                onValueChange = { phone = it },
                hint = "Teléfono",
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

            if (isEditMode && branch != null) {
                Text(
                    text = "UID: ${branch.uid}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            UiTayButton(
                uiTayText = if (isEditMode) "Guardar Cambios" else "Crear Sucursal",
                uiTayEnable = name.isNotBlank() && identifier.isNotBlank() && address.isNotBlank(),
                uiTayClick = {
                    val branchData = (branch ?: BranchModel(
                        nameBranch = "",
                        identifier = "",
                        description = "",
                        address = "",
                        phone = "",
                        latitude = "",
                        longitude = "",
                        uid = ""
                    )).copy(
                        nameBranch = name,
                        identifier = identifier,
                        description = description,
                        address = address,
                        phone = phone,
                        latitude = latitude,
                        longitude = longitude
                    )

                    if (isEditMode) {
                        viewModel.updateBranch(branchData) {
                            onBack()
                        }
                    } else {
                        viewModel.createBranch(branchData) {
                            onBack()
                        }
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
