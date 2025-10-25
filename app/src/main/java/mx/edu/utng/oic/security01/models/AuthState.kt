package mx.edu.utng.oic.security01.models

/**
 * Representa los diferentes estados de autenticación
 * Esto es un patrón Sealed Class - muy útil para manejar estados
 */

sealed class AuthState {
    object Idle : AuthState() //Estado inicial, sin acción
    object Loading : AuthState() //Procesando autenticación
    data class Success(val user : User) : AuthState() //Login exitoso
    data class Error(val message : String) : AuthState() //Ocurrió un error
    object Logout : AuthState() //Usuario cerró sesión
}