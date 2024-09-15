package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class TiketsActivity : AppCompatActivity() {

    private lateinit var nombreUsuario: String // Asegúrate de que esto esté inicializado correctamente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tikets) // Asegúrate de que el layout correcto esté establecido

        // Obtener el nombre de usuario del Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

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
}

