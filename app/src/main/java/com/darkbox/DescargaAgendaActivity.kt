package com.darkbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import java.io.IOException
import java.io.OutputStream

class DescargaAgendaActivity : ComponentActivity() {
    private lateinit var datePicker: DatePicker
    private lateinit var buttonBuscar: Button
    private lateinit var buttonDescargar: Button
    private lateinit var database: DatabaseReference
    private lateinit var selectedDate: String
    private val recordIds = mutableListOf<String>() // Lista para almacenar los IDs encontrados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descarga_agenda)

        datePicker = findViewById(R.id.datePicker)
        buttonBuscar = findViewById(R.id.button_buscar)
        buttonDescargar = findViewById(R.id.button_descargar)

        // Inicializa la referencia a la base de datos de Firebase
        database = FirebaseDatabase.getInstance().getReference("agenda")

        buttonBuscar.setOnClickListener {
            val selectedDate = getSelectedDate()
            if (selectedDate != null) {
                searchAgendaByDate(selectedDate)
            } else {
                Toast.makeText(this, "Por favor, selecciona una fecha válida", Toast.LENGTH_SHORT).show()
            }
        }

        buttonDescargar.setOnClickListener {
            if (recordIds.isNotEmpty()) {
                showFilePicker() // Llama al método para mostrar el selector de archivo
            } else {
                Toast.makeText(this, "No hay registros para descargar. Busca primero.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSelectedDate(): String? {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Los meses son indexados desde 0
        val year = datePicker.year

        return String.format("%04d%02d%02d", year, month, day) // Cambia el formato a AAAAMMDD
    }

    private fun searchAgendaByDate(selectedDate: String) {
        this.selectedDate = selectedDate // Almacena la fecha seleccionada

        // Busca en la base de datos usando la fecha formateada
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recordIds.clear() // Limpiar la lista antes de buscar

                // Iterar sobre todos los registros en el nodo 'agenda'
                for (data in snapshot.children) {
                    // Obtener el ID del registro
                    val recordId = data.key ?: continue // Salta si no hay ID

                    // Comparar los primeros 8 caracteres del ID con la fecha seleccionada
                    if (recordId.startsWith(selectedDate)) {
                        recordIds.add(recordId) // Agregar el ID a la lista si coincide
                    }
                }

                if (recordIds.isNotEmpty()) {
                    showRecordsDialog(recordIds)
                } else {
                    Toast.makeText(this@DescargaAgendaActivity, "No se encontraron registros para la fecha seleccionada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DescargaAgendaActivity, "Error al acceder a la base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecordsDialog(recordIds: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registros encontrados")

        // Solo mostrar los IDs en el AlertDialog
        builder.setItems(recordIds.toTypedArray(), null)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "agenda_${selectedDate}.txt") // Nombre del archivo
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                downloadRecordsToFile(uri) // Llama a la función para descargar registros
            }
        }
    }

    private fun downloadRecordsToFile(uri: Uri) {
        val contentBuilder = StringBuilder() // Usar StringBuilder para construir el contenido

        // Verificar si hay registros para procesar
        if (recordIds.isEmpty()) {
            Toast.makeText(this, "No hay registros para descargar", Toast.LENGTH_SHORT).show()
            return
        }

        // Procesar cada ID para obtener su información
        for (recordId in recordIds) {
            database.child(recordId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Aquí debes agregar la lógica para extraer la información que deseas
                        val recordInfo = snapshot.value.toString() // Obtén toda la información del registro

                        // Cambiar la forma en que se agrega el contenido con saltos de línea
                        contentBuilder.append("\nID: $recordId\n") // Agrega el ID
                        contentBuilder.append(recordInfo.replace(",", "\n")) // Reemplaza comas por saltos de línea
                        contentBuilder.append("\n") // Añadir un salto de línea extra para separar registros
                    }

                    // Guardar el contenido en el archivo después de procesar todos los registros
                    if (recordId == recordIds.last()) {
                        saveToFile(uri, contentBuilder.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DescargaAgendaActivity, "Error al acceder a los registros: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveToFile(uri: Uri, content: String) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                outputStream.write(content.toByteArray())
                Toast.makeText(this, "Archivo guardado correctamente", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar el archivo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CREATE_FILE_REQUEST_CODE = 1001 // Código de solicitud para crear un archivo
    }
}
