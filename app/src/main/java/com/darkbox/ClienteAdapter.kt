package com.darkbox

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
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
            txtCodCliente.text = formatText("Cod Cliente: ", cliente.cod_cliente)
            txtNumeroDocumento.text = formatText("Número Documento: ", cliente.numero_documento)
            txtNombres.text = formatText("Nombres: ", cliente.nombres)
            txtApellidos.text = formatText("Apellidos: ", cliente.apellidos)
            txtContactos.text = formatText("Contactos: ", cliente.contactos)
            txtCoordenadas.text = formatText("Coordenadas: ", cliente.coordenadas)
            txtCorreo.text = formatText("Correo: ", cliente.correo)
            txtDireccion.text = formatText("Dirección: ", cliente.direccion)
            txtEquipos.text = formatText("Equipos: ", cliente.equipos)
            txtHistorial.text = formatText("Historial: ", cliente.historial)
            txtIpAntena.text = formatText("IP Antena: ", cliente.ip_antena)
            txtIpRemota.text = formatText("IP Remota: ", cliente.ip_remota)
            txtObservaciones.text = formatText("Observaciones: ", cliente.observaciones)
            txtPlan.text = formatText("Plan: ", cliente.plan)
            txtSerialOnu.text = formatText("Serial ONU: ", cliente.serial_onu)
            txtSerialRouter.text = formatText("Serial Router: ", cliente.serial_router)
            txtSerialAntena.text = formatText("Serial Antena: ", cliente.serial_antena)
            txtTecnologia.text = formatText("Tecnología: ", cliente.tecnologia)
            txtTelefonos.text = formatText("Teléfonos: ", cliente.telefono)
            txtTipoDocumento.text = formatText("Tipo Documento: ", cliente.tipo_documento)
            txtZona.text = formatText("Zona: ", cliente.zona)
        }

        private fun formatText(label: String, value: String?): SpannableString {
            val spannableString = SpannableString("$label${value ?: "N/A"}")
            val labelEnd = label.length
            spannableString.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                0,
                labelEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
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
