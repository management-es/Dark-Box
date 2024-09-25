package com.darkbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.widget.Toast
import android.widget.TextView

class HistorialMediaAltaActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var gestionAdapter: GestionAdapter
    private val gestionList = mutableListOf<Gestion>()
    private lateinit var ticketIdTextView: TextView // Declara el TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_media_alta)

        // Obtener el ID del ticket desde el Intent
        val ticketId = intent.getStringExtra("TICKET_ID") ?: return
        val ticketIdResp = "${ticketId}-Resp" // Crear el ID con el sufijo

        // Inicializar el TextView para mostrar el ticket ID
        ticketIdTextView = findViewById(R.id.text_view_ticket_id)
        ticketIdTextView.text = "Ticket ID: $ticketIdResp" // Mostrar el ticket ID

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_view_gestiones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        gestionAdapter = GestionAdapter(gestionList)
        recyclerView.adapter = gestionAdapter

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().getReference("respuestas/$ticketIdResp")

        // Cargar las gestiones desde la base de datos
        loadGestiones()
    }

    private fun loadGestiones() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gestionList.clear()
                if (snapshot.exists()) {
                    for (gestionSnapshot in snapshot.children) {
                        val observaciones = gestionSnapshot.child("observaciones").getValue(String::class.java)
                        val usuario = gestionSnapshot.child("usuario").getValue(String::class.java)

                        if (observaciones != null && usuario != null) {
                            val gestion = Gestion(observaciones = observaciones, usuario = usuario)
                            gestionList.add(gestion)
                        }
                    }
                    if (gestionList.isEmpty()) {
                        Toast.makeText(this@HistorialMediaAltaActivity, "No se encontraron gestiones", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@HistorialMediaAltaActivity, "${gestionList.size} gestiones cargadas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HistorialMediaAltaActivity, "No se encontraron gestiones", Toast.LENGTH_SHORT).show()
                }
                gestionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialMediaAltaActivity, "Error al cargar gestiones: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Clase para representar cada gestión
    data class Gestion(
        val observaciones: String = "",
        val usuario: String = ""
    )
}
