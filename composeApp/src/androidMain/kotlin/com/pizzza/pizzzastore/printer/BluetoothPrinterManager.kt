package com.pizzza.pizzzastore.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothPrinterManager(private val context: Context) {
    private val TAG = "PrinterManager"
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    
    private var selectedDevice: BluetoothDevice? = null

    @SuppressLint("MissingPermission")
    fun autoDetectAndConnect() {
        if (!hasBluetoothPermission()) {
            _isConnected.value = false
            return
        }
        try {
            val bluetoothDevicesList = BluetoothPrintersConnections().list
            if (bluetoothDevicesList != null && bluetoothDevicesList.isNotEmpty()) {
                val connection = bluetoothDevicesList.firstOrNull { 
                    it.device.name.contains("Printer", ignoreCase = true) || 
                    it.device.name.contains("Thermal", ignoreCase = true) 
                } ?: bluetoothDevicesList.first()

                selectedDevice = connection.device
                _isConnected.value = true
            } else {
                _isConnected.value = false
            }
        } catch (e: Exception) {
            _isConnected.value = false
        }
    }

    fun printTicket(content: String) {
        if (!hasBluetoothPermission()) return
        
        if (selectedDevice == null) {
            autoDetectAndConnect()
        }

        selectedDevice?.let { device ->
            try {
                // Formato estándar de ticket (80mm o 58mm dinámico)
                val printer = EscPosPrinter(BluetoothConnection(device), 203, 72f, 48)
                printer.printFormattedText(content)
                printer.disconnectPrinter()
            } catch (e: Exception) {
                Log.e(TAG, "Error al imprimir: ${e.message}")
            }
        }
    }

    private fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
