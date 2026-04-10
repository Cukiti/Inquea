package com.inquea.inquea.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.inquea.inquea.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    businessId: String,
    onNavigateBack: () -> Unit,
    onProceedToPayment: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf("15 Oct") }
    var selectedTime by remember { mutableStateOf("") }
    
    val bookingState by viewModel.bookingState.collectAsState()
    
    val availableTimes = listOf("09:00", "10:00", "11:30", "14:00", "15:30", "17:00")

    LaunchedEffect(bookingState) {
        if (bookingState is Resource.Success) {
            onProceedToPayment()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita", color = Color.White) },
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
                    text = "Selecciona la Fecha",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("13 Oct", "14 Oct", "15 Oct", "16 Oct").forEach { date ->
                        DateBadge(
                            date = date,
                            isSelected = selectedDate == date,
                            onClick = { selectedDate = date }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Horarios Disponibles",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(availableTimes) { time ->
                        TimeBadge(
                            time = time,
                            isSelected = selectedTime == time,
                            onClick = { selectedTime = time }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                
                if (bookingState is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    PremiumButton(
                        text = "Proceder al Pago",
                        onClick = {
                            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                                viewModel.createBooking(businessId, selectedDate, selectedTime)
                            }
                        }
                    )
                }
                
                if (bookingState is Resource.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (bookingState as Resource.Error).message ?: "Error al reservar",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun DateBadge(date: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
    val parts = date.split(" ")
    
    Column(
        modifier = Modifier
            .width(64.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = parts[0], color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = parts[1], color = Color.LightGray, fontSize = 14.sp)
    }
}

@Composable
fun TimeBadge(time: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
    
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = time, color = Color.White, fontWeight = FontWeight.Medium)
    }
}