package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        buttonSaveEquipment.setOnClickListener {
            val equipo = spinnerEquipo.selectedItem.toString() // No se convierte a mayúsculas
            val serial = inputSerial.text.toString()
            val tecnologia = inputTecnologia.text.toString()
            val modelo = inputModelo.text.toString()
            val estado = spinnerEstado.selectedItem.toString() // No se convierte a mayúsculas
            val zona = spinnerZona.selectedItem.toString() // No se convierte a mayúsculas
            val observaciones = inputObservaciones.text.toString()  // Observaciones no se convertirá a mayúsculas

            // Validación para campos vacíos o seleccionados incorrectamente
            if (equipo == "Seleccionar" || serial.isEmpty() || tecnologia.isEmpty() || modelo.isEmpty() || estado == "Seleccionar" || zona == "Seleccionar") {
                // Mostrar un mensaje si hay campos vacíos o no seleccionados
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Convertir solo los campos serial, tecnologia y modelo a mayúsculas
                val serialUpper = serial.uppercase()
                val tecnologiaUpper = tecnologia.uppercase()
                val modeloUpper = modelo.uppercase()

                // Consulta a Firebase para verificar si el serial ya existe
                database.child("inventario").child(serialUpper)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Si el serial ya existe, muestra un mensaje
                                Toast.makeText(this@IngresarEquipoActivity, "El serial del equipo ya existe.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Si el serial no existe, muestra el diálogo de confirmación
                                showConfirmationDialogForEquipment(equipo, serialUpper, tecnologiaUpper, modeloUpper, estado, zona, observaciones)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Manejo de errores
                            Toast.makeText(this@IngresarEquipoActivity, "Error al verificar el serial.", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
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

        // Mostrar pantalla de carga
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)

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
