package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClientesActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference

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

        // Spinner de Tecnologia
        val spinnerTecnologia: Spinner = findViewById(R.id.spinner_tecnologia)


        // Configurar el adaptador para el Spinner zona
        ArrayAdapter.createFromResource(
            this,
            R.array.zona_options, // La referencia al array de strings
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerZona.adapter = adapter
        }

        // Configurar el adaptador para el Spinner plan
        ArrayAdapter.createFromResource(
            this,
            R.array.plan_options, // La referencia al array de strings
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPlan.adapter = adapter
        }

        // Configurar el adaptador para el Spinner tecnologia
        ArrayAdapter.createFromResource(
            this,
            R.array.tecnologia_options, // La referencia al array de strings
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTecnologia.adapter = adapter
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
            // Alternar la visibilidad del layout de ubicación
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
                val tipoDocumento = findViewById<EditText>(R.id.input_tipo_documento).text.toString()
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

                showConfirmationDialogForClient(
                    codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion,
                    telefono, correo, contactos, plan, tecnologia, equipos, ipAntena, ipRemota,
                    observaciones, historial, zona, coordenadas
                )
            }
        }
    }

    private fun validateClientData(): Boolean {
        val nombres = findViewById<EditText>(R.id.input_nombres).text.toString()
        val apellidos = findViewById<EditText>(R.id.input_apellidos).text.toString()
        val tipoDocumento = findViewById<EditText>(R.id.input_tipo_documento).text.toString()
        val numeroDocumento = findViewById<EditText>(R.id.input_numero_documento).text.toString()
        val direccion = findViewById<EditText>(R.id.input_direccion).text.toString()
        val telefono = findViewById<EditText>(R.id.input_telefono).text.toString()
        val correo = findViewById<EditText>(R.id.input_correo).text.toString()
        val contactos = findViewById<EditText>(R.id.input_contactos).text.toString()
        val tecnologia = findViewById<EditText>(R.id.input_tecnologia).text.toString()
        val equipos = findViewById<EditText>(R.id.input_equipos).text.toString()
        val ipAntena = findViewById<EditText>(R.id.input_ip_antena).text.toString()
        val ipRemota = findViewById<EditText>(R.id.input_ip_remota).text.toString()
        val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()
        val coordenadas = findViewById<EditText>(R.id.input_coordenadas).text.toString()

        return when {
            nombres.isEmpty() -> {
                showMessage("El campo 'Nombres' es obligatorio.")
                false
            }
            apellidos.isEmpty() -> {
                showMessage("El campo 'Apellidos' es obligatorio.")
                false
            }
            tipoDocumento.isEmpty() -> {
                showMessage("El campo 'Tipo Documento' es obligatorio.")
                false
            }
            numeroDocumento.isEmpty() -> {
                showMessage("El campo 'Número Documento' es obligatorio.")
                false
            }
            direccion.isEmpty() -> {
                showMessage("El campo 'Dirección' es obligatorio.")
                false
            }
            telefono.isEmpty() -> {
                showMessage("El campo 'Teléfono' es obligatorio.")
                false
            }
            correo.isEmpty() -> {
                showMessage("El campo 'Correo' es obligatorio.")
                false
            }
            contactos.isEmpty() -> {
                showMessage("El campo 'Contactos' es obligatorio.")
                false
            }
            tecnologia.isEmpty() -> {
                showMessage("El campo 'Tecnología' es obligatorio.")
                false
            }
            equipos.isEmpty() -> {
                showMessage("El campo 'Equipos' es obligatorio.")
                false
            }
            ipAntena.isEmpty() -> {
                showMessage("El campo 'IP Antena' es obligatorio.")
                false
            }
            ipRemota.isEmpty() -> {
                showMessage("El campo 'IP Remota' es obligatorio.")
                false
            }
            observaciones.isEmpty() -> {
                showMessage("El campo 'Observaciones' es obligatorio.")
                false
            }
            coordenadas.isEmpty() -> {
                showMessage("El campo 'Coordenadas' es obligatorio.")
                false
            }

            else -> true
        }
    }

    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showConfirmationDialogForClient(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String,
        numeroDocumento: String, direccion: String, telefono: String, correo: String,
        contactos: String, plan: String, tecnologia: String, equipos: String, ipAntena: String,
        ipRemota: String, observaciones: String, historial: String, zona: String, coordenadas: String
    ) {
        AlertDialog.Builder(this)
            .setTitle("Confirmación")
            .setMessage("¿Deseas guardar los datos del cliente?")
            .setPositiveButton("Sí") { _, _ ->
                saveClientData(
                    codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion,
                    telefono, correo, contactos, plan, tecnologia, equipos, ipAntena, ipRemota,
                    observaciones, historial, zona, coordenadas
                )
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun saveClientData(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String,
        numeroDocumento: String, direccion: String, telefono: String, correo: String,
        contactos: String, plan: String, tecnologia: String, equipos: String, ipAntena: String,
        ipRemota: String, observaciones: String, historial: String, zona: String, coordenadas: String
    ) {
        val clientData = mapOf(
            "nombres" to nombres,
            "apellidos" to apellidos,
            "tipoDocumento" to tipoDocumento,
            "numeroDocumento" to numeroDocumento,
            "direccion" to direccion,
            "telefono" to telefono,
            "correo" to correo,
            "contactos" to contactos,
            "plan" to plan,
            "tecnologia" to tecnologia,
            "equipos" to equipos,
            "ipAntena" to ipAntena,
            "ipRemota" to ipRemota,
            "observaciones" to observaciones,
            "historial" to historial,
            "zona" to zona,
            "coordenadas" to coordenadas
        )

        database.child("clientes").child(codCliente).setValue(clientData)
            .addOnSuccessListener {
                showMessage("Datos del cliente guardados exitosamente.")
                finish() // Regresa al menú anterior
            }
            .addOnFailureListener {
                showMessage("Error al guardar los datos del cliente.")
            }
    }
}
