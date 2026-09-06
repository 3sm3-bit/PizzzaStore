package com.pizzza.pizzzastore.printer

import com.pizzza.pizzzastore.model.ParentOrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketFormatter {
    fun formatOrder(order: ParentOrderModel): String {
        val sb = StringBuilder()
        
        val printDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        // Header
        sb.append("[C]<b>PIZZZA STORE</b>\n")
        sb.append("[C]$printDateTime\n")
        sb.append("[C]--------------------------------\n")
        
        // Order Info
        sb.append("[L]Pedido: ${order.uid.takeLast(6)}\n")
        sb.append("[L]Fecha Pedido: ${order.date}\n")
        sb.append("[L]Cliente: ${order.nameClient}\n")
        sb.append("[L]Tel: ${order.phone}\n")
        
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        sb.append("[L]Tipo: ${if (isDelivery) "DELIVERY" else "LOCAL"}\n")
        
        if (isDelivery && order.address.isNotBlank()) {
            sb.append("[L]Direccion: ${order.address}\n")
        }
        
        sb.append("[C]--------------------------------\n")
        
        // Items
        order.orders.forEach { item ->
            sb.append("[L]<b>${item.quantity} x ${item.nameProduct}</b>\n")
            sb.append("[L]  ${item.tamanio} - ${item.typeDough}\n")
            
            if (item.cheeseFilledCrust.trim().uppercase() == "SI") {
                sb.append("[L]  * Orilla de queso (+${item.priceChosse})\n")
            }
            
            if (item.note.isNotBlank()) {
                sb.append("[L]  Nota: ${item.note}\n")
            }
            
            val subtotal = (item.quantity.toDoubleOrNull() ?: 0.0) * (item.price.toDoubleOrNull() ?: 0.0)
            sb.append("[R]$${subtotal.toInt()}\n")
            sb.append("[L]\n")
        }
        
        sb.append("[C]--------------------------------\n")
        
        // Total
        sb.append("[R]<font size='big'>TOTAL: $${order.price}</font>\n")
        
        // Footer
        sb.append("[C]\n")
        sb.append("[C]¡Buen provecho!\n")
        sb.append("[C]Gracias por su preferencia\n")
        sb.append("[C]\n")
        sb.append("[C]\n") // Extra space for cutting
        
        return sb.toString()
    }
}
