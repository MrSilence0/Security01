package mx.edu.utng.oic.security01.network

import kotlinx.coroutines.delay
import mx.edu.utng.oic.security01.models.LoginRequest
import mx.edu.utng.oic.security01.models.LoginResponse
import mx.edu.utng.oic.security01.models.User
import retrofit2.Response
import java.util.UUID

/**
 * Servicio de API simulado para pruebas locales
 *
 * EXPLICACIÓN PARA ESTUDIANTES:
 * Esta clase simula un servidor real sin necesidad de tener uno activo.
 * Es como practicar fútbol con conos en lugar de jugadores reales.
 *
 * USO: Mientras desarrollan, pueden usar esto en lugar de la API real.
 * Cuando tengan servidor listo, solo cambian a RetrofitClient.
 */
object MockApiService {

    // Usuarios de prueba
    private val mockUsers = listOf(
        User(
            id = "1",
            email = "alumno@utng.edu.mx",
            name = "Estudiante Demo",
            token = generateToken()
        ),
        User(
            id = "2",
            email = "profesor@utng.edu.mx",
            name = "Profesor Demo",
            token = generateToken()
        ),
        User(
            id = "3",
            email = "admin@utng.edu.mx",
            name = "Administrador Demo",
            token = generateToken()
        )
    )

    /**
     * Simula el endpoint de login
     *
     * CREDENCIALES DE PRUEBA:
     * - Email: alumno@utng.edu.mx | Password: 123456
     * - Email: profesor@utng.edu.mx | Password: 123456
     * - Email: admin@utng.edu.mx | Password: 123456
     */
    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        // Simulamos latencia de red (1-2 segundos)
        delay((1000..2000).random().toLong())

        // Validaciones básicas
        if (loginRequest.email.isBlank() || loginRequest.password.isBlank()) {
            return Response.success(
                LoginResponse(
                    success = false,
                    message = "Email y contraseña son obligatorios",
                    user = null
                )
            )
        }

        // Buscamos el usuario
        val user = mockUsers.find { it.email == loginRequest.email }

        return if (user != null && loginRequest.password == "123456") {
            // Login exitoso
            val userWithNewToken = user.copy(token = generateToken())
            Response.success(
                LoginResponse(
                    success = true,
                    message = "Login exitoso",
                    user = userWithNewToken
                )
            )
        } else {
            // Credenciales incorrectas
            Response.success(
                LoginResponse(
                    success = false,
                    message = "Credenciales incorrectas",
                    user = null
                )
            )
        }
    }

    /**
     * Simula la validación de token
     */
    suspend fun validateToken(token: String): Response<LoginResponse> {
        delay(500)

        // Simulamos que todos los tokens son válidos
        // En producción, el servidor verificaría la firma JWT
        return Response.success(
            LoginResponse(
                success = true,
                message = "Token válido",
                user = null
            )
        )
    }

    /**
     * Simula el logout
     */
    suspend fun logout(token: String): Response<Unit> {
        delay(300)
        return Response.success(Unit)
    }

    /**
     * Genera un token JWT simulado
     *
     * NOTA: En producción, el token lo genera el servidor
     * con una firma criptográfica real (HMAC, RSA, etc.)
     */
    private fun generateToken(): String {
        val randomPart = UUID.randomUUID().toString().replace("-", "")
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.$randomPart.mock_signature"
    }
}