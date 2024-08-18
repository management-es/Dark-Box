package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent


class InventoryActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los campos y los botones
        val buttonIngresarEquipo: Button = findViewById(R.id.button_ingresar_equipo)
        val buttonVerInventario: Button = findViewById(R.id.button_ver_inventario)
        val equipmentInputLayout: View = findViewById(R.id.equipment_input_layout)
        val buttonSaveEquipment: Button = findViewById(R.id.button_save_equipment)

        // Configura el Spinner para el campo "Equipo"
        val spinnerEquipo: Spinner = findViewById(R.id.input_equipo)
        val equipoOptions = arrayOf("Seleccionar", "Antena Cliente", "Router", "Onu", "Enlace", "Sector")
        val equipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, equipoOptions)
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEquipo.adapter = equipoAdapter

        // Configura el Spinner para el campo "Estado"
        val spinnerEstado: Spinner = findViewById(R.id.input_estado)
        val estadoOptions = arrayOf("Seleccionar", "Activo", "Dañado", "Bodega", "Revisión")
        val estadoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estadoOptions)
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = estadoAdapter

        // Configura el Spinner para el campo "Zona"
        val spinnerZona: Spinner = findViewById(R.id.input_zona)
        val zonaOptions = arrayOf("Seleccionar", "Medellin", "Salgar", "Amaga")
        val zonaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, zonaOptions)
        zonaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerZona.adapter = zonaAdapter

        // Manejar el clic del botón de ingresar equipo
        buttonIngresarEquipo.setOnClickListener {
            equipmentInputLayout.visibility = View.VISIBLE
        }

        // Manejar el clic del botón Ver Inventario
        buttonVerInventario.setOnClickListener {
            val intent = Intent(this, VerInventarioActivity::class.java)
            startActivity(intent)
        }

        // Manejar el clic del botón de guardar equipo
        buttonSaveEquipment.setOnClickListener {
            val equipo = spinnerEquipo.selectedItem.toString()
            val serial = findViewById<EditText>(R.id.input_serial).text.toString()
            val tecnologia = findViewById<EditText>(R.id.input_tecnologia).text.toString()
            val modelo = findViewById<EditText>(R.id.input_modelo).text.toString()
            val estado = spinnerEstado.selectedItem.toString()
            val zona = spinnerZona.selectedItem.toString()
            val observaciones = findViewById<EditText>(R.id.input_observaciones).text.toString()

            showConfirmationDialogForEquipment(equipo, serial, tecnologia, modelo, estado, zona, observaciones)
        }
    }

    private fun showConfirmationDialogForEquipment(equipo: String, serial: String, tecnologia: String, modelo: String, estado: String, zona: String, observaciones: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Deseas guardar los datos del equipo?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            saveEquipmentData(equipo, serial, tecnologia, modelo, estado, zona, observaciones)
            dialog.dismiss()
            finish() // Close the activity and go back to the previous screen
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            finish() // Close the activity and go back to the previous screen
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
