package com.inquea.inquea.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inquea.inquea.ui.components.GlassCard
import com.inquea.inquea.ui.components.PremiumButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("card") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Pago", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                

                Text(
                    text = "Resumen del Servicio",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Corte de Cabello", color = Color.White, fontWeight = FontWeight.Medium)
                            Text(text = "$25.00", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "15 Oct • 10:00 AM", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total a retener", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = "$25.00", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Protección Inquea: El pago solo se libera al profesional tras completar el servicio.",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                

                Text(
                    text = "Método de Pago",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                PaymentMethodOption(
                    label = "Tarjeta de Crédito / Débito",
                    isSelected = selectedMethod == "card",
                    onClick = { selectedMethod = "card" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PaymentMethodOption(
                    label = "Google Pay",
                    isSelected = selectedMethod == "google_pay",
                    onClick = { selectedMethod = "google_pay" }
                )

                Spacer(modifier = Modifier.weight(1f))
                
                PremiumButton(
                    text = "Pagar y Reservar",
                    onClick = onPaymentSuccess
                )
            }
        }
    }
}

@Composable
fun PaymentMethodOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.DarkGray.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        } else {
            Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = Color.White, fontWeight = FontWeight.Medium)
    }
}