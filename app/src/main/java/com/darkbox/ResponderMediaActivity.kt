package com.darkbox

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ResponderMediaActivity : Activity() {

    private lateinit var descriptionTextView: TextView
    private lateinit var observacionesEditText: EditText
    private lateinit var usuarioTextView: TextView
    private lateinit var nombreUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_media)

        // Inicializar los componentes del layout
        descriptionTextView = findViewById(R.id.descriptionTextView)
        observacionesEditText = findViewById(R.id.observacionesEditText)
        usuarioTextView = findViewById(R.id.usuarioTextView)
        val enviarRespuestaButton = findViewById<Button>(R.id.enviarRespuestaButton)

        // Obtener el nombre de usuario desde el Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

        // Mostrar el nombre del usuario en pantalla
        usuarioTextView.text = "Usuario: $nombreUsuario"

        // Obtener el ID del ticket desde el Intent
        val ticketId = intent.getStringExtra("TICKET_ID")

        // Referencia a la base de datos para tickets
        val databaseTickets = FirebaseDatabase.getInstance().getReference("tickets")

        // Mostrar la descripción del ticket
        if (ticketId != null) {
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
                    } else {
                        descriptionTextView.text = "Información del ticket no disponible."
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ResponderMediaActivity, "Error al consultar el ticket: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
    }

    // Modelo de Ticket
    data class Ticket(
        val descripcionint: String? = null,
        val tipoSolicitud: String? = null,
        val importancia: String? = null
    )
}
