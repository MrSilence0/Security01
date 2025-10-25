package mx.edu.utng.oic.security01.network

import mx.edu.utng.oic.security01.models.LoginRequest
import mx.edu.utng.oic.security01.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interface que define todos los endpoints de nuestra API
 *
 * ANALOGÍA: Esto es como un menú de restaurante.
 * Define qué platos (servicios) puedes pedir y cómo pedirlos.
 */

interface ApiService {
    /**
     * Endpoint de login
     * @param loginRequest Credenciales del usuario
     * @return Respuesta con token y datos del usuario
     */

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    /**
     * Endpoint para validar que el token sigue siendo válido
     * @param token Token de autenticación en el header
     */

    @GET("auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): Response<LoginResponse>

    /**
     * Endpoint de logout (cierre de sesión en el servidor)
     * @param token Token para identificar la sesión a cerrar
     */

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
}