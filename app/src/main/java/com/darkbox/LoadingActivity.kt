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

class LoadingActivity : AppCompatActivity() {

    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        loadingAnimation = findViewById(R.id.loading_animation)
        errorMessage = findViewById(R.id.error_message)

        // Verificar conexión a internet
        if (isNetworkAvailable()) {
            showLoading()
            // Aquí podrías iniciar la consulta a Firebase
            fetchDataFromDatabase()
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

    // Función para verificar si hay conexión a internet, compatible con API 21+
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
            // Para versiones anteriores a Android M (API 23)
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    // Simulación de consulta a la base de datos (puedes reemplazarlo con tu lógica)
    private fun fetchDataFromDatabase() {
        // Aquí puedes añadir la lógica para consultar datos de Firebase
        // Cuando termines, puedes detener la animación de carga
        // loadingAnimation.visibility = View.GONE
    }
}
