package com.darkbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    private lateinit var zonaUsuario: String // Zona del usuario
    private val recordIds = mutableListOf<String>() // Lista para almacenar los IDs encontrados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descarga_agenda)

        datePicker = findViewById(R.id.datePicker)
        buttonBuscar = findViewById(R.id.button_buscar)
        buttonDescargar = findViewById(R.id.button_descargar)

        // Inicializa la referencia a la base de datos de Firebase
        database = FirebaseDatabase.getInstance().getReference("agenda")

        // Obtiene la zona del usuario desde el Intent y la asigna a la propiedad de clase
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: ""

        // Muestra un AlertDialog con la zona autorizada del usuario
        showZoneDialog()

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

    // Función para mostrar el AlertDialog con la zona del usuario
    private fun showZoneDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Zona Autorizada")
        builder.setMessage("Puedes descargar información de la zona: $zonaUsuario")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun getSelectedDate(): String? {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Los meses son indexados desde 0
        val year = datePicker.year

        return String.format("%04d%02d%02d", year, month, day) // Cambia el formato a AAAAMMDD
    }

    private fun searchAgendaByDate(selectedDate: String) {
        this.selectedDate = selectedDate

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recordIds.clear()

                for (data in snapshot.children) {
                    val recordId = data.key ?: continue

                    if (recordId.startsWith(selectedDate)) {
                        if (zonaUsuario == "Set-Admin") {
                            // Si es Set-Admin, agrega todos los registros sin filtrar por zona
                            recordIds.add(recordId)
                        } else {
                            // De lo contrario, verifica que la zona del registro coincida con la zona del usuario
                            val zonaRegistro = data.child("zona").getValue(String::class.java) ?: ""
                            if (zonaRegistro == zonaUsuario) {
                                recordIds.add(recordId)
                            }
                        }
                    }
                }

                if (recordIds.isNotEmpty()) {
                    showRecordsDialog(recordIds)
                } else {
                    Toast.makeText(this@DescargaAgendaActivity, "No se encontraron registros para la fecha y zona seleccionada", Toast.LENGTH_SHORT).show()
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
        var recordsProcessed = 0 // Contador de registros procesados

        if (recordIds.isEmpty()) {
            Toast.makeText(this, "No hay registros para descargar", Toast.LENGTH_SHORT).show()
            return
        }

        for (recordId in recordIds) {
            database.child(recordId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val recordInfo = snapshot.value.toString()
                        contentBuilder.append("\nID: $recordId\n")
                        contentBuilder.append(recordInfo.replace(",", "\n"))
                        contentBuilder.append("\n")
                    }

                    // Incrementa el contador de registros procesados
                    recordsProcessed++

                    // Solo guarda el archivo cuando todos los registros han sido procesados
                    if (recordsProcessed == recordIds.size) {
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
            finish() // Finaliza la actividad después de guardar el archivo
        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar el archivo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val CREATE_FILE_REQUEST_CODE = 1001
    }
}
