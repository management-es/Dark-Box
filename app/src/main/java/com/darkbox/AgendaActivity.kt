package com.darkbox

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import java.util.*

class AgendaActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        // Referencias a los elementos de la UI
        val buttonCrearAgenda: Button = findViewById(R.id.button_crear_agenda)
        val dateInputLayout: View = findViewById(R.id.date_input_layout)
        val dateEditText: EditText = findViewById(R.id.input_date)

        // Configurar la visibilidad inicial
        dateInputLayout.visibility = View.GONE

        // Listener para el botón Crear Agenda
        buttonCrearAgenda.setOnClickListener {
            dateInputLayout.visibility = View.VISIBLE
            buttonCrearAgenda.visibility = View.GONE
        }

        // Listener para mostrar el DatePickerDialog
        dateEditText.setOnClickListener {
            showDatePickerDialog(dateEditText)
        }
    }

    private fun showDatePickerDialog(dateEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                dateEditText.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
