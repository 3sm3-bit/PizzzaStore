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
        // Definimos un tamaño de 100mm de ancho por 150mm de largo para pedidos largos
        sb.append("SIZE 100 mm, 150 mm\n") 
        sb.append("GAP 3 mm, 0 mm\n")
        // DIRECTION 0,1 rotará el contenido para que salga en vertical
        sb.append("DIRECTION 0,1\n") 
        sb.append("CLS\n")
        
        // HEADER - Ajustamos coordenadas para modo vertical (X ahora es el eje corto)
        sb.append("TEXT 400,50,\"4\",0,3,3,2,\"♥\"\n")
        sb.append("TEXT 400,110,\"2\",0,1,1,2,\"$printDateTime\"\n")
        sb.append("BAR 50,140,700,3\n")
        
        // DATOS DEL PEDIDO
        sb.append("TEXT 50,170,\"3\",0,1,1,\"PEDIDO: ${order.uid.takeLast(6)}\"\n")
        sb.append("TEXT 50,210,\"3\",0,1,1,\"CLIENTE: ${order.nameClient}\"\n")
        
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")
        sb.append("TEXT 50,250,\"3\",0,1,1,\"TIPO: ${if (isDelivery) "DELIVERY" else "RECOJO"}\"\n")
        
        if (isDelivery && order.address.isNotBlank()) {
            // El texto ahora puede ser un poco más largo
            sb.append("TEXT 50,290,\"2\",0,1,1,\"DIR: ${order.address.take(50)}\"\n")
        }
        
        sb.append("BAR 50,330,700,2\n")
        
        // PRODUCTOS
        var currentY = 360
        order.orders.forEach { item ->
            sb.append("TEXT 50,$currentY,\"3\",0,1,1,\"${item.quantity} x ${item.nameProduct}\"\n")
            currentY += 45
            
            if (item.note.isNotBlank()) {
                sb.append("TEXT 80,$currentY,\"2\",0,1,1,\"Nota: ${item.note.take(45)}\"\n")
                currentY += 40
            }
            currentY += 10
        }
        
        // TOTAL - Ubicado más abajo según la cantidad de productos
        sb.append("BAR 50,$currentY,700,2\n")
        currentY += 40
        sb.append("TEXT 750,$currentY,\"4\",0,1,1,3,\"TOTAL: $${order.price}\"\n")
        
        // FOOTER
        currentY += 60
        sb.append("TEXT 400,$currentY,\"3\",0,1,1,2,\"¡Hecho con ♥!\"\n")
        
        // FINALIZAR
        sb.append("PRINT 1,1\n")
        
        return sb.toString()
    }

    // Mantenemos el anterior por si acaso, pero usaremos el TSPL
    fun formatOrder(order: ParentOrderModel): String = formatOrderTSPL(order)
}
