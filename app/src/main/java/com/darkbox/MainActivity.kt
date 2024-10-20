package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Help


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
                    onLogoutClick = { showLogoutConfirmationDialog() },
                    onSecondMenuClick = { navigateToTikets(rolUsuario) },
                    onSoporteClick = { navigateToSoporteDev(rolUsuario, zonaUsuario) }
                )
            }
        }

        // Mostrar pantalla de carga
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)
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

    private fun navigateToTikets(rol: String) {
        // Verificar el rol del usuario
        if (rolUsuario == "Tecnico") {
            showAccessDeniedDialog()
            return // Termina la ejecución si es Tecnico
        }
        val intent = Intent(this, TiketsActivity::class.java)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        intent.putExtra("ROL_USUARIO", rol)
        startActivity(intent)
    }

    private fun navigateToSoporteDev(zona: String, rol: String) {
        val intent = Intent(this, SoporteDevActivity::class.java)
        intent.putExtra("ZONA_USUARIO", zona)
        intent.putExtra("ROL_USUARIO", rol)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        startActivity(intent)
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
    onLogoutClick: () -> Unit,
    onSecondMenuClick: () -> Unit,
    onSoporteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var secondMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Barra superior con el título DarkBox centrado
            TopAppBar(
                modifier = Modifier.fillMaxWidth(), // Ocupa  el ancho
                title = {
                    // Centrar el título usando un Row con Arrangement.Center
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DarkBox",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f), // Ocupa el espacio disponible
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { secondMenuExpanded = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Second Menu")
                    }
                    DropdownMenu(
                        expanded = secondMenuExpanded,
                        onDismissRequest = { secondMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tickets", fontSize = 20.sp) },
                            onClick = {
                                secondMenuExpanded = false
                                onSecondMenuClick()
                            }
                        )
                    }
                },
                actions = {

                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(150.dp)
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
        // El contenido de la pantalla principal va aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Información del usuario y botón de cerrar sesión
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombreUsuario,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = rolUsuario, style = MaterialTheme.typography.bodyLarge)
                Text(text = zonaUsuario, style = MaterialTheme.typography.bodyLarge)

                // Mensaje de bienvenida
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
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

            // Ícono de Soporte + Botón de Cerrar Sesión
            Row(
                modifier = Modifier
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSoporteClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Help, // Puedes usar un ícono como Help o Support
                        contentDescription = "Soporte"
                    )
                }

                Button(
                    onClick = { onLogoutClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.medium)
                ) {
                    Text(text = "Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Copyright
            Text(
                text = "© 2024 KerneliX Software",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
