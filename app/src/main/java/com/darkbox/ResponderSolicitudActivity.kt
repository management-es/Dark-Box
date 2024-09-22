package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.database.*

class ResponderSolicitudActivity : AppCompatActivity() {

    private lateinit var nombreUsuario: String
    private lateinit var database: DatabaseReference
    private lateinit var ticketContainer: LinearLayout

    // Mapa para definir el orden de importancia
    private val importanciaOrder = mapOf(
        "Alta" to 1,
        "Media" to 2,
        "Baja" to 3
    )

    // Mapa para definir los drawables de borde de importancia
    private val importanciaBorders = mapOf(
        "Alta" to R.drawable.border_rojo,
        "Media" to R.drawable.border_naranja,
        "Baja" to R.drawable.border_azul
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_solicitud)

        // Obtener el nombre de usuario del Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

        // Encontrar los TextView y contenedor de tickets en el layout
        val tvUsuarioSolicitante = findViewById<TextView>(R.id.tvUsuarioSolicitante)
        ticketContainer = findViewById(R.id.ticketContainer)

        // Mostrar el nombre del usuario en el TextView
        tvUsuarioSolicitante.text = nombreUsuario

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("tickets")

        // Filtrar y mostrar la información del ticket
        fetchTicketData()
    }

    private fun fetchTicketData() {
        // Leer todos los tickets del nodo "tickets"
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lista para almacenar los tickets filtrados
                    val tickets = mutableListOf<Ticket>()

                    // Iterar sobre los resultados
                    for (ticketSnapshot in dataSnapshot.children) {
                        val ticket = ticketSnapshot.getValue(Ticket::class.java)
                        ticket?.let {
                            // Verificar si el nombre del usuario está en la lista de destinatarios
                            if (it.destinatarios.contains(nombreUsuario)) {
                                // Añadir ticket a la lista
                                tickets.add(it.copy(id = ticketSnapshot.key))
                            }
                        }
                    }

                    // Ordenar tickets por importancia (según el mapa) y luego por fecha
                    tickets.sortWith(compareBy<Ticket> { importanciaOrder[it.importancia] ?: Int.MAX_VALUE }
                        .thenBy { it.fechaRegistro })

                    // Limpiar el contenedor antes de agregar nuevos elementos
                    ticketContainer.removeAllViews()

                    // Agregar tickets ordenados al contenedor
                    for (ticket in tickets) {
                        val cardView = CardView(this@ResponderSolicitudActivity)
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 16) // Margen inferior
                        cardView.layoutParams = params
                        cardView.radius = 8f
                        // Establecer el drawable de borde basado en la importancia
                        val borderDrawable = importanciaBorders[ticket.importancia] ?: R.drawable.border_azul
                        cardView.background = resources.getDrawable(borderDrawable)

                        cardView.setContentPadding(16, 16, 16, 16)

                        val cardContent = LinearLayout(this@ResponderSolicitudActivity)
                        cardContent.orientation = LinearLayout.VERTICAL

                        val ticketDetails = TextView(this@ResponderSolicitudActivity)
                        ticketDetails.text = """
                            Ticket ID: ${ticket.id}
                            Descripción: ${ticket.descripcionint}
                            Importancia: ${ticket.importancia}
                            Tipo de Solicitud: ${ticket.tipoSolicitud}
                        """.trimIndent()
                        ticketDetails.textSize = 16f

                        val btnResponder = Button(this@ResponderSolicitudActivity)
                        btnResponder.text = "Responder"
                        btnResponder.setOnClickListener {
                            // Crear un Intent para ir a la actividad correspondiente según la importancia del ticket
                            val intent = when (ticket.importancia) {
                                "Alta" -> Intent(this@ResponderSolicitudActivity, ResponderAltaActivity::class.java)
                                "Media" -> Intent(this@ResponderSolicitudActivity, ResponderMediaActivity::class.java)
                                "Baja" -> Intent(this@ResponderSolicitudActivity, ResponderBajaActivity::class.java)
                                else -> null // En caso de que la importancia no sea válida
                            }

                            // Verificar que el intent no sea nulo antes de iniciar la actividad
                            if (intent != null) {
                                // Pasar el ID del ticket a la nueva actividad
                                intent.putExtra("TICKET_ID", ticket.id)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@ResponderSolicitudActivity, "Importancia del ticket no válida.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        cardContent.addView(ticketDetails)
                        cardContent.addView(btnResponder)

                        cardView.addView(cardContent)

                        ticketContainer.addView(cardView)
                    }
                } else {
                    Toast.makeText(this@ResponderSolicitudActivity, "No se encontraron tickets.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ResponderSolicitudActivity, "Error al consultar la base de datos: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Clase que representa la estructura de los tickets en la base de datos
    data class Ticket(
        val id: String? = null,
        val usuarioSolicitante: String = "",
        val tipoSolicitud: String = "",
        val importancia: String = "",
        val descripcionint: String = "",
        val destinatarios: List<String> = emptyList(),
        val fechaRegistro: String = ""
    )
}
