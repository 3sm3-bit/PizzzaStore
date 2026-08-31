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
import androidx.compose.foundation.background
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.ImeAction
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.swipe.UiTayUrlImage
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textM14
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
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showUrlImage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showUrlImage = true
    }

    val cameraManager = rememberUiTayCameraManager(
        uiTayNameFilePath = "product",
        listener = object : UiTayCameraManagerCompose.CameraControllerListener {
            override fun onCameraPermissionDenied() {
                android.util.Log.w("EditOtherProductScreen", "Camera permission denied")
            }

            override fun onGetImageCameraCompleted(path: String, img: Bitmap) {
                productBitmap = img.asImageBitmap()
                
                // Guardar los bytes localmente en lugar de subir inmediatamente
                val stream = ByteArrayOutputStream()
                img.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                selectedImageBytes = stream.toByteArray()
                println("EditOtherProductScreen: Imagen capturada y guardada en memoria")
            }
        }
    )

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Editar Producto",
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
                        } else if (urlImg.isNotBlank() && showUrlImage) {
                            UiTayUrlImage(
                                url = urlImg,
                                modifier = Modifier.fillMaxSize()
                            )
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
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { cameraManager.doCamera("product_img") },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cambiar Imagen")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DISPONIBLE", style = textB12,
                                color = tay_green_600)
                        }
                        Switch(
                            checked = stateAvailable,
                            onCheckedChange = { stateAvailable = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = tay_green_600,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFDDDFE2),
                                uncheckedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }

            UiTayEditLayout(
                value = name,
                onValueChange = { name = it },
                hint = "Nombre del Producto",
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

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

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
                uiTayText = "Guardar Cambios",
                uiTayClick = {
                    val updatedProduct = product.copy(
                        nameProduct = name,
                        price = price,
                        description = description,
                        currency = currency,
                        currencySymbol = currencySymbol,
                        state = stateAvailable,
                        urlImg = urlImg
                    )
                    viewModel.updateProduct(updatedProduct, selectedImageBytes) {
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
