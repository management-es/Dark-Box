package com.darkbox

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CrearSolicitudActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var destinatariosList: MutableList<String>
    private lateinit var destinatariosSeleccionados: BooleanArray
    private lateinit var selectedDestinatarios: MutableList<String>
    private lateinit var estadoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_solicitud)

        // Obtener referencias a los campos de la solicitud
        val usuarioSolicitante = findViewById<TextView>(R.id.usuarioSolicitante)
        val tipoSolicitud = findViewById<Spinner>(R.id.tipoSolicitud)
        val importancia = findViewById<TextView>(R.id.importancia)
        val descripcionint = findViewById<EditText>(R.id.descripcionint)
        val destinatarioButton = findViewById<Button>(R.id.destinatarioButton)
        val destinatariosTextView = findViewById<TextView>(R.id.destinatariosSeleccionados)
        val btnCrearSolicitud = findViewById<Button>(R.id.btnCrearSolicitud)
        estadoTextView = findViewById(R.id.estadoTextView)

        // Inicializar Firebase para el nodo 'access' (lista de destinatarios)
        database = FirebaseDatabase.getInstance().getReference("access")

        // Inicializar la lista para destinatarios seleccionados
        selectedDestinatarios = mutableListOf()

        // Obtener el nombre del usuario del Intent
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        usuarioSolicitante.text = nombreUsuario

        // Configurar el listener para el Spinner de tipo de solicitud
        tipoSolicitud.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tipo = tipoSolicitud.getItemAtPosition(position).toString()
                val nivelImportancia = when (tipo) {
                    "Falla Masiva" -> "Alta"
                    "Falla Local" -> "Media"
                    "Revisión de Cliente", "Otros" -> "Baja"
                    else -> "Desconocida"
                }
                importancia.text = nivelImportancia
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Cargar destinatarios desde Firebase
        cargarDestinatarios()

        // Configurar el botón para seleccionar destinatarios
        destinatarioButton.setOnClickListener {
            mostrarDialogoDestinatarios(destinatariosTextView)
        }

        // Listener del botón para crear la solicitud
        btnCrearSolicitud.setOnClickListener {
            val usuario = usuarioSolicitante.text.toString()
            val tipo = tipoSolicitud.selectedItem.toString()
            val nivelImportancia = importancia.text.toString()
            val desc = descripcionint.text.toString()
            val estado = "Pendiente" // Estado inicial

            if (usuario.isNotEmpty() && desc.isNotEmpty() && selectedDestinatarios.isNotEmpty()) {
                // Crear un diálogo de confirmación
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmar Solicitud")

                // Preparar los datos para mostrarlos en el mensaje
                val mensajeConfirmacion = """
            Usuario solicitante: $usuario
            Tipo de solicitud: $tipo
            Importancia: $nivelImportancia
            Estado: $estado
            Descripción: $desc
            Destinatarios: ${selectedDestinatarios.joinToString(", ")}
            
            ¿Deseas confirmar y guardar esta solicitud?
        """.trimIndent()

                builder.setMessage(mensajeConfirmacion)

                // Configurar el botón de confirmar
                builder.setPositiveButton("Confirmar") { dialog, _ ->
                    // Guardar los datos en Firebase si se confirma
                    guardarTicketEnFirebase(usuario, tipo, nivelImportancia, desc, estado)
                    dialog.dismiss()
                }

                // Configurar el botón de cancelar
                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    // Cerrar el diálogo sin hacer nada
                    dialog.dismiss()
                }

                // Mostrar el diálogo
                builder.create().show()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun cargarDestinatarios() {
        destinatariosList = mutableListOf()

        // Leer los datos del nodo 'access' en Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinatariosList.clear()

                for (data in snapshot.children) {
                    val nombreUsuario = data.child("nombreUsuario").getValue(String::class.java)
                    if (nombreUsuario != null) {
                        destinatariosList.add(nombreUsuario)
                    }
                }

                // Inicializar array de booleanos para los elementos seleccionados
                destinatariosSeleccionados = BooleanArray(destinatariosList.size)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CrearSolicitudActivity", "Error al cargar destinatarios", error.toException())
            }
        })
    }

    private fun mostrarDialogoDestinatarios(destinatariosTextView: TextView) {
        val nombresArray = destinatariosList.toTypedArray()

        // Crear un diálogo para la selección múltiple
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar Destinatarios")
        builder.setMultiChoiceItems(nombresArray, destinatariosSeleccionados) { _, which, isChecked ->
            if (isChecked) {
                selectedDestinatarios.add(nombresArray[which])
            } else {
                selectedDestinatarios.remove(nombresArray[which])
            }
        }
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            // Mostrar los destinatarios seleccionados
            if (selectedDestinatarios.isNotEmpty()) {
                destinatariosTextView.text = selectedDestinatarios.joinToString(", ")
            } else {
                destinatariosTextView.text = "No se han seleccionado destinatarios"
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun guardarTicketEnFirebase(usuario: String, tipo: String, importancia: String, descripcion: String, estado: String) {
        // Referencia al nodo "tickets" en Firebase
        val ticketsRef = FirebaseDatabase.getInstance().getReference("tickets")

        // Obtener fecha actual para el ID
        val fecha = SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())

        // Consultar la cantidad de tickets existentes para usar un número autoincremental
        ticketsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ticketCount = snapshot.childrenCount + 1
                val ticketId = "$fecha-$importancia-$ticketCount"

                // Crear el objeto ticket
                val ticket = mapOf(
                    "usuarioSolicitante" to usuario,
                    "tipoSolicitud" to tipo,
                    "importancia" to importancia,
                    "estado" to estado,
                    "descripcionint" to descripcion,
                    "destinatarios" to selectedDestinatarios,
                    "fechaRegistro" to fecha
                )

                // Guardar el ticket en la base de datos
                ticketsRef.child(ticketId).setValue(ticket)
                    .addOnSuccessListener {
                        Toast.makeText(this@CrearSolicitudActivity, "Ticket guardado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Log.e("CrearSolicitudActivity", "Error al guardar ticket", error)
                        Toast.makeText(this@CrearSolicitudActivity, "Error al guardar el ticket", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CrearSolicitudActivity", "Error al contar tickets", error.toException())
            }
        })
    }
}
