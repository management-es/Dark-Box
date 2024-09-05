package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.darkbox.ui.theme.DarkBoxTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtener el nombre de usuario, rol y zona del Intent
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Rol no especificado"
        val zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"

        setContent {
            DarkBoxTheme {
                MainScreen(
                    nombreUsuario = nombreUsuario,
                    rolUsuario = rolUsuario,
                    zonaUsuario = zonaUsuario,
                    onInventoryClick = { navigateToInventory(zonaUsuario) },
                    onClientesClick = { navigateToClientes(zonaUsuario) },
                    onAgendaClick = { navigateToAgenda(zonaUsuario) },
                    onInformesClick = { navigateToInformes() },
                    onCredencialesClick = { navigateToCredenciales() },
                    onLogoutClick = { handleLogout() } // Añadir función de cierre de sesión
                )
            }
        }
    }

    private fun navigateToInventory(zona: String) {
        val intent = Intent(this, InventoryActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona) // Pasa la zona directamente al Intent
        startActivity(intent)
    }

    private fun navigateToClientes(zona: String) {
        val intent = Intent(this, ClientesActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona) // Pasa la zona directamente al Intent
        startActivity(intent)
    }

    private fun navigateToAgenda(zona: String) {
        val intent = Intent(this, AgendaActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona) // Pasa la zona directamente al Intent
        startActivity(intent)
    }

    private fun navigateToInformes() {
        val intent = Intent(this, InformesActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCredenciales() {
        val intent = Intent(this, CredencialesActivity::class.java)
        startActivity(intent)
    }

    private fun handleLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Opcionalmente, terminar la actividad actual
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    nombreUsuario: String,
    rolUsuario: String,
    zonaUsuario: String,
    onInventoryClick: () -> Unit,
    onClientesClick: () -> Unit,
    onAgendaClick: () -> Unit,
    onInformesClick: () -> Unit,
    onCredencialesClick: () -> Unit,
    onLogoutClick: () -> Unit // Añadir función de clic para Cerrar sesión
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = nombreUsuario)
                        Text(text = rolUsuario, style = MaterialTheme.typography.bodySmall)
                        Text(text = zonaUsuario, style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Inventario") },
                            onClick = {
                                expanded = false
                                onInventoryClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clientes") },
                            onClick = {
                                expanded = false
                                onClientesClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Agenda") },
                            onClick = {
                                expanded = false
                                onAgendaClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Informes") },
                            onClick = {
                                expanded = false
                                onInformesClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Credenciales") },
                            onClick = {
                                expanded = false
                                onCredencialesClick()
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Ajuste para colocar el botón en la parte inferior
        ) {
            Greeting(name = nombreUsuario)

            // Espacio flexible que empuja el contenido hacia arriba
            Spacer(modifier = Modifier.weight(1f))

            // Botón de Cerrar sesión en la parte inferior
            Button(
                onClick = { onLogoutClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0), // Color gris claro
                    contentColor = Color.Black // Color del texto del botón
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.medium) // Color y forma del botón
            ) {
                Text(text = "Cerrar Sesión")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DarkBoxTheme {
        MainScreen(
            nombreUsuario = "Usuario",
            rolUsuario = "Rol",
            zonaUsuario = "Zona",
            onInventoryClick = {},
            onClientesClick = {},
            onAgendaClick = {},
            onInformesClick = {},
            onCredencialesClick = {},
            onLogoutClick = {} // Añadir función de clic para Cerrar sesión en la vista previa
        )
    }
}
