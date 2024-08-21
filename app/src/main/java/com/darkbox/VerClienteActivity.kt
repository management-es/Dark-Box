package com.darkbox

import android.view.View
import android.widget.TextView
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

data class Cliente(
    val cod_cliente: String? = null,
    val numero_documento: String? = null,
    val nombres: String? = null,
    val apellidos: String? = null,
    val contactos: String? = null,
    val coordenadas: String? = null,
    val correo: String? = null,
    val direccion: String? = null,
    val equipos: String? = null,
    val historial: String? = null,
    val ip_antena: String? = null,
    val ip_remota: String? = null,
    val observaciones: String? = null,
    val plan: String? = null,
    val serial_onu: String? = null,
    val serial_router: String? = null,
    val serial_antena: String? = null,
    val tecnologia: String? = null,
    val telefono: String? = null,
    val tipo_documento: String? = null,
    val zona: String? = null
)

class VerClienteActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var clientesRef: DatabaseReference

    private lateinit var btnBuscar: Button
    private lateinit var spinnerTipoBusqueda: Spinner
    private lateinit var edtBusqueda: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private lateinit var txtNoRecords: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_cliente)

        // Inicializa Firebase
        database = FirebaseDatabase.getInstance()
        clientesRef = database.getReference("clientes")

        // Vincular las vistas con sus IDs en el layout
        btnBuscar = findViewById(R.id.btnBuscar)
        spinnerTipoBusqueda = findViewById(R.id.spinnerTipoBusqueda)
        edtBusqueda = findViewById(R.id.edtBusqueda)
        recyclerView = findViewById(R.id.recyclerViewResultados)
        txtNoRecords = findViewById(R.id.txtNoRecords)

        // Configura el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClienteAdapter()
        recyclerView.adapter = adapter

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
        clientesRef.orderByChild("numero_documento").equalTo(documento).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val clientes = mutableListOf<Cliente>()
                for (child in snapshot.children) {
                    val cliente = child.getValue(Cliente::class.java)
                    if (cliente != null) {
                        clientes.add(cliente)
                    }
                }
                handleResults(clientes)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun buscarPorCodigoCliente(codigo: String) {
        clientesRef.orderByChild("cod_cliente").equalTo(codigo).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val clientes = mutableListOf<Cliente>()
                for (child in snapshot.children) {
                    val cliente = child.getValue(Cliente::class.java)
                    if (cliente != null) {
                        clientes.add(cliente)
                    }
                }
                handleResults(clientes)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun buscarPorNombreApellido(nombreApellido: String) {
        clientesRef.orderByChild("nombres").startAt(nombreApellido).endAt(nombreApellido + "\uf8ff").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val clientes = mutableListOf<Cliente>()
                for (child in snapshot.children) {
                    val cliente = child.getValue(Cliente::class.java)
                    if (cliente != null) {
                        clientes.add(cliente)
                    }
                }
                handleResults(clientes)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun handleResults(clientes: List<Cliente>) {
        if (clientes.isEmpty()) {
            recyclerView.visibility = View.GONE
            txtNoRecords.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            txtNoRecords.visibility = View.GONE
            adapter.submitList(clientes)
        }
    }
}
