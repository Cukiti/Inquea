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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.inquea.inquea.ui.components.PremiumButton
import com.inquea.inquea.ui.components.PremiumTextField
import com.inquea.inquea.ui.components.VideoPlayer
import com.inquea.inquea.utils.Resource

@Composable
fun UploadReelScreen(
    onUploadSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: UploadReelViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var isVideo by remember { mutableStateOf(true) }
    
    val context = LocalContext.current
    val uploadState by viewModel.uploadState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        mediaUri = uri
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri)
            isVideo = mimeType?.startsWith("video/") == true
        }
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
            .systemBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Crear Publicación",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sube una imagen o video corto",
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
                        launcher.launch("*/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uploadState is Resource.Loading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Subiendo...", color = Color.White)
                    }
                } else if (mediaUri != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isVideo) {
                            VideoPlayer(
                                videoUrl = mediaUri.toString(),
                                isPlaying = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AsyncImage(
                                model = mediaUri,
                                contentDescription = "Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                Text(text = "Toca para cambiar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Text(text = "Seleccionar Archivo", color = Color.White)
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
                text = if (uploadState is Resource.Loading) "Subiendo..." else "Publicar",
                onClick = { 
                    if (mediaUri != null && title.isNotEmpty() && uploadState !is Resource.Loading) {
                        viewModel.uploadReel(mediaUri!!, title, tags, isVideo)
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