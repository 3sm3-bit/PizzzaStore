package com.pizzza.pizzzastore.printer

import com.pizzza.pizzzastore.model.ParentOrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketFormatter {
    /**
     * Versión DEFINITIVA: Modo Etiqueta (TSPL)
     * - Formato Horizontal
     * - Datos (Cliente, Dir, Prod, Total) en fuente "2" (un poco más grande)
     * - Nombre negocio y fecha en fuente "1"
     * - Total visible (X=50)
     */
    fun formatOrder(order: ParentOrderModel): String {
        val sb = StringBuilder()
        val printDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        
        // 1. Tamaño 4x2 pulgadas
        sb.append("SIZE 101.6 mm, 50.8 mm\n") 
        sb.append("GAP 3 mm, 0 mm\n")
        sb.append("DIRECTION 1\n") 
        sb.append("CLS\n")
        
        // 2. HEADER - Se quedan en fuente "1"
        sb.append("TEXT 400,0,\"1\",0,1,1,2,\"PIZZZA STORE\"\n")
        sb.append("TEXT 400,20,\"1\",0,1,1,2,\"$printDateTime\"\n")
        
        // 3. DATOS - Subimos a fuente "2" y ajustamos espaciado a 35 dots
        sb.append("TEXT 50,55,\"2\",0,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        var currentY = 90
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        if (isDelivery && order.address.isNotBlank() && order.address != "null") {
            sb.append("TEXT 50,$currentY,\"2\",0,1,1,\"DIR: ${order.address}\"\n")
            currentY += 35
        }
        
        // 4. PRODUCTOS - Fuente "2" y espaciado de 35 dots
        order.orders.forEach { item ->
            sb.append("TEXT 50,$currentY,\"2\",0,1,1,\"${item.quantity}x ${item.nameProduct}\"\n")
            currentY += 35
            if (item.note.isNotBlank()) {
                sb.append("TEXT 50,$currentY,\"2\",0,1,1,\"Nota: ${item.note}\"\n")
                currentY += 35
            }
        }
        
        // 5. TOTAL - Fuente "2" y movido a la izquierda (X=50) para que sea visible
        sb.append("TEXT 50,$currentY,\"2\",0,1,1,\"TOTAL: $${order.price}\"\n")
        
        sb.append("PRINT 1,1\n")
        
        return sb.toString()
    }
}
