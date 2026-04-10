package com.inquea.inquea.ui.screens.search

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inquea.inquea.domain.model.BusinessProfile
import com.inquea.inquea.ui.components.PremiumTextField
import com.inquea.inquea.utils.Resource

@Composable
fun SearchRadarScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "RadarAnim")
    
    val radius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Radio"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explorar",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        PremiumTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.search(it)
            },
            label = "Buscar servicios..."
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        

        if (searchQuery.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.Cyan.copy(alpha = 1f - (radius / 300f)),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    
                    drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = 100f, style = Stroke(width = 1.dp.toPx()))
                    drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = 200f, style = Stroke(width = 1.dp.toPx()))
                    drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = 300f, style = Stroke(width = 1.dp.toPx()))
                    
                    drawCircle(color = Color.Green, radius = 8f, center = center.copy(x = center.x + 80f, y = center.y - 40f))
                    drawCircle(color = Color.Green, radius = 8f, center = center.copy(x = center.x - 120f, y = center.y + 60f))
                    drawCircle(color = Color.Green, radius = 8f, center = center.copy(x = center.x + 50f, y = center.y + 110f))
                }
                
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Cyan)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val foundCount = if (searchState is Resource.Success) {
                (searchState as Resource.Success<List<BusinessProfile>>).data?.size ?: 0
            } else 0
            
            Text(
                text = "Encontrados $foundCount negocios en tu área",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(label = "Belleza", onClick = { viewModel.search("Belleza") })
                FilterChip(label = "Salud", onClick = { viewModel.search("Salud") })
                FilterChip(label = "Hogar", onClick = { viewModel.search("Hogar") })
            }
        } else {
            // Results List
            when (searchState) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Error -> Text(text = "Error buscando", color = Color.Red)
                is Resource.Success -> {
                    val results = (searchState as Resource.Success<List<BusinessProfile>>).data ?: emptyList()
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(results) { business ->
                            BusinessResultItem(business)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessResultItem(business: BusinessProfile) {
    Surface(
        color = Color.DarkGray.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = business.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = business.specialty, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = business.description, color = Color.LightGray, fontSize = 14.sp, maxLines = 2)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "📍 ${business.location}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun FilterChip(label: String, onClick: () -> Unit) {
    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp
        )
    }
}