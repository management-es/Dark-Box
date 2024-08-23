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
        val buttonEditCredential: Button = findViewById(R.id.button_edit_credential)

        buttonAddCredential.setOnClickListener {
            // Lanzar la actividad AccessActivity
            val intent = Intent(this, AccessActivity::class.java)
            startActivity(intent)
        }

        buttonEditCredential.setOnClickListener {
            // Lanzar la actividad EditAccesActivity
            val intent = Intent(this, EditAccesActivity::class.java)
            startActivity(intent)
        }
    }
}
