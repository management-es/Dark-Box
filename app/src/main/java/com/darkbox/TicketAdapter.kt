package com.darkbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(
    private val ticketList: List<SeguimientoActivity.Ticket>,
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
                val estadoTicket = estadoTextView.text.toString() // Obtener el estado del TextView

                // Mostrar un Toast con el estado del ticket
                Toast.makeText(itemView.context, "Estado del ticket: $estadoTicket", Toast.LENGTH_SHORT).show()

                // Manejar la acción de cargar respuesta aquí
                when (estadoTicket) {
                    "Realizado" -> {
                        // Aquí puedes implementar la lógica para cargar la respuesta
                        Toast.makeText(itemView.context, "Cargar respuesta para el ticket: ${ticket.descripcionint}", Toast.LENGTH_SHORT).show()
                        // Aquí puedes agregar la lógica para cargar la respuesta...
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
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }
}

