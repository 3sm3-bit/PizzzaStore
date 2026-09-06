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
        
        // 1. Tamaño 4x2 pulgadas
        sb.append("SIZE 101.6 mm, 50.8 mm\n") 
        sb.append("GAP 3 mm, 0 mm\n")
        sb.append("DIRECTION 1\n") 
        sb.append("CLS\n")
        
        // 2. Todo con fuente "1" (la más pequeña) y sin líneas divisoras (BAR)
        // PIZZZA STORE pegado al borde Y=0
        sb.append("TEXT 400,0,\"1\",0,1,1,2,\"PIZZZA STORE\"\n")
        sb.append("TEXT 400,20,\"1\",0,1,1,2,\"$printDateTime\"\n")
        
        // 3. Datos pegados entre sí
        sb.append("TEXT 50,45,\"1\",0,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        var currentY = 65
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        if (isDelivery && order.address.isNotBlank() && order.address != "null") {
            sb.append("TEXT 50,$currentY,\"1\",0,1,1,\"DIR: ${order.address}\"\n")
            currentY += 20
        }
        
        // 4. Productos con espaciado mínimo
        order.orders.forEach { item ->
            sb.append("TEXT 50,$currentY,\"1\",0,1,1,\"${item.quantity}x ${item.nameProduct}\"\n")
            currentY += 20
            if (item.note.isNotBlank()) {
                sb.append("TEXT 80,$currentY,\"1\",0,1,1,\"Nota: ${item.note}\"\n")
                currentY += 20
            }
        }
        
        // 5. Total al final, también letra pequeña
        sb.append("TEXT 750,$currentY,\"1\",0,1,1,3,\"TOTAL: $${order.price}\"\n")
        
        sb.append("PRINT 1,1\n")
        
        return sb.toString()
    }
}
