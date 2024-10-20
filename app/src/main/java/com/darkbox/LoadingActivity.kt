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
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        loadingAnimation = findViewById(R.id.loading_animation)
        errorMessage = findViewById(R.id.error_message)

        database = FirebaseDatabase.getInstance().getReference("access")

        // Verificar conexión a internet
        if (isNetworkAvailable()) {
            showLoading()
            startTimer() // Iniciar temporizador
            fetchDataFromDatabase() // Consultar el nodo "access"
        } else {
            // Mostrar mensaje de error solo después de 30 segundos, no inmediatamente
            showLoading() // Mantener la animación de carga
            startTimer() // Aún iniciamos el temporizador para esperar 30 segundos
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
                    stopTimer() // Detener el temporizador si la consulta es exitosa
                    handler.postDelayed({
                        finish() // Cerrar la actividad de carga después de 5 segundos
                    }, 5000) // 5 segundos de demora
                } else {
                    // No mostrar el error inmediatamente, se deja que el temporizador lo gestione
                    stopTimer()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // En caso de error en la consulta, no detenemos el temporizador aún
                stopTimer() // Puedes detenerlo si prefieres no esperar en caso de un error de base de datos
            }
        })
    }

    private fun showErrorDialog(message: String) {
        stopTimer() // Asegurarse de detener el temporizador si ocurre un error
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
                // Aquí puedes actualizar la UI cada segundo si lo necesitas
            }

            override fun onFinish() {
                // Si el temporizador llega a 30 segundos y no se obtiene respuesta
                showErrorDialog("Revise su acceso a internet.")
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel() // Detener el temporizador si se recibe una respuesta antes de los 30 segundos
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer() // Asegurarse de detener el temporizador cuando se destruye la actividad
        handler.removeCallbacksAndMessages(null) // Cancelar cualquier retraso pendiente si la actividad se destruye
    }
}
