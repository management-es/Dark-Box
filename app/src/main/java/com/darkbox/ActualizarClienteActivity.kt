package com.darkbox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.database.*

class ActualizarClienteActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextBuscarCliente: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var textViewDatos: TextView
    private lateinit var textViewFisico: TextView
    private lateinit var textViewLogico: TextView
    private var clienteData: Client? = null // Variable para almacenar los datos del cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_cliente)

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference.child("clientes")

        // Inicializar vistas
        editTextBuscarCliente = findViewById(R.id.editText_buscar_cliente)
        buttonBuscar = findViewById(R.id.button_buscar)
        textViewDatos = findViewById(R.id.textView_datos)
        textViewFisico = findViewById(R.id.textView_fisico)
        textViewLogico = findViewById(R.id.textView_logico)

        // Configurar el botón de búsqueda
        buttonBuscar.setOnClickListener {
            val clienteId = editTextBuscarCliente.text.toString().trim()
            if (clienteId.isNotEmpty()) {
                buscarCliente(clienteId)
            }
        }

        // Configurar el botón para mostrar/ocultar información
        findViewById<Button>(R.id.button_datos).setOnClickListener {
            mostrarInformacionCliente() // Mostrar u ocultar información
        }

        findViewById<Button>(R.id.button_fisico).setOnClickListener {
            textViewFisico.text = "Texto Prueba" // Cambia aquí para mostrar información real
        }

        findViewById<Button>(R.id.button_logico).setOnClickListener {
            textViewLogico.text = "Texto Prueba" // Cambia aquí para mostrar información real
        }
    }

    private fun buscarCliente(clienteId: String) {
        database.child(clienteId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si el cliente existe, extraer la información
                    clienteData = snapshot.getValue(Client::class.java)
                    if (clienteData != null) {
                        // Almacenar información del cliente para usarla al mostrarla
                        textViewDatos.text = """
                            Nombres: ${clienteData?.nombres}
                            Apellidos: ${clienteData?.apellidos}
                            Tipo de documento: ${clienteData?.tipo_documento}
                            Número de documento: ${clienteData?.numero_documento}
                            Dirección: ${clienteData?.direccion}
                            Código de cliente: ${clienteData?.cod_cliente}
                            Contactos: ${clienteData?.contactos}
                            Correo: ${clienteData?.correo}
                            Coordenadas: ${clienteData?.coordenadas}
                            Teléfono: ${clienteData?.telefono}
                        """.trimIndent()
                    }
                } else {
                    textViewDatos.text = "Cliente no encontrado."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                textViewDatos.text = "Error al buscar cliente: ${error.message}"
            }
        })
    }

    private fun mostrarInformacionCliente() {
        if (textViewDatos.visibility == TextView.VISIBLE) {
            // Si está visible, ocultar el TextView
            textViewDatos.visibility = TextView.GONE
        } else {
            // Si está oculto, mostrar el TextView
            textViewDatos.visibility = TextView.VISIBLE
        }
    }
}


data class Client(
    var id: String? = "",
    var cod_cliente: String? = "",
    var nombres: String? = "",
    var apellidos: String? = "",
    var tipo_documento: String? = "",
    var numero_documento: String? = "",
    var direccion: String? = "",
    var coordenadas: String? = "",
    var telefono: String? = "",
    var correo: String? = "",
    var contactos: String? = ""
)
