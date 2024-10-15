package com.darkbox

import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DevOpSuDoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var informeAdapter: InformeAdapter
    private lateinit var database: DatabaseReference
    private lateinit var cantidadInformesPendientes: TextView
    private val informesList = mutableListOf<InformeAdapter.InformeError>()
    private val idsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devop_sudo)

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewInformes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().getReference("soportedev")

        // Inicializar el TextView para mostrar la cantidad de informes pendientes
        cantidadInformesPendientes = findViewById(R.id.tvCantidadInformesPendientes)

        // Cargar informes desde Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                informesList.clear()
                idsList.clear()

                var informesPendientesCount = 0

                for (informeSnapshot in snapshot.children) {
                    val informe = informeSnapshot.getValue(InformeAdapter.InformeError::class.java)
                    val id = informeSnapshot.key

                    if (informe != null && id != null) {
                        // Filtrar solo los informes que no están marcados como "Realizado"
                        if (informe.estado != "Realizado") {
                            informesList.add(informe)
                            idsList.add(id)
                            informesPendientesCount++
                        }
                    }
                }

                // Actualizar el TextView con la cantidad de informes pendientes
                cantidadInformesPendientes.text = "Informes Pendientes: $informesPendientesCount"

                // Configurar el adaptador con los informes
                informeAdapter = InformeAdapter(informesList, idsList)
                recyclerView.adapter = informeAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DevOpSuDoActivity, "Error al cargar los informes", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
