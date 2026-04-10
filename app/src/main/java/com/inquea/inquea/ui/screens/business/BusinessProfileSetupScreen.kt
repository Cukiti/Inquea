package com.inquea.inquea.ui.screens.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var businessName by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Configura tu Negocio",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Completa tu perfil profesional",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            PremiumTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = "Nombre del Negocio o Profesional"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = specialty,
                onValueChange = { specialty = it },
                label = "Especialidad (ej. Barbero, Tutor)"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = location,
                onValueChange = { location = it },
                label = "Ubicación / Dirección"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción breve de tus servicios"
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            if (saveState is Resource.Loading) {
                CircularProgressIndicator()
            } else {
                PremiumButton(
                    text = "Guardar y Continuar",
                    onClick = {
                        if (businessName.isNotEmpty() && specialty.isNotEmpty() && location.isNotEmpty()) {
                            viewModel.saveProfile(businessName, specialty, location, description)
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