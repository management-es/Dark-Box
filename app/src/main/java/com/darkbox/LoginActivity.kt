package com.darkbox

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etEmail)
        etContrasena = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        database = FirebaseDatabase.getInstance().reference.child("access")

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                // Verificar credenciales en Firebase
                database.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val storedContrasena = snapshot.child("contrasena").getValue(String::class.java)
                            val nombreUsuario = snapshot.child("nombreUsuario").getValue(String::class.java)
                            val parametro = snapshot.child("parametro").getValue(String::class.java)
                            val observaciones = snapshot.child("observaciones").getValue(String::class.java)

                            if (storedContrasena == contrasena) {
                                if (parametro == "Activo") {
                                    // Navegar a MainActivity con el nombre de usuario
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                                        putExtra("NOMBRE_USUARIO", nombreUsuario)
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Mostrar mensaje de confirmación si el usuario está inactivo
                                    showInactiveUserDialog(observaciones)
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showInactiveUserDialog(observaciones: String?) {
        AlertDialog.Builder(this)
            .setTitle("Usuario Inactivo")
            .setMessage("Este usuario se encuentra inactivo por: $observaciones")
            .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            .create()
            .show()
    }
}
