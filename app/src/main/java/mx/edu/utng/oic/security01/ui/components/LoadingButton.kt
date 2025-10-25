package mx.edu.utng.oic.security01.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
/**
 * Botón con indicador de carga
 *
 * EXPLICACIÓN: Este botón puede mostrar un "spinner" de carga
 * mientras procesa algo (como al hacer login).
 * Es como el botón de un ascensor que se ilumina mientras sube.
 */
@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            // Mostramos el spinner de carga
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cargando...")
            }
        } else {
            // Mostramos el texto normal
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}