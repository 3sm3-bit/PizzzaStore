package com.pizzza.pizzzastore.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: StoreViewModel,
    onNavigateToEditPizza: () -> Unit,
    onNavigateToEditOther: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.storeUiState

    LaunchedEffect(Unit) {
        viewModel.getProductsList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestros Productos", style = textB20) },
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
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.products) { product ->
                    Card(
                        onClick = {
                            viewModel.selectProduct(product)
                            if (product.type == "1") {
                                onNavigateToEditPizza()
                            } else {
                                onNavigateToEditOther()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                            // Mitad izquierda: Imagen
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color(0xFFE9ECEF)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (product.urlImg.isNotBlank()) {
                                    // Placeholder de imagen (Aquí se cargaría con Coil/Kamel)
                                    Text("📸", fontSize = 32.sp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.Gray.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            // Mitad derecha: Información
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(12.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = product.nameProduct,
                                        style = textB16,
                                        color = Color(0xFF1C1E21),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = product.description,
                                        style = textS12,
                                        color = Color(0xFF65676B),
                                        maxLines = 2,
                                        lineHeight = 14.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Surface(
                                        color = Color(0xFFF0F2F5),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (product.type == "1") product.tamanio else "Producto",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = textB10,
                                            color = Color(0xFF65676B)
                                        )
                                    }
                                    
                                    Text(
                                        text = "${product.currencySymbol}${product.price}",
                                        style = textB18,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
