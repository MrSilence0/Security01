package mx.edu.utng.oic.security01.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Importaciones necesarias de los archivos Color.kt y Type.kt en este mismo paquete:
// Se asume que los colores (Primary, PrimaryVariant, etc.) están definidos en Color.kt
// Se asume que la tipografía (Typography) está definida en Type.kt

/**
 * Esquema de colores para tema oscuro
 */
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    secondaryContainer = SecondaryVariant,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = Color.White,
    onSurface = Color.White,
)

/**
 * Esquema de colores para tema claro
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    secondaryContainer = SecondaryVariant,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
)

/**
 * Tema principal de la aplicación
 *
 * Explicación:
 * El tema es como la "paleta de colores" de un pintor.
 * Define todos los colores, fuentes y estilos que usaremos.
 *
 * @param darkTheme Si es true, usa colores oscuros
 * @param dynamicColor Si es true, usa colores del sistema (Android 12+) (NOTA: Se ignora en esta implementación básica)
 * @param content El contenido de la app que usará este tema
 */
@Composable
fun Security01Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Mantenemos la variable pero no se usa en el cuerpo actual
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Ajustamos el color de la barra de estado
            window.statusBarColor = colorScheme.primary.toArgb()
            // Configuramos los iconos de la barra de estado para que sean claros u oscuros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme // Si no es tema oscuro, los iconos deben ser oscuros
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
