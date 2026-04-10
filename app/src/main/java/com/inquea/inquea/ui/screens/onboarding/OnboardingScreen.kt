package com.inquea.inquea.ui.screens.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inquea.inquea.R
import com.inquea.inquea.ui.components.PremiumButton
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val imageResIds = listOf(
        R.drawable.barberia,
        R.drawable.salon,
        R.drawable.masajista
    )
    var currentIndex by remember { mutableIntStateOf(0) }

    // Cambia de imagen cada 4 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % imageResIds.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con Imágenes en Movimiento (Ken Burns effect simulado)
        Crossfade(
            targetState = currentIndex, 
            animationSpec = tween(1000),
            label = "ImageCrossfade"
        ) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "ScaleAnim")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f, // Pequeño zoom in para dar efecto de movimiento
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Scale"
            )

            Image(
                painter = painterResource(id = imageResIds[index]),
                contentDescription = "Background image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = scale, scaleY = scale)
            )
        }

        // Gradiente oscuro en la parte inferior para que resalten los textos y botones
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                        startY = 1000f
                    )
                )
        )

        // Contenido en la esquina inferior izquierda
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "inquea",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.5).sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Conecta con los mejores servicios\ncerca de ti.",
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Crear Cuenta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("Iniciar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
