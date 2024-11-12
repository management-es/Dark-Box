package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter

class ActDatosActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextBuscarCliente: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var textViewDatos: TextView
    private lateinit var buttonEditar: Button
    private lateinit var buttonGuardar: Button

    private lateinit var editTextNombres: EditText
    private lateinit var editTextApellidos: EditText
    private lateinit var spinnerTipoDocumento: Spinner
    private lateinit var editTextNumeroDocumento: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var editTextCodigoCliente: EditText
    private lateinit var editTextContactos: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var editTextCoordenadas: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var nombreUsuario: String
    private lateinit var zonaUsuario: String

    private var clienteData: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actdatos)


        // Obtener el nombre y la zona del usuario desde el intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario desconocido"
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona desconocida"

        // Mostrar el nombre del usuario en el TextView
        val textViewUsuarioLogueado: TextView = findViewById(R.id.textView_usuario_logueado)
        textViewUsuarioLogueado.text = "Usuario: $nombreUsuario"

        // Mostrar el AlertDialog con la zona del usuario
        mostrarDialogoZona(zonaUsuario)

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference.child("clientes")


        // Inicializar vistas
        editTextBuscarCliente = findViewById(R.id.editText_buscar_cliente)
        buttonBuscar = findViewById(R.id.button_buscar)
        textViewDatos = findViewById(R.id.textView_datos)
        buttonEditar = findViewById(R.id.button_editar)
        buttonGuardar = findViewById(R.id.button_guardar)

        // Inicializar EditTexts
        editTextNombres = findViewById(R.id.editText_nombres)
        editTextApellidos = findViewById(R.id.editText_apellidos)

        editTextNumeroDocumento = findViewById(R.id.editText_numero_documento)
        editTextDireccion = findViewById(R.id.editText_direccion)
        editTextCodigoCliente = findViewById(R.id.editText_codigo_cliente)
        editTextContactos = findViewById(R.id.editText_contactos)
        editTextCorreo = findViewById(R.id.editText_correo)
        editTextCoordenadas = findViewById(R.id.editText_coordenadas)
        editTextTelefono = findViewById(R.id.editText_telefono)

        spinnerTipoDocumento = findViewById(R.id.spinner_tipo_documento)

        // Deshabilitar los botones inicialmente
        buttonEditar.isEnabled = false
        buttonGuardar.isEnabled = false

        // Configurar el adaptador para el Spinner
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.tipo_documento_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDocumento.adapter = adapter

        // Configurar el botón de búsqueda
        buttonBuscar.setOnClickListener {

            // Mostrar pantalla de carga
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)

            val clienteId = editTextBuscarCliente.text.toString().trim()
            if (clienteId.isNotEmpty()) {
                buscarCliente(clienteId)
            }
        }

        // Configurar el botón de editar
        buttonEditar.setOnClickListener {
            toggleEditMode(true)
        }

        setupSaveButton()


    }

    private fun mostrarDialogoZona(zona: String) {
        val mensaje = "Solamente tienes autorizado editar los datos de la zona: $zona"
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Información de Zona")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }


    private fun buscarCliente(clienteId: String) {
        database.child(clienteId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    clienteData = snapshot.getValue(Client::class.java)
                    if (clienteData != null) {
                        // Comprobar si el usuario es un administrador o si el cliente pertenece a la zona
                        if (zonaUsuario == "Set-Admin" || clienteData!!.zona == zonaUsuario) {
                            // Mostrar datos en el TextView
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

                            // Cargar datos en los EditTexts
                            editTextNombres.setText(clienteData?.nombres)
                            editTextApellidos.setText(clienteData?.apellidos)
                            editTextNumeroDocumento.setText(clienteData?.numero_documento)
                            editTextDireccion.setText(clienteData?.direccion)
                            editTextCodigoCliente.setText(clienteData?.cod_cliente)
                            editTextContactos.setText(clienteData?.contactos)
                            editTextCorreo.setText(clienteData?.correo)
                            editTextCoordenadas.setText(clienteData?.coordenadas)
                            editTextTelefono.setText(clienteData?.telefono)

                            // Configurar el adaptador para cargar el tipo documento del cliente
                            val tipoDocumentoIndex = resources.getStringArray(R.array.tipo_documento_options)
                                .indexOf(clienteData?.tipo_documento)
                            if (tipoDocumentoIndex >= 0) {
                                spinnerTipoDocumento.setSelection(tipoDocumentoIndex)
                            }

                            // Habilitar los botones de editar y guardar
                            buttonEditar.isEnabled = true
                            buttonGuardar.isEnabled = true

                            // Ocultar los EditTexts inicialmente
                            hideEditTexts()
                        } else {
                            textViewDatos.text = "Este cliente no pertenece a tu zona."
                            buttonEditar.isEnabled = false
                            buttonGuardar.isEnabled = false
                        }
                    } else {
                        textViewDatos.text = "Datos del cliente no válidos."
                        buttonEditar.isEnabled = false
                        buttonGuardar.isEnabled = false
                    }
                } else {
                    textViewDatos.text = "Cliente no encontrado."
                    buttonEditar.isEnabled = false
                    buttonGuardar.isEnabled = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                textViewDatos.text = "Error al buscar cliente: ${error.message}"
                buttonEditar.isEnabled = false
                buttonGuardar.isEnabled = false
            }
        })
    }



    private fun toggleEditMode(editing: Boolean) {
        if (editing) {
            // Mostrar EditTexts y TextViews correspondientes
            textViewDatos.visibility = TextView.GONE

            // Muestra los TextViews
            findViewById<TextView>(R.id.textView_nombres).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_apellidos).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_tipo_documento).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_numero_documento).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_direccion).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_codigo_cliente).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_contactos).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_correo).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_coordenadas).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_telefono).visibility = TextView.VISIBLE

            // Muestra los EditTexts
            editTextNombres.visibility = EditText.VISIBLE
            editTextApellidos.visibility = EditText.VISIBLE

            spinnerTipoDocumento.visibility = Spinner.VISIBLE
            editTextNumeroDocumento.visibility = EditText.VISIBLE
            editTextDireccion.visibility = EditText.VISIBLE
            editTextCodigoCliente.visibility = EditText.VISIBLE
            editTextContactos.visibility = EditText.VISIBLE
            editTextCorreo.visibility = EditText.VISIBLE
            editTextCoordenadas.visibility = EditText.VISIBLE
            editTextTelefono.visibility = EditText.VISIBLE
        } else {
            // Ocultar EditTexts y mostrar TextViews
            textViewDatos.visibility = TextView.VISIBLE
            hideEditTexts()
            hideTextViews()
        }
    }

    private fun hideTextViews() {
        findViewById<TextView>(R.id.textView_nombres).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_apellidos).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_tipo_documento).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_numero_documento).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_direccion).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_codigo_cliente).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_contactos).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_correo).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_coordenadas).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_telefono).visibility = TextView.GONE
    }

    private fun hideEditTexts() {
        editTextNombres.visibility = EditText.GONE
        editTextApellidos.visibility = EditText.GONE
        spinnerTipoDocumento.visibility = Spinner.GONE
        editTextNumeroDocumento.visibility = EditText.GONE
        editTextDireccion.visibility = EditText.GONE
        editTextCodigoCliente.visibility = EditText.GONE
        editTextContactos.visibility = EditText.GONE
        editTextCorreo.visibility = EditText.GONE
        editTextCoordenadas.visibility = EditText.GONE
        editTextTelefono.visibility = EditText.GONE
    }

    private fun setupSaveButton() {
        buttonGuardar.setOnClickListener {


            // Tomar los valores de los EditText
            val nuevosDatos = mutableMapOf<String, String>()
            var cambios = false // Variable para verificar si hay cambios

            val nombresNuevo = editTextNombres.text.toString().trim()
            if (nombresNuevo != clienteData?.nombres) {
                nuevosDatos["nombres"] = nombresNuevo
                cambios = true
            }

            val apellidosNuevo = editTextApellidos.text.toString().trim()
            if (apellidosNuevo != clienteData?.apellidos) {
                nuevosDatos["apellidos"] = apellidosNuevo
                cambios = true
            }

            val tipoDocumentoNuevo = spinnerTipoDocumento.selectedItem.toString()
            if (tipoDocumentoNuevo != clienteData?.tipo_documento) {
                nuevosDatos["tipo_documento"] = tipoDocumentoNuevo
                cambios = true
            }

            val numeroDocumentoNuevo = editTextNumeroDocumento.text.toString().trim()
            if (numeroDocumentoNuevo != clienteData?.numero_documento) {
                nuevosDatos["numero_documento"] = numeroDocumentoNuevo
                cambios = true
            }

            val direccionNuevo = editTextDireccion.text.toString().trim()
            if (direccionNuevo != clienteData?.direccion) {
                nuevosDatos["direccion"] = direccionNuevo
                cambios = true
            }

            val codigoClienteNuevo = editTextCodigoCliente.text.toString().trim()
            if (codigoClienteNuevo != clienteData?.cod_cliente) {
                nuevosDatos["cod_cliente"] = codigoClienteNuevo
                cambios = true
            }

            val contactosNuevo = editTextContactos.text.toString().trim()
            if (contactosNuevo != clienteData?.contactos) {
                nuevosDatos["contactos"] = contactosNuevo
                cambios = true
            }

            val correoNuevo = editTextCorreo.text.toString().trim()
            if (correoNuevo != clienteData?.correo) {
                nuevosDatos["correo"] = correoNuevo
                cambios = true
            }

            val coordenadasNuevo = editTextCoordenadas.text.toString().trim()
            if (coordenadasNuevo != clienteData?.coordenadas) {
                nuevosDatos["coordenadas"] = coordenadasNuevo
                cambios = true
            }

            val telefonoNuevo = editTextTelefono.text.toString().trim()
            if (telefonoNuevo != clienteData?.telefono) {
                nuevosDatos["telefono"] = telefonoNuevo
                cambios = true
            }

            // Verificar si hay cambios
            if (cambios) {
                if (codigoClienteNuevo != clienteData?.cod_cliente) {
                    // Verificar si el código de cliente ya existe en la base de datos
                    database.child(codigoClienteNuevo).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                // Mostrar mensaje si el código ya existe
                                Toast.makeText(this@ActDatosActivity, "El código de cliente ya existe.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Si no existe, proceder con los cambios
                                showConfirmationDialog(nuevosDatos)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@ActDatosActivity, "Error al verificar el código de cliente: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // Si el código de cliente no ha cambiado, procede con los cambios
                    showConfirmationDialog(nuevosDatos)
                }
            } else {
                Toast.makeText(this, "No hay datos editados.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationDialog(nuevosDatos: Map<String, String>) {
        val previousValues = StringBuilder()
        val newValues = StringBuilder()

        // Comparar los valores anteriores y los nuevos
        clienteData?.let { cliente ->
            nuevosDatos.forEach { (key, nuevoValor) ->
                when (key) {
                    "nombres" -> {
                        previousValues.append("Nombres: ${cliente.nombres}\n")
                        newValues.append("Nuevos Nombres: $nuevoValor\n")
                    }
                    "apellidos" -> {
                        previousValues.append("Apellidos: ${cliente.apellidos}\n")
                        newValues.append("Nuevos Apellidos: $nuevoValor\n")
                    }
                    "tipo_documento" -> {
                        previousValues.append("Tipo de documento: ${cliente.tipo_documento}\n")
                        newValues.append("Nuevo Tipo de documento: $nuevoValor\n")
                    }
                    "numero_documento" -> {
                        previousValues.append("Número de documento: ${cliente.numero_documento}\n")
                        newValues.append("Nuevo Número de documento: $nuevoValor\n")
                    }
                    "direccion" -> {
                        previousValues.append("Dirección: ${cliente.direccion}\n")
                        newValues.append("Nueva Dirección: $nuevoValor\n")
                    }
                    "cod_cliente" -> {
                        previousValues.append("Código de cliente: ${cliente.cod_cliente}\n")
                        newValues.append("Nuevo Código de cliente: $nuevoValor\n")
                    }
                    "contactos" -> {
                        previousValues.append("Contactos: ${cliente.contactos}\n")
                        newValues.append("Nuevos Contactos: $nuevoValor\n")
                    }
                    "correo" -> {
                        previousValues.append("Correo: ${cliente.correo}\n")
                        newValues.append("Nuevo Correo: $nuevoValor\n")
                    }
                    "coordenadas" -> {
                        previousValues.append("Coordenadas: ${cliente.coordenadas}\n")
                        newValues.append("Nuevas Coordenadas: $nuevoValor\n")
                    }
                    "telefono" -> {
                        previousValues.append("Teléfono: ${cliente.telefono}\n")
                        newValues.append("Nuevo Teléfono: $nuevoValor\n")
                    }
                }
            }
        }

        // Formatear la fecha actual en AAAAMMDD
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        // Crear el mensaje de cambios para mostrar en el AlertDialog
        val mensaje = """
        Datos anteriores:
        $previousValues
        
        Datos nuevos:
        $newValues
    """.trimIndent()


        // Crear el AlertDialog
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Confirmar Cambios")
            .setMessage("Usuario: $nombreUsuario\nFecha: $currentDate\n\n$mensaje")
            .setPositiveButton("Guardar") { dialog, _ ->
                guardarCambios(nuevosDatos, mensaje, currentDate, nombreUsuario)
                dialog.dismiss()
                finish()  // Volver a la actividad anterior (ActualizarClienteActivity) al guardar
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                finish()  // Volver a la actividad anterior (ActualizarClienteActivity) al cancelar
            }
            .create()

        alertDialog.show()
    }

    private fun guardarCambios(nuevosDatos: Map<String, String>, mensaje: String, fecha: String, usuario: String) {

        val mensajeSeparadoPorComas = mensaje.lines().joinToString(", ")
        // Actualizar los datos del cliente en Firebase
        val clienteId = clienteData?.cod_cliente ?: return
        database.child(clienteId).updateChildren(nuevosDatos)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Referencia para "observacion-editado"
                    val observacionRef = database.child(clienteId).child("observacion-editado")

                    // Obtener la lista de observaciones para contar las existentes en la misma fecha
                    observacionRef.orderByKey().addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Contar cuántas observaciones existen con la misma fecha
                            var contador = 1
                            dataSnapshot.children.forEach { child ->
                                if (child.key?.startsWith(fecha) == true) {
                                    contador++
                                }
                            }

                            // Crear el ID en el formato "fecha+contador"
                            val observacionId = "$fecha$contador"

                            // Crear un objeto con los detalles de la edición
                            val observacionData = mapOf(
                                "usuario" to usuario,
                                "fecha" to fecha,
                                "detalle" to mensajeSeparadoPorComas
                            )

                            // Guardar la observación con el ID generado
                            observacionRef.child(observacionId).setValue(observacionData)
                                .addOnCompleteListener { observacionTask ->
                                    if (observacionTask.isSuccessful) {
                                        Toast.makeText(this@ActDatosActivity, "Datos y observación actualizados correctamente.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@ActDatosActivity, "Error al guardar observación: ${observacionTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Manejar error en la consulta
                            Toast.makeText(this@ActDatosActivity, "Error al contar observaciones: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                    // Alternativa
                    toggleEditMode(false)
                } else {
                    // Error al actualizar los datos
                    Toast.makeText(this@ActDatosActivity, "Error al actualizar datos: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

// Modelo de datos para el cliente
data class Client(
    var cod_cliente: String? = "",
    var nombres: String? = "",
    var apellidos: String? = "",
    var tipo_documento: String? = "",
    var numero_documento: String? = "",
    var direccion: String? = "",
    var coordenadas: String? = "",
    var telefono: String? = "",
    var correo: String? = "",
    var contactos: String? = "",
    var zona: String? = ""
)
