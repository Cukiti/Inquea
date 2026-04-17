package com.inquea.inquea.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.ui.components.VideoPlayer
import com.inquea.inquea.ui.screens.search.SearchRadarScreen
import com.inquea.inquea.ui.screens.chat.ChatListScreen
import com.inquea.inquea.ui.screens.profile.ProfileScreen
import com.inquea.inquea.utils.Resource

@Composable
fun ClientMainScreen(
    onAuthRequired: () -> Unit,
    onNavigateToBooking: (String) -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToResolution: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("Home") }
    
    val feedState by viewModel.feedState.collectAsState()
    val chatState by viewModel.chatState.collectAsState()
    
    LaunchedEffect(chatState) {
        if (chatState is Resource.Success) {
            onNavigateToChat((chatState as Resource.Success<String>).data!!)
            viewModel.resetChatState()
        }
    }

    Scaffold(
        bottomBar = {
            ClientBottomNavigation(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when (selectedTab) {
                "Home" -> {
                    when (feedState) {
                        is Resource.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        is Resource.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Error al cargar los reels", color = Color.White)
                            }
                        }
                        is Resource.Success -> {
                            val reels = (feedState as Resource.Success<List<BusinessReel>>).data ?: emptyList()
                            if (reels.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(text = "Aún no hay publicaciones disponibles", color = Color.White)
                                }
                            } else {
                                val pagerState = rememberPagerState(pageCount = { reels.size })
                                VerticalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    val isVisible = pagerState.currentPage == page
                                    ReelItem(
                                        reel = reels[page],
                                        isPlaying = isVisible,
                                        onInteraction = { 
                                            if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null) {
                                                onAuthRequired()
                                            }
                                        },
                                        onBookClick = { onNavigateToBooking(reels[page].businessId) },
                                        onMessageClick = { viewModel.startChat(reels[page].businessId, reels[page].businessName) }
                                    )
                                }
                            }
                        }
                    }
                    
                    if (chatState is Resource.Loading) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                "Busqueda" -> {
                    SearchRadarScreen()
                }
                "Reservas" -> {
                    ClientBookingsScreen()
                }
                "Chat" -> {
                    ChatListScreen(onChatClick = onNavigateToChat)
                }
                "Perfil" -> {
                    ProfileScreen(
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToResolution = onNavigateToResolution
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "$selectedTab Area", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ClientBottomNavigation(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, "home"),
            Triple("Busqueda", Icons.Default.Search, "search"),
            Triple("Reservas", Icons.Default.DateRange, "reservations"),
            Triple("Chat", Icons.Default.Email, "chat"),
            Triple("Perfil", Icons.Default.Person, "profile")
        )

        items.forEach { (label, icon, _) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                selected = selectedTab == label,
                onClick = { onTabSelected(label) },
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
}

@Composable
fun ReelItem(
    reel: BusinessReel,
    isPlaying: Boolean,
    onInteraction: () -> Unit,
    onBookClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (reel.isVideo) {
            VideoPlayer(
                videoUrl = reel.mediaUrl,
                isPlaying = isPlaying,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = reel.mediaUrl,
                contentDescription = "Business media",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 500f
                    )
                )
        )
        
        // Flash Offer Badge
        if (reel.hasFlashOffer) {
            Row(
                modifier = Modifier
                    .padding(top = 48.dp, start = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Yellow)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "OFERTA RELÁMPAGO", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InteractionButton(Icons.Default.Favorite, "Like", onInteraction)
            InteractionButton(Icons.Default.MailOutline, "Mensaje", onMessageClick)
            InteractionButton(Icons.Default.Add, "Seguir", onInteraction)
            InteractionButton(Icons.Default.Share, "Compartir", onInteraction)
            InteractionButton(Icons.Default.Star, "Guardar", onInteraction)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 32.dp, end = 80.dp)
        ) {
            Text(text = reel.businessName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = reel.specialty, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = reel.rating.toString(), color = Color.White, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = reel.description, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                reel.tags.forEach { tag ->
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color.DarkGray).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "#$tag", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Reservar Ahora", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InteractionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(icon, contentDescription = label, tint = Color.White)
        }
        Text(text = label, color = Color.White, fontSize = 10.sp)
    }
}