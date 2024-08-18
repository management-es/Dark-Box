package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.*

class VerInventarioActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var estadoSpinner: Spinner
    private lateinit var containerInformacion: LinearLayout
    private lateinit var containerButtons: LinearLayout
    private lateinit var textViewTitulo: TextView
    private lateinit var textViewTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_inventario)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("inventario")

        // Referencias a los elementos de la UI
        estadoSpinner = findViewById(R.id.spinner_estado)
        containerInformacion = findViewById(R.id.container_informacion)
        containerButtons = findViewById(R.id.container_buttons)
        textViewTitulo = findViewById(R.id.textView_titulo)
        textViewTotal = findViewById(R.id.textView_total) // Asegúrate de añadir un TextView para mostrar el total en tu layout principal

        // Configura los botones para filtrar por tipo de equipo
        findViewById<Button>(R.id.button_antena_cliente).setOnClickListener {
            mostrarEquiposFiltrados("Antena Cliente")
        }
        findViewById<Button>(R.id.button_router).setOnClickListener {
            mostrarEquiposFiltrados("Router")
        }
        findViewById<Button>(R.id.button_onu).setOnClickListener {
            mostrarEquiposFiltrados("Onu")
        }
        findViewById<Button>(R.id.button_enlace).setOnClickListener {
            mostrarEquiposFiltrados("Enlace")
        }
        findViewById<Button>(R.id.button_sector).setOnClickListener {
            mostrarEquiposFiltrados("Sector")
        }
    }

    private fun mostrarEquiposFiltrados(tipoEquipo: String) {
        val estadoSeleccionado = estadoSpinner.selectedItem.toString()
        containerInformacion.removeAllViews() // Limpia el contenedor
        containerButtons.visibility = View.GONE // Oculta los botones
        estadoSpinner.visibility = View.GONE // Oculta el Spinner
        textViewTitulo.text = "Equipo: $tipoEquipo\nEstado: $estadoSeleccionado"
        textViewTitulo.visibility = View.VISIBLE // Muestra el título

        var totalEquipos = 0

        database.orderByChild("equipo").equalTo(tipoEquipo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val estado = itemSnapshot.child("estado").value.toString()
                        if (estado == estadoSeleccionado || estadoSeleccionado == "Todo") {
                            val serial = itemSnapshot.child("serial").value.toString()
                            val observaciones = itemSnapshot.child("observaciones").value.toString()
                            val zona = itemSnapshot.child("zona").value.toString()
                            val tecnologia = itemSnapshot.child("tecnologia").value.toString()
                            val modelo = itemSnapshot.child("modelo").value.toString()

                            val itemView = View.inflate(this@VerInventarioActivity, R.layout.item_informacion, null)
                            val serialTextView = itemView.findViewById<TextView>(R.id.textView_serial)
                            val observacionesTextView = itemView.findViewById<TextView>(R.id.textView_observaciones)
                            val zonaTextView = itemView.findViewById<TextView>(R.id.textView_zona)
                            val tecnologiaTextView = itemView.findViewById<TextView>(R.id.textView_tecnologia)
                            val modeloTextView = itemView.findViewById<TextView>(R.id.textView_modelo)

                            serialTextView.text = "Serial: $serial"
                            observacionesTextView.text = "Observaciones: $observaciones"
                            zonaTextView.text = "Zona: $zona"
                            tecnologiaTextView.text = "Tecnología: $tecnologia"
                            modeloTextView.text = "Modelo: $modelo"

                            containerInformacion.addView(itemView)
                            totalEquipos++
                        }
                    }
                    textViewTotal.text = "Total $tipoEquipo $estadoSeleccionado: $totalEquipos"
                    textViewTotal.visibility = View.VISIBLE
                    containerInformacion.visibility = View.VISIBLE
                } else {
                    containerInformacion.visibility = View.GONE
                    textViewTotal.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}
