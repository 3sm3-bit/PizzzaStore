package com.pizzza.pizzzastore.printer

import com.pizzza.pizzzastore.model.ParentOrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketFormatter {
    fun formatOrder(order: ParentOrderModel): String {
        val sb = StringBuilder()
        val printDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // Header
        sb.append("[C]<b>PIZZZA STORE</b>\n")
        sb.append("[C]<font size='small'>$printDateTime</font>\n")
        
        // Order Info (ID removido por solicitud)
        sb.append("[L]<font size='small'>Cliente: ${order.nameClient}</font>\n")
        
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        
        // Solo mostramos la dirección si es DELIVERY
        if (isDelivery && order.address.isNotBlank() && order.address != "null") {
            sb.append("[L]<font size='small'>Direccion: ${order.address}</font>\n")
        }
        
        sb.append("[C]--------------------------------\n")
        
        // Items - Letra más pequeña para que quepa todo
        order.orders.forEach { item ->
            sb.append("[L]<font size='small'><b>${item.quantity} x ${item.nameProduct}</b></font>\n")
            sb.append("[L]<font size='small'>  ${item.tamanio} - ${item.typeDough}</font>\n")
            
            if (item.note.isNotBlank()) {
                sb.append("[L]<font size='small'>  Nota: ${item.note}</font>\n")
            }
            
            val subtotal = (item.quantity.toDoubleOrNull() ?: 0.0) * (item.price.toDoubleOrNull() ?: 0.0)
            sb.append("[R]<font size='small'>$${subtotal.toInt()}</font>\n")
        }
        
        sb.append("[C]--------------------------------\n")
        
        // Total - Negrita pero tamaño normal para que destaque
        sb.append("[R]<b>TOTAL: $${order.price}</b>\n")
        
        // Footer
        sb.append("[C]\n")
        sb.append("[C]<font size='small'>¡Buen provecho!</font>\n")
        sb.append("[C]\n\n\n") 
        
        return sb.toString()
    }
}
