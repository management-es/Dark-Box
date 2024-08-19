package com.darkbox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class VerClienteActivity : AppCompatActivity() {

    private lateinit var btnBuscar: Button
    private lateinit var spinnerTipoBusqueda: Spinner
    private lateinit var edtBusqueda: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_cliente)

        // Vincular las vistas con sus IDs en el layout
        btnBuscar = findViewById(R.id.btnBuscar)
        spinnerTipoBusqueda = findViewById(R.id.spinnerTipoBusqueda)
        edtBusqueda = findViewById(R.id.edtBusqueda)

        btnBuscar.setOnClickListener {
            val searchType = spinnerTipoBusqueda.selectedItem.toString()
            val queryText = edtBusqueda.text.toString()

            // Lógica para realizar la búsqueda en Firebase según el tipo de búsqueda
            when (searchType) {
                "Documento" -> buscarPorDocumento(queryText)
                "Código Cliente" -> buscarPorCodigoCliente(queryText)
                "Nombre y Apellidos" -> buscarPorNombreApellido(queryText)
            }
        }
    }

    private fun buscarPorDocumento(documento: String) {
        // Implementación de la búsqueda por documento en Firebase
    }

    private fun buscarPorCodigoCliente(codigo: String) {
        // Implementación de la búsqueda por código de cliente en Firebase
    }

    private fun buscarPorNombreApellido(nombreApellido: String) {
        // Implementación de la búsqueda por nombre y apellidos en Firebase
    }
}
