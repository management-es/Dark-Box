package com.darkbox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.darkbox.ui.theme.DarkBoxTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var nombreUsuario: String
    private lateinit var rolUsuario: String
    private lateinit var zonaUsuario: String

    private var shouldExit = false // Flag para controlar el cierre

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
                    onLogoutClick = { showLogoutConfirmationDialog() } // Mostrar diálogo de confirmación al hacer logout
                )
            }
        }
    }

    override fun onBackPressed() {
        if (!shouldExit) {
            showLogoutConfirmationDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToInventory(zona: String, rol: String) {
        val intent = Intent(this, InventoryActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona)
        intent.putExtra("ROL_USUARIO", rol)
        startActivity(intent)
    }

    private fun navigateToClientes(zona: String, rol: String) {
        val intent = Intent(this, ClientesActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona)
        intent.putExtra("ROL_USUARIO", rol)
        startActivity(intent)
    }

    private fun navigateToAgenda(zona: String, rol: String) {
        val intent = Intent(this, AgendaActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona)
        intent.putExtra("ROL_USUARIO", rol)
        startActivity(intent)
    }

    private fun navigateToInformes() {
        val intent = Intent(this, InformesActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCredenciales() {
        if (rolUsuario.trim().equals("SupUsrDo", ignoreCase = true)) {
            val intent = Intent(this, CredencialesActivity::class.java)
            startActivity(intent)
        } else {
            showAccessDeniedDialog()
        }
    }

    private fun showAccessDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Acceso Denegado")
            .setMessage("Su usuario no tiene acceso a esta función")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Estás seguro que deseas cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            handleLogout()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            shouldExit = false // Restablece el flag si el usuario decide no cerrar sesión
        }
        builder.setOnDismissListener {
            // Manejar el caso en que se desee cerrar la actividad
            if (shouldExit) {
                super.onBackPressed()
            }
        }
        builder.setCancelable(false)
        builder.create().show()
        shouldExit = true // Activa el flag para cerrar la actividad
    }

    private fun handleLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
            // Mover el TopAppBar más abajo
            Column {
                Spacer(modifier = Modifier.height(16.dp)) // Ajusta la altura según sea necesario
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Ajuste para distribuir espacio
        ) {
            // Información del usuario y botón de cerrar sesión
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f) // Ocupa el espacio disponible
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
                        .padding(top = 20.dp) // Mayor separación del contenido anterior
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

            // Botón de cerrar sesión
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

            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el botón y el copyright

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
