package com.pizzza.pizzzastore.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product.nameProduct,
                                    style = textB16,
                                    color = Color(0xFF1C1E21),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${product.currencySymbol}${product.price}",
                                    style = textB18,
                                    color = Color(0xFF10B981)
                                )
                            }

                            Text(
                                text = product.description,
                                style = textS12,
                                color = Color(0xFF65676B),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (product.type == "1") {
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFFF0F2F5),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = product.tamanio,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = textB10,
                                            color = Color(0xFF65676B)
                                        )
                                    }

                                    if (product.priceChosse.isNotBlank()) {
                                        Text(
                                            text = "🧀 Orilla: ${product.currencySymbol}${product.priceChosse}",
                                            style = textB10,
                                            color = Color(0xFF007BFF),
                                            modifier = Modifier.padding(top = 4.dp)
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
}
