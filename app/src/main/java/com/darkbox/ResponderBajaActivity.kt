package com.darkbox

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResponderBajaActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_baja)

        // Obtener el ID del ticket desde el Intent
        val ticketId = intent.getStringExtra("TICKET_ID")

        // Obtener los TextView del layout
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val clienteInfoTextView = findViewById<TextView>(R.id.clienteInfoTextView)
        val codClienteEditText = findViewById<EditText>(R.id.codClienteEditText)
        val buscarButton = findViewById<Button>(R.id.buscarButton)

        // Referencias a la base de datos
        val databaseTickets = FirebaseDatabase.getInstance().getReference("tickets")
        val databaseClientes = FirebaseDatabase.getInstance().getReference("clientes")

        // Mostrar la descripción del ticket
        if (ticketId != null) {
            databaseTickets.child(ticketId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ticket = dataSnapshot.getValue(Ticket::class.java)
                    if (ticket != null) {
                        descriptionTextView.text = """
                            Importancia: ${ticket.importancia}
                            Tipo de Solicitud: ${ticket.tipoSolicitud}
                            Descripción: ${ticket.descripcion}
                        """.trimIndent()
                    } else {
                        descriptionTextView.text = "Información del ticket no disponible."
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ResponderBajaActivity, "Error al consultar el ticket: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Buscar cliente por cod_cliente
        buscarButton.setOnClickListener {
            val codCliente = codClienteEditText.text.toString().trim()

            if (codCliente.isNotEmpty()) {
                Log.d("ResponderBajaActivity", "Buscando cliente con código: $codCliente")
                databaseClientes.orderByChild("cod_cliente").equalTo(codCliente)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.d("ResponderBajaActivity", "Cliente encontrado")
                                for (clienteSnapshot in dataSnapshot.children) {
                                    val cliente = clienteSnapshot.getValue(Cliente::class.java)
                                    if (cliente != null) {
                                        clienteInfoTextView.text = """
                                            Código de Cliente: ${cliente.cod_cliente}
                                            Nombres: ${cliente.nombres}
                                            Apellidos: ${cliente.apellidos}
                                            Número de Documento: ${cliente.numero_documento}
                                            Tipo de Documento: ${cliente.tipo_documento}
                                            Dirección: ${cliente.direccion}
                                            Teléfono: ${cliente.telefono}
                                            Zona: ${cliente.zona}
                                            Tecnología: ${cliente.tecnologia}
                                            Serial Antena: ${cliente.serial_antena}
                                            Serial ONU: ${cliente.serial_onu}
                                            Serial Router: ${cliente.serial_router}
                                            IP Antena: ${cliente.ip_antena}
                                            IP Remota: ${cliente.ip_remota}
                                        """.trimIndent()
                                    } else {
                                        clienteInfoTextView.text = "Datos del cliente no disponibles."
                                    }
                                }
                            } else {
                                Log.d("ResponderBajaActivity", "Cliente no encontrado")
                                clienteInfoTextView.text = "Cliente no encontrado."
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@ResponderBajaActivity, "Error al buscar cliente: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Por favor, ingrese el código del cliente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class Cliente(
        val cod_cliente: String? = null,
        val nombres: String? = null,
        val apellidos: String? = null,
        val numero_documento: String? = null,
        val tipo_documento: String? = null,
        val direccion: String? = null,
        val telefono: String? = null,
        val zona: String? = null,
        val tecnologia: String? = null,
        val serial_antena: String? = null,
        val serial_onu: String? = null,
        val serial_router: String? = null,
        val ip_antena: String? = null,
        val ip_remota: String? = null
    )

    // Clase modelo para los datos del ticket
    data class Ticket(
        val descripcion: String? = null,
        val tipoSolicitud: String? = null,
        val importancia: String? = null
    )
}
