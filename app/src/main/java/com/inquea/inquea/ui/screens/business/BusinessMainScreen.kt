package com.inquea.inquea.ui.screens.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.ui.components.GlassCard
import com.inquea.inquea.ui.screens.chat.ChatListScreen
import com.inquea.inquea.ui.screens.profile.ProfileScreen
import com.inquea.inquea.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BusinessMainScreen(
    onNavigateToUpload: () -> Unit,
    onNavigateToFlashOffer: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToResolution: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    viewModel: BusinessMainViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("Dashboard") }
    val myReelsState by viewModel.myReelsState.collectAsState()
    val bookingsState by viewModel.bookingsState.collectAsState()

    val activeReelsCount = myReelsState.data?.size ?: 0
    val bookingsCount = bookingsState.data?.size ?: 0
    val averageRating = if (activeReelsCount > 0) {
        myReelsState.data?.map { it.rating }?.average()?.toFloat() ?: 5.0f
    } else {
        5.0f
    }

    Scaffold(
        bottomBar = {
            BusinessBottomNavigation(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = 48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Subir Reel", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        if (selectedTab == "Dashboard") {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Dashboard Profesional",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Resumen de rendimiento",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MetricCard("Reels Activos", activeReelsCount.toString(), Modifier.weight(1f))
                        MetricCard("Reservas", bookingsCount.toString(), Modifier.weight(1f))
                    }
                }

                item {
                    MetricCard("Valoración Promedio", String.format(Locale.US, "%.1f ★", averageRating), Modifier.fillMaxWidth())
                }

                item {
                    Button(
                        onClick = onNavigateToFlashOffer,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Lanzar Oferta Relámpago", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tus Reels Activos",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                when (val state = myReelsState) {
                    is Resource.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    is Resource.Error -> {
                        item {
                            Text(text = state.message ?: "Error al cargar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is Resource.Success -> {
                        val reels = state.data ?: emptyList()
                        if (reels.isEmpty()) {
                            item {
                                Text(text = "No tienes reels activos", color = Color.Gray)
                            }
                        } else {
                            items(reels) { reel ->
                                var expanded by remember { mutableStateOf(false) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.DarkGray)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Gray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = reel.mediaUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // Overlay Icon
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (reel.isVideo) Icons.Default.PlayArrow else Icons.Default.Image,
                                                contentDescription = if (reel.isVideo) "Video" else "Imagen",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(text = reel.description.take(20) + if (reel.description.length > 20) "..." else "", color = Color.White, fontWeight = FontWeight.Bold)
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        Text(text = "Publicado el ${sdf.format(Date(reel.timestamp))}", color = Color.LightGray, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.White)
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier.background(Color.DarkGray)
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Eliminar", color = Color.Red) },
                                                onClick = {
                                                    expanded = false
                                                    viewModel.deleteReel(reel.id)
                                                },
                                                leadingIcon = {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(60.dp)) // padding for the FAB
                }
            }
        } else if (selectedTab == "Agenda") {
            BusinessAgendaScreen(modifier = Modifier.padding(paddingValues))
        } else if (selectedTab == "Mensajes") {
            Box(modifier = Modifier.padding(paddingValues)) {
                ChatListScreen(onChatClick = onNavigateToChat)
            }
        } else if (selectedTab == "Perfil") {
            Box(modifier = Modifier.padding(paddingValues)) {
                ProfileScreen(
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToResolution = onNavigateToResolution
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Sección en construcción", color = Color.White)
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column {
            Text(text = title, color = Color.Gray, fontSize = 12.sp)
            Text(text = value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BusinessBottomNavigation(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard", fontSize = 10.sp) },
            selected = selectedTab == "Dashboard",
            onClick = { onTabSelected("Dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Agenda") },
            label = { Text("Agenda", fontSize = 10.sp) },
            selected = selectedTab == "Agenda",
            onClick = { onTabSelected("Agenda") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Mensajes") },
            label = { Text("Mensajes", fontSize = 10.sp) },
            selected = selectedTab == "Mensajes",
            onClick = { onTabSelected("Mensajes") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) },
            selected = selectedTab == "Perfil",
            onClick = { onTabSelected("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.DarkGray
            )
        )
    }
}