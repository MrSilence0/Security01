package mx.edu.utng.oic.security01.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mx.edu.utng.oic.security01.ui.screens.HomeScreen
import mx.edu.utng.oic.security01.ui.screens.LoginScreen
import mx.edu.utng.oic.security01.ui.screens.SplashScreen

/**
 * Rutas de navegación de la aplicación
 *
 * Explicación:
 * Las rutas son como las direcciones de diferentes páginas web.
 * Cada pantalla tiene su propia "dirección" única.
 *
 * sealed class es perfecta porque nos asegura que solo existan
 * las rutas que definimos aquí, nada más.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
}

/**
 * Grafo de navegación principal
 *
 * ANALOGÍA DEL GRAFO DE NAVEGACIÓN:
 * Imaginen un mapa de metro. Cada estación es una pantalla,
 * y las líneas que las conectan son las rutas de navegación.
 * El NavHost es el sistema completo de metro que gestiona todo.
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ============================================
        // PANTALLA DE SPLASH
        // ============================================
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        // Eliminamos Splash del stack para que no se pueda volver
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        // ============================================
        // PANTALLA DE LOGIN
        // ============================================
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        // Eliminamos Login del stack
                        // Esto evita que al presionar "atrás" regrese al login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        // ============================================
        // PANTALLA HOME
        // ============================================
        composable(route = Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        // Limpiamos todo el stack de navegación
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}