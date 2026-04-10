package com.inquea.inquea.ui.screens.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashOfferScreen(
    onOfferCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FlashOfferViewModel = hiltViewModel()
) {
    var discount by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("1 hora") }
    var description by remember { mutableStateOf("") }
    
    val createState by viewModel.createState.collectAsState()
    
    LaunchedEffect(createState) {
        if (createState is Resource.Success) {
            onOfferCreated()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Oferta Relámpago", color = Color.White) },
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
            Icon(
                Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Atrae clientes ahora",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Las ofertas relámpago aparecen destacadas en el feed por tiempo limitado.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PremiumTextField(
                value = discount,
                onValueChange = { discount = it },
                label = "Porcentaje de Descuento (ej. 30%)"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = duration,
                onValueChange = { duration = it },
                label = "Duración de la oferta"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PremiumTextField(
                value = description,
                onValueChange = { description = it },
                label = "¿Para qué servicios aplica?"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (createState is Resource.Loading) {
                CircularProgressIndicator()
            } else {
                PremiumButton(
                    text = "Lanzar Oferta",
                    onClick = {
                        if (discount.isNotEmpty() && duration.isNotEmpty() && description.isNotEmpty()) {
                            viewModel.createOffer(discount, duration, description)
                        }
                    }
                )
            }
            
            if (createState is Resource.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (createState as Resource.Error).message ?: "Error al crear la oferta",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }
    }
}