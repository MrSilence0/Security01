package mx.edu.utng.oic.security01.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 * Clase que representa a un usuario en el sistema
 * @property id Identificador único del usuario
 * @property email Correo electrónico (usado para login)
 * @property name Nombre completo del usuario
 * @property token Token JWT para autenticación en API
 */

@Parcelize
data class User (
    val id: String,
    val email: String,
    val name: String,
    val token: String? = null
): Parcelable