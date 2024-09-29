package com.darkbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(private val tickets: List<SeguimientoActivity.Ticket>) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        val importanciaTextView: TextView = itemView.findViewById(R.id.importanciaTextView)
        val tipoSolicitudTextView: TextView = itemView.findViewById(R.id.tipoSolicitudTextView)
        val usuarioSolicitanteTextView: TextView = itemView.findViewById(R.id.usuarioSolicitanteTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticketseguimiento_adapter, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.descripcionTextView.text = "Descripción: ${ticket.descripcionint}"
        holder.estadoTextView.text = "Estado: ${ticket.estado}"
        holder.importanciaTextView.text = "Importancia: ${ticket.importancia}"
        holder.tipoSolicitudTextView.text = "Tipo de Solicitud: ${ticket.tipoSolicitud}"
        holder.usuarioSolicitanteTextView.text = "Usuario Solicitante: ${ticket.usuarioSolicitante}"
    }

    override fun getItemCount(): Int {
        return tickets.size
    }
}
