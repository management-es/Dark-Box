package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class TiketsActivity : AppCompatActivity() {

    private lateinit var nombreUsuario: String // Asegúrate de que esto esté inicializado correctamente
    private lateinit var rolUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_ticket) // Asegúrate de que el layout correcto esté establecido

        // Obtener el nombre de usuario del Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

        // Obtener el rol del Intent
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Usuario"

        // Verificar el rol del usuario
        if (rolUsuario == "Tecnico") {
            showAccessDeniedDialog()
            return // Termina la ejecución si es Tecnico
        }

        // Botón para ir a CrearSolicitudActivity
        val btnCrearSolicitud = findViewById<Button>(R.id.btnCrearSolicitud)
        btnCrearSolicitud.setOnClickListener {
            navigateToCrearSolicitud()
        }

        // Botón para ir a ResponderSolicitudActivity
        val btnResponderSolicitud = findViewById<Button>(R.id.btnResponderSolicitud)
        btnResponderSolicitud.setOnClickListener {
            navigateToResponderSolicitud()
        }

        // Botón para ir a Seguimiento
        val btnSeguimiento = findViewById<Button>(R.id.btnSeguimiento)
        btnSeguimiento.setOnClickListener {
            navigateToSeguimiento()
        }
    }

    private fun showAccessDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Acceso Denegado")
            .setMessage("No tienes acceso a esta función.")
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun navigateToCrearSolicitud() {
        val intent = Intent(this, CrearSolicitudActivity::class.java)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario) // Agrega el nombre del usuario al Intent
        startActivity(intent)
    }

    private fun navigateToResponderSolicitud() {
        val intent = Intent(this, ResponderSolicitudActivity::class.java)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        startActivity(intent)
    }

    private fun navigateToSeguimiento() {

        val intent = Intent(this, SeguimientoActivity::class.java)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        intent.putExtra("ROL_USUARIO", rolUsuario) // Agrega el rol al Intent
        startActivity(intent)
    }
}
