package com.pizzza.pizzzastore.ui.products

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.*
import com.valu.uitaycompose.utils.permission.UiTayCameraManagerCompose
import com.valu.uitaycompose.utils.permission.rememberUiTayCameraManager
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("1") } // Default to Pizza
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceChosse by remember { mutableStateOf("") } // Precio orilla de queso
    var tamanio by remember { mutableStateOf("GRANDE") } // Tamaño
    var currency by remember { mutableStateOf("MXN") }
    var currencySymbol by remember { mutableStateOf("$") }
    var stateAvailable by remember { mutableStateOf(true) }
    var productBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val cameraManager = rememberUiTayCameraManager(
        uiTayNameFilePath = "product_new",
        listener = object : UiTayCameraManagerCompose.CameraControllerListener {
            override fun onCameraPermissionDenied() {
            }

            override fun onGetImageCameraCompleted(path: String, img: Bitmap) {
                productBitmap = img.asImageBitmap()
                val stream = ByteArrayOutputStream()
                img.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                selectedImageBytes = stream.toByteArray()
            }
        }
    )

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Crear Nuevo Producto",
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
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de Imagen
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (productBitmap != null) {
                            Image(
                                bitmap = productBitmap!!,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { cameraManager.doCamera("new_product_img") },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Tomar Foto")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DISPONIBLE", style = textB12, color = tay_green_600)
                        Switch(
                            checked = stateAvailable,
                            onCheckedChange = { stateAvailable = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = tay_green_600
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }

            // Categoría
            Column {
                Text("Categoría", style = textB14, color = tay_red_600)
                val categoryList = listOf("Pizza" to "1", "Adicionales" to "2", "Bebida" to "3", "Promociones" to "4")
                categoryList.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { (label, value) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).clickable { type = value }
                            ) {
                                RadioButton(
                                    selected = type == value,
                                    onClick = { type = value },
                                    colors = RadioButtonDefaults.colors(selectedColor = tay_red_600)
                                )
                                Text(label, style = textM12, color = Color.Black)
                            }
                        }
                    }
                }
            }

            UiTayEditLayout(
                value = name,
                onValueChange = { name = it },
                hint = "Nombre del Producto",
                imeAction = ImeAction.Next,
                model = UiEditLayoutModel(
                    uiStrokeActiveColor = tay_red_600,
                    uiTextColor = tay_red_600,
                    uiTextActiveColor = tay_red_600,
                    uiTitleActiveColor = tay_red_600,
                    uiTextFont = textM14,
                    uiTitleFont = textM14
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (type == "1") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tamaño", style = textB14, color = tay_red_600)
                    Row(Modifier.fillMaxWidth()) {
                        listOf("CHICA", "MEDIANA", "GRANDE").forEach { size ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).clickable { tamanio = size }
                            ) {
                                RadioButton(
                                    selected = tamanio == size,
                                    onClick = { tamanio = size },
                                    colors = RadioButtonDefaults.colors(selectedColor = tay_red_600)
                                )
                                Text(size, style = textM12, color = Color.Black)
                            }
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UiTayEditLayout(
                            modifier = Modifier.weight(1f),
                            value = price,
                            onValueChange = { price = it },
                            hint = "Precio Pizza",
                            imeAction = ImeAction.Next,
                            model = UiEditLayoutModel(
                                uiStrokeActiveColor = tay_red_600,
                                uiTextColor = tay_red_600,
                                uiTextActiveColor = tay_red_600,
                                uiTitleActiveColor = tay_red_600,
                                uiTextFont = textM14,
                                uiTitleFont = textM14
                            )
                        )
                        UiTayEditLayout(
                            modifier = Modifier.weight(1f),
                            value = priceChosse,
                            onValueChange = { priceChosse = it },
                            hint = "Precio Orilla Queso",
                            imeAction = ImeAction.Done,
                            model = UiEditLayoutModel(
                                uiStrokeActiveColor = tay_red_600,
                                uiTextColor = tay_red_600,
                                uiTextActiveColor = tay_red_600,
                                uiTitleActiveColor = tay_red_600,
                                uiTextFont = textM14,
                                uiTitleFont = textM14
                            )
                        )
                    }
                }
            } else {
                UiTayEditLayout(
                    value = price,
                    onValueChange = { price = it },
                    hint = "Precio",
                    imeAction = ImeAction.Done,
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor = tay_red_600,
                        uiTextFont = textM14,
                        uiTitleFont = textM14
                    )
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UiTayEditLayout(
                    modifier = Modifier.weight(1f),
                    value = currency,
                    onValueChange = { currency = it },
                    hint = "Moneda",
                    imeAction = ImeAction.Done,
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor = tay_red_600,
                        uiTextFont = textM14,
                        uiTitleFont = textM14
                    )
                )
                UiTayEditLayout(
                    modifier = Modifier.weight(1f),
                    value = currencySymbol,
                    onValueChange = { currencySymbol = it },
                    hint = "Símbolo",
                    imeAction = ImeAction.Done,
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor = tay_red_600,
                        uiTextFont = textM14,
                        uiTitleFont = textM14
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            UiTayButton(
                uiTayText = "Crear Producto",
                uiTayEnable = name.isNotBlank() && price.isNotBlank(),
                uiTayClick = {
                    val newProduct = ProductModel(
                        nameProduct = name,
                        type = type,
                        price = price,
                        tamanio = if (type == "1") tamanio else "",
                        description = description,
                        priceChosse = if (type == "1") priceChosse else "",
                        currency = currency,
                        currencySymbol = currencySymbol,
                        state = stateAvailable,
                        urlImg = "",
                        uid = "" // Server generates this
                    )
                    viewModel.createProduct(newProduct, selectedImageBytes) {
                        onBack()
                    }
                },
                uiTayBtnModifier = UiTayButtonModel(
                    uTBgColor = tay_red_600,
                    uTStrokeColor = tay_red_600,
                    uTBgSelectedColor = tay_red_600,
                    uTStrokeSelectedColor = tay_red_600,
                )
            )
        }
    }
}
