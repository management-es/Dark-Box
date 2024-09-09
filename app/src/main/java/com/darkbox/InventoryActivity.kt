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
    private lateinit var zonaUsuario: String // Variable para almacenar la zona del usuario
    private lateinit var rolUsuario: String  // Variable para almacenar el rol del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        // Obtener la rol y zona del usuario desde el Intent
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Rol no especificado"

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los campos y los botones
        val buttonIngresarEquipo: Button = findViewById(R.id.button_ingresar_equipo)
        val buttonVerInventario: Button = findViewById(R.id.button_ver_inventario)
        val buttonActualizarEstado: Button = findViewById(R.id.button_actualizar_estado)

        // Manejar el clic del botón Ingresar Equipo
        buttonIngresarEquipo.setOnClickListener {
            if (rolUsuario == "Tecnico") {
                showAccessDeniedDialog() // Mostrar alerta si es técnico
            } else {
                val intent = Intent(this, IngresarEquipoActivity::class.java)
                intent.putExtra("ZONA_USUARIO", zonaUsuario) // Pasar la zona del usuario
                intent.putExtra("ROL_USUARIO", rolUsuario) // Pasar el rol del usuario
                startActivity(intent)
            }
        }

        // Manejar el clic del botón Ver Inventario
        buttonVerInventario.setOnClickListener {
            val intent = Intent(this, VerInventarioActivity::class.java)
            intent.putExtra("ZONA_USUARIO", zonaUsuario) // Pasar la zona del usuario
            startActivity(intent)
        }

        // Manejar el clic del botón Actualizar Estado
        buttonActualizarEstado.setOnClickListener {
            if (rolUsuario == "Tecnico") {
                showAccessDeniedDialog() // Mostrar alerta si es técnico
            } else {
                val intent = Intent(this, ActualizarEstadoActivity::class.java)
                intent.putExtra("ZONA_USUARIO", zonaUsuario) // Pasar la zona del usuario
                startActivity(intent)
            }
        }
    }

    // Función para mostrar el mensaje de acceso denegado
    private fun showAccessDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Acceso Denegado")
            .setMessage("Su usuario no tiene acceso a esta función")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
