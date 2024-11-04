package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ActualizarClienteActivity : ComponentActivity() {
    private lateinit var textViewTitulo: TextView
    private lateinit var buttonDatos: Button
    private lateinit var buttonServicio: Button


    private lateinit var nombreUsuario: String  // Variable para almacenar el nombre del usuario
    private lateinit var zonaUsuario: String // Variable para almacenar la zona del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_cliente)

        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "nombre no especificado"
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"

        // Inicializar el TextView para el título
        textViewTitulo = findViewById(R.id.titulo_actualizar_cliente)

        // Inicializar los botones
        buttonDatos = findViewById(R.id.button_datos)
        buttonServicio = findViewById(R.id.button_servicio)


        // clics de los botones
        buttonDatos.setOnClickListener {
            // Iniciar ActDatosActivity
            val intent = Intent(this, ActDatosActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ZONA_USUARIO", zonaUsuario)
            startActivity(intent)
        }

        buttonServicio.setOnClickListener {
            // Iniciar ActServicioActivity
            val intent = Intent(this, ActServicioActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ZONA_USUARIO", zonaUsuario)
            startActivity(intent)
        }
    }

    private fun mostrarDatos() {
        // Lógica para mostrar datos
        textViewTitulo.text = "Datos"
    }

    private fun mostrarServicio() {
        // Lógica para mostrar información física
        textViewTitulo.text = "Servicio"
    }


}
