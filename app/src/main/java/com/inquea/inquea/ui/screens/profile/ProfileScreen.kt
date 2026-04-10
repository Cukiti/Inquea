package com.inquea.inquea.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inquea.inquea.domain.model.UserProfile
import com.inquea.inquea.ui.components.GlassCard
import com.inquea.inquea.utils.Resource

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToResolution: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tu Perfil", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (profileState) {
            is Resource.Loading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
            is Resource.Error -> {
                Text(text = "Error al cargar perfil", color = MaterialTheme.colorScheme.error)
            }
            is Resource.Success -> {
                val profile = (profileState as Resource.Success<UserProfile?>).data
                Text(
                    text = profile?.name ?: "Usuario Inquea", 
                    color = Color.White, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = profile?.email ?: "usuario@inquea.com", 
                    color = Color.Gray, 
                    fontSize = 14.sp
                )
            }
            null -> {
                // Initial state
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ProfileOptionCard(
            icon = Icons.Default.SupportAgent,
            title = "Centro de Resoluciones",
            subtitle = "Ayuda y soporte técnico",
            onClick = onNavigateToResolution
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileOptionCard(
            icon = Icons.Default.Payment,
            title = "Métodos de Pago",
            subtitle = "Tarjetas y transferencias",
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun ProfileOptionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray)
        }
    }
}
