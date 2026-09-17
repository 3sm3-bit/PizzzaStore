package com.pizzza.pizzzastore.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.ui.AppViewModel
import com.valu.uitaycompose.utils.tay_amber_400
import com.valu.uitaycompose.utils.tay_blue_400
import com.valu.uitaycompose.utils.tay_green_400
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_grey_400
import com.valu.uitaycompose.utils.tay_purple_400
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB10
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM14
import com.valu.uitaycompose.utils.textM16
import com.valu.uitaycompose.utils.textS12
import com.valu.uitaycompose.utils.textS14
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: AppViewModel,
    onNavigateToMenuOptions: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState = viewModel.orderUiState
    var showSheet by remember { mutableStateOf(false) }
    var showDriverSheet by remember { mutableStateOf(false) }
    var orderForDriver by remember { mutableStateOf<ParentOrderModel?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Determinamos el número de columnas basado en el ancho de la pantalla (Tablet vs Celular)
    // Usamos smallestScreenWidthDp para detectar tablets de 7 pulgadas o más (sw600dp)
    // Esto asegura que en celulares siempre sea 1 columna y en tablets siempre sean 2.
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val columns = if (configuration.smallestScreenWidthDp >= 600) 2 else 1

    LaunchedEffect(uiState.selectedOrder) {
        showSheet = uiState.selectedOrder != null
    }

    LaunchedEffect(Unit) {
        if (!uiState.isInitialLoaded) {
            viewModel.getGeneralOrderList()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.userRole?.trim()?.uppercase() == "ADMIN") {
                FloatingActionButton(
                    onClick = onNavigateToMenuOptions,
                    containerColor = tay_green_600,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Ver Productos",
                        modifier = Modifier.size(28.dp)
                    )
                }
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
                        text = "Pedidos",
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

                        IconButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFDDDFE2), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                modifier = Modifier.size(20.dp),
                                tint = tay_red_600
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusIndicator(
                        text = "PENDIENTES",
                        count = uiState.countPendientes,
                        color = Color(0xFF3B82F6),
                        selected = uiState.selectedFilter != "ENTREGADO",
                        onClick = { viewModel.applyFilter("PENDIENTES") },
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Estado Conexión",
                            modifier = Modifier.size(20.dp),
                            tint = if (uiState.isSocketConnected) Color(0xFF10B981) else Color(0xFFF44336)
                        )

                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Estado Impresora",
                            modifier = Modifier.size(20.dp),
                            tint = if (uiState.isPrinterConnected) Color(0xFF10B981) else Color(0xFFF44336)
                        )
                    }

                    StatusIndicator(
                        text = "ENTREGADOS",
                        count = uiState.countEntregado,
                        color = Color(0xFF10B981),
                        selected = uiState.selectedFilter == "ENTREGADO",
                        onClick = { viewModel.applyFilter("ENTREGADO") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.filteredOrders) { order ->
                            OrderCard(
                                order = order,
                                backgroundColor = Color.White,
                                textColor = Color(0xFF1C1E21),
                                onDetailClick = { viewModel.selectOrder(order) },
                                onStateChange = {
                                    val currentState = order.state.trim().uppercase()
                                    val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
                                    
                                    if (currentState == "LISTO" && isDelivery) {
                                        val drivers = uiState.drivers
                                        if (drivers.size >= 2) {
                                            orderForDriver = order
                                            showDriverSheet = true
                                        } else {
                                            val driverUid = if (drivers.size == 1) drivers.first().uid ?: "0" else "0"
                                            viewModel.updateOrderState(order, "ENVIADO", driverId = driverUid)
                                        }
                                    } else {
                                        if (currentState == "CONFIRMADO" && !uiState.isPrinterConnected) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("⚠️ Impresora desconectada")
                                            }
                                        }
                                        viewModel.avanzarEstado(order)
                                    }
                                }
                            )

                    }
                }
            }

            if (showSheet && uiState.selectedOrder != null) {
                OrderDetailSheet(
                    order = uiState.selectedOrder,
                    onDismiss = { viewModel.selectOrder(null) },
                    onReprint = {
                        if (uiState.isPrinterConnected) {
                            viewModel.reprintOrder(uiState.selectedOrder)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("⚠️ Impresora desconectada")
                            }
                        }
                    }
                )
            }

            if (showDriverSheet && orderForDriver != null) {
                DriverSelectionSheet(
                    drivers = uiState.drivers,
                    onDismiss = { 
                        showDriverSheet = false
                        orderForDriver = null
                    },
                    onDriverSelected = { driverUid ->
                        orderForDriver?.let { order ->
                            viewModel.updateOrderState(order, "ENVIADO", driverId = driverUid)
                        }
                        showDriverSheet = false
                        orderForDriver = null
                    }
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text(text = "Cerrar Sesión", style = textB20, color = tay_red_600) },
                    text = {
                        Text(
                            text = "¿Estás seguro de que deseas cerrar sesión?",
                            style = textM14, color = Color.Gray
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLogoutDialog = false
                                viewModel.resetOrderState()
                                viewModel.logout {
                                    onLogout()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = tay_red_600)
                        ) {
                            Text("Sí, salir", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("No", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
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
    onStateChange: () -> Unit
) {
    val statusColor = when (order.state.trim().uppercase()) {
        "CONFIRMADO" -> tay_blue_400 // Azul
        "RECEPCIONADO" -> tay_purple_400 // Violeta/Morado
        "LISTO" -> tay_green_400      // Verde
        "ENVIADO" -> tay_amber_400    // Ámbar/Naranja
        "ENTREGADO" -> tay_grey_400  // Gris
        else -> tay_green_400
    }

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
                    
                    Icon(
                        imageVector = if (order.canal.trim().uppercase() == "CALL") Icons.Default.Phone else Icons.Default.Smartphone,
                        contentDescription = "Canal: ${order.canal}",
                        modifier = Modifier.padding(horizontal = 8.dp).size(18.dp),
                        tint = Color(0xFF65676B)
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${item.quantity} ${item.nameProduct}",
                                    style = textM14,
                                    color = textColor
                                )
                                Text(
                                    text = " - ${item.tamanio}, ${item.typeDough}",
                                    style = textS12,
                                    fontSize = 10.sp,
                                    color = Color(0xFF65676B),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDelivery) Icons.Default.Home else Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isDelivery) Color(0xFFE91E63) else Color(0xFF007BFF)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDelivery) "DELIVERY" else "RECOJO EN LOCAL",
                                style = textB12,
                                fontSize = 11.sp,
                                color = if (isDelivery) Color(0xFFE91E63) else Color(0xFF007BFF)
                            )
                        }
                        if (isDelivery && order.address.isNotBlank() && order.address.lowercase() != "null") {
                            Text(
                                text = order.address,
                                style = textS12,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                color = Color(0xFF65676B),
                                maxLines = 1,
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
                    
                    val actionButtonText = when (order.state.trim().uppercase()) {
                        "CONFIRMADO" -> "IMPRIMIR"
                        "RECEPCIONADO" -> "LISTO"
                        "LISTO" -> if (order.reception.trim().uppercase().contains("DELIVERY")) "ENVIADO" else "ENTREGADO"
                        else -> null
                    }

                    if (actionButtonText != null) {
                        Button(
                            onClick = { onStateChange() },
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = statusColor
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = actionButtonText,
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
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        color = if (selected) color else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDFE2)),
        onClick = onClick,
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
                color = if (selected) Color.White else Color(0xFF1C1E21)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                color = if (selected) Color.White.copy(alpha = 0.2f) else color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = textB12,
                    color = if (selected) Color.White else color
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailSheet(
    order: ParentOrderModel,
    onDismiss: () -> Unit,
    onReprint: () -> Unit
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
                    modifier = Modifier.size(18.dp),
                    tint = if (isDelivery) Color(0xFFE91E63) else Color(0xFF007BFF)
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                                        text = "🧀 Con orilla de queso (+$${item.priceChosse})",
                                        style = textB10,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                if (item.note.isNotBlank()) {
                                    Text(
                                        text = "📝 Nota: ${item.note}",
                                        style = textS12,
                                        color = Color(0xFFE91E63),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$${(item.price.toDoubleOrNull() ?: 0.0).toInt()}",
                                    style = textB16,
                                    color = Color(0xFF1C1E21)
                                )
                                Text(
                                    text = "unit.",
                                    style = textS12,
                                    fontSize = 10.sp,
                                    color = Color(0xFF8A8D91)
                                )
                            }
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onReprint,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("REIMPRIMIR TICKET", style = textB16)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSelectionSheet(
    drivers: List<com.pizzza.pizzzastore.repository.network.model.UserResponse>,
    onDismiss: () -> Unit,
    onDriverSelected: (String) -> Unit
) {
    var selectedDriverUid by remember { mutableStateOf("") }

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
            Text(
                text = "Asignar Repartidor",
                style = textB20,
                color = Color(0xFF1C1E21)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (drivers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay repartidores disponibles",
                        style = textM14,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                ) {
                    items(drivers) { driver ->
                        val isSelected = selectedDriverUid == driver.uid
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDriverUid = driver.uid ?: "" }
                        ) {
                            Surface(
                                color = if (isSelected) tay_blue_400 else tay_blue_400.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, tay_blue_400) else null,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Smartphone,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else tay_blue_400,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = driver.nameUser ?: "Driver",
                                style = textB10,
                                color = if (isSelected) tay_blue_400 else Color(0xFF1C1E21),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onDriverSelected(selectedDriverUid) },
                    enabled = selectedDriverUid.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tay_green_600,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "CONFIRMAR ENVÍO",
                        style = textB14,
                        color = Color.White
                    )
                }
            }
        }
    }
}
