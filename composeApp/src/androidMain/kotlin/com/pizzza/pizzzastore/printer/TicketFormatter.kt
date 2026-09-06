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
        
        // Comandos Base TSPL
        sb.append("SIZE 100 mm, 100 mm\n") 
        sb.append("GAP 3 mm, 0 mm\n")
        sb.append("DIRECTION 1\n") 
        sb.append("CLS\n")
        
        // HEADER - PIZZZA STORE
        sb.append("TEXT 400,30,\"3\",0,1,1,2,\"PIZZZA STORE\"\n")
        sb.append("TEXT 400,75,\"1\",0,1,1,2,\"$printDateTime\"\n")
        sb.append("BAR 50,105,700,2\n")
        
        // CLIENTE
        sb.append("TEXT 50,130,\"2\",0,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        // DIRECCIÓN (Condicional)
        var currentY = 170
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        if (isDelivery && order.address.isNotBlank() && order.address != "null") {
            sb.append("TEXT 50,$currentY,\"1\",0,1,1,\"DIR: ${order.address}\"\n")
            currentY += 40
        }
        
        sb.append("BAR 50,$currentY,700,1\n")
        currentY += 30
        
        // PRODUCTOS (Letra más pequeña para asegurar que todo quepa)
        order.orders.forEach { item ->
            sb.append("TEXT 50,$currentY,\"2\",0,1,1,\"${item.quantity} x ${item.nameProduct}\"\n")
            currentY += 35
            
            if (item.note.isNotBlank()) {
                sb.append("TEXT 80,$currentY,\"1\",0,1,1,\"Nota: ${item.note}\"\n")
                currentY += 30
            }
        }
        
        // TOTAL
        currentY += 10
        sb.append("BAR 50,$currentY,700,1\n")
        currentY += 25
        sb.append("TEXT 750,$currentY,\"3\",0,1,1,3,\"TOTAL: $${order.price}\"\n")
        
        // IMPRIMIR
        sb.append("PRINT 1,1\n")
        
        return sb.toString()
    }
}
