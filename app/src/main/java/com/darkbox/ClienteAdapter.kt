package com.darkbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ClienteAdapter : ListAdapter<Cliente, ClienteAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = getItem(position)
        holder.bind(cliente)
    }

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCodCliente: TextView = itemView.findViewById(R.id.txtCodCliente)
        private val txtNumeroDocumento: TextView = itemView.findViewById(R.id.txtNumeroDocumento)
        private val txtNombres: TextView = itemView.findViewById(R.id.txtNombres)
        private val txtApellidos: TextView = itemView.findViewById(R.id.txtApellidos)

        fun bind(cliente: Cliente) {
            txtCodCliente.text = cliente.cod_cliente
            txtNumeroDocumento.text = cliente.numero_documento
            txtNombres.text = cliente.nombres
            txtApellidos.text = cliente.apellidos
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem.numero_documento == newItem.numero_documento
        }

        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem == newItem
        }
    }
}
