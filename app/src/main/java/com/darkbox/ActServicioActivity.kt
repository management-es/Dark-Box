package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter
import android.widget.Spinner

class ActServicioActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextBuscarCliente: EditText
    private lateinit var buttonBuscar: Button
    private lateinit var textViewDatos: TextView
    private lateinit var buttonEditar: Button
    private lateinit var buttonGuardar: Button


    private lateinit var spinnerPlan: Spinner
    private lateinit var spinnerTecnologia: Spinner

    private lateinit var editTextSerialAntena: EditText
    private lateinit var editTextSerialOnu: EditText
    private lateinit var editTextSerialRouter: EditText
    private lateinit var editTextEquipos: EditText
    private lateinit var editTextIpAntena: EditText
    private lateinit var editTextIpRemota: EditText
    private lateinit var nombreUsuario: String
    private lateinit var zonaUsuario: String

    private var clienteData: ClientServicio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actservicio)

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


        editTextSerialAntena = findViewById(R.id.editText_serial_antena)
        editTextSerialOnu = findViewById(R.id.editText_serial_onu)
        editTextSerialRouter = findViewById(R.id.editText_serial_router)
        editTextEquipos = findViewById(R.id.editText_equipos)
        editTextIpAntena = findViewById(R.id.editText_ip_antena)
        editTextIpRemota = findViewById(R.id.editText_ip_remota)

        // Configurar el adaptador para el Spinner plan
        spinnerPlan = findViewById(R.id.spinner_plan)
        val planAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.plan_options,
            android.R.layout.simple_spinner_item
        )
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlan.adapter = planAdapter

        // Configurar el adaptador para el Spinner tecnologia
        spinnerTecnologia = findViewById(R.id.spinner_tecnologia)
        val tecnologiaAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.tecnologia_options,
            android.R.layout.simple_spinner_item
        )
        tecnologiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTecnologia.adapter = tecnologiaAdapter


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
                    clienteData = snapshot.getValue(ClientServicio::class.java)
                    if (clienteData != null) {
                        if (zonaUsuario == "Set-Admin" || clienteData!!.zona == zonaUsuario) {
                            findViewById<TextView>(R.id.textView_nombre).text = "Nombre: ${clienteData?.nombres}"
                            findViewById<TextView>(R.id.textView_apellidos).text = "Apellidos: ${clienteData?.apellidos}"

                            textViewDatos.text = """
                            Plan: ${clienteData?.plan}
                            Tecnología: ${clienteData?.tecnologia}
                            Serial Antena: ${clienteData?.serial_antena}
                            Serial ONU: ${clienteData?.serial_onu}
                            Serial Router: ${clienteData?.serial_router}
                            Equipos: ${clienteData?.equipos}
                            IP Antena: ${clienteData?.ip_antena}
                            IP Remota: ${clienteData?.ip_remota}
                        """.trimIndent()



                            editTextSerialAntena.setText(clienteData?.serial_antena)
                            editTextSerialOnu.setText(clienteData?.serial_onu)
                            editTextSerialRouter.setText(clienteData?.serial_router)
                            editTextEquipos.setText(clienteData?.equipos)
                            editTextIpAntena.setText(clienteData?.ip_antena)
                            editTextIpRemota.setText(clienteData?.ip_remota)

                            // Configurar el adaptador para cargar el plan
                            val PlanIndex = resources.getStringArray(R.array.plan_options)
                                .indexOf(clienteData?.plan)
                            if (PlanIndex >= 0) {
                                spinnerPlan.setSelection(PlanIndex)
                            }

                            // Configurar el adaptador para cargar tecnologia
                            val TecnologiaIndex = resources.getStringArray(R.array.tecnologia_options)
                                .indexOf(clienteData?.tecnologia)
                            if (TecnologiaIndex >= 0) {
                                spinnerTecnologia.setSelection(TecnologiaIndex)
                            }


                            hideEditTexts()
                        } else {
                            textViewDatos.text = "Este cliente no pertenece a tu zona."
                        }
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


    private fun toggleEditMode(editing: Boolean) {
        if (editing) {
            textViewDatos.visibility = TextView.GONE

            findViewById<TextView>(R.id.textView_plan).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_tecnologia).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_serial_antena).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_serial_onu).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_serial_router).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_equipos).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_ip_antena).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textView_ip_remota).visibility = TextView.VISIBLE


            spinnerPlan.visibility = Spinner.VISIBLE
            spinnerTecnologia.visibility = Spinner.VISIBLE
            editTextSerialAntena.visibility = EditText.VISIBLE
            editTextSerialOnu.visibility = EditText.VISIBLE
            editTextSerialRouter.visibility = EditText.VISIBLE
            editTextEquipos.visibility = EditText.VISIBLE
            editTextIpAntena.visibility = EditText.VISIBLE
            editTextIpRemota.visibility = EditText.VISIBLE
        } else {
            textViewDatos.visibility = TextView.VISIBLE
            hideEditTexts()
            hideTextViews()
        }
    }

    private fun hideTextViews() {
        findViewById<TextView>(R.id.textView_plan).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_tecnologia).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_serial_antena).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_serial_onu).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_serial_router).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_equipos).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_ip_antena).visibility = TextView.GONE
        findViewById<TextView>(R.id.textView_ip_remota).visibility = TextView.GONE
    }

    private fun hideEditTexts() {

        spinnerPlan.visibility = Spinner.GONE
        spinnerTecnologia.visibility = Spinner.GONE
        editTextSerialAntena.visibility = EditText.GONE
        editTextSerialOnu.visibility = EditText.GONE
        editTextSerialRouter.visibility = EditText.GONE
        editTextEquipos.visibility = EditText.GONE
        editTextIpAntena.visibility = EditText.GONE
        editTextIpRemota.visibility = EditText.GONE
    }

    private fun setupSaveButton() {
        buttonGuardar.setOnClickListener {
            val nuevosDatos = mutableMapOf<String, String>()
            var cambios = false

            val planNuevo = spinnerPlan.selectedItem.toString()
            if (planNuevo != clienteData?.plan) {
                nuevosDatos["plan"] = planNuevo
                cambios = true
            }

            val tecnologiaNueva = spinnerTecnologia.selectedItem.toString()
            if (tecnologiaNueva != clienteData?.tecnologia) {
                nuevosDatos["tecnologia"] = tecnologiaNueva
                cambios = true
            }

            val serialAntenaNuevo = editTextSerialAntena.text.toString().trim()
            if (serialAntenaNuevo != clienteData?.serial_antena) {
                nuevosDatos["serial_antena"] = serialAntenaNuevo
                cambios = true
            }

            val serialOnuNuevo = editTextSerialOnu.text.toString().trim()
            if (serialOnuNuevo != clienteData?.serial_onu) {
                nuevosDatos["serial_onu"] = serialOnuNuevo
                cambios = true
            }

            val serialRouterNuevo = editTextSerialRouter.text.toString().trim()
            if (serialRouterNuevo != clienteData?.serial_router) {
                nuevosDatos["serial_router"] = serialRouterNuevo
                cambios = true
            }

            val equiposNuevo = editTextEquipos.text.toString().trim()
            if (equiposNuevo != clienteData?.equipos) {
                nuevosDatos["equipos"] = equiposNuevo
                cambios = true
            }

            val ipAntenaNueva = editTextIpAntena.text.toString().trim()
            if (ipAntenaNueva != clienteData?.ip_antena) {
                nuevosDatos["ip_antena"] = ipAntenaNueva
                cambios = true
            }

            val ipRemotaNueva = editTextIpRemota.text.toString().trim()
            if (ipRemotaNueva != clienteData?.ip_remota) {
                nuevosDatos["ip_remota"] = ipRemotaNueva
                cambios = true
            }

            if (cambios) {
                // Show confirmation dialog datos
                showConfirmationDialog(nuevosDatos)
            } else {
                Toast.makeText(this, "No se detectaron cambios", Toast.LENGTH_SHORT).show()
                toggleEditMode(false)
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

                    "plan" -> {
                        previousValues.append("Plan: ${cliente.plan}\n")
                        newValues.append("Nuevo Plan: $nuevoValor\n")
                    }
                    "tecnologia" -> {
                        previousValues.append("tecnologia: ${cliente.tecnologia}\n")
                        newValues.append("Nueva Tecnologia: $nuevoValor\n")
                    }
                    "serial_antena" -> {
                        previousValues.append("serial antena: ${cliente.serial_antena}\n")
                        newValues.append("Nuevo serial antena: $nuevoValor\n")
                    }
                    "serial_onu" -> {
                        previousValues.append("serial onu: ${cliente.serial_onu}\n")
                        newValues.append("Nuevo serial de ONU: $nuevoValor\n")
                    }
                    "serial_router" -> {
                        previousValues.append("serial router: ${cliente.serial_router}\n")
                        newValues.append("Nuevo serial de router: $nuevoValor\n")
                    }
                    "equipos" -> {
                        previousValues.append("equipos: ${cliente.equipos}\n")
                        newValues.append("Nuevos Equipos Adicionales: $nuevoValor\n")
                    }
                    "ip_antena" -> {
                        previousValues.append("ip antena: ${cliente.ip_antena}\n")
                        newValues.append("Nuevas ip antena: $nuevoValor\n")
                    }
                    "ip_remota" -> {
                        previousValues.append("ip remota: ${cliente.ip_remota}\n")
                        newValues.append("Nueva ip remota: $nuevoValor\n")
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
                                        Toast.makeText(this@ActServicioActivity, "Datos y observación actualizados correctamente.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@ActServicioActivity, "Error al guardar observación: ${observacionTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Manejar error en la consulta
                            Toast.makeText(this@ActServicioActivity, "Error al contar observaciones: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                    // Alternativa
                    toggleEditMode(false)
                } else {
                    // Error al actualizar los datos
                    Toast.makeText(this@ActServicioActivity, "Error al actualizar datos: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}

// Modelo de datos para el cliente
data class ClientServicio(
    var cod_cliente: String? = "",
    var nombres: String? = "",
    var apellidos: String? = "",
    var plan: String? = "",
    var tecnologia: String? = "",
    var serial_antena: String? = "",
    var serial_onu: String? = "",
    var serial_router: String? = "",
    var equipos: String? = "",
    var ip_antena: String? = "",
    var ip_remota: String? = "",
    var zona: String? = ""

)
