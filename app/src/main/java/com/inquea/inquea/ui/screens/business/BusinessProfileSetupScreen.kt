package com.inquea.inquea.ui.screens.business

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inquea.inquea.ui.components.PremiumButton
import com.inquea.inquea.ui.components.PremiumTextField
import com.inquea.inquea.utils.Resource

@Composable
fun BusinessProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: BusinessProfileViewModel = hiltViewModel()
) {
    var specialty by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var isVideo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        mediaUri = uri
        // Simple check to see if it's a video based on content type returned from picker
        // For simplicity in this demo, we'll assume it's an image unless specified otherwise,
        // but real implementation should check content resolver.
    }

    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(saveState) {
        if (saveState is Resource.Success) {
            onSetupComplete()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tu Carta de Presentación",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega una imagen de perfil o un video corto para que los clientes vean tu trabajo.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .clickable(enabled = saveState !is Resource.Loading) {
                        launcher.launch("image/*") // You can change to "*/*" to allow video
                    },
                contentAlignment = Alignment.Center
            ) {
                if (mediaUri != null) {
                    Text("¡Archivo Seleccionado!", color = MaterialTheme.colorScheme.primary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Toca para agregar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            PremiumTextField(
                value = specialty,
                onValueChange = { specialty = it },
                label = "¿Cuál es tu especialidad principal? (ej. Barbero)"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = description,
                onValueChange = { description = it },
                label = "Describe tus servicios para tus clientes"
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            if (saveState is Resource.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                PremiumButton(
                    text = "Finalizar Configuración",
                    onClick = {
                        if (specialty.isNotEmpty() && description.isNotEmpty()) {
                            viewModel.updateProfile(specialty, description, mediaUri, isVideo)
                        }
                    }
                )
            }
            
            if (saveState is Resource.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (saveState as Resource.Error).message ?: "Error al guardar",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}