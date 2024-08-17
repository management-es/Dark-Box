package com.darkbox

import android.app.DatePickerDialog
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class OtrasGestionesActivity : ComponentActivity() {

    private lateinit var inputTipoGestion: TextInputEditText
    private lateinit var inputDescripcion: TextInputEditText
    private lateinit var inputFechaGestion: TextInputEditText
    private lateinit var buttonGuardarGestion: Button
    private var fechaSeleccionada: String? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otras_gestiones)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().getReference("agenda")

        // Referencias a los elementos de la UI
        inputTipoGestion = findViewById(R.id.input_tipo_gestion)
        inputDescripcion = findViewById(R.id.input_descripcion)
        inputFechaGestion = findViewById(R.id.input_fecha_gestion)
        buttonGuardarGestion = findViewById(R.id.button_guardar_gestion)

        // Configurar el campo de fecha
        inputFechaGestion.setOnClickListener {
            showDatePickerDialog()
        }

        // Configurar el botón Guardar Gestión
        buttonGuardarGestion.setOnClickListener {
            guardarGestion()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fechaFormateada = "$dayOfMonth/${month + 1}/$year"
                inputFechaGestion.setText(fechaFormateada)

                // Almacena la fecha en formato YYYYMMDD
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                fechaSeleccionada = sdf.format(Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun guardarGestion() {
        val tipoGestion = inputTipoGestion.text.toString()
        val descripcion = inputDescripcion.text.toString()

        // Verificar si todos los campos requeridos están llenos
        if (tipoGestion.isNotEmpty() && descripcion.isNotEmpty() && fechaSeleccionada != null) {
            // Construir el mensaje de confirmación con los datos
            val mensajeConfirmacion = """
            ¿Desea guardar la siguiente gestión?
            
            Tipo de Gestión: $tipoGestion
            Descripción: $descripcion
            Fecha: $fechaSeleccionada
        """.trimIndent()

            // Crear un diálogo de confirmación
            AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage(mensajeConfirmacion)
                .setPositiveButton("Sí") { _, _ ->
                    // Crear el ID de gestión basado en la fecha y agregar el sufijo "ot-gestion"
                    val idGestion = "${fechaSeleccionada}-ot-gestion"

                    // Crear el objeto de datos
                    val gestion = mapOf(
                        "tipoGestion" to tipoGestion,
                        "descripcion" to descripcion,
                        "fecha" to fechaSeleccionada
                    )

                    // Guardar los datos en Firebase bajo el nodo "agenda"
                    database.child(idGestion).setValue(gestion)
                        .addOnSuccessListener {
                            // Mostrar mensaje de éxito
                            Toast.makeText(this, "Gestión guardada con éxito", Toast.LENGTH_SHORT).show()
                            finish()  // Cierra la actividad
                        }
                        .addOnFailureListener {
                            // Mostrar mensaje de error
                            Toast.makeText(this, "Error al guardar la gestión", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()  // Cierra el diálogo si se cancela
                }
                .show()
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

}
