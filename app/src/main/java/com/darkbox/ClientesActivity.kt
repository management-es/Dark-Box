package com.darkbox

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*

class ClientesActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var inputSerialOnu: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference


        // Inicializa las vistas
        inputSerialOnu = findViewById(R.id.input_serial_onu)

        // Referencias a los campos y botones
        val buttonIngresarCliente: Button = findViewById(R.id.button_ingresar_cliente)
        val optionsLayout: View = findViewById(R.id.options_layout)
        val buttonDatosCliente: Button = findViewById(R.id.button_datos_cliente)
        val datosClienteLayout: View = findViewById(R.id.datos_cliente_layout)
        val buttonServicio: Button = findViewById(R.id.button_servicio)
        val servicioLayout: View = findViewById(R.id.servicio_layout)
        val buttonAdicionales: Button = findViewById(R.id.button_adicionales)
        val adicionalesLayout: View = findViewById(R.id.adicionales_layout)
        val buttonAgregarCliente: Button = findViewById(R.id.button_agregar_cliente)
        val buttonUbicacion: Button = findViewById(R.id.button_ubicacion)
        val ubicacionLayout: View = findViewById(R.id.ubicacion_layout)

        // Spinner zona
        val spinnerZona: Spinner = findViewById(R.id.spinner_zona)

        // Spinner de Plan
        val spinnerPlan: Spinner = findViewById(R.id.spinner_plan)

        // Configurar el Spinner para Tipo de Documento
        val spinnerTipoDocumento: Spinner = findViewById(R.id.spinner_tipo_documento)
        ArrayAdapter.createFromResource(
            this,
            R.array.tipo_documento_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipoDocumento.adapter = adapter
        }

        // Configurar el adaptador para el Spinner zona
        ArrayAdapter.createFromResource(
            this,
            R.array.zona_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerZona.adapter = adapter
        }

        // Configurar el adaptador para el Spinner plan
        ArrayAdapter.createFromResource(
            this,
            R.array.plan_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPlan.adapter = adapter
        }

        // Configurar el Spinner de Tecnología
        val spinnerTecnologia: Spinner = findViewById(R.id.spinner_tecnologia)
        ArrayAdapter.createFromResource(
            this,
            R.array.tecnologia_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTecnologia.adapter = adapter
        }

        val inputSerialOnu: EditText = findViewById(R.id.input_serial_onu)
        val inputSerialAntena: EditText = findViewById(R.id.input_serial_antena)
        val inputSerialRouter: EditText = findViewById(R.id.input_serial_router)

        // Listener para el Spinner de Tecnología
        spinnerTecnologia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTecnologia = spinnerTecnologia.selectedItem.toString()
                when (selectedTecnologia) {
                    "Fibra Óptica" -> {
                        inputSerialOnu.visibility = View.VISIBLE
                        inputSerialAntena.visibility = View.GONE
                        inputSerialRouter.visibility = View.GONE
                        loadOnuSerials() // Cargar seriales de ONUs disponibles en estado Bodega
                    }
                    "Radio Enlace" -> {
                        inputSerialOnu.visibility = View.GONE
                        inputSerialAntena.visibility = View.VISIBLE
                        inputSerialRouter.visibility = View.VISIBLE
                    }
                    else -> {
                        inputSerialOnu.visibility = View.GONE
                        inputSerialAntena.visibility = View.GONE
                        inputSerialRouter.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                inputSerialOnu.visibility = View.GONE
                inputSerialAntena.visibility = View.GONE
                inputSerialRouter.visibility = View.GONE
            }
        }


        // Inicializa el layout de datos cliente, servicio y adicionales
        datosClienteLayout.visibility = View.GONE
        servicioLayout.visibility = View.GONE
        adicionalesLayout.visibility = View.GONE

        // Manejar el clic del botón de ingresar cliente
        buttonIngresarCliente.setOnClickListener {
            optionsLayout.visibility = View.VISIBLE
        }

        // Manejar el clic del botón de datos cliente
        buttonDatosCliente.setOnClickListener {
            datosClienteLayout.visibility = View.VISIBLE
            servicioLayout.visibility = View.GONE
            adicionalesLayout.visibility = View.GONE
        }

        // Manejar el clic del botón de ubicación
        buttonUbicacion.setOnClickListener {
            if (ubicacionLayout.visibility == View.GONE) {
                ubicacionLayout.visibility = View.VISIBLE
            } else {
                ubicacionLayout.visibility = View.GONE
            }
        }

        // Manejar el clic del botón de servicio
        buttonServicio.setOnClickListener {
            datosClienteLayout.visibility = View.GONE
            servicioLayout.visibility = View.VISIBLE
            adicionalesLayout.visibility = View.GONE
        }

        // Manejar el clic del botón de adicionales
        buttonAdicionales.setOnClickListener {
            datosClienteLayout.visibility = View.GONE
            servicioLayout.visibility = View.GONE
            adicionalesLayout.visibility = View.VISIBLE
        }



        // Manejar el clic del botón de agregar cliente
        buttonAgregarCliente.setOnClickListener {
            if (validateClientData()) {
                val codCliente = findViewById<EditText>(R.id.input_cod_cliente).text.toString()
                val nombres = findViewById<EditText>(R.id.input_nombres).text.toString()
                val apellidos = findViewById<EditText>(R.id.input_apellidos).text.toString()
                val tipoDocumento = spinnerTipoDocumento.selectedItem.toString()
                val numeroDocumento = findViewById<EditText>(R.id.input_numero_documento).text.toString()
                val direccion = findViewById<EditText>(R.id.input_direccion).text.toString()
                val telefono = findViewById<EditText>(R.id.input_telefono).text.toString()
                val correo = findViewById<EditText>(R.id.input_correo).text.toString()
                val contactos = findViewById<EditText>(R.id.input_contactos).text.toString()
                val plan = spinnerPlan.selectedItem.toString()
                val tecnologia = spinnerTecnologia.selectedItem.toString()
                val equipos = findViewById<EditText>(R.id.input_equipos).text.toString()
                val ipAntena = findViewById<EditText>(R.id.input_ip_antena).text.toString()
                val ipRemota = findViewById<EditText>(R.id.input_ip_remota).text.toString()
                val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()
                val historial = findViewById<EditText>(R.id.input_historial).text.toString()
                val zona = spinnerZona.selectedItem.toString()
                val coordenadas = findViewById<EditText>(R.id.input_coordenadas).text.toString()
                val serialOnu = findViewById<EditText>(R.id.input_serial_onu).text.toString()
                showConfirmationDialogForClient(
                    codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion,
                    telefono, correo, contactos, plan, tecnologia, equipos, ipAntena, ipRemota,
                    observaciones, historial, zona, coordenadas, serialOnu
                )
            }
        }
    }

    private fun loadOnuSerials() {
        val serialOnuList = mutableListOf<String>()
        val serialOnuAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, serialOnuList)

        database.child("inventario").orderByChild("estado").equalTo("Bodega").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (child in snapshot.children) {
                    val serial = child.key // Obtener el serial desde la clave del nodo
                    if (serial != null) {
                        serialOnuList.add(serial)
                    }
                }

                if (serialOnuList.isNotEmpty()) {
                    // Mostrar el AlertDialog con los seriales
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Selecciona un Serial ONU")
                        .setItems(serialOnuList.toTypedArray()) { _, which ->
                            val selectedSerial = serialOnuList[which]
                            inputSerialOnu.setText(selectedSerial)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                } else {
                    Toast.makeText(this, "No se encontraron ONUs en estado Bodega", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontraron ONUs en estado Bodega", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            // Añadido log de error
            Log.e("ClientesActivity", "Error al cargar los seriales de ONUs", exception)
            Toast.makeText(this, "Error al cargar los seriales de ONUs", Toast.LENGTH_SHORT).show()
        }
    }



    private fun validateClientData(): Boolean {
        val nombres = findViewById<EditText>(R.id.input_nombres).text.toString()
        val apellidos = findViewById<EditText>(R.id.input_apellidos).text.toString()
        val numeroDocumento = findViewById<EditText>(R.id.input_numero_documento).text.toString()
        val direccion = findViewById<EditText>(R.id.input_direccion).text.toString()
        val telefono = findViewById<EditText>(R.id.input_telefono).text.toString()
        val correo = findViewById<EditText>(R.id.input_correo).text.toString()
        val contactos = findViewById<EditText>(R.id.input_contactos).text.toString()
        val equipos = findViewById<EditText>(R.id.input_equipos).text.toString()
        val ipAntena = findViewById<EditText>(R.id.input_ip_antena).text.toString()
        val ipRemota = findViewById<EditText>(R.id.input_ip_remota).text.toString()
        val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()
        val historial = findViewById<EditText>(R.id.input_historial).text.toString()
        val coordenadas = findViewById<EditText>(R.id.input_coordenadas).text.toString()

        return when {
            nombres.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa los nombres del cliente", Toast.LENGTH_SHORT).show()
                false
            }
            apellidos.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa los apellidos del cliente", Toast.LENGTH_SHORT).show()
                false
            }
            numeroDocumento.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa el número de documento", Toast.LENGTH_SHORT).show()
                false
            }
            direccion.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa la dirección", Toast.LENGTH_SHORT).show()
                false
            }
            telefono.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa el teléfono", Toast.LENGTH_SHORT).show()
                false
            }
            correo.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa el correo", Toast.LENGTH_SHORT).show()
                false
            }
            contactos.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa los contactos", Toast.LENGTH_SHORT).show()
                false
            }
            equipos.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa los equipos", Toast.LENGTH_SHORT).show()
                false
            }
            ipAntena.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa la IP de la antena", Toast.LENGTH_SHORT).show()
                false
            }
            ipRemota.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa la IP remota", Toast.LENGTH_SHORT).show()
                false
            }
            observaciones.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa observaciones", Toast.LENGTH_SHORT).show()
                false
            }
            historial.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa el historial", Toast.LENGTH_SHORT).show()
                false
            }
            coordenadas.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa las coordenadas", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun showConfirmationDialogForClient(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String, plan: String, tecnologia: String,
        equipos: String, ipAntena: String, ipRemota: String, observaciones: String, historial: String, zona: String,
        coordenadas: String, serialOnu: String
    ) {
        val message = """
            Código Cliente: $codCliente
            Nombres: $nombres
            Apellidos: $apellidos
            Tipo Documento: $tipoDocumento
            Número Documento: $numeroDocumento
            Dirección: $direccion
            Teléfono: $telefono
            Correo: $correo
            Contactos: $contactos
            Plan: $plan
            Tecnología: $tecnologia
            Serial ONU: $serialOnu
            Equipos: $equipos
            IP Antena: $ipAntena
            IP Remota: $ipRemota
            Observaciones: $observaciones
            Historial: $historial
            Zona: $zona
            Coordenadas: $coordenadas
            
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Confirmar Datos")
            .setMessage(message)
            .setPositiveButton("Confirmar") { _, _ ->
                saveClientData(
                    codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion,
                    telefono, correo, contactos, plan, tecnologia, equipos, ipAntena, ipRemota,
                    observaciones, historial, zona, coordenadas, serialOnu
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveClientData(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String, plan: String, tecnologia: String,
        equipos: String, ipAntena: String, ipRemota: String, observaciones: String, historial: String, zona: String,
        coordenadas: String, serialOnu: String
    ) {
        val clientRef = database.child("clientes").child(codCliente)

        // Verificar si el cliente ya existe
        clientRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // El cliente ya existe, mostrar un mensaje de error
                Toast.makeText(this, "El código de cliente ya existe", Toast.LENGTH_SHORT).show()
            } else {
                // El cliente no existe, proceder a guardar los datos
                val clientData = mapOf(
                    "cod_cliente" to codCliente,
                    "nombres" to nombres,
                    "apellidos" to apellidos,
                    "tipo_documento" to tipoDocumento,
                    "numero_documento" to numeroDocumento,
                    "direccion" to direccion,
                    "telefono" to telefono,
                    "correo" to correo,
                    "contactos" to contactos,
                    "plan" to plan,
                    "tecnologia" to tecnologia,
                    "equipos" to equipos,
                    "ip_antena" to ipAntena,
                    "ip_remota" to ipRemota,
                    "observaciones" to observaciones,
                    "historial" to historial,
                    "zona" to zona,
                    "coordenadas" to coordenadas,
                    "serial_onu" to serialOnu
                )

                clientRef.setValue(clientData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cliente agregado exitosamente", Toast.LENGTH_SHORT).show()
                        finish() // Opcional: cierra la actividad
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al agregar cliente", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al verificar el código de cliente", Toast.LENGTH_SHORT).show()
        }
    }


}

