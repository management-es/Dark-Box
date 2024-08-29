package com.darkbox

import android.app.DatePickerDialog
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class SolicitudInstalacionActivity : ComponentActivity() {

    private lateinit var inputNombre: TextInputEditText
    private lateinit var inputApellidos: TextInputEditText
    private lateinit var inputDireccion: TextInputEditText
    private lateinit var inputCoordenadas: TextInputEditText
    private lateinit var inputTelefono: TextInputEditText
    private lateinit var inputContactos: TextInputEditText
    private lateinit var inputObservaciones: TextInputEditText
    private lateinit var inputFecha: TextInputEditText
    private lateinit var spinnerZona: Spinner
    private lateinit var buttonGuardarSolicitud: Button
    private var fechaSeleccionada: String? = null
    private lateinit var inputNumeroDocumento: TextInputEditText

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solicitud_instalacion)

        inputNumeroDocumento = findViewById(R.id.input_numero_documento_cliente)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().getReference("agenda")

        // Referencias a los elementos de la UI
        inputNombre = findViewById(R.id.input_nombre_cliente)
        inputApellidos = findViewById(R.id.input_apellidos_cliente)
        inputDireccion = findViewById(R.id.input_direccion_cliente)
        inputCoordenadas = findViewById(R.id.input_coordenadas_cliente)
        inputTelefono = findViewById(R.id.input_telefono_cliente)
        inputContactos = findViewById(R.id.input_contactos_cliente)
        inputObservaciones = findViewById(R.id.input_observaciones)
        inputFecha = findViewById(R.id.input_fecha)
        spinnerZona = findViewById(R.id.spinner_zona)
        buttonGuardarSolicitud = findViewById(R.id.button_guardar_solicitud)

        // Configurar el Spinner para Zona
        ArrayAdapter.createFromResource(
            this,
            R.array.zona_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerZona.adapter = adapter
        }

        // Configurar el campo de fecha
        inputFecha.setOnClickListener {
            showDatePickerDialog()
        }

        // Configurar el botón Guardar Solicitud
        buttonGuardarSolicitud.setOnClickListener {
            guardarSolicitudInstalacion()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fechaFormateada = "$dayOfMonth/${month + 1}/$year"
                inputFecha.setText(fechaFormateada)

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

    private fun guardarSolicitudInstalacion() {
        val nombre = inputNombre.text.toString()
        val apellidos = inputApellidos.text.toString()
        val numeroDocumento = inputNumeroDocumento.text.toString()
        val direccion = inputDireccion.text.toString()
        val coordenadas = inputCoordenadas.text.toString()
        val telefono = inputTelefono.text.toString()
        val contactos = inputContactos.text.toString()
        val observaciones = inputObservaciones.text.toString()
        val zona = spinnerZona.selectedItem.toString()

        if (nombre.isNotEmpty() && apellidos.isNotEmpty() && numeroDocumento.isNotEmpty() && direccion.isNotEmpty() &&
            coordenadas.isNotEmpty() && telefono.isNotEmpty() && contactos.isNotEmpty() &&
            observaciones.isNotEmpty() && fechaSeleccionada != null) {

            // Base del ID
            val baseIdSolicitud = "${fechaSeleccionada}-sol_inst"

            // Buscar solicitudes existentes con el mismo formato de ID
            database.orderByKey().startAt(baseIdSolicitud).endAt("${baseIdSolicitud}\uf8ff")
                .get()
                .addOnSuccessListener { snapshot ->
                    // Determinar el siguiente número disponible
                    val maxSuffix = snapshot.children.mapNotNull { solicitudSnapshot ->
                        val id = solicitudSnapshot.key ?: return@mapNotNull null
                        val suffix = id.removePrefix(baseIdSolicitud).toIntOrNull()
                        suffix ?: 0
                    }.maxOrNull() ?: 0

                    // Crear el nuevo ID con el siguiente número disponible
                    val idSolicitud = "$baseIdSolicitud${maxSuffix + 1}"

                    // Crear el mensaje para el cuadro de confirmación
                    val mensajeConfirmacion = """
                    Nombre: $nombre
                    Apellidos: $apellidos
                    Número de Documento: $numeroDocumento
                    Dirección: $direccion
                    Coordenadas: $coordenadas
                    Teléfono: $telefono
                    Contactos: $contactos
                    Zona: $zona
                    Observaciones: $observaciones
                    Fecha: ${inputFecha.text.toString()}
                """.trimIndent()

                    // Crear el cuadro de diálogo
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Confirmar Solicitud de Instalación")
                    builder.setMessage(mensajeConfirmacion)

                    // Botón de Confirmar
                    builder.setPositiveButton("Confirmar") { _, _ ->
                        // Crear el objeto de datos
                        val solicitud = mapOf(
                            "nombre" to nombre,
                            "apellidos" to apellidos,
                            "numero_documento" to numeroDocumento,
                            "direccion" to direccion,
                            "coordenadas" to coordenadas,
                            "telefono" to telefono,
                            "contactos" to contactos,
                            "observaciones" to observaciones,
                            "zona" to zona,
                            "fecha" to fechaSeleccionada
                        )

                        // Guardar los datos en Firebase
                        database.child(idSolicitud).setValue(solicitud)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Solicitud guardada con éxito", Toast.LENGTH_SHORT).show()
                                finish()  // Cierra la actividad
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar la solicitud", Toast.LENGTH_SHORT).show()
                            }
                    }

                    // Botón de Cancelar
                    builder.setNegativeButton("Cancelar", null)

                    // Mostrar el cuadro de diálogo
                    builder.show()

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al verificar solicitudes existentes", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
