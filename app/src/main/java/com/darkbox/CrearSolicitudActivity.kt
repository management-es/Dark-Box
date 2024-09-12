package com.darkbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class CrearSolicitudActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_solicitud)

        // Mostrar un texto en pantalla
        val textView = findViewById<TextView>(R.id.textoPrueba)
        textView.text = "Este es un texto de prueba en CrearSolicitudActivity"
    }
}
