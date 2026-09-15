package com.pizzza.pizzzastore.ui.register

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    initialLat: String? = null,
    initialLng: String? = null,
    initialAddress: String? = null,
    onConfirm: (String, String, String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val initialLatLng = remember {
        val lat = initialLat?.toDoubleOrNull()
        val lng = initialLng?.toDoubleOrNull()
        if (lat != null && lng != null) LatLng(lat, lng) else null
    }

    val defaultLocation = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng ?: defaultLocation, 15f)
    }

    var currentAddress by remember { mutableStateOf(initialAddress ?: "Obteniendo dirección...") }
    var currentLatLng by remember { mutableStateOf(initialLatLng ?: defaultLocation) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun updateAddress(latLng: LatLng) {
        currentLatLng = latLng
        scope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    currentAddress = addresses[0].getAddressLine(0)
                }
            } catch (_: Exception) {
                currentAddress = "Ubicación seleccionada"
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    updateAddress(userLatLng)
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (initialLatLng != null) return@LaunchedEffect
        
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    updateAddress(userLatLng)
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                        )
                    }
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            updateAddress(cameraPositionState.position.target)
        }
    }

    Scaffold(
        topBar = {

            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Selecciona tu Ubicación",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(tay_red_50)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack.invoke()
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            )

            // Fixed Pin in the center
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = tay_red_600,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .offset(y = (-24).dp)
            )

            // Info and confirm button at the bottom
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(16.dp))
                    UiTayButton(
                        uiTayText = "Confirmar Ubicación",
                        uiTayClick = {
                            onConfirm(
                                currentAddress,
                                currentLatLng.latitude.toString(),
                                currentLatLng.longitude.toString()
                            )
                            onBack()
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
    }
}
