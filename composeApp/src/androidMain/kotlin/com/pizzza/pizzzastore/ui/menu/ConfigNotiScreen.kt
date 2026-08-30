package com.pizzza.pizzzastore.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigNotiScreen(
    appViewModel: AppViewModel,
    storeViewModel: StoreViewModel,
    onBack: () -> Unit
) {
    val appUiState = appViewModel.orderUiState
    val storeUiState = storeViewModel.storeUiState
    val selectedBranchId = appUiState.selectedBranchId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configura notificaciones de sucursal", style = textB20) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1C1E21)
                )
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(storeUiState.branches) { branch ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = branch.identifier == selectedBranchId,
                                onClick = { appViewModel.updateSelectedBranchForNotifications(branch.identifier) }
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = branch.nameBranch,
                                style = textB16,
                                color = Color(0xFF1C1E21)
                            )
                        }
                    }
                }
            }
        }
    }
}
