package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccessActivity : AppCompatActivity() {

    // Declarar una referencia a la base de datos
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference.child("access")

        // Configurar Spinner para Rol
        val spinnerRol: Spinner = findViewById(R.id.spinnerRol)
        ArrayAdapter.createFromResource(
            this,
            R.array.rol_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRol.adapter = adapter
        }

        // Configurar Spinner para Zona de Credenciales
        val spinnerZonaCredenciales: Spinner = findViewById(R.id.zona_credenciales)
        ArrayAdapter.createFromResource(
            this,
            R.array.zona_credenciales,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerZonaCredenciales.adapter = adapter
        }

        // Configurar Spinner para Parámetros
        val spinnerParametro: Spinner = findViewById(R.id.spinnerParametro)
        ArrayAdapter.createFromResource(
            this,
            R.array.spinnerParametro,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParametro.adapter = adapter
        }

        // Inicializar las vistas de observación
        val textObservaciones: TextView = findViewById(R.id.textObservaciones)
        val editObservaciones: EditText = findViewById(R.id.editObservaciones)

        // Configurar el listener para el Spinner de Parámetro
        spinnerParametro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 1) { // Suponiendo que "Inactivo" es la segunda opción en el Spinner
                    textObservaciones.visibility = View.VISIBLE
                    editObservaciones.visibility = View.VISIBLE
                } else {
                    textObservaciones.visibility = View.GONE
                    editObservaciones.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Puedes dejarlo vacío si no necesitas manejar el caso cuando nada está seleccionado
            }
        }

        // Implementación del botón para agregar credenciales
        val buttonAgregarCredencial: Button = findViewById(R.id.buttonAgregarCredencial)
        buttonAgregarCredencial.setOnClickListener {
            // Obtener los valores de los campos
            val nombreUsuario: String = findViewById<EditText>(R.id.nombreUsuario).text.toString()
            val usuario: String = findViewById<EditText>(R.id.usuario).text.toString()
            val contrasena: String = findViewById<EditText>(R.id.contrasena).text.toString()
            val rol: String = spinnerRol.selectedItem.toString()
            val zona: String = spinnerZonaCredenciales.selectedItem.toString()
            val parametro: String = spinnerParametro.selectedItem.toString()
            val observaciones: String = editObservaciones.text.toString()

            // Crear un objeto para los datos a guardar
            val accessData = mapOf(
                "nombreUsuario" to nombreUsuario,
                "usuario" to usuario,
                "contrasena" to contrasena,
                "rol" to rol,
                "zona" to zona,
                "parametro" to parametro,
                "observaciones" to observaciones
            )

            // Generar una clave única para el nuevo registro
            val newKey = database.push().key

            if (newKey != null) {
                // Guardar los datos en el nodo "access" bajo la clave generada
                database.child(newKey).setValue(accessData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Credencial agregada exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al agregar credencial", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
