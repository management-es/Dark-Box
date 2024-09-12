package com.darkbox

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CrearSolicitudActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_solicitud)

        // Obtener referencias a los campos de la solicitud
        val usuarioSolicitante = findViewById<TextView>(R.id.usuarioSolicitante)
        val tipoSolicitud = findViewById<Spinner>(R.id.tipoSolicitud)
        val importancia = findViewById<TextView>(R.id.importancia) // Cambiado a TextView
        val descripcion = findViewById<EditText>(R.id.descripcion)
        val destinatario = findViewById<EditText>(R.id.destinatario)
        val btnCrearSolicitud = findViewById<Button>(R.id.btnCrearSolicitud)

        // Obtener el nombre del usuario del Intent
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        usuarioSolicitante.text = nombreUsuario

        // Configurar el listener para el Spinner
        tipoSolicitud.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtener el tipo de solicitud seleccionado
                val tipo = tipoSolicitud.getItemAtPosition(position).toString()
                Log.d("CrearSolicitudActivity", "Tipo seleccionado: $tipo")

                // Determinar la importancia en función del tipo de solicitud
                val nivelImportancia = when (tipo) {
                    "Falla Masiva" -> "Alta"
                    "Falla Local" -> "Media"
                    "Revisión de Cliente", "Otros" -> "Baja"
                    else -> "Desconocida"
                }
                // Mostrar el nivel de importancia para depuración
                Log.d("CrearSolicitudActivity", "Importancia determinada: $nivelImportancia")
                // Actualizar la TextView de importancia
                importancia.text = nivelImportancia
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona nada
            }
        }

        btnCrearSolicitud.setOnClickListener {
            // Validar y procesar la solicitud
            val usuario = usuarioSolicitante.text.toString()
            val tipo = tipoSolicitud.selectedItem.toString()
            val nivelImportancia = importancia.text.toString()
            val desc = descripcion.text.toString()
            val dest = destinatario.text.toString()

            if (usuario.isNotEmpty() && desc.isNotEmpty() && dest.isNotEmpty()) {
                // Aquí podrías agregar la lógica para crear la solicitud
                Toast.makeText(this, "Solicitud creada por $usuario con importancia $nivelImportancia", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
