package com.darkbox

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import com.google.firebase.database.*

class VerInventarioActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private val inventoryList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_inventario)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("inventario")

        // Referencia a la ListView
        listView = findViewById(R.id.listView_inventario)

        // Adaptador para mostrar los datos
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, inventoryList)
        listView.adapter = adapter

        // Leer datos de la base de datos
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                inventoryList.clear()
                for (itemSnapshot in snapshot.children) {
                    val equipo = itemSnapshot.child("equipo").value.toString()
                    val serial = itemSnapshot.child("serial").value.toString()
                    inventoryList.add("$equipo - $serial")
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                error.toException().printStackTrace()
            }
        })
    }
}
