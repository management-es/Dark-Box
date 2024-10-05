package com.darkbox

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RespuestaBajaActivity : AppCompatActivity() {
    private lateinit var tecnologiaTextView: TextView
    private lateinit var enviadosAntenaTextView: TextView
    private lateinit var enviadosRouterTextView: TextView
    private lateinit var nivelesDbm1TextView: TextView
    private lateinit var nivelesDbm2TextView: TextView
    private lateinit var observacionesTextView: TextView
    private lateinit var perdidaAntenaTextView: TextView
    private lateinit var perdidaRouterTextView: TextView
    private lateinit var recibidosRouterTextView: TextView
    private lateinit var sectorAntenaTextView: TextView
    private lateinit var tiempoConectividadTextView: TextView

    // Campos para Fibra Óptica
    private lateinit var velocidadContratadaTextView: TextView
    private lateinit var potenciaFibraTextView: TextView

    private lateinit var apellidosTextView: TextView
    private lateinit var codClienteTextView: TextView
    private lateinit var nombresTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respuesta_baja)

        // Inicializar vistas
        tecnologiaTextView = findViewById(R.id.tecnologiaTextView)
        enviadosAntenaTextView = findViewById(R.id.enviadosAntenaTextView)
        enviadosRouterTextView = findViewById(R.id.enviadosRouterTextView)
        nivelesDbm1TextView = findViewById(R.id.nivelesDbm1TextView)
        nivelesDbm2TextView = findViewById(R.id.nivelesDbm2TextView)
        observacionesTextView = findViewById(R.id.observacionesTextView)
        perdidaAntenaTextView = findViewById(R.id.perdidaAntenaTextView)
        perdidaRouterTextView = findViewById(R.id.perdidaRouterTextView)
        recibidosRouterTextView = findViewById(R.id.recibidosRouterTextView)
        sectorAntenaTextView = findViewById(R.id.sectorAntenaTextView)
        tiempoConectividadTextView = findViewById(R.id.tiempoConectividadTextView)

        // Inicializar vistas para Fibra Óptica
        velocidadContratadaTextView = findViewById(R.id.velocidadContratadaTextView)
        potenciaFibraTextView = findViewById(R.id.potenciaFibraTextView)

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

                    if (tecnologia == "Radio Enlace") {
                        mostrarCamposRadioEnlace()
                        // Asignar valores específicos para Radio Enlace
                        val enviadosAntena = desarrolloSnapshot.child("enviadosAntena").getValue(String::class.java)
                        val enviadosRouter = desarrolloSnapshot.child("enviadosRouter").getValue(String::class.java)
                        val nivelesDbm1 = desarrolloSnapshot.child("nivelesDbm1").getValue(String::class.java)
                        val nivelesDbm2 = desarrolloSnapshot.child("nivelesDbm2").getValue(String::class.java)
                        val observaciones = desarrolloSnapshot.child("observacionesAntena").getValue(String::class.java)
                        val perdidaAntena = desarrolloSnapshot.child("perdidaAntena").getValue(String::class.java)
                        val perdidaRouter = desarrolloSnapshot.child("perdidaRouter").getValue(String::class.java)
                        val recibidosRouter = desarrolloSnapshot.child("recibidosRouter").getValue(String::class.java)
                        val sectorAntena = desarrolloSnapshot.child("sectorAntena").getValue(String::class.java)
                        val tiempoConectividad = desarrolloSnapshot.child("tiempoConectividad").getValue(String::class.java)

                        // Asignar valores a los TextViews
                        enviadosAntenaTextView.text = createSpannable("Enviados Antena: ", enviadosAntena ?: "No se encontraron enviados antena")
                        enviadosRouterTextView.text = createSpannable("Enviados Router: ", enviadosRouter ?: "No se encontraron enviados router")
                        nivelesDbm1TextView.text = createSpannable("Niveles dBm 1: ", nivelesDbm1 ?: "No se encontraron niveles dBm 1")
                        nivelesDbm2TextView.text = createSpannable("Niveles dBm 2: ", nivelesDbm2 ?: "No se encontraron niveles dBm 2")
                        observacionesTextView.text = createSpannable("Observaciones Antena: ", observaciones ?: "No se encontraron observaciones")
                        perdidaAntenaTextView.text = createSpannable("Pérdida Antena: ", perdidaAntena ?: "No se encontró pérdida en antena")
                        perdidaRouterTextView.text = createSpannable("Pérdida Router: ", perdidaRouter ?: "No se encontró pérdida en router")
                        recibidosRouterTextView.text = createSpannable("Recibidos Router: ", recibidosRouter ?: "No se encontraron recibidos en router")
                        sectorAntenaTextView.text = createSpannable("Sector Antena: ", sectorAntena ?: "No se encontró sector de la antena")
                        tiempoConectividadTextView.text = createSpannable("Tiempo de Conectividad: ", tiempoConectividad ?: "No se encontró tiempo de conectividad")

                    } else if (tecnologia == "Fibra Óptica") {
                        mostrarCamposFibraOptica()
                        // Asignar valores específicos para Fibra Óptica
                        val velocidadContratada = desarrolloSnapshot.child("velocidadContratada").getValue(String::class.java)
                        val potenciaFibra = desarrolloSnapshot.child("potenciaFibra").getValue(String::class.java)

                        velocidadContratadaTextView.text = createSpannable("Velocidad Contratada: ", velocidadContratada ?: "No se encontró la velocidad contratada")
                        potenciaFibraTextView.text = createSpannable("Potencia Fibra: ", potenciaFibra ?: "No se encontró la potencia de la fibra")
                    }

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

    // Mostrar los campos para Radio Enlace y ocultar los de Fibra Óptica
    private fun mostrarCamposRadioEnlace() {
        enviadosAntenaTextView.visibility = View.VISIBLE
        enviadosRouterTextView.visibility = View.VISIBLE
        nivelesDbm1TextView.visibility = View.VISIBLE
        nivelesDbm2TextView.visibility = View.VISIBLE
        observacionesTextView.visibility = View.VISIBLE
        perdidaAntenaTextView.visibility = View.VISIBLE
        perdidaRouterTextView.visibility = View.VISIBLE
        recibidosRouterTextView.visibility = View.VISIBLE
        sectorAntenaTextView.visibility = View.VISIBLE
        tiempoConectividadTextView.visibility = View.VISIBLE

        // Ocultar campos de Fibra Óptica
        velocidadContratadaTextView.visibility = View.GONE
        potenciaFibraTextView.visibility = View.GONE
    }

    // Mostrar los campos para Fibra Óptica y ocultar los de Radio Enlace
    private fun mostrarCamposFibraOptica() {
        enviadosAntenaTextView.visibility = View.GONE
        enviadosRouterTextView.visibility = View.GONE
        nivelesDbm1TextView.visibility = View.GONE
        nivelesDbm2TextView.visibility = View.GONE
        observacionesTextView.visibility = View.GONE
        perdidaAntenaTextView.visibility = View.GONE
        perdidaRouterTextView.visibility = View.GONE
        recibidosRouterTextView.visibility = View.GONE
        sectorAntenaTextView.visibility = View.GONE
        tiempoConectividadTextView.visibility = View.GONE

        // Mostrar campos de Fibra Óptica
        velocidadContratadaTextView.visibility = View.VISIBLE
        potenciaFibraTextView.visibility = View.VISIBLE
    }
}
