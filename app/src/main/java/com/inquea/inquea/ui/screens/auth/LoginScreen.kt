package com.inquea.inquea.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inquea.inquea.ui.components.GlassCard
import com.inquea.inquea.ui.components.PremiumButton
import com.inquea.inquea.ui.components.PremiumTextField
import com.inquea.inquea.utils.Resource

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is Resource.Success) {
            onLoginSuccess((authState as Resource.Success<String>).data!!)
            viewModel.resetState()
        }
    }
    
    fun validateForm(): Boolean {
        var isValid = true
        
        if (email.isBlank()) {
            emailError = "El correo es requerido"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Formato de correo inválido"
            isValid = false
        } else {
            emailError = null
        }
        
        if (password.isBlank()) {
            passwordError = "La contraseña es requerida"
            isValid = false
        } else {
            passwordError = null
        }
        
        return isValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Bienvenido a Inquea",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia sesión para continuar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PremiumTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = "Correo Electrónico",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = emailError != null,
                        errorMessage = emailError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PremiumTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = "Contraseña",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordError != null,
                        errorMessage = passwordError
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (authState is Resource.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        PremiumButton(
                            text = "Iniciar Sesión",
                            onClick = {
                                if (validateForm()) {
                                    viewModel.login(email.trim(), password)
                                }
                            }
                        )
                    }
                    
                    if (authState is Resource.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (authState as Resource.Error).message ?: "Error al iniciar sesión",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            

            Text(
                text = "O inicia sesión con",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialLoginButton(icon = Icons.Default.AccountCircle, label = "Google") {  }
                Spacer(modifier = Modifier.width(24.dp))
                SocialLoginButton(icon = Icons.Default.Phone, label = "Teléfono") {  }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Regístrate",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}