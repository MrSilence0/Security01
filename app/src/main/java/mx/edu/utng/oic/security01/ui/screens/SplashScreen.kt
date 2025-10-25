package mx.edu.utng.oic.security01.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
// Importación corregida para el paquete 'oic'
import mx.edu.utng.oic.security01.viewmodel.AuthViewModel

/**
 * Pantalla de Splash (Carga inicial)
 *
 * PROPÓSITO DE LA SPLASH SCREEN:
 * 1. Verificar si hay sesión activa.
 * 2. Mostrar branding de la app.
 * 3. Dar tiempo para inicializar componentes.
 *
 * ANALOGÍA: Es como la portada de un libro. Te muestra qué esperar
 * mientras el libro (app) se prepara para abrirse completamente.
 */
@Composable
fun SplashScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // ============================================
    // ANIMACIÓN DEL LOGO
    // ============================================
    val scale = remember { Animatable(0f) }
    // Verificamos si hay sesión
    val isLoggedIn = viewModel.isLoggedIn()

    LaunchedEffect(key1 = true) {
        // Animación de entrada del logo
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        // Esperamos 2 segundos
        delay(2000)

        // Navegamos según si hay sesión o no
        if (isLoggedIn) {
            // Validamos el token antes de ir a Home
            viewModel.validateToken()
            delay(500) // Damos tiempo a la validación
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    // ============================================
    // UI DEL SPLASH
    // ============================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con animación (Usamos Icon en lugar de Image/painterResource)
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Logo de Seguridad",
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale.value),
                tint = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nombre de la app
            Text(
                text = "Security App",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Indicador de carga
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp
            )
        }
    }
}
