package com.darkbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GestionAdapter(private val gestiones: List<HistorialMediaAltaActivity.Gestion>) :
    RecyclerView.Adapter<GestionAdapter.GestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gestion, parent, false) // Asegúrate de que este sea el layout correcto
        return GestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: GestionViewHolder, position: Int) {
        val gestion = gestiones[position]
        holder.bind(gestion)
    }

    override fun getItemCount(): Int = gestiones.size

    class GestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val observacionesTextView: TextView = itemView.findViewById(R.id.gestion_observaciones)
        private val usuarioTextView: TextView = itemView.findViewById(R.id.gestion_usuario)

        fun bind(gestion: HistorialMediaAltaActivity.Gestion) {
            observacionesTextView.text = gestion.observaciones
            usuarioTextView.text = "Usuario: ${gestion.usuario}"
        }
    }
}
