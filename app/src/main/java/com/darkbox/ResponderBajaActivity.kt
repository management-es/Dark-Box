package com.darkbox

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResponderBajaActivity : Activity() {

    private lateinit var contenedorONU: LinearLayout
    private lateinit var contenedorAntena: LinearLayout
    private lateinit var contenedorRouter: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_baja)

        // Inicializar contenedores
        contenedorONU = findViewById(R.id.contenedorONU)
        contenedorAntena = findViewById(R.id.contenedorAntena)
        contenedorRouter = findViewById(R.id.contenedorRouter)

        // Ocultar contenedores por defecto
        contenedorONU.visibility = View.GONE
        contenedorAntena.visibility = View.GONE
        contenedorRouter.visibility = View.GONE

        // Obtener el TextView para mostrar el nombre del usuario
        val usuarioTextView = findViewById<TextView>(R.id.usuarioTextView)

        // Obtener el nombre del usuario desde el Intent
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        if (nombreUsuario != null) {
            usuarioTextView.text = "Usuario: $nombreUsuario"
        } else {
            usuarioTextView.text = "Usuario no disponible"
        }

        // Inicializar el botón enviarRespuestaButton
        val enviarRespuestaButton = findViewById<Button>(R.id.enviarRespuestaButton)

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
                                            Tecnología: ${cliente.tecnologia}
                                        """.trimIndent()

                                        // Mostrar u ocultar los contenedores según la tecnología
                                        when (cliente.tecnologia) {
                                            "Fibra Óptica" -> {
                                                contenedorONU.visibility = View.VISIBLE
                                                contenedorAntena.visibility = View.GONE
                                                contenedorRouter.visibility = View.GONE
                                            }
                                            "Radio Enlace" -> {
                                                contenedorONU.visibility = View.GONE
                                                contenedorAntena.visibility = View.VISIBLE
                                                contenedorRouter.visibility = View.VISIBLE
                                            }
                                            else -> {
                                                contenedorONU.visibility = View.GONE
                                                contenedorAntena.visibility = View.GONE
                                                contenedorRouter.visibility = View.GONE
                                            }
                                        }
                                    } else {
                                        clienteInfoTextView.text = "Datos del cliente no disponibles."
                                    }
                                }
                            } else {
                                Log.d("ResponderBajaActivity", "Cliente no encontrado")
                                clienteInfoTextView.text = "Cliente no encontrado."
                                contenedorONU.visibility = View.GONE
                                contenedorAntena.visibility = View.GONE
                                contenedorRouter.visibility = View.GONE
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

        // Funcionalidad del botón enviarRespuestaButton
        enviarRespuestaButton.setOnClickListener {
            val respuestaId = "$ticketId-Resp"

            // Verificar si ya existe una respuesta para el ticket
            FirebaseDatabase.getInstance().getReference("respuestas").child(respuestaId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si ya existe una respuesta, mostrar un mensaje
                            Toast.makeText(this@ResponderBajaActivity, "Ya se ha enviado una respuesta para este ticket.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Continuar con la creación del AlertDialog para enviar la respuesta
                            val description = descriptionTextView.text.toString()
                            val clienteInfo = clienteInfoTextView.text.toString()

                            // Extraer los campos importantes
                            val importancia = description.substringAfter("Importancia:").substringBefore("Tipo de Solicitud:").trim()
                            val tipoSolicitud = description.substringAfter("Tipo de Solicitud:").substringBefore("Descripción:").trim()
                            val descripcion = description.substringAfter("Descripción:").trim()
                            val tecnologia = clienteInfo.substringAfter("Tecnología:").trim()

                            // Crear un mapa con los datos
                            val respuestaData = HashMap<String, Any>()
                            respuestaData["descripcion"] = mapOf(
                                "tipoSolicitud" to tipoSolicitud,
                                "importancia" to importancia,
                                "descripcionint" to descripcion
                            )

                            respuestaData["cliente"] = mapOf(
                                "cod_cliente" to clienteInfo.substringAfter("Código de Cliente:").substringBefore("\n").trim(),
                                "nombres" to clienteInfo.substringAfter("Nombres:").substringBefore("\n").trim(),
                                "apellidos" to clienteInfo.substringAfter("Apellidos:").substringBefore("\n").trim()
                            )
                            respuestaData["responsable"] = nombreUsuario ?: "Desconocido"

                            // Crear subnodo "desarrollo" que incluya tecnología y sus datos
                            val desarrolloData = HashMap<String, Any>()
                            desarrolloData["tecnologia"] = tecnologia

                            // Obtener información según la tecnología
                            if (tecnologia == "Radio Enlace") {
                                val puertoAntena = findViewById<EditText>(R.id.puertoAntenaEditText).text.toString()
                                val nivelesDbm1 = findViewById<EditText>(R.id.nivelesDbmAntenaEditText).text.toString()
                                val nivelesDbm2 = findViewById<EditText>(R.id.nivelesDbm2AntenaEditText).text.toString()
                                val sectorAntena = findViewById<EditText>(R.id.sectorAntenaEditText).text.toString()
                                val tiempoConectividad = findViewById<EditText>(R.id.tiempoConectividadAntenaEditText).text.toString()
                                val capacidadSaturacion = findViewById<EditText>(R.id.capacidadSaturacionAntenaEditText).text.toString()
                                val enviadosAntena = findViewById<EditText>(R.id.enviadosAntenaEditText).text.toString()
                                val recibidosAntena = findViewById<EditText>(R.id.recibidosAntenaEditText).text.toString()
                                val perdidaAntena = findViewById<EditText>(R.id.perdidaAntenaEditText).text.toString()
                                val actualizacionAntena = findViewById<EditText>(R.id.actualizacionAntenaEditText).text.toString()
                                val observacionesAntena = findViewById<EditText>(R.id.observacionesAntenaEditText).text.toString()

                                // Agregar información de la antena al subnodo "desarrollo"
                                desarrolloData["puertoAntena"] = puertoAntena
                                desarrolloData["nivelesDbm1"] = nivelesDbm1
                                desarrolloData["nivelesDbm2"] = nivelesDbm2
                                desarrolloData["sectorAntena"] = sectorAntena
                                desarrolloData["tiempoConectividad"] = tiempoConectividad
                                desarrolloData["capacidadSaturacion"] = capacidadSaturacion
                                desarrolloData["enviadosAntena"] = enviadosAntena
                                desarrolloData["recibidosAntena"] = recibidosAntena
                                desarrolloData["perdidaAntena"] = perdidaAntena
                                desarrolloData["actualizacionAntena"] = actualizacionAntena
                                desarrolloData["observacionesAntena"] = observacionesAntena

                                // Datos del router
                                desarrolloData["enviadosRouter"] = findViewById<EditText>(R.id.enviadosRouterEditText).text.toString()
                                desarrolloData["recibidosRouter"] = findViewById<EditText>(R.id.recibidosRouterEditText).text.toString()
                                desarrolloData["perdidaRouter"] = findViewById<EditText>(R.id.perdidaRouterEditText).text.toString()
                            } else if (tecnologia == "Fibra Óptica") {
                                val enviadosONU = findViewById<EditText>(R.id.enviadosOnuEditText).text.toString()
                                val recibidosONU = findViewById<EditText>(R.id.recibidosOnuEditText).text.toString()
                                val perdidaONU = findViewById<EditText>(R.id.perdidaOnuEditText).text.toString()
                                val nivelesDbmONU = findViewById<EditText>(R.id.nivelesDbmOnuEditText).text.toString()
                                val nivelesDbm2ONU = findViewById<EditText>(R.id.nivelesDbm2OnuEditText).text.toString()
                                val observacionesONU = findViewById<EditText>(R.id.observacionOnuEditText).text.toString()

                                // Agregar información de la ONU al subnodo "desarrollo"
                                desarrolloData["enviadosONU"] = enviadosONU
                                desarrolloData["recibidosONU"] = recibidosONU
                                desarrolloData["perdidaONU"] = perdidaONU
                                desarrolloData["nivelesDbmONU"] = nivelesDbmONU
                                desarrolloData["nivelesDbm2ONU"] = nivelesDbm2ONU
                                desarrolloData["observacionesONU"] = observacionesONU
                            }

                            // Agregar el subnodo "desarrollo" a la respuesta
                            respuestaData["desarrollo"] = desarrolloData

                            // Crear y mostrar un AlertDialog de confirmación
                            val alertDialog = AlertDialog.Builder(this@ResponderBajaActivity)
                                .setTitle("Confirmar envío")
                                .setMessage("¿Deseas guardar estos datos como respuesta del ticket? Una vez enviado, no podrás editar la respuesta.")
                                .setPositiveButton("Sí") { dialog, _ ->
                                    // Guardar la respuesta en la base de datos si el usuario confirma
                                    FirebaseDatabase.getInstance().getReference("respuestas").child(respuestaId)
                                        .setValue(respuestaData)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Actualizar el estado del ticket a "Realizado"
                                                if (ticketId != null) {
                                                    val ticketRef = FirebaseDatabase.getInstance().getReference("tickets").child(ticketId)
                                                    ticketRef.child("estado").setValue("Realizado") // Cambiar estado a "Realizado"
                                                        .addOnCompleteListener { stateTask ->
                                                            if (stateTask.isSuccessful) {
                                                                // Mostrar alerta indicando que ya no se puede editar
                                                                AlertDialog.Builder(this@ResponderBajaActivity)
                                                                    .setTitle("Respuesta enviada")
                                                                    .setMessage("La respuesta ha sido enviada exitosamente y el estado del ticket ha sido actualizado a 'Realizado'.")
                                                                    .setPositiveButton("Aceptar") { dialog, _ ->
                                                                        finish() // Cerrar la actividad actual
                                                                    }
                                                                    .show()
                                                            } else {
                                                                Toast.makeText(this@ResponderBajaActivity, "Error al actualizar el estado del ticket: ${stateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                }
                                            } else {
                                                Toast.makeText(this@ResponderBajaActivity, "Error al guardar respuesta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                                .setNegativeButton("Cancelar", null) // No hacer nada si el usuario cancela
                                .show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@ResponderBajaActivity, "Error al verificar respuesta: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // Modelo de Cliente
    data class Cliente(
        val cod_cliente: String? = null,
        val nombres: String? = null,
        val apellidos: String? = null,
        val tecnologia: String? = null
    )

    // Modelo de Ticket
    data class Ticket(
        val descripcionint: String? = null,
        val tipoSolicitud: String? = null,
        val importancia: String? = null
    )
}
