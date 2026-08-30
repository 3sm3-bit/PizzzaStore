package com.pizzza.pizzzastore.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.utils.textB20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPizzaScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    val product = viewModel.storeUiState.selectedProduct ?: return

    var name by remember { mutableStateOf(product.nameProduct) }
    var price by remember { mutableStateOf(product.price) }
    var description by remember { mutableStateOf(product.description) }
    var priceChosse by remember { mutableStateOf(product.priceChosse) }
    var currency by remember { mutableStateOf(product.currency) }
    var currencySymbol by remember { mutableStateOf(product.currencySymbol) }
    var stateAvailable by remember { mutableStateOf(product.state) }
    var selectedSize by remember { mutableStateOf(product.tamanio) }

    val sizes = listOf("CHICO", "MEDIANO", "GRANDE")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Pizza", style = textB20) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val updatedProduct = product.copy(
                            nameProduct = name,
                            price = price,
                            tamanio = selectedSize,
                            description = description,
                            priceChosse = priceChosse,
                            currency = currency,
                            currencySymbol = currencySymbol,
                            state = stateAvailable
                        )
                        viewModel.updateProduct(updatedProduct) {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = priceChosse,
                    onValueChange = { priceChosse = it },
                    label = { Text("Precio Orilla Queso") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Moneda (e.g. MXN)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = currencySymbol,
                    onValueChange = { currencySymbol = it },
                    label = { Text("Símbolo") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Tamaño:", style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup()) {
                sizes.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (text == selectedSize),
                                onClick = { selectedSize = text },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedSize),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (stateAvailable) "Estado: DISPONIBLE" else "Estado: NO DISPONIBLE",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = stateAvailable,
                    onCheckedChange = { stateAvailable = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedProduct = product.copy(
                        nameProduct = name,
                        price = price,
                        tamanio = selectedSize,
                        description = description,
                        priceChosse = priceChosse,
                        currency = currency,
                        currencySymbol = currencySymbol,
                        state = stateAvailable
                    )
                    viewModel.updateProduct(updatedProduct) {
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
