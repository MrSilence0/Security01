package mx.edu.utng.oic.security01.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Importaciones corregidas para el paquete 'oic'
import mx.edu.utng.oic.security01.models.AuthState
import mx.edu.utng.oic.security01.models.User
import mx.edu.utng.oic.security01.viewmodel.AuthViewModel

/**
 * Pantalla principal después del login
 *
 * Explicación:
 * Esta es la pantalla que ven los usuarios después de iniciar sesión.
 * Muestra información del usuario y permite cerrar sesión.
 *
 * Es como la página principal de su perfil en redes sociales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel = viewModel(),
    onLogout: () -> Unit
) {
    // ============================================
    // ESTADOS Y OBSERVABLES
    // ============================================
    val authState by viewModel.authState.collectAsState(initial = AuthState.Idle)
    // Asumimos que AuthViewModel tiene un StateFlow llamado currentUser que emite User o null
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ============================================
    // OBSERVAR CAMBIOS DE ESTADO
    // ============================================
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Logout -> {
                // Navegamos a la pantalla de Login/Splash
                onLogout()
            }
            else -> {}
        }
    }

    // ============================================
    // ACTUALIZAR ACTIVIDAD DEL USUARIO
    // ============================================
    /**
     * Este efecto actualiza el timestamp de sesión
     * cada vez que el usuario entra a esta pantalla.
     *
     * IMPORTANTE: Esto mantiene la sesión activa mientras
     * el usuario está usando la app.
     */
    DisposableEffect(Unit) {
        viewModel.updateUserActivity()
        onDispose { }
    }

    // ============================================
    // UI PRINCIPAL
    // ============================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Botón de logout en la barra superior
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // ============================================
            // TARJETA DE INFORMACIÓN DEL USUARIO
            // ============================================
            currentUser?.let { user ->
                UserInfoCard(user = user)
                Spacer(modifier = Modifier.height(32.dp))
                // ============================================
                // SECCIÓN DE SEGURIDAD
                // ============================================
                SecuritySection(viewModel = viewModel)
                Spacer(modifier = Modifier.height(32.dp))
                // ============================================
                // BOTÓN DE CERRAR SESIÓN
                // ============================================
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } ?: run {
                // Si el usuario es nulo (cargando o error), mostramos un placeholder
                Text(
                    "Cargando información del usuario...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
                // Opcionalmente, un indicador de carga
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator()
                }
            }
        }

        // ============================================
        // DIÁLOGO DE CONFIRMACIÓN DE LOGOUT
        // ============================================
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {
                    Text(
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas cerrar sesión?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                        }
                    ) {
                        Text("Cerrar Sesión")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

/**
 * Tarjeta que muestra la información del usuario
 *
 * COMPOSABLE INTERNO: Esta función solo se usa dentro de HomeScreen
 * Es como una "subreceta" dentro de una receta principal.
 */
@Composable
private fun UserInfoCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar del usuario
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Nombre del usuario
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Email del usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // ID del usuario (solo para demostración)
            Text(
                text = "ID: ${user.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
/**
 * Sección de información de seguridad
 *
 * EXPLICACIÓN: Muestra información relevante sobre la sesión
 * y medidas de seguridad activas.
 */
@Composable
private fun SecuritySection(viewModel: AuthViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Información de Seguridad",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Items de seguridad
            SecurityItem(
                title = "Sesión Encriptada",
                description = "Tus datos están protegidos con encriptación AES-256"
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecurityItem(
                title = "Token de Autenticación",
                description = "Token JWT activo y verificado"
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecurityItem(
                title = "Expiración de Sesión",
                description = "Tu sesión expirará después de 24 horas de inactividad"
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Botón para validar token

            TextButton(
                onClick = { viewModel.validateToken() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Validar Token en Servidor")
            }
        }
    }
}
/**
 * Item individual de seguridad
 */
@Composable
private fun SecurityItem(
    title: String,
    description: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Se corrigió el cálculo de lineHeight ya que la función .value.dp * float no es estándar
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
            // Línea de altura removida para evitar problemas de compilación en un entorno estándar de Compose
        )
    }
}
