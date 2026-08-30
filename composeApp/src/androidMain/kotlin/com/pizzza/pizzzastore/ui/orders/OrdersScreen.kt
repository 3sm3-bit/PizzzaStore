package com.pizzza.pizzzastore.ui.orders

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.ui.AppViewModel
import com.valu.uitaycompose.utils.*
import com.valu.uitaycompose.utils.permission.rememberUiTayPermissionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(viewModel: AppViewModel, onNavigateToMenuOptions: () -> Unit) {
    val permissionManager = rememberUiTayPermissionManager()
    val uiState = viewModel.orderUiState
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.selectedOrder) {
        showSheet = uiState.selectedOrder != null
    }

    LaunchedEffect(Unit) {
        viewModel.getGeneralOrderList()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.requestPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
                Log.d("Notifications", "Permission granted for order alerts")
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToMenuOptions,
                containerColor = Color(0xFF007BFF),
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Ver Productos",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Gestión de Pedidos",
                        style = textB20,
                        fontSize = 18.sp,
                        color = Color(0xFF1C1E21)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFDDDFE2), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refrescar",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF007BFF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Indicadores de estado (Resumen)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusIndicator(
                        text = "CONFIRMADO",
                        count = uiState.countConfirmado,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                    StatusIndicator(
                        text = "LISTO",
                        count = uiState.countListo,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.orders.isEmpty() && !viewModel.uiStateBase.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No se encontraron pedidos",
                                style = textM16,
                                color = Color.Gray
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Intentar de nuevo")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(uiState.orders) { order ->
                            OrderCard(
                                order = order,
                                backgroundColor = Color.White,
                                textColor = Color(0xFF1C1E21),
                                onDetailClick = { viewModel.selectOrder(order) },
                                onStateChange = { action ->
                                    if (action == "AVANZAR") {
                                        viewModel.avanzarEstado(order)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (showSheet && uiState.selectedOrder != null) {
                OrderDetailSheet(
                    order = uiState.selectedOrder,
                    onDismiss = { viewModel.selectOrder(null) }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: ParentOrderModel,
    backgroundColor: Color,
    textColor: Color,
    onDetailClick: () -> Unit,
    onStateChange: (String) -> Unit
) {
    val statusColor = if (order.state.trim().uppercase() == "CONFIRMADO") Color(0xFF3B82F6) else Color(0xFF10B981)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // Fila 1: Nombre del Cliente y Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.nameClient,
                        style = textB16,
                        color = textColor,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = order.state.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = textB10,
                            color = statusColor
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp), color = Color(0xFFF0F2F5))

                // Desglose de Productos (Orders)
                order.orders.forEach { item ->
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity} ${item.nameProduct} ${item.typeDough}",
                                style = textM14,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            val subtotal = (item.quantity.toDoubleOrNull() ?: 0.0) * (item.price.toDoubleOrNull() ?: 0.0)
                            Text(
                                text = "$${subtotal.toInt()}",
                                style = textB14,
                                color = textColor
                            )
                        }

                        if (item.cheeseFilledCrust.trim().uppercase() == "SI") {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "con orilla de queso",
                                    style = textS12,
                                    color = Color(0xFF65676B)
                                )
                                Text(
                                    text = "$${item.priceChosse}",
                                    style = textS12,
                                    color = Color(0xFF65676B)
                                )
                            }
                        }

                        if (item.note.isNotBlank()) {
                            Text(
                                text = "Nota: ${item.note}",
                                fontSize = 10.sp,
                                color = Color(0xFF8A8D91),
                                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp), color = Color(0xFFF0F2F5))

                // Logística y Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
                        Text(
                            text = if (isDelivery) "🏠 DELIVERY" else "🛍️ RECOJO EN LOCAL",
                            style = textB12,
                            fontSize = 11.sp,
                            color = if (isDelivery) Color(0xFFE91E63) else Color(0xFF007BFF)
                        )
                        if (isDelivery && order.address.isNotBlank() && order.address.lowercase() != "null") {
                            Text(
                                text = order.address,
                                style = textS12,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                color = Color(0xFF65676B),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "TOTAL", style = textB10, color = Color(0xFF8A8D91))
                        Text(
                            text = "$${order.price}",
                            style = textB20,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila 3: Botones de Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDetailClick,
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF0F2F5)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "DETALLE",
                            fontSize = 12.sp,
                            style = textB12,
                            color = Color(0xFF1C1E21)
                        )
                    }
                    
                    if (order.state.trim().uppercase() != "LISTO") {
                        Button(
                            onClick = { onStateChange("AVANZAR") },
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = statusColor
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "AVANZAR",
                                fontSize = 12.sp,
                                style = textB12,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(
    text: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = textB10,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = textB12,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailSheet(
    order: ParentOrderModel,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF0F2F5),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header del Detalle
            Text(
                text = "Detalle del Pedido",
                style = textB20,
                color = Color(0xFF1C1E21)
            )
            Text(
                text = "Cliente: ${order.nameClient}",
                style = textM16,
                color = Color(0xFF007BFF),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    imageVector = if (isDelivery) Icons.Default.Home else Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isDelivery) Color(0xFFE91E63) else Color(0xFF007BFF)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDelivery) "Envío a: ${order.address}" else "Recojo en local",
                    style = textS14,
                    color = Color(0xFF65676B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de productos
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                order.orders.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Círculo con cantidad
                            Surface(
                                color = Color(0xFFF0F2F5),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "x${item.quantity}", style = textB16, color = Color(0xFF1C1E21))
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.nameProduct, style = textB16, color = Color(0xFF1C1E21))
                                Text(
                                    text = "${item.tamanio} • ${item.typeDough}",
                                    style = textS12,
                                    color = Color(0xFF65676B)
                                )
                                if (item.cheeseFilledCrust.trim().uppercase() == "SI") {
                                    Text(
                                        text = "🧀 Con orilla de queso",
                                        style = textB10,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "$${(item.price.toDoubleOrNull() ?: 0.0).toInt()}",
                                style = textB16,
                                color = Color(0xFF1C1E21)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resumen de precios
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total del Pedido", style = textB16, color = Color(0xFF1C1E21))
                        Text(
                            text = "$${order.price}",
                            style = textB20,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}
