package com.darkbox

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class ResponderAltaActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_alta)

        // Creamos un TextView para mostrar el texto
        val textView = TextView(this)
        textView.text = "Responder solicitud de alta"

        // Configuramos el layout para que se muestre el texto
        setContentView(textView)
    }
}