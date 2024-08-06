package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
            val codCliente = findViewById<EditText>(R.id.input_cod_cliente).text.toString()
            val nombres = findViewById<EditText>(R.id.input_nombres).text.toString()
            val apellidos = findViewById<EditText>(R.id.input_apellidos).text.toString()
            val tipoDocumento = findViewById<EditText>(R.id.input_tipo_documento).text.toString()
            val numeroDocumento = findViewById<EditText>(R.id.input_numero_documento).text.toString()
            val direccion = findViewById<EditText>(R.id.input_direccion).text.toString()
            val telefono = findViewById<EditText>(R.id.input_telefono).text.toString()
            val correo = findViewById<EditText>(R.id.input_correo).text.toString()
            val contactos = findViewById<EditText>(R.id.input_contactos).text.toString()
            val plan = findViewById<EditText>(R.id.input_plan).text.toString()
            val tecnologia = findViewById<EditText>(R.id.input_tecnologia).text.toString()
            val equipos = findViewById<EditText>(R.id.input_equipos).text.toString()
            val ipAntena = findViewById<EditText>(R.id.input_ip_antena).text.toString()
            val ipRemota = findViewById<EditText>(R.id.input_ip_remota).text.toString()
            val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()
            val historial = findViewById<EditText>(R.id.input_historial).text.toString()

            showConfirmationDialogForClient(codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion, telefono, correo, contactos, plan, tecnologia, equipos, ipAntena, ipRemota, observaciones, historial)
        }
    }

    private fun showConfirmationDialogForClient(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String, plan: String, tecnologia: String,
        equipos: String, ipAntena: String, ipRemota: String, observaciones: String, historial: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Deseas guardar los datos del cliente?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            saveClientData(
                codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion, telefono, correo, contactos,
                plan, tecnologia, equipos, ipAntena, ipRemota, observaciones, historial
            )
            dialog.dismiss()
            finish() // Close the activity and go back to the previous screen
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            finish() // Close the activity and go back to the previous screen
        }
        builder.create().show()
    }

    private fun saveClientData(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String, plan: String, tecnologia: String,
        equipos: String, ipAntena: String, ipRemota: String, observaciones: String, historial: String
    ) {
        val clientData = mapOf(
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
            "historial" to historial
        )

        // Save client data in the database with the provided Cod. Cliente as the key
        database.child("clientes").child(codCliente).setValue(clientData)
            .addOnSuccessListener {
                // Data written successfully
                showMessage("Datos del cliente guardados")
            }
            .addOnFailureListener { exception ->
                // Error handling
                exception.printStackTrace()
                showMessage("Error al guardar los datos del cliente")
            }
    }

    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Resultado")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
