package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*

class ActualizarEstadoActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var editTextBuscarSerial: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var containerDetalles: LinearLayout
    private lateinit var textViewSerial: TextView
    private lateinit var textViewTecnologia: TextView
    private lateinit var textViewModelo: TextView
    private lateinit var textViewEstado: TextView
    private lateinit var textViewZona: TextView
    private lateinit var textViewObservaciones: TextView
    private lateinit var spinnerNuevoEstado: Spinner
    private lateinit var buttonGuardarEstado: Button

    private var serialBuscado: String? = null
    private var estadoAnterior: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_estado)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("inventario")

        // Referencias a los elementos de la UI
        editTextBuscarSerial = findViewById(R.id.editText_buscar_serial)
        buttonBuscar = findViewById(R.id.button_buscar)
        containerDetalles = findViewById(R.id.container_detalles)
        textViewSerial = findViewById(R.id.textView_serial)
        textViewTecnologia = findViewById(R.id.textView_tecnologia)
        textViewModelo = findViewById(R.id.textView_modelo)
        textViewEstado = findViewById(R.id.textView_estado)
        textViewZona = findViewById(R.id.textView_zona)
        textViewObservaciones = findViewById(R.id.textView_observaciones)
        spinnerNuevoEstado = findViewById(R.id.spinner_nuevo_estado)
        buttonGuardarEstado = findViewById(R.id.button_guardar_estado)

        buttonBuscar.setOnClickListener {
            buscarEquipo()
        }

        buttonGuardarEstado.setOnClickListener {
            mostrarConfirmacionCambioEstado()
        }
    }

    private fun buscarEquipo() {
        // Convertir el serial buscado a mayúsculas
        serialBuscado = editTextBuscarSerial.text.toString().trim().uppercase()

        // Mostrar pantalla de carga
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)

        if (!serialBuscado.isNullOrEmpty()) {
            // Realiza la búsqueda utilizando el serial en mayúsculas
            database.orderByChild("serial").equalTo(serialBuscado).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (itemSnapshot in snapshot.children) {
                            val tecnologia = itemSnapshot.child("tecnologia").value.toString()
                            val modelo = itemSnapshot.child("modelo").value.toString()
                            estadoAnterior = itemSnapshot.child("estado").value.toString()
                            val zona = itemSnapshot.child("zona").value.toString()
                            val observaciones = itemSnapshot.child("observaciones").value.toString()

                            textViewSerial.text = "Serial: $serialBuscado"
                            textViewTecnologia.text = "Tecnología: $tecnologia"
                            textViewModelo.text = "Modelo: $modelo"
                            textViewEstado.text = "Estado: $estadoAnterior"
                            textViewZona.text = "Zona: $zona"
                            textViewObservaciones.text = "Observaciones: $observaciones"

                            containerDetalles.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@ActualizarEstadoActivity, "Equipo no encontrado", Toast.LENGTH_SHORT).show()
                        containerDetalles.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
        } else {
            Toast.makeText(this, "Por favor, ingrese un número de serie", Toast.LENGTH_SHORT).show()
        }
    }


    private fun mostrarConfirmacionCambioEstado() {
        val nuevoEstado = spinnerNuevoEstado.selectedItem.toString()


        if (!serialBuscado.isNullOrEmpty() && estadoAnterior != null) {
            AlertDialog.Builder(this)
                .setTitle("Confirmar Cambio de Estado")
                .setMessage("¿Desea realizar el cambio de estado de $estadoAnterior a $nuevoEstado?")
                .setPositiveButton("Confirmar") { _, _ ->
                    cambiarEstado(nuevoEstado)

                    // Mostrar pantalla de carga
                    val intent = Intent(this, LoadingActivity::class.java)
                    startActivity(intent)

                }
                .setNegativeButton("Rechazar", null)
                .show()
        } else {
            Toast.makeText(this, "Por favor, busque un equipo antes de cambiar el estado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cambiarEstado(nuevoEstado: String) {
        database.orderByChild("serial").equalTo(serialBuscado).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val key = itemSnapshot.key
                        key?.let {
                            database.child(it).child("estado").setValue(nuevoEstado).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@ActualizarEstadoActivity, "Estado actualizado", Toast.LENGTH_SHORT).show()
                                    estadoAnterior = nuevoEstado  // Actualiza el estado anterior para futuras confirmaciones
                                    finish()  // Regresa al menú anterior
                                } else {
                                    Toast.makeText(this@ActualizarEstadoActivity, "Error al actualizar el estado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}
