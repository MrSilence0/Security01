package mx.edu.utng.oic.security01.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Aseguramos que las importaciones de tu proyecto apunten al nuevo paquete 'oic'
import mx.edu.utng.oic.security01.R
import mx.edu.utng.oic.security01.models.AuthState
import mx.edu.utng.oic.security01.ui.components.CustomTextField
import mx.edu.utng.oic.security01.ui.components.ErrorDialog
import mx.edu.utng.oic.security01.ui.components.LoadingButton
import mx.edu.utng.oic.security01.viewmodel.AuthViewModel

/**
 * Pantalla de Login con Jetpack Compose
 *
 * ESTRUCTURA DE UNA PANTALLA COMPOSE:
 * 1. Estados locales (remember)
 * 2. Observación de ViewModels
 * 3. UI (interfaz visual)
 * 4. Efectos secundarios (LaunchedEffect, SideEffect)
 *
 * ANALOGÍA PARA ESTUDIANTES:
 * Imaginen que están montando una obra de teatro:
 * - Estados = Los diálogos que van cambiando
 * - ViewModel = El director que coordina todo
 * - UI = El escenario y los actores
 * - Efectos = Las luces y sonido que reaccionan a la acción
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    // ============================================
    // PASO 1: ESTADOS LOCALES
    // ============================================
    // Estados para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Estados para validaciones
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    // Estado para mostrar diálogo de error
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    // ============================================
    // PASO 2: OBSERVACIÓN DEL VIEWMODEL
    // ============================================
    val authState by viewModel.authState.collectAsState(initial = AuthState.Idle)

    // ============================================
    // PASO 3: EFECTOS SECUNDARIOS
    // ============================================
    /**
     * LaunchedEffect observa cambios en authState
     *
     * EXPLICACIÓN: Es como un "vigilante" que está atento
     * a cambios específicos y reacciona cuando ocurren.
     */
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Login exitoso - navegamos a la siguiente pantalla
                onLoginSuccess()
            }
            is AuthState.Error -> {
                // Mostramos el error
                errorMessage = (authState as AuthState.Error).message
                showErrorDialog = true
                viewModel.resetAuthState()
            }
            else -> {
                // Idle o Loading - no hacemos nada
            }
        }
    }
    // ============================================
    // PASO 4: FUNCIÓN DE VALIDACIÓN
    // ============================================
    /**
     * Valida los campos antes de enviar
     *
     * IMPORTANTE: Siempre validar en el cliente Y en el servidor
     * Cliente: Para dar feedback inmediato al usuario
     * Servidor: Por seguridad (nunca confiar solo en el cliente)
     */
    fun validateFields(): Boolean {
        var isValid = true
        // Validar email
        when {
            email.isBlank() -> {
                emailError = "El email es obligatorio"
                isValid = false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Formato de email inválido"
                isValid = false
            }
            else -> emailError = null
        }
        // Validar password
        when {
            password.isBlank() -> {
                passwordError = "La contraseña es obligatoria"
                isValid = false
            }
            password.length < 6 -> {
                passwordError = "Mínimo 6 caracteres"
                isValid = false
            }
            else -> passwordError = null
        }
        return isValid
    }
    // ============================================
    // PASO 5: FUNCIÓN DE LOGIN
    // ============================================
    fun performLogin() {
        if (validateFields()) {
            viewModel.login(email, password)
        }
    }
    // ============================================
    // PASO 6: UI - INTERFAZ VISUAL
    // ============================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Hacemos la columna scrolleable por si el teclado cubre contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ============================================
            // LOGO Y TÍTULOS
            // ============================================
            // Asegúrate de tener R.drawable.ic_security en tu proyecto
            Image(
                painter = painterResource(id = R.drawable.ic_security),
                contentDescription = "Logo de la app",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            // ============================================
            // CAMPOS DE TEXTO
            // ============================================
            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null // Limpiamos el error al escribir
                },
                label = "Correo electrónico",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                isError = emailError != null,
                errorMessage = emailError,
                enabled = authState !is AuthState.Loading
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = "Contraseña",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = { performLogin() },
                isError = passwordError != null,
                errorMessage = passwordError,
                enabled = authState !is AuthState.Loading
            )
            Spacer(modifier = Modifier.height(32.dp))
            // ============================================
            // BOTÓN DE LOGIN
            // ============================================
            LoadingButton(
                text = "Iniciar Sesión",
                onClick = { performLogin() },
                isLoading = authState is AuthState.Loading,
                enabled = authState !is AuthState.Loading
            )
            Spacer(modifier = Modifier.height(16.dp))
            // ============================================
            // TEXTO DE AYUDA (opcional)
            // ============================================
            TextButton(
                onClick = { /* TODO: Navegar a recuperar contraseña */ },
                enabled = authState !is AuthState.Loading
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            // ============================================
            // VERSIÓN DE LA APP (Info adicional)
            // ============================================
            Text(
                text = "Versión 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // ============================================
        // DIÁLOGO DE ERROR
        // ============================================
        if (showErrorDialog) {
            ErrorDialog(
                title = "Error de autenticación",
                message = errorMessage,
                onDismiss = { showErrorDialog = false }
            )
        }
    }
}
