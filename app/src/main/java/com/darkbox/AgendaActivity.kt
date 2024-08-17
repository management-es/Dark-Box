package com.darkbox

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.app.AlertDialog

class AgendaActivity : ComponentActivity() {

    private lateinit var spinnerClientes: Spinner
    private lateinit var searchView: SearchView
    private lateinit var database: DatabaseReference
    private lateinit var textViewNombreCliente: TextView
    private var allClientes = mutableListOf<String>() // Para almacenar todos los clientes y permitir búsqueda
    private lateinit var spinnerGestion: Spinner
    private lateinit var editTextObservaciones: TextInputEditText
    private lateinit var buttonCargarCliente: Button
    private var fechaSeleccionada: String? = null
    private lateinit var buttonSolicitudInstalacion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        // Referencias a los elementos de la UI
        val buttonCrearAgenda: Button = findViewById(R.id.button_crear_agenda)
        val buttonVerAgenda = findViewById<Button>(R.id.button_ver_agenda)
        val dateInputLayout: View = findViewById(R.id.date_input_layout)
        val dateEditText: EditText = findViewById(R.id.input_date)
        spinnerClientes = findViewById(R.id.spinner_clientes)
        searchView = findViewById(R.id.search_view)
        textViewNombreCliente = findViewById(R.id.text_view_nombre_cliente)
        spinnerGestion = findViewById(R.id.spinner_gestion)
        editTextObservaciones = findViewById(R.id.editText_observaciones)
        buttonCargarCliente = findViewById(R.id.button_cargar_cliente)
        buttonSolicitudInstalacion = findViewById(R.id.button_solicitud_instalacion)

        // Configura el Spinner de Gestión
        configurarSpinnerGestion()

        // Configura el botón Cargar Cliente
        buttonCargarCliente.setOnClickListener {
            mostrarDialogoConfirmacion()
        }


        // Configura el botón Solicitud Instalación
        buttonSolicitudInstalacion.setOnClickListener {
            val intent = Intent(this, SolicitudInstalacionActivity::class.java)
            startActivity(intent)
        }

        // Referencia al botón Otras Gestiones
        val buttonOtrasGestiones = findViewById<Button>(R.id.button_otras_gestiones)
        buttonOtrasGestiones.setOnClickListener {
            val intent = Intent(this, OtrasGestionesActivity::class.java)
            startActivity(intent)
        }


        // Configurar la visibilidad inicial
        dateInputLayout.visibility = View.GONE

        // Listener para el botón Crear Agenda
        buttonCrearAgenda.setOnClickListener {
            dateInputLayout.visibility = View.VISIBLE
            buttonCrearAgenda.visibility = View.GONE
            cargarClientes()  // Cargar clientes al hacer clic en "Crear Agenda"
        }

        buttonVerAgenda.setOnClickListener {
            // Abrir la nueva actividad "Ver Agenda"
            val intent = Intent(this, VerAgendaActivity::class.java)
            startActivity(intent)
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
                val fechaFormateada = "$dayOfMonth/${month + 1}/$year"
                dateEditText.setText(fechaFormateada)

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

    private fun configurarSpinnerGestion() {
        val opcionesGestion = resources.getStringArray(R.array.spinner_gestion_opciones)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGestion)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGestion.adapter = adapter
    }

    private fun mostrarDialogoConfirmacion() {
        val gestionSeleccionada = spinnerGestion.selectedItem.toString()
        val observaciones = editTextObservaciones.text.toString()
        val clienteSeleccionado = spinnerClientes.selectedItem.toString()

        // Verifica si se ha seleccionado un cliente y una fecha
        if (clienteSeleccionado.isNotEmpty() && fechaSeleccionada != null) {
            // Obtener datos del cliente desde Firebase
            database.child(clienteSeleccionado).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombreCliente = snapshot.child("nombres").getValue(String::class.java)
                    val apellidosCliente = snapshot.child("apellidos").getValue(String::class.java)
                    val numeroDocumento = snapshot.child("numeroDocumento").getValue(String::class.java)
                    val direccion = snapshot.child("direccion").getValue(String::class.java)
                    val coordenadas = snapshot.child("coordenadas").getValue(String::class.java)
                    val telefono = snapshot.child("telefono").getValue(String::class.java)
                    val contactos = snapshot.child("contactos").getValue(String::class.java)

                    val datosConfirmacion = """
                        Cliente: $clienteSeleccionado
                        Nombre: ${nombreCliente ?: "Nombre no encontrado"}
                        Apellidos: ${apellidosCliente ?: "Apellidos no encontrados"}
                        Documento: ${numeroDocumento ?: "Documento no encontrado"}
                        Dirección: ${direccion ?: "Dirección no encontrada"}
                        Coordenadas: ${coordenadas ?: "Coordenadas no encontradas"}
                        Teléfono: ${telefono ?: "Teléfono no encontrado"}
                        Contactos: ${contactos ?: "Contactos no encontrados"}
                        Fecha: $fechaSeleccionada
                        Gestión: $gestionSeleccionada
                        Observaciones: $observaciones
                    """.trimIndent()

                    // Mostrar cuadro de diálogo de confirmación
                    AlertDialog.Builder(this@AgendaActivity)
                        .setTitle("Confirmación de Datos")
                        .setMessage("Estos son los datos que serán cargados:\n$datosConfirmacion")
                        .setPositiveButton("Aceptar") { _, _ ->
                            cargarClienteConfirmado(clienteSeleccionado, gestionSeleccionada, observaciones)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AgendaActivity, "Error al obtener los datos del cliente", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@AgendaActivity, "Seleccione un cliente y una fecha", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarClienteConfirmado(clienteSeleccionado: String, gestionSeleccionada: String, observaciones: String) {
        // Obtener datos del cliente desde Firebase
        database.child(clienteSeleccionado).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombreCliente = snapshot.child("nombres").getValue(String::class.java)
                val apellidosCliente = snapshot.child("apellidos").getValue(String::class.java)
                val numeroDocumento = snapshot.child("numeroDocumento").getValue(String::class.java)
                val direccion = snapshot.child("direccion").getValue(String::class.java)
                val coordenadas = snapshot.child("coordenadas").getValue(String::class.java)
                val telefono = snapshot.child("telefono").getValue(String::class.java)
                val contactos = snapshot.child("contactos").getValue(String::class.java)

                // Crear la clave única combinando fecha y codCliente
                val claveUnica = "$fechaSeleccionada-$clienteSeleccionado"

                // Crear un mapa con todos los datos del cliente y las observaciones
                val clienteData: Map<String, Any?> = mapOf(
                    "nombre" to nombreCliente,
                    "apellidos" to apellidosCliente,
                    "numeroDocumento" to numeroDocumento,
                    "direccion" to direccion,
                    "coordenadas" to coordenadas,
                    "telefono" to telefono,
                    "contactos" to contactos,
                    "gestion" to gestionSeleccionada,
                    "observaciones" to observaciones
                )

                // Guardar los datos en Firebase Realtime Database con la clave única
                val database = FirebaseDatabase.getInstance().getReference("agenda")
                val newEntryRef = database.child(claveUnica)  // Usa la clave única para almacenar los datos

                newEntryRef.setValue(clienteData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@AgendaActivity, "Cliente cargado exitosamente", Toast.LENGTH_SHORT).show()
                            // Limpia los campos si lo deseas
                            spinnerGestion.setSelection(0)
                            editTextObservaciones.text?.clear()
                            // Regresar al menú anterior
                            finish()
                        } else {
                            Toast.makeText(this@AgendaActivity, "Error al cargar el cliente", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgendaActivity, "Error al obtener los datos del cliente", Toast.LENGTH_SHORT).show()
            }
        })
    }
}