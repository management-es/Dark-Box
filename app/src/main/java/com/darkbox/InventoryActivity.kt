package com.darkbox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.darkbox.ui.theme.DarkBoxTheme

class InventoryActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        // Inicializa la referencia a la base de datos
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los campos y el botón
        val inputCliente: EditText = findViewById(R.id.input_cliente)
        val buttonSave: Button = findViewById(R.id.button_save)

        // Manejar el clic del botón de guardar
        buttonSave.setOnClickListener {
            val cliente = inputCliente.text.toString()
            saveData(cliente)
        }

        // Leer datos de la base de datos
        readData()
    }

    private fun saveData(cliente: String) {
        // Crear un mapa con los datos
        val inventoryData = mapOf(
            "serial" to "1234567890123456789012345678901234567890",
            "tecnologia" to "Nombre de la tecnología",
            "modelo" to "Nombre del modelo",
            "estado" to "Estado del dispositivo",
            "cliente" to cliente
        )

        // Escribir los datos en la base de datos
        database.child("inventario").setValue(inventoryData)
            .addOnSuccessListener {
                // Datos escritos correctamente
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                exception.printStackTrace()
            }
    }

    private fun readData() {
        // Leer los datos de la base de datos
        database.child("inventario").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val serial = snapshot.child("serial").getValue(String::class.java)
                    val tecnologia = snapshot.child("tecnologia").getValue(String::class.java)
                    val modelo = snapshot.child("modelo").getValue(String::class.java)
                    val estado = snapshot.child("estado").getValue(String::class.java)
                    val cliente = snapshot.child("cliente").getValue(String::class.java)

                    // Actualizar los TextViews en la interfaz de usuario
                    findViewById<TextView>(R.id.value_serial).text = serial ?: "No data"
                    findViewById<TextView>(R.id.value_tecnologia).text = tecnologia ?: "No data"
                    findViewById<TextView>(R.id.value_modelo).text = modelo ?: "No data"
                    findViewById<TextView>(R.id.value_estado).text = estado ?: "No data"
                    findViewById<TextView>(R.id.value_cliente).text = cliente ?: "No data"
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                exception.printStackTrace()
            }
    }
}
