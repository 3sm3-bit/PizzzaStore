package com.pizzza.pizzzastore.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
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
                Log.d(TAG, "Impresora detectada: ${selectedDevice?.name}")
                _isConnected.value = true
            } else {
                Log.d(TAG, "No se encontraron dispositivos Bluetooth vinculados")
                _isConnected.value = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en auto-detección: ${e.message}")
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
                val connection = BluetoothConnection(device)
                connection.connect()
                
                // Envío directo de comandos TSPL (GBK es el estándar de estas impresoras)
                val bytes = content.toByteArray(charset("GBK"))
                connection.write(bytes)
                connection.send()
                
                Thread.sleep(500)
                connection.disconnect()
                
                Log.d(TAG, "Etiqueta TSPL enviada correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error en envío TSPL: ${e.message}")
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
