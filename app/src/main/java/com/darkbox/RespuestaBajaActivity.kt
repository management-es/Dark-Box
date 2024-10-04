package com.darkbox

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RespuestaBajaActivity : AppCompatActivity() {
    private lateinit var tecnologiaTextView: TextView
    private lateinit var enviadosTextView: TextView
    private lateinit var nivelesDbmTextView: TextView
    private lateinit var observacionesTextView: TextView
    private lateinit var perdidaTextView: TextView
    private lateinit var recibidosTextView: TextView
    private lateinit var apellidosTextView: TextView
    private lateinit var codClienteTextView: TextView
    private lateinit var nombresTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respuesta_baja)

        // Inicializar vistas
        tecnologiaTextView = findViewById(R.id.tecnologiaTextView)
        enviadosTextView = findViewById(R.id.enviadosTextView)
        nivelesDbmTextView = findViewById(R.id.nivelesDbmTextView)
        observacionesTextView = findViewById(R.id.observacionesTextView)
        perdidaTextView = findViewById(R.id.perdidaTextView)
        recibidosTextView = findViewById(R.id.recibidosTextView)
        apellidosTextView = findViewById(R.id.apellidosTextView)
        codClienteTextView = findViewById(R.id.codClienteTextView)
        nombresTextView = findViewById(R.id.nombresTextView)

        // Obtener el Ticket ID pasado por el intent
        val ticketId = intent.getStringExtra("TICKET_ID")

        // Inicializar la referencia a la base de datos de Firebase
        database = FirebaseDatabase.getInstance().getReference("respuestas")

        if (ticketId != null) {
            cargarRespuesta(ticketId)
        }
    }

    private fun cargarRespuesta(ticketId: String) {
        // Buscar en Firebase la respuesta con el Ticket ID correspondiente
        val query = database.orderByKey().startAt(ticketId).endAt("${ticketId}\uf8ff")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (respuestaSnapshot in dataSnapshot.children) {
                    val desarrolloSnapshot = respuestaSnapshot.child("desarrollo")

                    // Obtener el valor de tecnología
                    val tecnologia = desarrolloSnapshot.child("tecnologia").getValue(String::class.java)
                    tecnologiaTextView.text = createSpannable("Tecnología: ", tecnologia ?: "No se encontró la tecnología")

                    // Inicializar variables para los campos
                    var enviados: String? = null
                    val nivelesDbmList = mutableListOf<String>() // Usar una lista para almacenar múltiples valores de nivelesDbm
                    var observaciones: String? = null
                    var perdida: String? = null
                    var recibidos: String? = null

                    // Iterar por todos los hijos del nodo "desarrollo"
                    for (campoSnapshot in desarrolloSnapshot.children) {
                        val key = campoSnapshot.key
                        val value = campoSnapshot.getValue(String::class.java)

                        // Comprobar si el nombre del campo empieza con los prefijos deseados
                        when {
                            key?.startsWith("enviados", ignoreCase = true) == true -> {
                                enviados = value ?: "No se encontraron los enviados"
                            }
                            key?.startsWith("nivelesDbm", ignoreCase = true) == true -> {
                                // Agregar los valores de nivelesDbm a la lista
                                nivelesDbmList.add(value ?: "No se encontró nivelesDbm")
                            }
                            key?.startsWith("observaciones", ignoreCase = true) == true -> {
                                observaciones = value ?: "No se encontraron observaciones"
                            }
                            key?.startsWith("perdida", ignoreCase = true) == true -> {
                                perdida = value ?: "No se encontró la pérdida"
                            }
                            key?.startsWith("recibidos", ignoreCase = true) == true -> {
                                recibidos = value ?: "No se encontraron los recibidos"
                            }
                        }
                    }

                    // Concatenar los valores de nivelesDbm
                    val nivelesDbmConcatenado = if (nivelesDbmList.size >= 2) {
                        "Niveles dBm: ${nivelesDbmList[1]} / ${nivelesDbmList[0]}"
                    } else if (nivelesDbmList.isNotEmpty()) {
                        "Niveles dBm: ${nivelesDbmList[0]}"
                    } else {
                        "No se encontraron los niveles dBm"
                    }

                    // Asignar valores a los TextViews después de completar la iteración
                    enviadosTextView.text = createSpannable("Enviados: ", enviados ?: "No se encontraron los enviados")
                    nivelesDbmTextView.text = createSpannable("", nivelesDbmConcatenado)
                    observacionesTextView.text = createSpannable("Observaciones: ", observaciones ?: "No se encontraron observaciones")
                    perdidaTextView.text = createSpannable("Pérdida: ", perdida ?: "No se encontró la pérdida")
                    recibidosTextView.text = createSpannable("Recibidos: ", recibidos ?: "No se encontraron los recibidos")

                    // Obtener datos del subnodo cliente
                    val clienteSnapshot = respuestaSnapshot.child("cliente")
                    val apellidos = clienteSnapshot.child("apellidos").getValue(String::class.java)
                    val codCliente = clienteSnapshot.child("cod_cliente").getValue(String::class.java)
                    val nombres = clienteSnapshot.child("nombres").getValue(String::class.java)

                    apellidosTextView.text = createSpannable("Apellidos: ", apellidos ?: "No se encontraron apellidos")
                    codClienteTextView.text = createSpannable("Código Cliente: ", codCliente ?: "No se encontró el código del cliente")
                    nombresTextView.text = createSpannable("Nombres: ", nombres ?: "No se encontraron nombres")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })
    }

    // Método para crear un SpannableString con negrita
    private fun createSpannable(label: String, value: String): SpannableString {
        val spannableString = SpannableString("$label$value")
        spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }
}
