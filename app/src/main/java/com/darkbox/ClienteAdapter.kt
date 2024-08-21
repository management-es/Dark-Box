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
        private val txtContactos: TextView = itemView.findViewById(R.id.txtContactos)
        private val txtCoordenadas: TextView = itemView.findViewById(R.id.txtCoordenadas)
        private val txtCorreo: TextView = itemView.findViewById(R.id.txtCorreo)
        private val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        private val txtEquipos: TextView = itemView.findViewById(R.id.txtEquipos)
        private val txtHistorial: TextView = itemView.findViewById(R.id.txtHistorial)
        private val txtIpAntena: TextView = itemView.findViewById(R.id.txtIpAntena)
        private val txtIpRemota: TextView = itemView.findViewById(R.id.txtIpRemota)
        private val txtObservaciones: TextView = itemView.findViewById(R.id.txtObservaciones)
        private val txtPlan: TextView = itemView.findViewById(R.id.txtPlan)
        private val txtSerialOnu: TextView = itemView.findViewById(R.id.txtSerialOnu)
        private val txtSerialRouter: TextView = itemView.findViewById(R.id.txtSerialRouter)
        private val txtSerialAntena: TextView = itemView.findViewById(R.id.txtSerialAntena)
        private val txtTecnologia: TextView = itemView.findViewById(R.id.txtTecnologia)
        private val txtTelefonos: TextView = itemView.findViewById(R.id.txtTelefonos)
        private val txtTipoDocumento: TextView = itemView.findViewById(R.id.txtTipoDocumento)
        private val txtZona: TextView = itemView.findViewById(R.id.txtZona)

        fun bind(cliente: Cliente) {
            txtCodCliente.text = "Cod Cliente: ${cliente.cod_cliente ?: "N/A"}"
            txtNumeroDocumento.text = "Número Documento: ${cliente.numero_documento ?: "N/A"}"
            txtNombres.text = "Nombres: ${cliente.nombres ?: "N/A"}"
            txtApellidos.text = "Apellidos: ${cliente.apellidos ?: "N/A"}"
            txtContactos.text = "Contactos: ${cliente.contactos ?: "N/A"}"
            txtCoordenadas.text = "Coordenadas: ${cliente.coordenadas ?: "N/A"}"
            txtCorreo.text = "Correo: ${cliente.correo ?: "N/A"}"
            txtDireccion.text = "Dirección: ${cliente.direccion ?: "N/A"}"
            txtEquipos.text = "Equipos: ${cliente.equipos ?: "N/A"}"
            txtHistorial.text = "Historial: ${cliente.historial ?: "N/A"}"
            txtIpAntena.text = "IP Antena: ${cliente.ip_antena ?: "N/A"}"
            txtIpRemota.text = "IP Remota: ${cliente.ip_remota ?: "N/A"}"
            txtObservaciones.text = "Observaciones: ${cliente.observaciones ?: "N/A"}"
            txtPlan.text = "Plan: ${cliente.plan ?: "N/A"}"
            txtSerialOnu.text = "Serial ONU: ${cliente.serial_onu ?: "N/A"}"
            txtSerialRouter.text = "Serial Router: ${cliente.serial_router ?: "N/A"}"
            txtSerialAntena.text = "Serial Antena: ${cliente.serial_antena ?: "N/A"}"
            txtTecnologia.text = "Tecnología: ${cliente.tecnologia ?: "N/A"}"
            txtTelefonos.text = "Teléfono: ${cliente.telefono ?: "N/A"}"
            txtTipoDocumento.text = "Tipo Documento: ${cliente.tipo_documento ?: "N/A"}"
            txtZona.text = "Zona: ${cliente.zona ?: "N/A"}"
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


