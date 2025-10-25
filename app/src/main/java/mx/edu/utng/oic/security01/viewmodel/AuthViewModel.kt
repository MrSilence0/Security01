package mx.edu.utng.oic.security01.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.oic.security01.models.AuthState
import mx.edu.utng.oic.security01.models.User
import mx.edu.utng.oic.security01.repository.AuthRepository

/**
 * ViewModel para gestionar la autenticación
 *
 * ANALOGÍA DEL VIEWMODEL:
 * Imaginen que están en un restaurante. Ustedes (Activity/Fragment) no van
 * a la cocina a preparar su comida. Le dicen al mesero (ViewModel)
 * qué quieren, y el mesero se comunica con la cocina (Repository).
 *
 * El ViewModel sobrevive a cambios de configuración (rotación de pantalla)
 * mientras que las Activities/Fragments se destruyen y recrean.
 */

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Inicializamos el repositorio, inyectando el contexto de la aplicación
    private val repository = AuthRepository(application)

    // LiveData para observar cambios en el estado de autenticación (Idle, Loading, Success, Error, Logout)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    // LiveData para el usuario actual
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()



    /**
     * Inicializamos el ViewModel verificando si hay sesión activa
     */
    init {
        checkExistingSession()
    }

    /**
     * Verifica si existe una sesión guardada al iniciar la app
     */
    private fun checkExistingSession() {
        viewModelScope.launch {
            if (repository.isLoggedIn()) {
                val user = repository.getCurrentUser()
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)

                    // Validamos el token con el servidor para confirmar validez
                    validateToken()
                }
            }
        }
    }

    /**
     * Inicia sesión con email y contraseña
     *
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     */
    fun login(email: String, password: String) {
        // Cambiamos el estado a Loading para indicar que la operación está en curso
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            // Llamamos al repositorio de forma asíncrona
            val result = repository.login(email, password)

            // Procesamos el resultado
            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Success(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Error desconocido en el login"
                )
            }
        }
    }

    /**
     * Valida el token actual con el servidor
     * Útil para verificar si la sesión sigue válida sin necesidad de re-loguearse
     */
    fun validateToken() {
        viewModelScope.launch {
            val result = repository.validateToken()

            result.onSuccess { isValid ->
                if (!isValid) {
                    // Si el repositorio determinó que es inválido, ya ejecutó logout,
                    // solo actualizamos el estado del ViewModel.
                    _currentUser.value = null
                    _authState.value = AuthState.Logout
                }
            }.onFailure {
                // Error al validar (ej. sin conexión), mantenemos la sesión local
                // El usuario puede seguir usando la app offline hasta que el repositorio
                // fuerce el cierre.
            }
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.logout()

            // Independientemente de si el logout en el servidor falla o no,
            // forzamos la limpieza local y el estado de Logout.
            result.onSuccess {
                _currentUser.value = null
                _authState.value = AuthState.Logout
            }.onFailure { exception ->
                // Aún si hay un error de red, forzamos el logout local en el ViewModel
                _currentUser.value = null
                _authState.value = AuthState.Logout
            }
        }
    }

    /**
     * Reinicia el estado a Idle (Inactivo)
     * Útil después de que la UI haya consumido un estado de Error o Success
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    /**
     * Actualiza la actividad del usuario en el almacenamiento seguro
     */
    fun updateUserActivity() {
        repository.updateActivity()
    }

    /**
     * Verifica si hay sesión activa
     */
    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }
}