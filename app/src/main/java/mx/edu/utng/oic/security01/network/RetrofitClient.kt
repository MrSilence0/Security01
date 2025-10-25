package mx.edu.utng.oic.security01.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Clase Singleton para configurar Retrofit
 *
 * IMPORTANTE PARA ESTUDIANTES:
 * Un Singleton es como tener UN SOLO teléfono en toda la escuela
 * para llamar al exterior. Todos usan el mismo teléfono, no necesitamos
 * tener uno por persona.
 */

object RetrofitClient {

    // URL base de la API - CAMBIAR POR LA URL REAL
    private const val BASE_URL = "https://api.ejemplo.com/"

    // Tag para logs - NUNCA mostrar tokens aquí
    private const val TAG = "RetrofitClient"

    /**
     * Configuramos el interceptor de logs
     * CRÍTICO: En producción, este debe estar desactivado o solo mostrar headers
     */

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        // Filtramos información sensible antes de hacer log
        val filteredMessage = filterSensitiveData(message)
        Log.d(TAG, filteredMessage)
    }.apply {
        // CAMBIAR A NONE en producción
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Configuramos el cliente HTTP
     */

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
// Aquí podemos agregar headers comunes a todas las peticiones
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para conectar
        .readTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para leer respuesta
        .writeTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para enviar datos
        .build()

    /**
     * Instancia de Retrofit - se crea una sola vez
     */

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)

        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Servicio de API - punto de acceso para hacer llamadas
     */

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    /**
     * Función para filtrar datos sensibles de los logs
     * NUNCA debe aparecer en logs:
     * - Passwords
     * - Tokens completos
     * - Información personal sensible
     */

    private fun filterSensitiveData(message: String): String {
        var filtered = message
        // Ocultamos passwords
        if (filtered.contains("password")) {
            filtered = filtered.replace(
                Regex("\"password\"\\s*:\\s*\"[^\"]*\""),
                "\"password\":\"***HIDDEN***\""
            )
        }

        // Ocultamos tokens completos, solo mostramos primeros y últimos 4 caracteres
        if (filtered.contains("Authorization")) {
            filtered = filtered.replace(
                Regex("Bearer [A-Za-z0-9._-]+"),
            "Bearer ****"
            )
        }
        if (filtered.contains("\"token\"")) {
            filtered = filtered.replace(
                Regex("\"token\"\\s*:\\s*\"([^\"]{4})[^\"]*([^\"]{4})\""),
            "\"token\":\"$1****$2\""
            )
        }
        
        return filtered
    }
}