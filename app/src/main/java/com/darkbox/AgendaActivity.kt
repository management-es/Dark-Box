package com.darkbox

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.*
import java.util.*
import com.google.android.material.textfield.TextInputEditText
import android.widget.ArrayAdapter

class AgendaActivity : ComponentActivity() {

    private lateinit var spinnerClientes: Spinner
    private lateinit var searchView: SearchView
    private lateinit var database: DatabaseReference
    private lateinit var textViewNombreCliente: TextView
    private var allClientes = mutableListOf<String>() // Para almacenar todos los clientes y permitir búsqueda
    private lateinit var spinnerGestion: Spinner
    private lateinit var editTextObservaciones: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        // Referencias a los elementos de la UI
        val buttonCrearAgenda: Button = findViewById(R.id.button_crear_agenda)
        val dateInputLayout: View = findViewById(R.id.date_input_layout)
        val dateEditText: EditText = findViewById(R.id.input_date)
        spinnerClientes = findViewById(R.id.spinner_clientes)
        searchView = findViewById(R.id.search_view)
        textViewNombreCliente = findViewById(R.id.text_view_nombre_cliente)
        spinnerGestion = findViewById(R.id.spinner_gestion)

        // Configura el Spinner
        configurarSpinnerGestion()

        editTextObservaciones = findViewById(R.id.editText_observaciones)



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

        // Listener para el spinner para mostrar el nombre del cliente seleccionado
        spinnerClientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val codCliente = parent.getItemAtPosition(position).toString()
                mostrarNombreCliente(codCliente)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                textViewNombreCliente.text = ""
            }
        }
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

    private fun mostrarNombreCliente(codCliente: String) {
        database.child(codCliente).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombreCliente = snapshot.child("nombres").getValue(String::class.java)
                val apellidosCliente = snapshot.child("apellidos").getValue(String::class.java)
                val numeroDocumento = snapshot.child("numeroDocumento").getValue(String::class.java)
                val direccion = snapshot.child("direccion").getValue(String::class.java)
                val coordenadas = snapshot.child("coordenadas").getValue(String::class.java)
                val telefono = snapshot.child("telefono").getValue(String::class.java)
                val contactos = snapshot.child("contactos").getValue(String::class.java)

                val informacionCliente = """
                Nombre: ${nombreCliente ?: "Nombre no encontrado"}
                Apellidos: ${apellidosCliente ?: "Apellidos no encontrados"}
                Documento: ${numeroDocumento ?: "Documento no encontrado"}
                Dirección: ${direccion ?: "Dirección no encontrada"}
                Coordenadas: ${coordenadas ?: "Coordenadas no encontradas"}
                Teléfono: ${telefono ?: "Teléfono no encontrado"}
                Contactos: ${contactos ?: "Contactos no encontrados"}
            """.trimIndent()

                textViewNombreCliente.text = informacionCliente
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgendaActivity, "Error al obtener los datos del cliente", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun filtrarClientes(query: String?) {
        val filteredClientes = allClientes.filter { cliente ->
            cliente.contains(query ?: "", ignoreCase = true)
        }
        actualizarSpinner(filteredClientes)
    }

    private fun showDatePickerDialog(dateEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                dateEditText.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun configurarSpinnerGestion() {
        val opcionesGestion = resources.getStringArray(R.array.spinner_gestion_opciones)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGestion)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGestion.adapter = adapter
    }
}

