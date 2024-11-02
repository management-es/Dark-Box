package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ActualizarClienteActivity : ComponentActivity() {
    private lateinit var textViewTitulo: TextView
    private lateinit var buttonDatos: Button
    private lateinit var buttonFisico: Button
    private lateinit var buttonLogico: Button

    private lateinit var nombreUsuario: String  // Variable para almacenar el nombre del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_cliente)

        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "nombre no especificado"

        // Inicializar el TextView para el título
        textViewTitulo = findViewById(R.id.titulo_actualizar_cliente)

        // Inicializar los botones
        buttonDatos = findViewById(R.id.button_datos)
        buttonFisico = findViewById(R.id.button_fisico)
        buttonLogico = findViewById(R.id.button_logico)

        // Configurar los clics de los botones
        buttonDatos.setOnClickListener {
            // Iniciar ActDatosActivity
            val intent = Intent(this, ActDatosActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            startActivity(intent)
        }

        buttonFisico.setOnClickListener {
            // Acción para el botón Físico
            mostrarFisico()
        }

        buttonLogico.setOnClickListener {
            // Acción para el botón Lógico
            mostrarLogico()
        }
    }

    private fun mostrarDatos() {
        // Lógica para mostrar datos
        textViewTitulo.text = "Datos"
    }

    private fun mostrarFisico() {
        // Lógica para mostrar información física
        textViewTitulo.text = "Físico"
    }

    private fun mostrarLogico() {
        // Lógica para mostrar información lógica
        textViewTitulo.text = "Lógico"
    }
}
