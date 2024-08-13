package com.darkbox

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.*
import java.util.*

class AgendaActivity : ComponentActivity() {

    private lateinit var spinnerClientes: Spinner
    private lateinit var searchView: SearchView
    private lateinit var database: DatabaseReference
    private var allClientes = mutableListOf<String>() // Para almacenar todos los clientes y permitir búsqueda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        // Referencias a los elementos de la UI
        val buttonCrearAgenda: Button = findViewById(R.id.button_crear_agenda)
        val dateInputLayout: View = findViewById(R.id.date_input_layout)
        val dateEditText: EditText = findViewById(R.id.input_date)
        spinnerClientes = findViewById(R.id.spinner_clientes)
        searchView = findViewById(R.id.search_view)

        // Configurar la visibilidad inicial
        dateInputLayout.visibility = View.GONE

        // Listener para el botón Crear Agenda
        buttonCrearAgenda.setOnClickListener {
            dateInputLayout.visibility = View.VISIBLE
            buttonCrearAgenda.visibility = View.GONE
            cargarClientes()  // Cargar clientes al hacer clic en "Crear Agenda"
        }

        // Listener para mostrar el DatePickerDialog
        dateEditText.setOnClickListener {
            showDatePickerDialog(dateEditText)
        }

        // Configurar el SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarClientes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarClientes(newText)
                return true
            }
        })
    }

    private fun cargarClientes() {
        // Obtener referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("clientes")

        // Escuchar los cambios en los datos
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allClientes.clear()
                for (clienteSnapshot in snapshot.children) {
                    val codCliente = clienteSnapshot.key
                    codCliente?.let {
                        allClientes.add(it)
                    }
                }

                // Configurar el adaptador para el Spinner
                actualizarSpinner(allClientes)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
                Toast.makeText(this@AgendaActivity, "Error al cargar los clientes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarSpinner(clientes: List<String>) {
        val adapter = ArrayAdapter(this@AgendaActivity, android.R.layout.simple_spinner_item, clientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClientes.adapter = adapter
        spinnerClientes.visibility = View.VISIBLE  // Hacer visible el Spinner después de cargar los datos
    }

    private fun filtrarClientes(query: String?) {
        val filteredClientes = allClientes.filter { cliente ->
            cliente.contains(query ?: "", ignoreCase = true) || // Filtro por codCliente
                    buscarPorCampoEnCliente(cliente, "nombres", query) || // Filtro por nombres
                    buscarPorCampoEnCliente(cliente, "apellidos", query) || // Filtro por apellidos
                    buscarPorCampoEnCliente(cliente, "numeroDocumento", query) // Filtro por número de documento
        }
        actualizarSpinner(filteredClientes)
    }

    private fun buscarPorCampoEnCliente(cliente: String, campo: String, query: String?): Boolean {
        // Implementa la lógica para buscar por otros campos si están disponibles en el cliente
        // Por ejemplo, podrías realizar una consulta adicional a Firebase para obtener estos campos
        // Para simplificar, este método siempre devuelve false en este ejemplo
        return false
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
