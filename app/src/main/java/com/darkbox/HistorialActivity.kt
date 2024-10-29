package com.darkbox

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.darkbox.ui.theme.DarkBoxTheme
import com.google.firebase.database.*

class HistorialActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la referencia de Firebase
        database = FirebaseDatabase.getInstance().reference

        setContent {
            DarkBoxTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HistorialScreen()
                }
            }
        }
    }

    @Composable
    fun HistorialScreen() {
        var codCliente by remember { mutableStateOf(TextFieldValue("")) }
        var historialText by remember { mutableStateOf("Historial") }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            TextField(
                value = codCliente,
                onValueChange = { codCliente = it },
                label = { Text("Ingrese cod_cliente") },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(onClick = { buscarHistorial(codCliente.text) { historial -> historialText = historial } }) {
                Text("Buscar")
            }

            Text(
                historialText,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )
        }
    }

    private fun buscarHistorial(codCliente: String, onResult: (String) -> Unit) {
        if (codCliente.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un código de cliente.", Toast.LENGTH_SHORT).show()
            return
        }

        var historial = ""

        // Realiza la búsqueda en el nodo "agenda" recorriendo los IDs para verificar si contienen el código del cliente
        database.child("agenda").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    val registroId = dataSnapshot.key ?: ""

                    if (registroId.endsWith("-$codCliente")) {
                        // Si el ID contiene el código del cliente, obtenemos su información
                        val agendaInfo = dataSnapshot.value.toString()
                        historial += "Agenda:\n$agendaInfo\n"
                    }
                }

                if (historial.isEmpty()) {
                    historial = "No se encontraron registros en agenda para el cliente $codCliente."
                }
                onResult(historial)  // Actualiza el historial
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialActivity, "Error en la búsqueda de agenda: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
