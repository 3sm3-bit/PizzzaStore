package com.pizzza.pizzzastore.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.permission.rememberUiTayCameraManager
import com.valu.uitaycompose.utils.permission.UiTayCameraManagerCompose
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOtherProductScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    val product = viewModel.storeUiState.selectedProduct ?: return

    var name by remember { mutableStateOf(product.nameProduct) }
    var price by remember { mutableStateOf(product.price) }
    var description by remember { mutableStateOf(product.description) }
    var currency by remember { mutableStateOf(product.currency) }
    var currencySymbol by remember { mutableStateOf(product.currencySymbol) }
    var stateAvailable by remember { mutableStateOf(product.state) }
    var urlImg by remember { mutableStateOf(product.urlImg) }
    var productBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val cameraManager = rememberUiTayCameraManager(
        uiTayNameFilePath = "product",
        listener = object : UiTayCameraManagerCompose.CameraControllerListener {
            override fun onCameraPermissionDenied() {
                android.util.Log.w("EditOtherProductScreen", "Camera permission denied")
            }

            override fun onGetImageCameraCompleted(path: String, img: Bitmap) {
                productBitmap = img.asImageBitmap()
                
                // Subir imagen al servidor
                val stream = ByteArrayOutputStream()
                img.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                
                viewModel.uploadProductImage(byteArray) { newUrl ->
                    urlImg = newUrl
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto", style = textB20) },
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
                            description = description,
                            currency = currency,
                            currencySymbol = currencySymbol,
                            state = stateAvailable,
                            urlImg = urlImg
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
            // Sección de Imagen
            Row(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (productBitmap != null) {
                            Image(
                                bitmap = productBitmap!!,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (urlImg.isNotBlank()) {
                            Text("Imagen cargada", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save, // Placeholder
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
                
                Button(
                    onClick = { cameraManager.doCamera("product_img") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cambiar Imagen")
                }
            }

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

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Moneda") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = currencySymbol,
                    onValueChange = { currencySymbol = it },
                    label = { Text("Símbolo") },
                    modifier = Modifier.weight(1f)
                )
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
                        description = description,
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
