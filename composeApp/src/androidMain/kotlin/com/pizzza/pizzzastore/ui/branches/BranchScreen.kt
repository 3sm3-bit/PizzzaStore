package com.pizzza.pizzzastore.ui.branches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textS12
import com.valu.uitaycompose.utils.textM14

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchScreen(
    viewModel: StoreViewModel,
    onNavigateToEdit: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.storeUiState

    LaunchedEffect(Unit) {
        viewModel.getBranchesList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestras Sucursales", style = textB20) },
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
        if (uiState.branches.isEmpty() && viewModel.uiStateBase.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF007BFF))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.branches) { branch ->
                    BranchCard(
                        branch = branch,
                        onClick = {
                            viewModel.selectBranch(branch)
                            onNavigateToEdit()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BranchCard(branch: BranchModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = branch.nameBranch,
                style = textB16,
                color = Color(0xFF1C1E21)
            )
            
            Text(
                text = branch.description,
                style = textS12,
                color = Color(0xFF65676B),
                modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F2F5))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF007BFF)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = branch.address,
                    style = textM14,
                    color = Color(0xFF1C1E21)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF10B981)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = branch.phone,
                    style = textM14,
                    color = Color(0xFF1C1E21)
                )
            }
        }
    }
}
