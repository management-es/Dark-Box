package com.darkbox

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class EditAccesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listViewUsuarios: ListView
    private lateinit var editTextNombreUsuario: EditText
    private lateinit var editTextUsuario: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var spinnerRol: Spinner
    private lateinit var spinnerZona: Spinner
    private lateinit var spinnerParametro: Spinner
    private lateinit var editTextObservaciones: EditText
    private lateinit var buttonActualizar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editacces)

        database = FirebaseDatabase.getInstance().reference.child("access")

        listViewUsuarios = findViewById(R.id.listViewUsuarios)
        editTextNombreUsuario = findViewById(R.id.editTextNombreUsuario)
        editTextUsuario = findViewById(R.id.editTextUsuario)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        spinnerRol = findViewById(R.id.spinnerRol)
        spinnerZona = findViewById(R.id.spinnerZona)
        spinnerParametro = findViewById(R.id.spinnerParametro)
        editTextObservaciones = findViewById(R.id.editTextObservaciones)
        buttonActualizar = findViewById(R.id.buttonActualizar)

        // Configurar Spinner para Rol
        ArrayAdapter.createFromResource(
            this,
            R.array.rol_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRol.adapter = adapter
        }

        // Configurar Spinner para Zona
        ArrayAdapter.createFromResource(
            this,
            R.array.zona_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerZona.adapter = adapter
        }

        // Configurar Spinner para Parámetro
        ArrayAdapter.createFromResource(
            this,
            R.array.spinnerParametro,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParametro.adapter = adapter
        }

        // Cargar usuarios desde Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuarios = mutableListOf<String>()
                for (userSnapshot in snapshot.children) {
                    val usuario = userSnapshot.child("usuario").getValue(String::class.java)
                    if (usuario != null) {
                        usuarios.add(usuario)
                    }
                }
                val adapter = ArrayAdapter(this@EditAccesActivity, android.R.layout.simple_list_item_1, usuarios)
                listViewUsuarios.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditAccesActivity, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
        })

        listViewUsuarios.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = listViewUsuarios.getItemAtPosition(position) as String
            loadUserData(selectedUser)
        }

        buttonActualizar.setOnClickListener {
            val nombreUsuario = editTextNombreUsuario.text.toString().trim()
            val usuario = editTextUsuario.text.toString().trim()
            val contrasena = editTextContrasena.text.toString().trim()
            val rol = spinnerRol.selectedItem.toString()
            val zona = spinnerZona.selectedItem.toString()
            val parametro = spinnerParametro.selectedItem.toString()
            val observaciones = editTextObservaciones.text.toString().trim()

            // Validación para campos vacíos
            if (nombreUsuario.isEmpty()) {
                editTextNombreUsuario.error = "Este campo no puede estar vacío"
                editTextNombreUsuario.requestFocus()
                return@setOnClickListener
            }
            if (usuario.isEmpty()) {
                editTextUsuario.error = "Este campo no puede estar vacío"
                editTextUsuario.requestFocus()
                return@setOnClickListener
            }
            if (contrasena.isEmpty()) {
                editTextContrasena.error = "Este campo no puede estar vacío"
                editTextContrasena.requestFocus()
                return@setOnClickListener
            }
            if (rol == "Seleccionar") {
                Toast.makeText(this, "Por favor selecciona un rol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (zona == "Seleccionar") {
                Toast.makeText(this, "Por favor selecciona una zona", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (parametro == "Seleccionar") {
                Toast.makeText(this, "Por favor selecciona un parámetro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val updatedData = mapOf(
                "nombreUsuario" to nombreUsuario,
                "usuario" to usuario,
                "contrasena" to contrasena,
                "rol" to rol,
                "zona" to zona,
                "parametro" to parametro,
                "observaciones" to observaciones
            )

            val confirmMessage = """
                ¿Deseas guardar los siguientes cambios?
                
                Nombre de Usuario: $nombreUsuario
                Usuario: $usuario
                Contraseña: $contrasena
                Rol: $rol
                Zona: $zona
                Parámetro: $parametro
                Observaciones: $observaciones
            """.trimIndent()

            // Mostrar alerta de confirmación
            AlertDialog.Builder(this)
                .setTitle("Confirmar Cambios")
                .setMessage(confirmMessage)
                .setPositiveButton("Confirmar") { _, _ ->
                    // Actualizar la base de datos
                    database.child(usuario).updateChildren(updatedData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Credencial actualizada exitosamente", Toast.LENGTH_SHORT).show()
                            finish() // Volver a la actividad anterior
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al actualizar credencial", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun loadUserData(usuario: String) {
        database.child(usuario).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                editTextNombreUsuario.setText(dataSnapshot.child("nombreUsuario").getValue(String::class.java))
                editTextUsuario.setText(dataSnapshot.child("usuario").getValue(String::class.java))
                editTextContrasena.setText(dataSnapshot.child("contrasena").getValue(String::class.java))
                spinnerRol.setSelection((spinnerRol.adapter as ArrayAdapter<String>).getPosition(dataSnapshot.child("rol").getValue(String::class.java)))
                spinnerZona.setSelection((spinnerZona.adapter as ArrayAdapter<String>).getPosition(dataSnapshot.child("zona").getValue(String::class.java)))
                spinnerParametro.setSelection((spinnerParametro.adapter as ArrayAdapter<String>).getPosition(dataSnapshot.child("parametro").getValue(String::class.java)))
                editTextObservaciones.setText(dataSnapshot.child("observaciones").getValue(String::class.java))
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
