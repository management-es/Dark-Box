package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var nombreUsuario: String
    private lateinit var rolUsuario: String
    private lateinit var zonaUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtener el nombre de usuario, rol y zona del Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Rol no especificado"
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Zona no especificada"

        setContent {
            DarkBoxTheme {
                MainScreen(
                    nombreUsuario = nombreUsuario,
                    rolUsuario = rolUsuario,
                    zonaUsuario = zonaUsuario,
                    onInventoryClick = { navigateToInventory(zonaUsuario, rolUsuario) },
                    onClientesClick = { navigateToClientes(zonaUsuario, rolUsuario) },
                    onAgendaClick = { navigateToAgenda(zonaUsuario, rolUsuario) },
                    onInformesClick = { navigateToInformes() },
                    onCredencialesClick = { navigateToCredenciales() },
                    onLogoutClick = { handleLogout() } // Añadir función de cierre de sesión
                )
            }
        }
    }

    private fun navigateToInventory(zona: String, rol: String) {
        val intent = Intent(this, InventoryActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona) // Pasa la zona directamente al Intent
        intent.putExtra("ROL_USUARIO", rol) // Pasa el rol directamente al Intent
        startActivity(intent)
    }

    private fun navigateToClientes(zona: String, rol: String) {
        val intent = Intent(this, ClientesActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona) // Pasa la zona directamente al Intent
        intent.putExtra("ROL_USUARIO", rol) // Pasa el rol directamente al Intent
        startActivity(intent)
    }

    private fun navigateToAgenda(zona: String, rol: String) {
        val intent = Intent(this, AgendaActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona) // Pasa la zona directamente al Intent
        intent.putExtra("ROL_USUARIO", rol) // Pasa el rol directamente al Intent
        startActivity(intent)
    }

    private fun navigateToInformes() {
        val intent = Intent(this, InformesActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCredenciales() {
        if (rolUsuario.trim().equals("SupUsrDo", ignoreCase = true)) {
            // Iniciar CredencialesActivity si el rol es SupUsrDo
            val intent = Intent(this, CredencialesActivity::class.java)
            startActivity(intent)
        } else {
            // Mostrar mensaje de acceso denegado si el rol no es SupUsrDo
            showAccessDeniedDialog()
        }
    }

    private fun showAccessDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Acceso Denegado")
            .setMessage("Su usuario no tiene acceso a esta función")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Previene que el diálogo se cierre al tocar fuera de él
            .create()
            .show()
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
    onLogoutClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("DarkBox") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(200.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Inventario", fontSize = 20.sp) },
                            onClick = {
                                expanded = false
                                onInventoryClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clientes", fontSize = 20.sp) },
                            onClick = {
                                expanded = false
                                onClientesClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Agenda", fontSize = 20.sp) },
                            onClick = {
                                expanded = false
                                onAgendaClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Informes", fontSize = 20.sp) },
                            onClick = {
                                expanded = false
                                onInformesClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Credenciales", fontSize = 20.sp) },
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Nombre del usuario en negrita
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nombreUsuario,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold // Negrita para el nombre
                )
                Text(text = rolUsuario, style = MaterialTheme.typography.bodyLarge)
                Text(text = zonaUsuario, style = MaterialTheme.typography.bodyLarge)

                // Mensaje de bienvenida en un recuadro
                Box(
                    modifier = Modifier
                        .padding(top = 50.dp) // Mayor separación del contenido anterior
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Bienvenido a la aplicación de gestión Técnico-Administrativa de Telecco",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = { onLogoutClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.medium)
            ) {
                Text(text = "Cerrar Sesión")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Copyright en la parte inferior
            Text(
                text = "© 2024 KerneliX Software",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Bienvenido $name!",
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
