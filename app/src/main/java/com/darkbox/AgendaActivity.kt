package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity


class AgendaActivity : ComponentActivity() {


    private lateinit var zonaUsuario: String // Variable para almacenar la zona del usuario
    private lateinit var rolUsuario: String  // Variable para almacenar el rol del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        // Obtener la zona del usuario desde el Intent
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Rol no especificado"

        // Referencias a los elementos de la UI
        val buttonCrearAgenda: Button = findViewById(R.id.button_crear_agenda)
        val buttonVerAgenda = findViewById<Button>(R.id.button_ver_agenda)
        val dateInputLayout: View = findViewById(R.id.date_input_layout)
        val dateEditText: EditText = findViewById(R.id.input_date)




        // Referencia al botón Otras Gestiones
        val buttonOtrasGestiones = findViewById<Button>(R.id.button_otras_gestiones)
        buttonOtrasGestiones.setOnClickListener {
            val intent = Intent(this, OtrasGestionesActivity::class.java)
            startActivity(intent)
        }

        // Configurar la visibilidad inicial
        dateInputLayout.visibility = View.GONE

        // Listener para el botón Crear Agenda
        buttonCrearAgenda.setOnClickListener {
            if (rolUsuario == "Tecnico") {
                showAccessDeniedDialog() // Mostrar alerta si es técnico
            } else {
                val intent = Intent(this, CrearAgendaActivity::class.java)
                intent.putExtra("ZONA_USUARIO", zonaUsuario)
                intent.putExtra("ROL_USUARIO", rolUsuario)
                startActivity(intent)
            }
        }

        buttonVerAgenda.setOnClickListener {
            // Abrir la nueva actividad "Ver Agenda"
            val intent = Intent(this, VerAgendaActivity::class.java)
            intent.putExtra("ZONA_USUARIO", zonaUsuario) // Pasar la zona del usuario a VerAgendaActivity
            startActivity(intent)
        }

    }

    // Función para mostrar el mensaje de acceso denegado
    private fun showAccessDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Acceso Denegado")
            .setMessage("Su usuario no tiene acceso a esta función")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
