package com.darkbox

import android.content.Intent
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

        // Mostrar pantalla de carga
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)

        val historialList = mutableListOf<Pair<String, String>>()

        // Búsqueda en el nodo "agenda"
        database.child("agenda").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    val registroId = dataSnapshot.key ?: ""

                    if (registroId.endsWith("-$codCliente")) {
                        val fecha = registroId.take(8)  // Extrae los primeros 8 caracteres como fecha
                        val agendaInfo = formatearAgendaInfo(dataSnapshot)
                        historialList.add(fecha to "Agenda (ID: $registroId): $agendaInfo\n")
                    }
                }

                // Realiza la búsqueda en "respuestas" después de procesar "agenda"
                buscarEnRespuestas(codCliente) { respuestasHistorialList ->
                    historialList.addAll(respuestasHistorialList)

                    // Ordena la lista por fecha en orden descendente
                    historialList.sortByDescending { it.first }

                    // Combina todos los registros en una sola cadena de texto
                    val historial = historialList.joinToString("\n") { it.second }

                    if (historial.isEmpty()) {
                        onResult("No se encontraron registros para el cliente $codCliente.")
                    } else {
                        onResult(historial)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialActivity, "Error en la búsqueda de agenda: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatearAgendaInfo(dataSnapshot: DataSnapshot): String {
        val cliente = dataSnapshot.child("cliente").getValue(String::class.java) ?: "N/A"
        val nombre = dataSnapshot.child("nombre").getValue(String::class.java) ?: "N/A"
        val apellidos = dataSnapshot.child("apellidos").getValue(String::class.java) ?: "N/A"
        val documento = dataSnapshot.child("documento").getValue(String::class.java) ?: "N/A"
        val direccion = dataSnapshot.child("direccion").getValue(String::class.java) ?: "N/A"
        val coordenadas = dataSnapshot.child("coordenadas").getValue(String::class.java) ?: "N/A"
        val telefono = dataSnapshot.child("telefono").getValue(String::class.java) ?: "N/A"
        val contactos = dataSnapshot.child("contactos").getValue(String::class.java) ?: "N/A"
        val zona = dataSnapshot.child("zona").getValue(String::class.java) ?: "N/A"
        val gestion = dataSnapshot.child("gestion").getValue(String::class.java) ?: "N/A"
        val fecha = dataSnapshot.child("fecha").getValue(String::class.java) ?: "N/A"
        val observaciones = dataSnapshot.child("observaciones").getValue(String::class.java) ?: "N/A"
        val observacionesCancelacion = dataSnapshot.child("observaciones-cancelacion").getValue(String::class.java) ?: "N/A"

        return """
        Cliente: $cliente
        Nombre: $nombre
        Apellidos: $apellidos
        Documento: $documento
        Dirección: $direccion
        Coordenadas: $coordenadas
        Teléfono: $telefono
        Contactos: $contactos
        Zona: $zona
        Gestión: $gestion
        Fecha: $fecha
        Observaciones: $observaciones
        Observaciones-Cancelación: $observacionesCancelacion
    """.trimIndent().replace("\n", ",")
    }


    private fun buscarEnRespuestas(codCliente: String, onResult: (List<Pair<String, String>>) -> Unit) {
        val respuestasList = mutableListOf<Pair<String, String>>()

        database.child("respuestas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    val respuestaId = dataSnapshot.key ?: ""

                    if (respuestaId.contains("Baja")) {
                        val clienteNode = dataSnapshot.child("cliente")
                        val clienteCod = clienteNode.child("cod_cliente").getValue(String::class.java)

                        if (clienteCod == codCliente) {
                            val fecha = respuestaId.take(8)  // Extrae los primeros 8 caracteres como fecha
                            val respuestaInfo = dataSnapshot.value.toString()
                            respuestasList.add(fecha to "Respuestas (ID: $respuestaId): $respuestaInfo\n")
                        }
                    }
                }


                // Devuelve la lista de respuestas para agregarse al historial
                onResult(respuestasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialActivity, "Error en la búsqueda de respuestas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
