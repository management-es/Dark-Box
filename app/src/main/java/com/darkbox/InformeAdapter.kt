package com.darkbox

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class InformeAdapter(private val informes: List<InformeAdapter.InformeError>, private val ids: List<String>) :
    RecyclerView.Adapter<InformeAdapter.InformeViewHolder>() {

    // Clase interna para los informes
    data class InformeError(
        val descripcionError: String = "",
        val zona: String = "",
        val rol: String = "",
        val nombreUsuario: String = "",
        val estado: String = ""  // Estado del informe (Realizado o no)
    )

    class InformeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val informeID: TextView = itemView.findViewById(R.id.tvInformeID)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcionInforme)
        val zona: TextView = itemView.findViewById(R.id.tvZonaInforme)
        val usuario: TextView = itemView.findViewById(R.id.tvUsuarioInforme)
        val btnRealizado: Button = itemView.findViewById(R.id.btnRealizado)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_informe, parent, false)
        return InformeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InformeViewHolder, position: Int) {
        val informe = informes[position]
        val id = ids[position]

        holder.informeID.text = "ID: $id"
        holder.descripcion.text = informe.descripcionError
        holder.zona.text = "Zona: ${informe.zona}"
        holder.usuario.text = "Usuario: ${informe.nombreUsuario}"

        // Si el informe ya está marcado como realizado, deshabilitar el botón "Realizado"
        if (informe.estado == "Realizado") {
            holder.btnRealizado.isEnabled = false
            holder.btnRealizado.text = "Realizado"
        }

        // Acción del botón "Realizado" con confirmación
        holder.btnRealizado.setOnClickListener {
            // Crear el AlertDialog para confirmar si el problema está completamente resuelto
            val context: Context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmación de marcado como Realizado")
                .setMessage("¿Estás seguro de que has solucionado el problema en su totalidad? Después de marcarlo como realizado, este informe no se podrá modificar ni eliminar.")
                .setPositiveButton("Marcar como Realizado") { _, _ ->
                    // Actualizar el estado de "Realizado" en la base de datos
                    FirebaseDatabase.getInstance().getReference("soportedev").child(id)
                        .child("estado").setValue("Realizado")
                        .addOnSuccessListener {
                            // Deshabilitar el botón "Realizado" y cambiar su texto en el UI
                            holder.btnRealizado.isEnabled = false
                            holder.btnRealizado.text = "Realizado"
                            Toast.makeText(context, "Informe marcado como Realizado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al actualizar el informe", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss() // Cierra el dialogo sin realizar ninguna acción
                }

            // Mostrar el AlertDialog
            builder.create().show()
        }

        // Acción del botón "Eliminar" con confirmación
        holder.btnEliminar.setOnClickListener {
            // Crear el AlertDialog para confirmación de eliminación
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirmación de eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este informe? Esta acción no se podrá deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    // Eliminar el informe de la base de datos
                    FirebaseDatabase.getInstance().getReference("soportedev").child(id).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Informe eliminado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(holder.itemView.context, "Error al eliminar el informe", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss() // Cierra el dialogo sin realizar ninguna acción
                }

            // Mostrar el AlertDialog
            builder.create().show()
        }
    }

    override fun getItemCount(): Int = informes.size
}
