package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class TiketsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tikets) // Asegúrate de que el layout correcto esté establecido

        // Botón para ir a CrearSolicitudActivity
        val btnCrearSolicitud = findViewById<Button>(R.id.btnCrearSolicitud)
        btnCrearSolicitud.setOnClickListener {
            val intent = Intent(this, CrearSolicitudActivity::class.java)
            startActivity(intent)
        }
    }
}
