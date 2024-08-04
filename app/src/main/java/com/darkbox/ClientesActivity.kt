package com.darkbox

import android.os.Bundle
import android.view.View
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

        val buttonIngresarCliente: Button = findViewById(R.id.button_ingresar_cliente)
        val clientInputLayout: View = findViewById(R.id.client_input_layout)
        val buttonShowClientData: Button = findViewById(R.id.button_show_client_data)
        val clientDataLayout: View = findViewById(R.id.client_data_layout)
        val buttonShowService: Button = findViewById(R.id.button_show_service)
        val serviceDataLayout: View = findViewById(R.id.service_data_layout)
        val buttonShowAdditional: Button = findViewById(R.id.button_show_additional)
        val additionalDataLayout: View = findViewById(R.id.additional_data_layout)
        val buttonAgregar: Button = findViewById(R.id.button_agregar)

        // Manejar el clic del botón de ingresar cliente
        buttonIngresarCliente.setOnClickListener {
            clientInputLayout.visibility = View.VISIBLE
        }

        // Manejar el clic del botón de mostrar datos del cliente
        buttonShowClientData.setOnClickListener {
            clientDataLayout.visibility = if (clientDataLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Manejar el clic del botón de mostrar datos del servicio
        buttonShowService.setOnClickListener {
            serviceDataLayout.visibility = if (serviceDataLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Manejar el clic del botón de mostrar datos adicionales
        buttonShowAdditional.setOnClickListener {
            additionalDataLayout.visibility = if (additionalDataLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Manejar el clic del botón de agregar
        buttonAgregar.setOnClickListener {
            val estado = findViewById<Spinner>(R.id.spinner_estado).selectedItem.toString()
            val nombres = findViewById<EditText>(R.id.input_nombres).text.toString()
            val apellidos = findViewById<EditText>(R.id.input_apellidos).text.toString()
            val tipoDocumento = findViewById<EditText>(R.id.input_tipo_documento).text.toString()
            val numeroDocumento = findViewById<EditText>(R.id.input_numero_documento).text.toString()
            val direccion = findViewById<EditText>(R.id.input_direccion).text.toString()
            val telefono = findViewById<EditText>(R.id.input_telefono).text.toString()
            val correo = findViewById<EditText>(R.id.input_correo).text.toString()
            val contactos = findViewById<EditText>(R.id.input_contactos).text.toString()
            val codCliente = findViewById<EditText>(R.id.input_cod_cliente).text.toString()
            val plan = findViewById<EditText>(R.id.input_plan).text.toString()
            val tecnologia = findViewById<EditText>(R.id.input_tecnologia).text.toString()
            val equipos = findViewById<EditText>(R.id.input_equipos).text.toString()
            val ipAntena = findViewById<EditText>(R.id.input_ip_antena).text.toString()
            val ipRemota = findViewById<EditText>(R.id.input_ip_remota).text.toString()
            val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()
            val historial = findViewById<EditText>(R.id.input_historial).text.toString()

            showConfirmationDialogForClient(
                estado, nombres, apellidos, tipoDocumento, numeroDocumento, direccion, telefono,
                correo, contactos, codCliente, plan, tecnologia, equipos, ipAntena, ipRemota,
                observaciones, historial
            )
        }
    }

    private fun showConfirmationDialogForClient(
        estado: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String,
        codCliente: String, plan: String, tecnologia: String, equipos: String, ipAntena: String,
        ipRemota: String, observaciones: String, historial: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Deseas guardar los datos del cliente?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            saveClientData(
                estado, nombres, apellidos, tipoDocumento, numeroDocumento, direccion, telefono,
                correo, contactos, codCliente, plan, tecnologia, equipos, ipAntena, ipRemota,
                observaciones, historial
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
        estado: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String,
        codCliente: String, plan: String, tecnologia: String, equipos: String, ipAntena: String,
        ipRemota: String, observaciones: String, historial: String
    ) {
        // Crear un mapa con los datos del cliente
        val clientData = mapOf(
            "estado" to estado,
            "nombres" to nombres,
            "apellidos" to apellidos,
            "tipoDocumento" to tipoDocumento,
            "numeroDocumento" to numeroDocumento,
            "direccion" to direccion,
            "telefono" to telefono,
            "correo" to correo,
            "contactos" to contactos,
            "codCliente" to codCliente,
            "plan" to plan,
            "tecnologia" to tecnologia,
            "equipos" to equipos,
            "ipAntena" to ipAntena,
            "ipRemota" to ipRemota,
            "observaciones" to observaciones,
            "historial" to historial
        )

        // Escribir los datos del cliente en la base de datos con una clave única
        database.child("clientes").push().setValue(clientData)
            .addOnSuccessListener {
                // Datos escritos correctamente
                showMessage("Datos del cliente guardados")
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
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
