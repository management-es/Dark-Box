package com.darkbox

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(
    private var ticketList: List<SeguimientoActivity.Ticket>, // Cambié a var para permitir la actualización de la lista
    private val onCargarRespuestaClick: (SeguimientoActivity.Ticket) -> Unit // Lambda para manejar el clic
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        val importanciaTextView: TextView = itemView.findViewById(R.id.importanciaTextView)
        val tipoSolicitudTextView: TextView = itemView.findViewById(R.id.tipoSolicitudTextView)
        val usuarioSolicitanteTextView: TextView = itemView.findViewById(R.id.usuarioSolicitanteTextView)
        val destinatariosTextView: TextView = itemView.findViewById(R.id.destinatariosTextView)
        val btnCargarRespuesta: Button = itemView.findViewById(R.id.btnCargarRespuesta)

        init {
            btnCargarRespuesta.setOnClickListener {
                val ticket = ticketList[adapterPosition] // Obtener el ticket correspondiente
                val estadoTicket = estadoTextView.text.toString().substringAfter("Estado: ").trim()
                val importanciaTicket = importanciaTextView.text.toString().substringAfter("Importancia: ").trim()

                // Mostrar un Toast con el estado del ticket
                Toast.makeText(itemView.context, "Estado del ticket: $estadoTicket", Toast.LENGTH_SHORT).show()

                // Manejar la acción de cargar respuesta aquí
                when (estadoTicket) {
                    "Realizado" -> {
                        val ticketId = ticket.ticketId
                        if (ticketId != null) {
                            val context = itemView.context

                            // Verificar la importancia del ticket
                            when (importanciaTicket) {
                                "Baja" -> {
                                    val intent = Intent(context, RespuestaBajaActivity::class.java)
                                    intent.putExtra("TICKET_ID", ticketId) // Pasar el Ticket ID a la nueva actividad
                                    context.startActivity(intent)
                                }
                                "Media", "Alta" -> {
                                    val intent = Intent(context, RespuestaMediaAltaActivity::class.java)
                                    intent.putExtra("TICKET_ID", ticketId) // Pasar el Ticket ID a la nueva actividad
                                    context.startActivity(intent)
                                }
                                else -> {
                                    Toast.makeText(context, "Importancia no reconocida", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(itemView.context, "El Ticket ID es nulo", Toast.LENGTH_SHORT).show()
                        }
                    }

                    "Pendiente" -> {
                        // Mostrar AlertDialog
                        AlertDialog.Builder(itemView.context)
                            .setTitle("Estado de la Solicitud")
                            .setMessage("Esta solicitud aún está pendiente por respuesta.")
                            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                            .setCancelable(true)
                            .show()
                    }

                    else -> {
                        Toast.makeText(itemView.context, "No se puede cargar respuesta para este estado del ticket.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticketseguimiento_adapter, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        holder.descripcionTextView.text = "Descripción: ${ticket.descripcionint}"
        holder.estadoTextView.text = "Estado: ${ticket.estado}"
        holder.importanciaTextView.text = "Importancia: ${ticket.importancia}"
        holder.tipoSolicitudTextView.text = "Tipo de Solicitud: ${ticket.tipoSolicitud}"
        holder.usuarioSolicitanteTextView.text = "Usuario Solicitante: ${ticket.usuarioSolicitante}"
        holder.destinatariosTextView.text = "Destinatarios: ${ticket.destinatarios.joinToString(", ")}"
        holder.itemView.findViewById<TextView>(R.id.ticketIdTextView).text = "Ticket ID: ${ticket.ticketId}"
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }

    // Método para actualizar la lista de tickets y notificar al RecyclerView
    fun updateTickets(newTickets: List<SeguimientoActivity.Ticket>) {
        ticketList = newTickets  // Actualiza la lista de tickets
        notifyDataSetChanged()  // Notifica que los datos han cambiado
    }
}
