package com.darkbox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class SoporteDevActivity : AppCompatActivity() {

    // Definir las vistas
    private lateinit var descripcionErrorEditText: EditText
    private lateinit var btnEnviarInforme: Button
    private lateinit var zonaUsuarioTextView: TextView
    private lateinit var rolUsuarioTextView: TextView
    private lateinit var nombreUsuarioTextView: TextView
    private lateinit var database: DatabaseReference

    // Variables para recibir los datos del Intent
    private lateinit var zona: String
    private lateinit var rol: String
    private lateinit var nombreUsuario: String

    // Definir el formato de fecha
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soporte_dev)

        // Inicializar las vistas
        descripcionErrorEditText = findViewById(R.id.etDescripcionError)
        btnEnviarInforme = findViewById(R.id.btnEnviarInforme)
        zonaUsuarioTextView = findViewById(R.id.tvZonaUsuario)
        rolUsuarioTextView = findViewById(R.id.tvRolUsuario)
        nombreUsuarioTextView = findViewById(R.id.tvNombreUsuario)

        // Inicializar la referencia de la base de datos de Firebase
        database = FirebaseDatabase.getInstance().reference.child("soportedev")

        // Obtener los datos del Intent
        zona = intent.getStringExtra("ZONA_USUARIO") ?: "Desconocida"
        rol = intent.getStringExtra("ROL_USUARIO") ?: "Desconocido"
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Desconocido"

        // Mostrar los datos recibidos en los TextViews
        zonaUsuarioTextView.text = "Zona: $zona"
        rolUsuarioTextView.text = "Rol: $rol"
        nombreUsuarioTextView.text = "Usuario: $nombreUsuario"

        // Configurar el botón para enviar el informe
        btnEnviarInforme.setOnClickListener {
            val descripcionError = descripcionErrorEditText.text.toString()

            // Verificar que el campo no esté vacío
            if (descripcionError.isNotEmpty()) {
                // Obtener la fecha actual en formato AAAAMMDD
                val currentDate = dateFormat.format(Date())

                // Generar una clave única combinando la fecha y el número auto-incrementable
                generateUniqueKey(currentDate, descripcionError)
            } else {
                // Si el campo está vacío, mostrar un mensaje de advertencia
                Toast.makeText(this, "Por favor, describe el problema", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para generar una clave única
    private fun generateUniqueKey(currentDate: String, descripcionError: String) {
        database.orderByKey().limitToLast(1).get().addOnSuccessListener { snapshot ->
            val lastKey = snapshot.children.firstOrNull()?.key
            val incrementedNumber = if (lastKey != null && lastKey.startsWith(currentDate)) {
                val lastNumber = lastKey.substring(8).toInt()
                lastNumber + 1
            } else {
                1
            }

            // Crear la clave única
            val uniqueKey = "$currentDate$incrementedNumber"

            // Crear un objeto con los datos
            val informe = InformeError(descripcionError, zona, rol, nombreUsuario)

            // Enviar los datos a Firebase con la clave única
            database.child(uniqueKey).setValue(informe)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Mostrar un mensaje de éxito
                        Toast.makeText(this, "Informe enviado correctamente", Toast.LENGTH_SHORT).show()
                        // Cerrar la actividad y volver al menú anterior
                        finish()
                    } else {
                        // Mostrar un mensaje de error si la carga falla
                        Toast.makeText(this, "Error al enviar el informe", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

// Definir la clase de datos para almacenar el informe
data class InformeError(
    val descripcionError: String,
    val zona: String,
    val rol: String,
    val nombreUsuario: String
)
