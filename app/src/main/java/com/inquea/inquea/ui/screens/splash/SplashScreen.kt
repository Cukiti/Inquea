package com.inquea.inquea.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inquea.inquea.ui.screens.auth.AuthViewModel
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToClient: () -> Unit,
    onNavigateToBusiness: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    
    val roleState by viewModel.roleState.collectAsState()
    var animationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.checkCurrentUserRole()
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }
        delay(1500)
        animationFinished = true
    }
    
    LaunchedEffect(roleState, animationFinished) {
        if (animationFinished && roleState != null && roleState !is Resource.Loading) {
            when (val state = roleState) {
                is Resource.Success -> {
                    val role = state.data
                    if (role == "business") {
                        onNavigateToBusiness()
                    } else if (role == "client") {
                        onNavigateToClient()
                    } else {
                        onNavigateToOnboarding()
                    }
                }
                is Resource.Error -> onNavigateToOnboarding()
                else -> onNavigateToOnboarding()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "inquea",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}