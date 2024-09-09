package com.darkbox

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IngresarEquipoActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var zonaUsuario: String // Variable para almacenar la zona del usuario
    private lateinit var rolUsuario: String  // Variable para almacenar el rol del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresar_equipo)

        // Obtener la rol y zona del usuario desde el Intent
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Rol no especificado"

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los campos del formulario
        val spinnerEquipo: Spinner = findViewById(R.id.input_equipo)
        val spinnerEstado: Spinner = findViewById(R.id.input_estado)
        val spinnerZona: Spinner = findViewById(R.id.input_zona)
        val inputSerial: EditText = findViewById(R.id.input_serial)
        val inputTecnologia: EditText = findViewById(R.id.input_tecnologia)
        val inputModelo: EditText = findViewById(R.id.input_modelo)
        val inputObservaciones: EditText = findViewById(R.id.input_observaciones)
        val buttonSaveEquipment: Button = findViewById(R.id.button_save_equipment)

        // Configura el Spinner para el campo "Equipo"
        val equipoOptions = arrayOf("Seleccionar", "Antena Cliente", "Router", "Onu", "Enlace", "Sector")
        val equipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, equipoOptions)
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEquipo.adapter = equipoAdapter

        // Configura el Spinner para el campo "Estado"
        val estadoOptions = arrayOf("Seleccionar", "Activo", "Dañado", "Bodega", "Revisión")
        val estadoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estadoOptions)
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = estadoAdapter

        // Configura el Spinner para el campo "Zona" utilizando el string-array del archivo strings.xml
        val zonaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.zona_options, // Nombre del string-array en strings.xml
            android.R.layout.simple_spinner_item
        )
        zonaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerZona.adapter = zonaAdapter

        // Manejar el clic del botón de guardar equipo
        buttonSaveEquipment.setOnClickListener {
            val equipo = spinnerEquipo.selectedItem.toString()
            val serial = inputSerial.text.toString()
            val tecnologia = inputTecnologia.text.toString()
            val modelo = inputModelo.text.toString()
            val estado = spinnerEstado.selectedItem.toString()
            val zona = spinnerZona.selectedItem.toString()
            val observaciones = inputObservaciones.text.toString()

            // Mostrar el diálogo de confirmación antes de guardar
            showConfirmationDialogForEquipment(equipo, serial, tecnologia, modelo, estado, zona, observaciones)
        }
    }

    private fun showConfirmationDialogForEquipment(equipo: String, serial: String, tecnologia: String, modelo: String, estado: String, zona: String, observaciones: String) {
        val message = """
            ¿Deseas guardar los datos del equipo?
            
            Equipo: $equipo
            Serial: $serial
            Tecnología: $tecnologia
            Modelo: $modelo
            Estado: $estado
            Zona: $zona
            Observaciones: $observaciones
        """.trimIndent()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage(message)
        builder.setPositiveButton("Sí") { dialog, _ ->
            saveEquipmentData(equipo, serial, tecnologia, modelo, estado, zona, observaciones)
            dialog.dismiss()
            Toast.makeText(this, "Equipo ingresado correctamente", Toast.LENGTH_SHORT).show()
            finish() // Cerrar la actividad y regresar a la pantalla anterior
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun saveEquipmentData(equipo: String, serial: String, tecnologia: String, modelo: String, estado: String, zona: String, observaciones: String) {
        // Crear un mapa con los datos del equipo
        val equipmentData = mapOf(
            "equipo" to equipo,
            "serial" to serial,
            "tecnologia" to tecnologia,
            "modelo" to modelo,
            "estado" to estado,
            "zona" to zona,
            "observaciones" to observaciones
        )

        // Escribir los datos del equipo en la base de datos usando el serial como clave
        database.child("inventario").child(serial).setValue(equipmentData)
            .addOnSuccessListener {
                // Datos escritos correctamente
                showMessage("Datos del equipo guardados")
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                exception.printStackTrace()
                showMessage("Error al guardar los datos del equipo")
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
