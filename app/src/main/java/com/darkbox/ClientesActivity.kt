package com.darkbox

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkbox.ui.theme.DarkBoxTheme

class ClientesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        // Referencias a los campos y el botón
        val buttonIngresarCliente: Button = findViewById(R.id.button_ingresar_cliente)
        val clienteInputLayout: View = findViewById(R.id.cliente_input_layout)
        val buttonSaveCliente: Button = findViewById(R.id.button_save_cliente)

        // Manejar el clic del botón de ingresar cliente
        buttonIngresarCliente.setOnClickListener {
            clienteInputLayout.visibility = View.VISIBLE
        }

        // Manejar el clic del botón de guardar cliente
        buttonSaveCliente.setOnClickListener {
            // Aquí puedes agregar la lógica para guardar los datos del cliente
            // Por ahora, simplemente ocultaremos el layout de ingreso
            clienteInputLayout.visibility = View.GONE
        }
    }
}
