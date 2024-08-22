package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CredencialesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credenciales) // Inflar el archivo XML

        val buttonAddCredential: Button = findViewById(R.id.button_add_credential)

        buttonAddCredential.setOnClickListener {
            // Lanzar la actividad PerfilesDeAccesoActivity
            val intent = Intent(this, AccessActivity::class.java)
            startActivity(intent)
        }
    }
}
