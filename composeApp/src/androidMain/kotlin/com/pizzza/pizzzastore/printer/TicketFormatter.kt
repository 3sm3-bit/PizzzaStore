package com.pizzza.pizzzastore.printer

import com.pizzza.pizzzastore.model.ParentOrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketFormatter {
    /**
     * Versión DEFINITIVA: Modo Etiqueta (TSPL)
     * - Formato Horizontal (como la Opción B que funcionó)
     * - Letra pequeña para que quepa todo el pedido
     * - Sin "Pedido:" ID
     * - Dirección solo en DELIVERY
     */
    fun formatOrder(order: ParentOrderModel): String {
        val sb = StringBuilder()
        val printDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        
        // 1. Tamaño exacto: 4 pulgadas (101.6mm) x 2 pulgadas (50.8mm)
        sb.append("SIZE 101.6 mm, 50.8 mm\n") 
        sb.append("GAP 3 mm, 0 mm\n")
        sb.append("DIRECTION 1\n") 
        sb.append("CLS\n")
        
        // 2. HEADER - Al borde superior (Y=0) y sin espacios extra
        sb.append("TEXT 400,0,\"3\",0,1,1,2,\"PIZZZA STORE\"\n")
        sb.append("TEXT 400,35,\"1\",0,1,1,2,\"$printDateTime\"\n")
        sb.append("BAR 50,55,700,2\n")
        
        // 3. DATOS - Líneas pegadas
        sb.append("TEXT 50,70,\"2\",0,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        var currentY = 100
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        if (isDelivery && order.address.isNotBlank() && order.address != "null") {
            sb.append("TEXT 50,$currentY,\"1\",0,1,1,\"DIR: ${order.address}\"\n")
            currentY += 25
        }
        
        sb.append("BAR 50,$currentY,700,1\n")
        currentY += 20
        
        // 4. PRODUCTOS - Espaciado mínimo (22 dots)
        order.orders.forEach { item ->
            sb.append("TEXT 50,$currentY,\"1\",0,1,1,\"${item.quantity}x ${item.nameProduct}\"\n")
            currentY += 22
            if (item.note.isNotBlank()) {
                sb.append("TEXT 80,$currentY,\"1\",0,1,1,\"Nota: ${item.note}\"\n")
                currentY += 22
            }
        }
        
        // 5. TOTAL - Ajustado al final de la etiqueta de 2 pulgadas (aprox 400 dots)
        sb.append("BAR 50,350,700,1\n")
        sb.append("TEXT 750,365,\"3\",0,1,1,3,\"TOTAL: $${order.price}\"\n")
        
        sb.append("PRINT 1,1\n")
        
        return sb.toString()
    }
}
