package mx.edu.utng.oic.security01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.oic.security01.navigation.NavigationGraph
import mx.edu.utng.oic.security01.navigation.Screen
import mx.edu.utng.oic.security01.ui.theme.Security01Theme
import mx.edu.utng.oic.security01.viewmodel.AuthViewModel

/**
 * Activity Principal de la aplicación
 *
 * Explicación:
 * En Compose, la Activity es MUCHO más simple que en Views tradicionales.
 * Solo hace dos cosas principales:
 * 1. Configura el tema
 * 2. Inicia el grafo de navegación
 */
class MainActivity : ComponentActivity() {
    // ViewModel compartido en toda la app
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalamos el splash screen del sistema
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            Security01Theme {
                // Surface es el contenedor base
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SecurityApp()
                }
            }
        }
    }
}

/**
 * Composable principal de la aplicación.
 * Establece el controlador de navegación y define el punto de partida.
 */
@Composable
fun SecurityApp() {
    val navController = rememberNavController()
    NavigationGraph(
        navController = navController,
        startDestination = Screen.Splash.route // La aplicación siempre arranca en el Splash
    )
}
