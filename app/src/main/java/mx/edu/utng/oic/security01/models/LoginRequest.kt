package mx.edu.utng.oic.security01.models

/**
 * Datos necesarios para hacer login
 * Esta información se enviará al servidor
 */

class LoginRequest(
    val email: String,
    val password: String
) {
}