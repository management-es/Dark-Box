package com.darkbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class InformesActivity : AppCompatActivity() {

    private lateinit var edtInformeDetail: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informes)

        edtInformeDetail = findViewById(R.id.edtInformeDetail)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val informeDetail = edtInformeDetail.text.toString()
            // Implementa la lógica para enviar el informe aquí
        }
    }
}
