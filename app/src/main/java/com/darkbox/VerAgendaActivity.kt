package com.darkbox

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.database.*

class VerAgendaActivity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var buttonFiltrar: Button
    private lateinit var layoutAgendaResults: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_agenda)

        datePicker = findViewById(R.id.date_picker)
        buttonFiltrar = findViewById(R.id.button_filtrar)
        layoutAgendaResults = findViewById(R.id.layout_agenda_results)

        buttonFiltrar.setOnClickListener {
            filterByDate()
        }
    }

    private fun filterByDate() {
        val selectedYear = datePicker.year
        val selectedMonth = datePicker.month + 1 // Meses en DatePicker son base 0
        val selectedDay = datePicker.dayOfMonth

        val selectedDate = String.format("%04d%02d%02d", selectedYear, selectedMonth, selectedDay) // Formato YYYYMMDD

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("agenda")

        reference.orderByKey().startAt("$selectedDate-").endAt("$selectedDate~").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                layoutAgendaResults.removeAllViews() // Limpiar los resultados anteriores

                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.key
                    val tipoGestion = snapshot.child("tipoGestion").getValue(String::class.java)
                    val descripcion = snapshot.child("descripcion").getValue(String::class.java)
                    val estado = snapshot.child("estado").getValue(String::class.java)
                    val zona = snapshot.child("zona").getValue(String::class.java) // Obtener la zona

                    // Crear un nuevo CardView para cada registro
                    val cardView = CardView(this@VerAgendaActivity)
                    val cardParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    cardParams.setMargins(0, 0, 0, 16)
                    cardView.layoutParams = cardParams
                    cardView.setPadding(16, 16, 16, 16)
                    cardView.radius = 8f
                    cardView.cardElevation = 4f

                    // Configurar color de fondo según el estado
                    when (estado) {
                        "realizado" -> cardView.setCardBackgroundColor(ContextCompat.getColor(this@VerAgendaActivity, R.color.green_pastel))
                        "cancelado" -> cardView.setCardBackgroundColor(ContextCompat.getColor(this@VerAgendaActivity, R.color.red_pastel))
                        else -> cardView.setCardBackgroundColor(ContextCompat.getColor(this@VerAgendaActivity, R.color.cardview_background))
                    }

                    // Crear un layout horizontal para contener el TextView y los botones
                    val contentLayout = LinearLayout(this@VerAgendaActivity)
                    contentLayout.orientation = LinearLayout.HORIZONTAL
                    val contentLayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    contentLayout.layoutParams = contentLayoutParams

                    // Crear un TextView para el contenido del CardView
                    val textView = TextView(this@VerAgendaActivity)
                    val textParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    textView.layoutParams = textParams
                    textView.setTextColor(ContextCompat.getColor(this@VerAgendaActivity, android.R.color.black))
                    textView.textSize = 16f

                    // Establecer el contenido basado en el tipo de ID
                    textView.text = when {
                        id?.contains("sol_inst") == true -> {
                            """
                            ID: $id
                            Nombre: ${snapshot.child("nombre").getValue(String::class.java)}
                            Apellidos: ${snapshot.child("apellidos").getValue(String::class.java)}
                            Dirección: ${snapshot.child("direccion").getValue(String::class.java)}
                            Coordenadas: ${snapshot.child("coordenadas").getValue(String::class.java)}
                            Teléfono: ${snapshot.child("telefono").getValue(String::class.java)}
                            Contacto: ${snapshot.child("contactos").getValue(String::class.java)}
                            Gestión: Instalación
                            Zona: $zona
                            Observaciones: ${snapshot.child("observaciones").getValue(String::class.java)}
                            
                            """.trimIndent()
                        }
                        id?.contains("ot") == true -> {
                            """
                            ID: $id
                            Tipo de Gestión: $tipoGestion
                            Zona: $zona
                            Descripción: $descripcion
                            
                            """.trimIndent()
                        }
                        else -> {
                            """
                            ID: $id
                            Nombre: ${snapshot.child("nombre").getValue(String::class.java)}
                            Apellidos: ${snapshot.child("apellidos").getValue(String::class.java)}
                            Documento: ${snapshot.child("numeroDocumento").getValue(String::class.java)}
                            Dirección: ${snapshot.child("direccion").getValue(String::class.java)}
                            Coordenadas: ${snapshot.child("coordenadas").getValue(String::class.java)}
                            Teléfono: ${snapshot.child("telefono").getValue(String::class.java)}
                            Contactos: ${snapshot.child("contactos").getValue(String::class.java)}
                            Gestión: ${snapshot.child("gestion").getValue(String::class.java)}
                            Zona: $zona
                            Observaciones: ${snapshot.child("observaciones").getValue(String::class.java)}
                            
                            """.trimIndent()
                        }
                    }

                    // Crear un LinearLayout vertical para los botones
                    val buttonLayout = LinearLayout(this@VerAgendaActivity)
                    buttonLayout.orientation = LinearLayout.VERTICAL
                    val buttonLayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    buttonLayout.layoutParams = buttonLayoutParams

                    // Crear botón "Cancelado" (X roja)
                    val buttonCancelado = Button(this@VerAgendaActivity)
                    buttonCancelado.text = "❌"
                    buttonCancelado.textSize = 20f
                    buttonCancelado.setBackgroundColor(ContextCompat.getColor(this@VerAgendaActivity, R.color.red_pastel))
                    buttonCancelado.visibility = if (estado == null) View.VISIBLE else View.GONE // Ocultar si ya se ha seleccionado un estado

                    // Crear botón "Realizado" (Visto Bueno verde)
                    val buttonRealizado = Button(this@VerAgendaActivity)
                    buttonRealizado.text = "✔️"
                    buttonRealizado.textSize = 20f
                    buttonRealizado.setBackgroundColor(ContextCompat.getColor(this@VerAgendaActivity, R.color.green_pastel))
                    buttonRealizado.visibility = if (estado == null) View.VISIBLE else View.GONE // Ocultar si ya se ha seleccionado un estado

                    buttonRealizado.setOnClickListener {
                        showConfirmationDialog(id, "realizado")
                    }

                    buttonCancelado.setOnClickListener {
                        showCancelationDialog(id)
                    }

                    // Agregar botones al layout de botones
                    buttonLayout.addView(buttonRealizado)
                    buttonLayout.addView(buttonCancelado)

                    // Agregar el TextView y el layout de botones al layout principal
                    contentLayout.addView(textView)
                    contentLayout.addView(buttonLayout)

                    // Agregar el layout principal al CardView
                    cardView.addView(contentLayout)

                    // Agregar el CardView al layout principal
                    layoutAgendaResults.addView(cardView)
                }

                if (layoutAgendaResults.childCount > 0) {
                    layoutAgendaResults.visibility = View.VISIBLE
                    datePicker.visibility = View.GONE
                    buttonFiltrar.visibility = View.GONE
                } else {
                    Toast.makeText(this@VerAgendaActivity, "No se encontraron resultados para la fecha seleccionada.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("VerAgendaActivity", "Error al cargar datos", databaseError.toException())
            }
        })
    }

    private fun showConfirmationDialog(id: String?, estado: String) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Confirmación")
        alertDialog.setMessage("¿Está seguro de marcar esta tarea como ${if (estado == "realizado") "realizado" else "cancelado"}?")
        alertDialog.setPositiveButton("Sí") { dialog, _ ->
            updateTaskState(id, estado)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showCancelationDialog(id: String?) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = "Ingrese una observación"

        alertDialog.setTitle("Observación de Cancelación")
        alertDialog.setMessage("Ingrese una observación para la cancelación")
        alertDialog.setView(input)
        alertDialog.setPositiveButton("Guardar") { dialog, _ ->
            val observacion = input.text.toString()
            if (observacion.isNotEmpty()) {
                updateTaskState(id, "cancelado", observacion)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Debe ingresar una observación", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun updateTaskState(id: String?, estado: String, observacion: String? = null) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("agenda").child(id ?: return)

        reference.child("estado").setValue(estado)
        observacion?.let {
            reference.child("observaciones").setValue(it)
        }

        Toast.makeText(this, "Estado actualizado a $estado", Toast.LENGTH_SHORT).show()
        // Regresar al menú principal
        finish()
    }
}
