package com.darkbox

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccessActivity : AppCompatActivity() {

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

        val textObservaciones: TextView = findViewById(R.id.textObservaciones)
        val editObservaciones: EditText = findViewById(R.id.editObservaciones)

        spinnerParametro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 1) {
                    textObservaciones.visibility = View.VISIBLE
                    editObservaciones.visibility = View.VISIBLE
                } else {
                    textObservaciones.visibility = View.GONE
                    editObservaciones.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val buttonAgregarCredencial: Button = findViewById(R.id.buttonAgregarCredencial)
        buttonAgregarCredencial.setOnClickListener {
            val nombreUsuario: String = findViewById<EditText>(R.id.nombreUsuario).text.toString().trim()
            val usuario: String = findViewById<EditText>(R.id.usuario).text.toString().trim()
            val contrasena: String = findViewById<EditText>(R.id.contrasena).text.toString().trim()
            val rol: String = spinnerRol.selectedItem.toString()
            val zona: String = spinnerZonaCredenciales.selectedItem.toString()
            val parametro: String = spinnerParametro.selectedItem.toString()
            val observaciones: String = editObservaciones.text.toString().trim()

            // Validar el campo usuario para evitar espacios, puntos y guiones
            if (usuario.isEmpty()) {
                Toast.makeText(this, "El campo Usuario no puede estar vacío.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (usuario.contains(" ") || usuario.contains(".") || usuario.contains("-")) {
                Toast.makeText(this, "El campo Usuario no puede contener espacios, puntos ni guiones.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accessData = mapOf(
                "nombreUsuario" to nombreUsuario,
                "usuario" to usuario,
                "contrasena" to contrasena,
                "rol" to rol,
                "zona" to zona,
                "parametro" to parametro,
                "observaciones" to observaciones
            )

            val confirmMessage = """
                ¿Deseas guardar la siguiente credencial?
                
                Nombre de Usuario: $nombreUsuario
                Usuario: $usuario
                Rol: $rol
                Zona: $zona
                Parámetro: $parametro
                Observaciones: $observaciones
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage(confirmMessage)
                .setPositiveButton("Guardar") { _, _ ->
                    // Usar el valor de usuario como la clave del registro en Firebase
                    database.child(usuario).setValue(accessData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Credencial agregada exitosamente", Toast.LENGTH_SHORT).show()
                            finish() // Volver al menú principal
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al agregar credencial", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
