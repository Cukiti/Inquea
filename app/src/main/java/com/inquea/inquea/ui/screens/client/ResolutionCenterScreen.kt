package com.inquea.inquea.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inquea.inquea.ui.components.PremiumButton
import com.inquea.inquea.ui.components.PremiumTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResolutionCenterScreen(
    onNavigateBack: () -> Unit,
    onSubmitReport: () -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Centro de Resoluciones", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "¿Hubo un problema con tu servicio?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Tu pago está retenido. Cuéntanos qué pasó y un mediador te ayudará.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PremiumTextField(value = subject, onValueChange = { subject = it }, label = "Motivo del reporte")
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = description,
                onValueChange = { description = it },
                label = "Detalles de lo ocurrido",
                modifier = Modifier.height(120.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            PremiumButton(text = "Enviar Reporte", onClick = onSubmitReport)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}