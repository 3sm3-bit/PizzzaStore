package com.pizzza.pizzzastore.ui.branches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBranchScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    val branch = viewModel.storeUiState.selectedBranch ?: return

    var name by remember { mutableStateOf(branch.nameBranch) }
    var identifier by remember { mutableStateOf(branch.identifier) }
    var description by remember { mutableStateOf(branch.description) }
    var address by remember { mutableStateOf(branch.address) }
    var phone by remember { mutableStateOf(branch.phone) }
    var latitude by remember { mutableStateOf(branch.latitude) }
    var longitude by remember { mutableStateOf(branch.longitude) }

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Editar Sucursal",
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
        containerColor = Color(0xFFF0F2F5),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de Sucursal") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text("Identificador") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitud") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitud") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "UID: ${branch.uid}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    val updatedBranch = branch.copy(
                        nameBranch = name,
                        identifier = identifier,
                        description = description,
                        address = address,
                        phone = phone,
                        latitude = latitude,
                        longitude = longitude
                    )
                    viewModel.updateBranch(updatedBranch) {
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Cambios")
            }
        }
    }
}
