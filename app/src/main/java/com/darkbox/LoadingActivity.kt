package com.darkbox

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import android.view.View
import android.widget.TextView
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import com.google.firebase.database.*

class LoadingActivity : AppCompatActivity() {

    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var errorMessage: TextView
    private var timer: CountDownTimer? = null
    private lateinit var database: DatabaseReference
    private val handler = Handler(Looper.getMainLooper()) // Handler para el retraso de 7 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        loadingAnimation = findViewById(R.id.loading_animation)
        errorMessage = findViewById(R.id.error_message)

        database = FirebaseDatabase.getInstance().getReference("access") // Nodo "access" en Firebase

        // Verificar conexión a internet
        if (isNetworkAvailable()) {
            showLoading()
            startTimer() // Iniciar temporizador
            fetchDataFromDatabase() // Consultar el nodo "access"
        } else {
            showError()
        }
    }

    private fun showLoading() {
        loadingAnimation.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
    }

    private fun showError() {
        loadingAnimation.visibility = View.GONE
        errorMessage.visibility = View.VISIBLE
    }

    // Función para verificar si hay conexión a internet
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    // Consultar la base de datos Firebase en el nodo "access"
    private fun fetchDataFromDatabase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si hay registros en el nodo "access"
                    stopTimer()
                    // Demorar 7 segundos antes de cerrar la actividad
                    handler.postDelayed({
                        finish() // Cerrar la actividad de carga después de 7 segundos
                    }, 5000) // 5 segundos de demora
                } else {
                    // Si no hay registros
                    stopTimer()
                    showErrorDialog("No se encontraron registros en la base de datos")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                stopTimer()
                showErrorDialog("Error al consultar la base de datos: ${databaseError.message}")
            }
        })
    }

    private fun showErrorDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Error de conexión")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish() // Cerrar la actividad de carga
        }
        builder.setCancelable(false)
        builder.show()
    }

    // Iniciar temporizador de 30 segundos
    private fun startTimer() {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Cada segundo, puedes actualizar algo si lo necesitas
            }

            override fun onFinish() {
                // Si el temporizador llega a 30 segundos y no se obtiene respuesta
                showErrorDialog("Revise su acceso a internet.")
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel() // Detenemos el temporizador si se recibe respuesta antes de los 30 segundos
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer() // Asegurarse de detener el temporizador cuando se destruye la actividad
        handler.removeCallbacksAndMessages(null) // Cancelar cualquier retraso pendiente si la actividad se destruye
    }
}
