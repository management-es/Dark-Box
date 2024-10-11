package com.darkbox

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RespuestaMediaAltaActivity : AppCompatActivity() {

    private lateinit var ticketIdTextView: TextView
    private lateinit var gestionTextView: TextView
    private var ticketId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respuesta_media_alta)

        // Inicializar las vistas
        ticketIdTextView = findViewById(R.id.ticketIdTextView)
        gestionTextView = findViewById(R.id.gestionTextView)

        // Obtener el Ticket ID pasado por el Intent
        ticketId = intent.getStringExtra("TICKET_ID")

        if (ticketId != null) {
            ticketIdTextView.text = "Ticket ID: $ticketId"

            // Llamar a la función para buscar gestiones en Firebase
            buscarGestionesEnFirebase(ticketId!!)
        } else {
            // Mostrar mensaje de error si no hay Ticket ID
            Toast.makeText(this, "Error: No se proporcionó un Ticket ID.", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad si no hay un Ticket ID válido
        }

        // Configuración del botón Finalizar
        val btnFinalizar = findViewById<android.widget.Button>(R.id.btnFinalizar)
        btnFinalizar.setOnClickListener {
            finish() // Cierra la actividad y regresa a la pantalla anterior
        }
    }

    // Función para buscar gestiones en Firebase
    private fun buscarGestionesEnFirebase(ticketId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val respuestaRef = databaseReference.child("respuestas").child("${ticketId}-Resp") // Asegúrate de agregar "-Resp"

        respuestaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val gestiones = StringBuilder()
                    var i = 0

                    // Validar si existen los subnodos de gestiones
                    while (dataSnapshot.child("gestion$i").exists()) {
                        val gestionSnapshot = dataSnapshot.child("gestion$i")
                        val observaciones = gestionSnapshot.child("observaciones").getValue(String::class.java) ?: "No hay observaciones"
                        val usuario = gestionSnapshot.child("usuario").getValue(String::class.java) ?: "Usuario desconocido"

                        // Formatear la cadena para incluir observaciones y usuario
                        gestiones.append("Gestión $i:\n")
                        gestiones.append("Observaciones: $observaciones\n")
                        gestiones.append("Usuario: $usuario\n\n") // Añadir doble salto de línea para espaciado

                        i++
                    }

                    // Si no se encontraron gestiones, mostrar un mensaje adecuado
                    if (gestiones.isEmpty()) {
                        gestionTextView.text = "No se encontraron gestiones para este ticket."
                    } else {
                        // Mostrar las gestiones en el TextView
                        gestionTextView.text = gestiones.toString().trim() // Eliminar espacios innecesarios al final
                    }
                } else {
                    // Mostrar error si no se encuentran gestiones
                    Toast.makeText(this@RespuestaMediaAltaActivity, "Error: No se encontraron gestiones para este ticket.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Mostrar error si falla la consulta a Firebase
                val errorMessage = "Error al obtener las gestiones: ${databaseError.message}"
                Toast.makeText(this@RespuestaMediaAltaActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

}
