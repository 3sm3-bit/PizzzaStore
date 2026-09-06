package com.pizzza.pizzzastore.printer

import com.pizzza.pizzzastore.model.ParentOrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketFormatter {
    /**
     * Genera comandos TSPL para una etiqueta de 100mm x 150mm en formato ECHADO (Landscape)
     */
    fun formatOrderTSPL(order: ParentOrderModel): String {
        val sb = StringBuilder()
        val printDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        
        // Configuración de Etiqueta
        sb.append("SIZE 100 mm, 150 mm\n") 
        sb.append("GAP 3 mm, 0 mm\n")
        sb.append("DIRECTION 1\n") 
        sb.append("CLS\n")
        
        // En modo "Echado", rotamos el texto 90 grados (el cuarto parámetro es 90)
        // X ahora controla la posición vertical en el papel (0-800)
        // Y ahora controla la posición horizontal (0-1200)
        
        // HEADER
        sb.append("TEXT 750,50,\"3\",90,1,1,\"PIZZZA STORE\"\n")
        sb.append("TEXT 700,50,\"2\",90,1,1,\"$printDateTime\"\n")
        sb.append("BAR 670,50,2,1100\n") // Línea larga a lo largo de los 150mm
        
        // DATOS DEL PEDIDO
        sb.append("TEXT 630,50,\"2\",90,1,1,\"PEDIDO: ${order.uid.takeLast(6)}\"\n")
        sb.append("TEXT 590,50,\"2\",90,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        val tipo = if (isDelivery) "DELIVERY" else "RECOJO"
        sb.append("TEXT 550,50,\"2\",90,1,1,\"TIPO: $tipo\"\n")
        
        if (isDelivery && order.address.isNotBlank() && order.address != "null") {
            sb.append("TEXT 510,50,\"1\",90,1,1,\"DIR: ${order.address}\"\n")
        }
        
        sb.append("BAR 480,50,1,1100\n")
        
        // PRODUCTOS (Letra más pequeña para que quepa todo)
        var currentX = 440
        order.orders.forEach { item ->
            sb.append("TEXT $currentX,50,\"2\",90,1,1,\"${item.quantity} x ${item.nameProduct} (${item.tamanio})\"\n")
            currentX -= 40
            
            if (item.note.isNotBlank()) {
                sb.append("TEXT $currentX,80,\"1\",90,1,1,\"Nota: ${item.note}\"\n")
                currentX -= 35
            }
        }
        
        // TOTAL (Ubicado al final de la etiqueta horizontal)
        sb.append("BAR 150,50,1,1100\n")
        sb.append("TEXT 100,800,\"3\",90,1,1,\"TOTAL: $${order.price}\"\n")
        
        // FINALIZAR
        sb.append("PRINT 1,1\n")
        
        return sb.toString()
    }

    fun formatOrder(order: ParentOrderModel): String = formatOrderTSPL(order)
}
