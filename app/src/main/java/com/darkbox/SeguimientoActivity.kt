package com.darkbox

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*
import androidx.appcompat.app.AlertDialog


class SeguimientoActivity : AppCompatActivity() {

    private var selectedDate: String? = null // Variable para almacenar la fecha seleccionada
    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketAdapter: TicketAdapter
    private val ticketList: MutableList<Ticket> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)

        // Otras inicializaciones
        val frmSeguimientoTextView = findViewById<TextView>(R.id.frmSeguimientoTextView)
        frmSeguimientoTextView.text = "frm seguimiento"

        val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        recyclerView = findViewById(R.id.ticketRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa ticketAdapter con el lambda para el botón "Cargar Respuesta"
        ticketAdapter = TicketAdapter(ticketList) { ticket ->
            // Mostrar el estado del ticket al hacer clic en el botón
            val estadoTicket = ticket.estado ?: "Estado desconocido" // Manejar el caso de null

            // Mostrar un Toast con el estado del ticket
            Toast.makeText(this, "Estado del ticket: $estadoTicket", Toast.LENGTH_SHORT).show()

            // Manejar la acción de cargar respuesta aquí
            when (estadoTicket) {
                "Realizado" -> {
                    // Aquí puedes implementar la lógica para cargar la respuesta
                    Toast.makeText(this, "Cargar respuesta para el ticket: ${ticket.descripcionint}", Toast.LENGTH_SHORT).show()
                    // Aquí puedes agregar la lógica para cargar la respuesta...
                }
                "Pendiente" -> {
                    // Mostrar AlertDialog
                    AlertDialog.Builder(this)
                        .setTitle("Estado de la Solicitud")
                        .setMessage("Esta solicitud aún está pendiente por respuesta.")
                        .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                        .setCancelable(true)
                        .show()
                }
                else -> {
                    // Mostrar un Toast para el caso de estado null o desconocido
                    Toast.makeText(this, "El estado del ticket es desconocido.", Toast.LENGTH_SHORT).show()
                }
            }
        }


// Configura el RecyclerView con el adaptador
        recyclerView.adapter = ticketAdapter


        btnSelectDate.setOnClickListener {
            // Obtener la fecha actual
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Mostrar el DatePickerDialog
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Formatear la fecha seleccionada
                selectedDate = String.format("%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay)
                tvSelectedDate.text = "Selected Date: $selectedDate"

            }, year, month, day)
            datePickerDialog.show()
        }

        btnSearch.setOnClickListener {
            selectedDate?.let { date ->
                searchTicketsByDate(date)
            } ?: Toast.makeText(this, "Por favor, selecciona una fecha primero", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchTicketsByDate(date: String) {
        // Obtener una referencia a la base de datos de Firebase
        val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

        // Crear la consulta para buscar tickets que comiencen con la fecha seleccionada
        val query = firebaseDatabase.child("tickets")
            .orderByKey()
            .startAt(date)
            .endAt(date + "\uf8ff") // Para incluir lo que comience con esa fecha

        // Escuchar los resultados de la consulta
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultCount = snapshot.childrenCount.toInt() // Contar la cantidad de resultados

                // Comprobar si hay resultados
                if (resultCount > 0) {
                    val tickets = mutableListOf<Ticket>() // Lista de tickets válidos

                    // Recorrer los resultados
                    for (ticketSnapshot in snapshot.children) {
                        val ticketId = ticketSnapshot.key // Obtiene el ID del ticket

                        // Comprobar si el ticketId comienza con la fecha seleccionada
                        if (ticketId != null && ticketId.startsWith(date)) {
                            // Extraer la información del ticket
                            val descripcionint = ticketSnapshot.child("descripcionint").getValue(String::class.java)
                            val estado = ticketSnapshot.child("estado").getValue(String::class.java)
                            val importancia = ticketSnapshot.child("importancia").getValue(String::class.java)
                            val tipoSolicitud = ticketSnapshot.child("tipoSolicitud").getValue(String::class.java)
                            val usuarioSolicitante = ticketSnapshot.child("usuarioSolicitante").getValue(String::class.java)

                            // Extraer destinatarios
                            val destinatarios = mutableListOf<String>()
                            ticketSnapshot.child("destinatarios").children.forEach { destinatarioSnapshot ->
                                destinatarios.add(destinatarioSnapshot.getValue(String::class.java) ?: "")
                            }

                            // Crear un nuevo ticket y agregarlo a la lista
                            val ticket = Ticket(descripcionint ?: "", estado ?: "", importancia ?: "", tipoSolicitud ?: "", usuarioSolicitante ?: "", destinatarios)
                            tickets.add(ticket)
                        }
                    }

                    // Configura el RecyclerView con la lista de tickets y el manejo del botón
                    recyclerView.adapter = TicketAdapter(tickets) { ticket ->
                        // Manejar la acción de cargar respuesta
                        Toast.makeText(this@SeguimientoActivity, "Cargar respuesta para el ticket: ${ticket.descripcionint}", Toast.LENGTH_SHORT).show()
                        // Aquí puedes iniciar una nueva actividad o mostrar un diálogo, según lo que necesites hacer
                    }

                    // Mostrar la cantidad de tickets válidos
                    Toast.makeText(this@SeguimientoActivity, "Cantidad de resultados: ${tickets.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SeguimientoActivity, "No se encontraron tickets para la fecha: $date", Toast.LENGTH_SHORT).show()
                    Log.d("SeguimientoActivity", "No se encontraron tickets para la fecha: $date")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SeguimientoActivity", "Error al acceder a la base de datos: ${error.message}")
            }
        })
    }



    // Clase Ticket definida aquí
    data class Ticket(
        val descripcionint: String? = null,
        val estado: String? = null,
        val importancia: String? = null,
        val tipoSolicitud: String? = null,
        val usuarioSolicitante: String? = null,
        val destinatarios: List<String> = emptyList()
    )
}
