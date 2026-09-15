package com.pizzza.pizzzastore.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzastore.repository.network.model.UserResponse
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.login.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import com.pizzza.pizzzastore.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListUserScreen(
    viewModel: StoreViewModel,
    authViewModel: AuthViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState = viewModel.storeUiState

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Lista de Usuarios",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(tay_red_50)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                            .iconEnd(R.drawable.ic_add)
                            .showEndIcon(true)
                    ) { value ->
                        if(value){
                            onBack.invoke()

                        }else{
                            authViewModel.selectUser(null)
                            onNavigateToRegister.invoke()
                        }
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
        ) {
            // Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    text = "Colaborador",
                    isSelected = uiState.userFilter == "COLABORADOR",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setUserFilter("COLABORADOR") }
                )
                FilterButton(
                    text = "Clientes",
                    isSelected = uiState.userFilter == "CLIENTE",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setUserFilter("CLIENTE") }
                )
            }

            // Lista
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredUsers) { user ->
                    val context = LocalContext.current
                    UserItem(
                        user = user,
                        onClick = {
                            authViewModel.selectUser(user)
                            onNavigateToRegister()
                        },
                        onDelete = {
                            viewModel.deleteUser(user.uid ?: "") {
                                Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF007BFF) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun UserItem(user: UserResponse, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF0F2F5), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nameUser ?: "Sin nombre",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = user.rol ?: "Sin rol",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}
