package com.darkbox

import android.app.DatePickerDialog
import android.content.Intent
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


class SeguimientoActivity : AppCompatActivity() {

    private var selectedDate: String? = null // Variable para almacenar la fecha seleccionada
    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketAdapter: TicketAdapter
    private val ticketList: MutableList<Ticket> = mutableListOf()
    private lateinit var nombreUsuario: String
    private lateinit var rolUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)

        // Obtener el nombre de usuario del Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Usuario"


        val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnAvanzado = findViewById<Button>(R.id.btnAvanzado)
        recyclerView = findViewById(R.id.ticketRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa ticketAdapter con el lambda para el botón "Cargar Respuesta"
        ticketAdapter = TicketAdapter(ticketList) { ticket ->
            // Manejar la acción de cargar respuesta aquí
            Toast.makeText(this, "Cargar respuesta para el ticket: ${ticket.descripcionint}", Toast.LENGTH_SHORT).show()
            // Aquí puedes iniciar una nueva actividad o mostrar un diálogo, según lo que necesites hacer
        }


    // Configura el RecyclerView con el adaptador
        recyclerView.adapter = ticketAdapter


        btnAvanzado.setOnClickListener {
            val intent = Intent(this, FiltradoSeguimientoActivity::class.java)
            startActivity(intent)
        }


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
                tvSelectedDate.text = "Fecha Seleccionada: $selectedDate"

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
        val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        val query = firebaseDatabase.child("tickets")
            .orderByKey()
            .startAt(date)
            .endAt(date + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultCount = snapshot.childrenCount.toInt()

                if (resultCount > 0) {
                    val tickets = mutableListOf<Ticket>()

                    for (ticketSnapshot in snapshot.children) {
                        val ticketId = ticketSnapshot.key ?: continue  // Obtiene el ID del ticket

                        if (ticketId.startsWith(date)) {
                            val descripcionint = ticketSnapshot.child("descripcionint").getValue(String::class.java)
                            val estado = ticketSnapshot.child("estado").getValue(String::class.java)
                            val importancia = ticketSnapshot.child("importancia").getValue(String::class.java)
                            val tipoSolicitud = ticketSnapshot.child("tipoSolicitud").getValue(String::class.java)
                            val usuarioSolicitante = ticketSnapshot.child("usuarioSolicitante").getValue(String::class.java)

                            // Filtrar por rol de usuario
                            if (rolUsuario in listOf("Directivo", "SupUsrDo", "Administrativo") || usuarioSolicitante == nombreUsuario) {
                                val destinatarios = mutableListOf<String>()
                                ticketSnapshot.child("destinatarios").children.forEach { destinatarioSnapshot ->
                                    destinatarios.add(destinatarioSnapshot.getValue(String::class.java) ?: "")
                                }

                                // Crear un nuevo ticket con el Ticket ID
                                val ticket = Ticket(ticketId, descripcionint ?: "", estado ?: "", importancia ?: "", tipoSolicitud ?: "", usuarioSolicitante ?: "", destinatarios)
                                tickets.add(ticket)
                            }
                        }
                    }

                    if (tickets.isNotEmpty()) {
                        recyclerView.adapter = TicketAdapter(tickets) { ticket ->
                            Toast.makeText(this@SeguimientoActivity, "Cargar respuesta para el ticket: ${ticket.descripcionint}", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this@SeguimientoActivity, "Cantidad de resultados: ${tickets.size}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SeguimientoActivity, "No tienes tickets para la fecha: $date", Toast.LENGTH_SHORT).show()
                    }
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


    // Clase Ticket definida
    data class Ticket(
        val ticketId: String? = null,
        val descripcionint: String? = null,
        val estado: String? = null,
        val importancia: String? = null,
        val tipoSolicitud: String? = null,
        val usuarioSolicitante: String? = null,
        val destinatarios: List<String> = emptyList()
    )
}
