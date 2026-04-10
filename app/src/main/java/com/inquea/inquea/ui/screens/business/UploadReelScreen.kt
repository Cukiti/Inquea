package com.inquea.inquea.ui.screens.business

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun UploadReelScreen(
    onUploadSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: UploadReelViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    
    val uploadState by viewModel.uploadState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        videoUri = uri
    }

    LaunchedEffect(uploadState) {
        if (uploadState is Resource.Success) {
            onUploadSuccess()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Subir Nuevo Reel",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Máximo 10 segundos por video",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.DarkGray)
                    .clickable(enabled = uploadState !is Resource.Loading) {
                        launcher.launch("video/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uploadState is Resource.Loading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Subiendo...", color = Color.White)
                    }
                } else if (videoUri != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                        Text(text = "Video Seleccionado", color = MaterialTheme.colorScheme.primary)
                        Text(text = "Toca para cambiar", color = Color.LightGray, fontSize = 12.sp)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Text(text = "Seleccionar Video", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            PremiumTextField(
                value = title,
                onValueChange = { title = it },
                label = "Descripción del servicio"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = tags,
                onValueChange = { tags = it },
                label = "Etiquetas (ej. #barber #salon)"
            )

            Spacer(modifier = Modifier.weight(1f))
            
            if (uploadState is Resource.Error) {
                Text(
                    text = (uploadState as Resource.Error).message ?: "Error al subir",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            PremiumButton(
                text = if (uploadState is Resource.Loading) "Subiendo..." else "Publicar Reel",
                onClick = { 
                    if (videoUri != null && title.isNotEmpty() && uploadState !is Resource.Loading) {
                        viewModel.uploadReel(videoUri!!, title, tags)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateBack, enabled = uploadState !is Resource.Loading) {
                Text(text = "Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}