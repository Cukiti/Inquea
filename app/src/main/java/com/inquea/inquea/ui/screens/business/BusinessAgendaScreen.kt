package com.inquea.inquea.ui.screens.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inquea.inquea.ui.components.GlassCard
import com.inquea.inquea.utils.Resource

@Composable
fun BusinessAgendaScreen(
    modifier: Modifier = Modifier,
    viewModel: BusinessAgendaViewModel = hiltViewModel()
) {
    val bookingsState by viewModel.bookingsState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Tu Agenda",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Hoy, 15 de Octubre",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when (bookingsState) {
            is Resource.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is Resource.Error -> {
                Text(
                    text = (bookingsState as Resource.Error).message ?: "Error al cargar la agenda",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is Resource.Success -> {
                val bookings = (bookingsState as Resource.Success).data ?: emptyList()
                if (bookings.isEmpty()) {
                    Text(
                        text = "No tienes citas agendadas.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(bookings) { booking ->
                            AppointmentCard(
                                time = booking.time,
                                clientName = booking.clientName,
                                service = booking.service,
                                status = booking.status
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(time: String, clientName: String, service: String, status: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                Text(text = time, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                // Text(text = time.split(" ")[1], color = Color.Gray, fontSize = 12.sp)
            }
            
            HorizontalDivider(
                color = Color.DarkGray,
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp)
                    .padding(horizontal = 16.dp)
            )
            

            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(text = clientName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = service, color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status, 
                    color = if (status == "Confirmed") MaterialTheme.colorScheme.primary else Color.Yellow, 
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Icon(Icons.Default.Person, contentDescription = "Cliente", tint = Color.Gray)
        }
    }
}