package com.darkbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.*
import androidx.appcompat.app.AlertDialog


class FiltrarAgendaActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference // Referencia a Firebase
    private lateinit var zonaUsuario: String
    private lateinit var rolUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtrar_agenda)

        // Obtener los datos del Intent
        zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: ""
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: ""

        // Inicializar referencia a Firebase
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los elementos del layout
        val spinnerMeses: Spinner = findViewById(R.id.spinner_meses)
        val inputAnio: EditText = findViewById(R.id.input_anio)
        val buttonAplicarFiltro: Button = findViewById(R.id.button_aplicar_filtro)
        val resultadosTextView: TextView = findViewById(R.id.resultados_text_view)
        val zonaUsuario = intent.getStringExtra("ZONA_USUARIO") ?: "Desconocida"


        // Mostrar el AlertDialog con la zona
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Información de Zona")
        builder.setMessage("Solamente puedes ver la información filtrada para la zona: $zonaUsuario")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss() // Cerrar el diálogo cuando se presiona "Aceptar"
        }
        builder.show()

        // Lista de meses con su formato numérico
        val meses = listOf(
            "01 - Enero", "02 - Febrero", "03 - Marzo", "04 - Abril",
            "05 - Mayo", "06 - Junio", "07 - Julio", "08 - Agosto",
            "09 - Septiembre", "10 - Octubre", "11 - Noviembre", "12 - Diciembre"
        )

        // Configurar el Spinner con la lista de meses
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMeses.adapter = adapter

        // Referencia al TableLayout para las tres columnas
        val tableLayout: TableLayout = findViewById(R.id.table_resultados)

        // Listener para el botón Aplicar Filtro
        buttonAplicarFiltro.setOnClickListener {
            val mesSeleccionado = spinnerMeses.selectedItem.toString().substring(0, 2) // Obtener el código del mes
            val anioIngresado = inputAnio.text.toString() // Obtener el año ingresado

            if (anioIngresado.isBlank() || anioIngresado.length != 4) {
                Toast.makeText(this, "Por favor ingrese un año válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar la tabla cuando se haga el filtro
            tableLayout.visibility = TableLayout.VISIBLE

            // Realizar la búsqueda en Firebase
            buscarAgendaEnFirebase(anioIngresado, mesSeleccionado, resultadosTextView, tableLayout)
        }

        // Configuración inicial de la tabla
        configurarTabla(tableLayout)
    }

    private fun configurarTabla(tableLayout: TableLayout) {
        // Agregar las tres columnas al TableLayout
        val rowHeader = TableRow(this)
        val solicitudInstalacionHeader = TextView(this).apply {
            text = "Solicitud \nInstalación"
            setPadding(16, 8, 16, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        val solicitudSobreClienteHeader = TextView(this).apply {
            text = "Solicitud \nSobre Cliente"
            setPadding(16, 8, 16, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        val otrasGestionesHeader = TextView(this).apply {
            text = "Otras \nGestiones"
            setPadding(16, 8, 16, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        rowHeader.addView(solicitudInstalacionHeader)
        rowHeader.addView(solicitudSobreClienteHeader)
        rowHeader.addView(otrasGestionesHeader)
        tableLayout.addView(rowHeader)

        // Agregar las tres filas debajo de cada columna
        for (i in 1..3) {
            val row = TableRow(this)
            val statusText = when (i) {
                1 -> "Pendiente"
                2 -> "Realizado"
                3 -> "Cancelado"
                else -> ""
            }

            // Crear las celdas con texto por defecto
            val solicitudInstalacionCell = TextView(this).apply {
                text = statusText
                tag = "solicitud_instalacion_$statusText" // Usar un tag para identificar la celda
                setPadding(16, 8, 16, 8)
            }
            val solicitudSobreClienteCell = TextView(this).apply {
                text = statusText
                tag = "solicitud_sobre_cliente_$statusText" // Usar un tag para identificar la celda
                setPadding(16, 8, 16, 8)
            }
            val otrasGestionesCell = TextView(this).apply {
                text = statusText
                tag = "otras_gestiones_$statusText" // Usar un tag para identificar la celda
                setPadding(16, 8, 16, 8)
            }

            // Agregar las celdas a la fila
            row.addView(solicitudInstalacionCell)
            row.addView(solicitudSobreClienteCell)
            row.addView(otrasGestionesCell)

            // Agregar la fila al TableLayout
            tableLayout.addView(row)
        }
    }

    private fun buscarAgendaEnFirebase(
        anio: String,
        mes: String,
        resultadosTextView: TextView,
        tableLayout: TableLayout
    ) {
        val inicioRango = "${anio}${mes}01-" // Inicio del rango
        val finRango = "${anio}${mes}31~" // Fin del rango, '~' asegura que incluya hasta el último día del mes

        database.child("agenda")
            .orderByKey()
            .startAt(inicioRango)
            .endAt(finRango)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    var contadorPendiente = 0
                    var contadorRealizado = 0
                    var contadorCancelado = 0
                    var contadorPendienteCliente = 0
                    var contadorRealizadoCliente = 0
                    var contadorCanceladoCliente = 0
                    var contadorPendienteGestion = 0
                    var contadorRealizadoGestion = 0
                    var contadorCanceladoGestion = 0

                    val resultados = StringBuilder()

                    for (child in snapshot.children) {
                        val id = child.key ?: "Sin ID" // ID del nodo
                        val fecha = id.split("-").firstOrNull() ?: "Sin fecha" // Extraer la fecha del ID
                        val tipoGestion = child.child("tipoGestion").getValue(String::class.java) ?: "N/A"
                        val gestion = child.child("gestion").getValue(String::class.java) ?: "N/A"
                        val observacionCancelacion =
                            child.child("observacion-cancelacion").getValue(String::class.java) ?: "N/A"
                        val estado = child.child("estado").getValue(String::class.java) ?: "Pendiente"
                        val zona = child.child("zona").getValue(String::class.java) ?: ""

                        // Filtrar por zona si el usuario no es técnico
                        if (rolUsuario != "Tecnico" && zona == zonaUsuario) {
                            // Contar solicitudes según el estado en Solicitud Instalación
                            if (id.contains("sol_inst")) {
                                when (estado.lowercase()) {
                                    "pendiente" -> contadorPendiente++
                                    "realizado" -> contadorRealizado++
                                    "cancelado" -> contadorCancelado++
                                }
                            }

                            // Contar solicitudes sobre cliente (ID contiene "C" después del guion)
                            if (id.split("-").getOrNull(1)?.contains("C") == true) {
                                when (estado.lowercase()) {
                                    "pendiente" -> contadorPendienteCliente++
                                    "realizado" -> contadorRealizadoCliente++
                                    "cancelado" -> contadorCanceladoCliente++
                                }
                            }

                            // Contar "Otras Gestiones" (ID contiene "ot-gestion")
                            if (id.contains("ot-gestion")) {
                                when (estado.lowercase()) {
                                    "pendiente" -> contadorPendienteGestion++
                                    "realizado" -> contadorRealizadoGestion++
                                    "cancelado" -> contadorCanceladoGestion++
                                }
                            }

                            // Mostrar los resultados con el formato actual
                            resultados.append(
                                """
                                ___________________________________________
                                ID: $id
                                Fecha: $fecha
                                Tipo de Gestión: $tipoGestion
                                Gestión: $gestion
                                Observación/Cancelación: $observacionCancelacion
                                Estado: $estado

                                """.trimIndent()  // Espacio adicional entre cada ID y sus datos
                            )
                        }
                    }

                    // Actualizar el conteo en la interfaz
                    actualizarConteo(tableLayout, contadorPendiente, contadorRealizado, contadorCancelado,
                        contadorPendienteCliente, contadorRealizadoCliente, contadorCanceladoCliente,
                        contadorPendienteGestion, contadorRealizadoGestion, contadorCanceladoGestion)

                    // Actualizar el TextView de resultados
                    resultadosTextView.text = resultados.toString()
                } else {
                    resultadosTextView.text = "No se encontraron resultados para el mes y año seleccionados."
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FiltrarAgendaActivity", "Error al buscar en Firebase", exception)
                Toast.makeText(this, "Error al realizar la búsqueda", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarConteo(
        tableLayout: TableLayout,
        contadorPendiente: Int,
        contadorRealizado: Int,
        contadorCancelado: Int,
        contadorPendienteCliente: Int,
        contadorRealizadoCliente: Int,
        contadorCanceladoCliente: Int,
        contadorPendienteGestion: Int,
        contadorRealizadoGestion: Int,
        contadorCanceladoGestion: Int
    ) {
        // Actualizar las celdas de "Pendiente", "Realizado", "Cancelado" en la columna "Solicitud Instalación"
        tableLayout.findViewWithTag<TextView>("solicitud_instalacion_Pendiente")
            ?.text = "Pendiente: $contadorPendiente"
        tableLayout.findViewWithTag<TextView>("solicitud_instalacion_Realizado")
            ?.text = "Realizado: $contadorRealizado"
        tableLayout.findViewWithTag<TextView>("solicitud_instalacion_Cancelado")
            ?.text = "Cancelado: $contadorCancelado"

        // Actualizar las celdas de "Pendiente", "Realizado", "Cancelado" en la columna "Solicitud Sobre Cliente"
        tableLayout.findViewWithTag<TextView>("solicitud_sobre_cliente_Pendiente")
            ?.text = "Pendiente: $contadorPendienteCliente"
        tableLayout.findViewWithTag<TextView>("solicitud_sobre_cliente_Realizado")
            ?.text = "Realizado: $contadorRealizadoCliente"
        tableLayout.findViewWithTag<TextView>("solicitud_sobre_cliente_Cancelado")
            ?.text = "Cancelado: $contadorCanceladoCliente"

        // Actualizar las celdas de "Pendiente", "Realizado", "Cancelado" en la columna "Otras Gestiones"
        tableLayout.findViewWithTag<TextView>("otras_gestiones_Pendiente")
            ?.text = "Pendiente: $contadorPendienteGestion"
        tableLayout.findViewWithTag<TextView>("otras_gestiones_Realizado")
            ?.text = "Realizado: $contadorRealizadoGestion"
        tableLayout.findViewWithTag<TextView>("otras_gestiones_Cancelado")
            ?.text = "Cancelado: $contadorCanceladoGestion"
    }
}
