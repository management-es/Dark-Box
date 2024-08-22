package com.darkbox

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class AccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)

        // Configurar Spinner para Rol
        val spinnerRol: Spinner = findViewById(R.id.spinnerRol)
        ArrayAdapter.createFromResource(
            this,
            R.array.rol_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRol.adapter = adapter
        }

        // Configurar Spinner para Zona de Credenciales
        val spinnerZonaCredenciales: Spinner = findViewById(R.id.zona_credenciales)
        ArrayAdapter.createFromResource(
            this,
            R.array.zona_credenciales,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerZonaCredenciales.adapter = adapter
        }

        // Configurar Spinner para Zona de Credenciales
        val spinnerParametro: Spinner = findViewById(R.id.spinnerParametro)
        ArrayAdapter.createFromResource(
            this,
            R.array.spinnerParametro,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParametro.adapter = adapter
        }

        // Implementación del botón para agregar credenciales
        val buttonAgregarCredencial: Button = findViewById(R.id.buttonAgregarCredencial)
        buttonAgregarCredencial.setOnClickListener {
            // Implementar la lógica para agregar credenciales aquí
        }
    }
}
