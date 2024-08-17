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
                    cardView.setCardBackgroundColor(ContextCompat.getColor(this@VerAgendaActivity, R.color.cardview_background)) // Color más oscuro

                    // Crear un TextView para el contenido del CardView
                    val textView = TextView(this@VerAgendaActivity)
                    val textParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
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
                            Observaciones: ${snapshot.child("observaciones").getValue(String::class.java)}
                            """.trimIndent()
                        }
                        id?.contains("ot") == true -> {
                            """
                            ID: $id
                            Tipo de Gestión: $tipoGestion
                            Descripción: $descripcion
                            """.trimIndent()
                        }
                        else -> {
                            """
                            ID: $id
                            Nombre: ${snapshot.child("nombre").getValue(String::class.java)}
                            Apellidos: ${snapshot.child("apellidos").getValue(String::class.java)}
                            Documento: ${snapshot.child("documento").getValue(String::class.java)}
                            Dirección: ${snapshot.child("direccion").getValue(String::class.java)}
                            Coordenadas: ${snapshot.child("coordenadas").getValue(String::class.java)}
                            Teléfono: ${snapshot.child("telefono").getValue(String::class.java)}
                            Contactos: ${snapshot.child("contactos").getValue(String::class.java)}
                            Gestión: ${snapshot.child("gestion").getValue(String::class.java)}
                            Observaciones: ${snapshot.child("observaciones").getValue(String::class.java)}
                            """.trimIndent()
                        }
                    }

                    // Agregar el TextView al CardView y el CardView al LinearLayout
                    cardView.addView(textView)
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
}
