package mx.edu.utng.oic.security01.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import mx.edu.utng.oic.security01.models.User

/**
 * Clase para almacenar datos sensibles de forma ENCRIPTADA

 *
 * ANALOGÍA PARA ESTUDIANTES:
 * Imaginen que tienen un diario personal. SharedPreferences normal
 * es como escribir en el diario en lenguaje normal - cualquiera que
 * lo encuentre puede leerlo.

 * EncryptedSharedPreferences es como escribir en código secreto
 * que solo ustedes pueden descifrar.
 */

class SecureStorage(context: Context) {
    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"


        //Tiempo de expiración de sesión: 24 hrs
        private const val SESSION_TIMEOUT = 24 * 60 * 60 * 1000L //milisegundos
    }

    //Creamos la "llave maestra" para encriptar
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM) //Algoritmo de encriptación militar
        .build()

    //Creamos las preferencias encriptadas
    private val sharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    /**
     * Guarda la sesión completa del usuario
     * Esta función se llama después de un login exitoso
     */

    fun saveUserSession(user: User) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, user.token)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_NAME, user.name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_SESSION_TIMESTAMP, System.currentTimeMillis())
            apply() // Guardamos de forma asíncrona
        }
    }

    /**
     * Obtiene el token de autenticación
     * IMPORTANTE: Este token NUNCA debe mostrarse en logs o UI
     */

    fun getToken(): String? {
        return if (isSessionValid()) {
            sharedPreferences.getString(KEY_TOKEN, null)
        } else {
            clearSession() // Sesión expirada, limpiamos todo
            null
        }
    }

    /**
     * Recupera los datos del usuario guardados
     */
    fun getUserData(): User? {
        if (!isSessionValid()) {
            clearSession()
            return null
        }
        val token = sharedPreferences.getString(KEY_TOKEN, null)
        val id = sharedPreferences.getString(KEY_USER_ID, null)
        val email = sharedPreferences.getString(KEY_USER_EMAIL, null)
        val name = sharedPreferences.getString(KEY_USER_NAME, null)

        // Si todos los datos existen, creamos el objeto User
        return if (token != null && id != null && email != null && name != null) {
            User(id, email, name, token)
        } else {
            null
        }
    }

    /**
     * Verifica si el usuario tiene sesión activa
     */

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && isSessionValid()
    }

    /**
     * Verifica si la sesión no ha expirado
     * CONCEPTO IMPORTANTE: Las sesiones deben tener tiempo límite por seguridad
     */

    private fun isSessionValid(): Boolean {
        val sessionTimestamp = sharedPreferences.getLong(KEY_SESSION_TIMESTAMP,
            0L)

        val currentTime = System.currentTimeMillis()
        val sessionAge = currentTime - sessionTimestamp

        return sessionAge < SESSION_TIMEOUT
    }

    /**
     * Actualiza el timestamp de la sesión
     * Llamar cada vez que el usuario interactúe con la app
     */

    fun updateSessionTimestamp() {
        sharedPreferences.edit().apply {
            putLong(KEY_SESSION_TIMESTAMP, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Cierra la sesión y elimina TODOS los datos sensibles
     * CRÍTICO: Debe ser exhaustivo para evitar fugas de información
     */

    fun clearSession() {
        sharedPreferences.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)

            remove(KEY_USER_NAME)
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_SESSION_TIMESTAMP)
            apply()
        }
    }
}