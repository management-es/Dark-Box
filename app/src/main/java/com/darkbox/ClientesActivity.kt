package com.darkbox

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClientesActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var zonaUsuario: String // Variable para almacenar la zona del usuario
    private lateinit var rolUsuario: String  // Variable para almacenar el rol del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        // Obtener la zona del usuario desde el Intent
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Rol no especificado"

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los campos y botones
        val buttonIngresarCliente: Button = findViewById(R.id.button_ingresar_cliente)
        val btnVerCliente: Button = findViewById(R.id.btnVerCliente)
        val optionsLayout: View = findViewById(R.id.options_layout)

        btnVerCliente.setOnClickListener {
            val intent = Intent(this, VerClienteActivity::class.java)
            intent.putExtra("ZONA_USUARIO", zonaUsuario) // Pasar la zona del usuario a VerClienteActivity
            startActivity(intent)
        }

        buttonIngresarCliente.setOnClickListener {
            if (rolUsuario == "Tecnico") {
                showAccessDeniedDialog() // Mostrar alerta si es técnico
            } else {
                optionsLayout.visibility = View.VISIBLE
                // Navegar a IngresarClienteActivity después de mostrar el layout
                val intent = Intent(this, IngresarClienteActivity::class.java)
                intent.putExtra("ZONA_USUARIO", zonaUsuario) // Pasar la zona del usuario a IngresarClienteActivity
                intent.putExtra("ROL_USUARIO", rolUsuario) // Pasar el rol del usuario a IngresarClienteActivity
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
