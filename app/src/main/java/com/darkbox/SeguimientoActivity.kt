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

class SeguimientoActivity : AppCompatActivity() {

    private var selectedDate: String? = null // Variable para almacenar la fecha seleccionada
    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketAdapter: TicketAdapter
    private val ticketList: MutableList<Ticket> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)

        val frmSeguimientoTextView = findViewById<TextView>(R.id.frmSeguimientoTextView)
        frmSeguimientoTextView.text = "frm seguimiento"

        val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        recyclerView = findViewById(R.id.ticketRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        ticketAdapter = TicketAdapter(ticketList)
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
            .endAt(date + "\uf8ff") // Para incluir todo lo que comience con esa fecha

        // Escuchar los resultados de la consulta
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultCount = snapshot.childrenCount.toInt() // Contar la cantidad de resultados

                // Comprobar si hay resultados
                if (resultCount > 0) {
                    ticketList.clear() // Limpiar la lista de tickets
                    var validTicketCount = 0 // Inicializar contador de tickets válidos

                    // Recorrer los resultados
                    for (ticketSnapshot in snapshot.children) {
                        val ticketId = ticketSnapshot.key // Obtiene el ID del ticket

                        // Comprobar si el ticketId comienza con la fecha seleccionada
                        if (ticketId != null && ticketId.startsWith(date)) {
                            validTicketCount++ // Incrementar solo si es un ticket válido

                            // Extraer la información del ticket
                            val descripcionint = ticketSnapshot.child("descripcionint").getValue(String::class.java)
                            val estado = ticketSnapshot.child("estado").getValue(String::class.java)
                            val importancia = ticketSnapshot.child("importancia").getValue(String::class.java)
                            val tipoSolicitud = ticketSnapshot.child("tipoSolicitud").getValue(String::class.java)
                            val usuarioSolicitante = ticketSnapshot.child("usuarioSolicitante").getValue(String::class.java)

                            // Crear un objeto Ticket y agregarlo a la lista
                            val ticket = Ticket(descripcionint, estado, importancia, tipoSolicitud, usuarioSolicitante)
                            ticketList.add(ticket)
                        }
                    }

                    ticketAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado

                    // Mostrar la cantidad de tickets válidos
                    Toast.makeText(this@SeguimientoActivity, "Cantidad de resultados: $validTicketCount", Toast.LENGTH_SHORT).show()
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
        val usuarioSolicitante: String? = null
    )
}
