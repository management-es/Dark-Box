package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
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

        // Referencias a los botones y layouts
        val buttonIngresarCliente: Button = findViewById(R.id.button_ingresar_cliente)
        val clientInputLayout: LinearLayout = findViewById(R.id.client_input_layout)
        val clientDataLayout: LinearLayout = findViewById(R.id.client_data_layout)
        val serviceDataLayout: LinearLayout = findViewById(R.id.service_data_layout)
        val additionalDataLayout: LinearLayout = findViewById(R.id.additional_data_layout)
        val buttonAgregar: Button = findViewById(R.id.button_agregar)

        // Manejar el clic del botón de ingresar cliente
        buttonIngresarCliente.setOnClickListener {
            clientInputLayout.visibility = View.VISIBLE
        }

        // Manejar el clic del botón de mostrar datos del cliente
        findViewById<Button>(R.id.button_show_client_data).setOnClickListener {
            clientDataLayout.visibility = if (clientDataLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Manejar el clic del botón de mostrar datos del servicio
        findViewById<Button>(R.id.button_show_service).setOnClickListener {
            serviceDataLayout.visibility = if (serviceDataLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Manejar el clic del botón de mostrar datos adicionales
        findViewById<Button>(R.id.button_show_additional).setOnClickListener {
            additionalDataLayout.visibility = if (additionalDataLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Manejar el clic del botón de agregar cliente
        buttonAgregar.setOnClickListener {
            val codCliente = findViewById<EditText>(R.id.input_cod_cliente).text.toString()
            val nombres = findViewById<EditText>(R.id.input_nombres).text.toString()
            val apellidos = findViewById<EditText>(R.id.input_apellidos).text.toString()
            val tipoDocumento = findViewById<EditText>(R.id.input_tipo_documento).text.toString()
            val numeroDocumento = findViewById<EditText>(R.id.input_numero_documento).text.toString()
            val direccion = findViewById<EditText>(R.id.input_direccion).text.toString()
            val telefono = findViewById<EditText>(R.id.input_telefono).text.toString()
            val correo = findViewById<EditText>(R.id.input_correo).text.toString()
            val contactos = findViewById<EditText>(R.id.input_contactos).text.toString()
            val estado = findViewById<Spinner>(R.id.spinner_estado).selectedItem.toString()
            val plan = findViewById<EditText>(R.id.input_plan).text.toString()
            val tecnologia = findViewById<EditText>(R.id.input_tecnologia).text.toString()
            val equipos = findViewById<EditText>(R.id.input_equipos).text.toString()
            val ipAntena = findViewById<EditText>(R.id.input_ip_antena).text.toString()
            val ipRemota = findViewById<EditText>(R.id.input_ip_remota).text.toString()
            val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()
            val historial = findViewById<EditText>(R.id.input_historial).text.toString()

            // Verifica que todos los campos obligatorios estén llenos
            if (codCliente.isEmpty()) {
                showMessage("El código del cliente es obligatorio.")
                return@setOnClickListener
            }

            showConfirmationDialogForClient(
                codCliente, nombres, apellidos, tipoDocumento, numeroDocumento,
                direccion, telefono, correo, contactos, estado, plan, tecnologia,
                equipos, ipAntena, ipRemota, observaciones, historial
            )
        }
    }

    private fun showConfirmationDialogForClient(
        codCliente: String, nombres: String, apellidos: String, tipoDocumento: String, numeroDocumento: String,
        direccion: String, telefono: String, correo: String, contactos: String, estado: String, plan: String,
        tecnologia: String, equipos: String, ipAntena: String, ipRemota: String, observaciones: String, historial: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Deseas guardar los datos del cliente?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            saveClientData(
                codCliente, nombres, apellidos, tipoDocumento, numeroDocumento, direccion,
                telefono, correo, contactos, estado, plan, tecnologia, equipos, ipAntena, ipRemota, observaciones, historial
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
        direccion: String, telefono: String, correo: String, contactos: String, estado: String, plan: String,
        tecnologia: String, equipos: String, ipAntena: String, ipRemota: String, observaciones: String, historial: String
    ) {
        // Crear un mapa con los datos del cliente
        val clientData = mapOf(
            "nombres" to nombres,
            "apellidos" to apellidos,
            "tipo_documento" to tipoDocumento,
            "numero_documento" to numeroDocumento,
            "direccion" to direccion,
            "telefono" to telefono,
            "correo" to correo,
            "contactos" to contactos,
            "estado" to estado,
            "plan" to plan,
            "tecnologia" to tecnologia,
            "equipos" to equipos,
            "ip_antena" to ipAntena,
            "ip_remota" to ipRemota,
            "observaciones" to observaciones,
            "historial" to historial
        )

        // Escribir los datos del cliente en la base de datos usando el código del cliente como clave
        database.child("clientes").child(codCliente).setValue(clientData)
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
