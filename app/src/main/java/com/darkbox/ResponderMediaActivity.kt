package com.darkbox

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ResponderMediaActivity : Activity() {

    private lateinit var descriptionTextView: TextView
    private lateinit var observacionesEditText: EditText
    private lateinit var usuarioTextView: TextView
    private lateinit var cerrarTicketButton: Button  // Agregar referencia para el botón
    private lateinit var nombreUsuario: String
    private lateinit var ticketId: String // Agregar referencia para el Ticket ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_media)

        // Inicializar los componentes del layout
        descriptionTextView = findViewById(R.id.descriptionTextView)
        observacionesEditText = findViewById(R.id.observacionesEditText)
        usuarioTextView = findViewById(R.id.usuarioTextView)
        cerrarTicketButton = findViewById(R.id.cerrarTicketButton) // Inicializar botón de cerrar ticket
        val enviarRespuestaButton = findViewById<Button>(R.id.enviarRespuestaButton)

        // Obtener el nombre de usuario desde el Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

        // Obtener el ID del ticket desde el Intent
        ticketId = intent.getStringExtra("TICKET_ID") ?: ""

        // Mostrar el nombre del usuario en pantalla
        usuarioTextView.text = "Usuario: $nombreUsuario"

        // Referencia a la base de datos para tickets
        val databaseTickets = FirebaseDatabase.getInstance().getReference("tickets")

        // Mostrar la descripción del ticket
        if (ticketId.isNotEmpty()) {
            databaseTickets.child(ticketId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ticket = dataSnapshot.getValue(Ticket::class.java)
                    if (ticket != null) {
                        descriptionTextView.text = """
                            Ticket ID: $ticketId
                            Importancia: ${ticket.importancia}
                            Tipo de Solicitud: ${ticket.tipoSolicitud}
                            Descripción: ${ticket.descripcionint}
                        """.trimIndent()

                        // Validar si el usuario es el solicitante
                        val usuarioSolicitante = dataSnapshot.child("usuarioSolicitante").getValue(String::class.java)
                        if (usuarioSolicitante != nombreUsuario) {
                            cerrarTicketButton.isEnabled = false
                            cerrarTicketButton.alpha = 0.5f // Cambiar la opacidad del botón para indicar que está deshabilitado

                            // Crear el AlertDialog
                            AlertDialog.Builder(this@ResponderMediaActivity)
                                .setTitle("Permiso restringido")
                                .setMessage("Este ticket únicamente puede ser cerrado por el usuario solicitante.")
                                .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                    } else {
                        descriptionTextView.text = "Información del ticket no disponible."
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ResponderMediaActivity, "Error al consultar el ticket: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Funcionalidad del botón cerrarTicketButton
        cerrarTicketButton.setOnClickListener {
            // Crear el AlertDialog
            AlertDialog.Builder(this)
                .setTitle("Cerrar Ticket")
                .setMessage("Al cerrar este ticket no podrás volver a activarlo, por favor revisar detenidamente el historial y las respuestas a la solicitud.")
                .setPositiveButton("Cerrar Ticket") { dialog, which ->
                    // Actualizar el estado del ticket a "Realizado"
                    databaseTickets.child(ticketId).child("estado").setValue("Realizado")
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Ticket cerrado y marcado como realizado.", Toast.LENGTH_SHORT).show()
                                finish() // Cerrar la actividad actual
                            } else {
                                Toast.makeText(this, "Error al cerrar el ticket: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    dialog.dismiss() // Cerrar el diálogo si se cancela
                }
                .show() // Mostrar el AlertDialog
        }


        // Funcionalidad del botón enviarRespuestaButton
        enviarRespuestaButton.setOnClickListener {
            val respuestaId = "$ticketId-Resp"
            val observaciones = observacionesEditText.text.toString().trim()

            if (observaciones.isNotEmpty()) {
                FirebaseDatabase.getInstance().getReference("respuestas").child(respuestaId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val gestionCount = dataSnapshot.childrenCount.toInt()
                            val nuevaGestionKey = "gestion$gestionCount"

                            // Datos a guardar, incluyendo las observaciones y el nombre del usuario que responde
                            val respuestaData = HashMap<String, Any>()
                            respuestaData[nuevaGestionKey] = mapOf(
                                "observaciones" to observaciones,
                                "usuario" to nombreUsuario  // Guardar el nombre del usuario que responde
                            )

                            FirebaseDatabase.getInstance().getReference("respuestas").child(respuestaId)
                                .updateChildren(respuestaData)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        AlertDialog.Builder(this@ResponderMediaActivity)
                                            .setTitle("Respuesta enviada")
                                            .setMessage("La respuesta ha sido enviada exitosamente como $nuevaGestionKey.")
                                            .setPositiveButton("Aceptar") { dialog, _ ->
                                                finish() // Cerrar la actividad actual
                                            }
                                            .show()
                                    } else {
                                        Toast.makeText(this@ResponderMediaActivity, "Error al guardar respuesta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@ResponderMediaActivity, "Error al verificar respuesta: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Por favor, ingrese sus observaciones.", Toast.LENGTH_SHORT).show()
            }
        }

        // Funcionalidad del botón Historial
        val botonHistorial: Button = findViewById(R.id.button_historial)
        botonHistorial.setOnClickListener {
            val intent = Intent(this, HistorialMediaAltaActivity::class.java)
            ticketId.let {
                intent.putExtra("TICKET_ID", it)
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Ticket ID no disponible.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Modelo de Ticket
    data class Ticket(
        val descripcionint: String? = null,
        val tipoSolicitud: String? = null,
        val importancia: String? = null,
        val usuarioSolicitante: String? = null // Agregar la propiedad usuarioSolicitante
    )
}
