package com.pizzza.pizzzastore.printer

import com.pizzza.pizzzastore.model.ParentOrderModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketFormatter {
    /**
     * Genera comandos TSPL para una etiqueta de 100mm x 150mm (estándar de este modelo)
     */
    fun formatOrderTSPL(order: ParentOrderModel): String {
        val sb = StringBuilder()
        val printDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        
        // Comandos Iniciales TSPL
        sb.append("SIZE 100 mm, 100 mm\n") // Ajustamos a etiqueta de 100x100mm
        sb.append("GAP 3 mm, 0 mm\n")      // Espacio entre etiquetas
        sb.append("DIRECTION 1\n")          // Orientación normal
        sb.append("CLS\n")                  // Limpiar memoria de la impresora
        
        // HEADER - Centrado (Aprox X=400 para papel de 100mm/800dots)
        sb.append("TEXT 400,30,\"4\",0,1,1,2,\"PIZZZA STORE\"\n")
        sb.append("TEXT 400,80,\"2\",0,1,1,2,\"$printDateTime\"\n")
        sb.append("BAR 50,120,700,3\n") // Línea divisoria
        
        // DATOS DEL PEDIDO
        sb.append("TEXT 50,150,\"3\",0,1,1,\"PEDIDO: ${order.uid.takeLast(6)}\"\n")
        sb.append("TEXT 50,190,\"3\",0,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        sb.append("TEXT 50,230,\"3\",0,1,1,\"TIPO: ${if (isDelivery) "DELIVERY" else "RECOJO"}\"\n")
        
        if (isDelivery && order.address.isNotBlank()) {
            sb.append("TEXT 50,270,\"2\",0,1,1,\"DIR: ${order.address.take(40)}\"\n")
        }
        
        sb.append("BAR 50,310,700,2\n")
        
        // PRODUCTOS (Manejamos el eje Y dinámicamente)
        var currentY = 340
        order.orders.forEach { item ->
            sb.append("TEXT 50,$currentY,\"3\",0,1,1,\"${item.quantity} x ${item.nameProduct}\"\n")
            currentY += 40
            
            if (item.note.isNotBlank()) {
                sb.append("TEXT 80,$currentY,\"2\",0,1,1,\"Nota: ${item.note.take(35)}\"\n")
                currentY += 35
            }
            currentY += 10
        }
        
        // TOTAL
        sb.append("BAR 50,$currentY,700,2\n")
        currentY += 30
        sb.append("TEXT 750,$currentY,\"4\",0,1,1,3,\"TOTAL: $${order.price}\"\n")
        
        // FINALIZAR
        sb.append("PRINT 1,1\n") // Imprimir 1 copia
        
        return sb.toString()
    }

    // Mantenemos el anterior por si acaso, pero usaremos el TSPL
    fun formatOrder(order: ParentOrderModel): String = formatOrderTSPL(order)
}
