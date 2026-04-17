package com.inquea.inquea.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.inquea.inquea.domain.model.Booking
import com.inquea.inquea.ui.components.GlassCard
import com.inquea.inquea.utils.Resource

@Composable
fun ClientBookingsScreen(
    viewModel: ClientBookingsViewModel = hiltViewModel()
) {
    val bookingsState by viewModel.bookingsState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Mis Reservas",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (bookingsState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = (bookingsState as Resource.Error).message ?: "Error al cargar reservas",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is Resource.Success -> {
                val bookings = (bookingsState as Resource.Success).data ?: emptyList()
                if (bookings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Aún no tienes reservas", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(bookings) { booking ->
                            BookingItem(booking = booking)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    val statusColor = when (booking.status) {
        "Confirmed" -> Color.Green
        "Cancelled" -> MaterialTheme.colorScheme.error
        else -> Color.Yellow // Pending
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = booking.service,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "$${booking.price}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Fecha: ${booking.date} • ${booking.time}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Text(
                    text = booking.status,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
