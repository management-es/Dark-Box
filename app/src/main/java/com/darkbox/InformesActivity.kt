package com.darkbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button

class InformesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informes)

        // Obtener referencias a los botones
        val btnInstalacion: Button = findViewById(R.id.btnInstalacion)
        val btnMantenimiento: Button = findViewById(R.id.btnMantenimiento)
        val btnDesmonte: Button = findViewById(R.id.btnDesmonte)
        val btnInspeccionPreoperacional: Button = findViewById(R.id.btnInspeccionPreoperacional)

        // Establecer los manejadores de clic para cada botón
        btnInstalacion.setOnClickListener { openUrl("https://docs.google.com/forms/d/e/1FAIpQLSftpnH2cvuZxNmmXXaWZRubH2eupvZ7vg_Hsmo9T---hWmWgw/viewform") }
        btnMantenimiento.setOnClickListener { openUrl("https://docs.google.com/forms/d/e/1FAIpQLSeR7fg2GHl1FgBtND2HUl_qrg2rm-G_dyzsD0_9M3fREJqkbw/viewform") }
        btnDesmonte.setOnClickListener { openUrl("https://docs.google.com/forms/d/e/1FAIpQLSc0UVXmarAU0jm1306axSGH4w6WG47VGVD6DY978Dk6aiYXNg/viewform?pli=1") }
        btnInspeccionPreoperacional.setOnClickListener { openUrl("https://docs.google.com/forms/d/e/1FAIpQLSd3nJSSKjcfxU2Zrs0vPeejqeXGT53nS9boiWu-J-6f5rPISQ/viewform") }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
}
