package com.inquea.inquea.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun RegisterScreen(
    isInitialBusiness: Boolean = false,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isProvider by remember { mutableStateOf(isInitialBusiness) }
    
    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var businessNameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is Resource.Success) {
            onRegisterSuccess((authState as Resource.Success<String>).data!!)
            viewModel.resetState()
        }
    }
    
    fun validateForm(): Boolean {
        var isValid = true
        
        if (name.isBlank()) {
            nameError = "El nombre es requerido"
            isValid = false
        } else {
            nameError = null
        }
        
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
        } else if (password.length < 6) {
            passwordError = "Debe tener al menos 6 caracteres"
            isValid = false
        } else {
            passwordError = null
        }
        
        if (isProvider) {
            if (businessName.isBlank()) {
                businessNameError = "El nombre del negocio es requerido"
                isValid = false
            } else {
                businessNameError = null
            }
            
            if (address.isBlank()) {
                addressError = "La dirección es requerida"
                isValid = false
            } else {
                addressError = null
            }
            
            if (phone.isBlank()) {
                phoneError = "El número de teléfono es requerido"
                isValid = false
            } else {
                phoneError = null
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Crea tu cuenta",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Únete a inquea hoy",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RoleSelector(
                            text = "Cliente",
                            isSelected = !isProvider,
                            onClick = { isProvider = false },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        RoleSelector(
                            text = "Negocio",
                            isSelected = isProvider,
                            onClick = { isProvider = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    PremiumTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            if (nameError != null) nameError = null
                        },
                        label = if (isProvider) "Nombre del Propietario" else "Nombre Completo",
                        isError = nameError != null,
                        errorMessage = nameError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isProvider) {
                        PremiumTextField(
                            value = businessName,
                            onValueChange = { 
                                businessName = it
                                if (businessNameError != null) businessNameError = null
                            },
                            label = "Nombre del Negocio",
                            isError = businessNameError != null,
                            errorMessage = businessNameError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PremiumTextField(
                            value = address,
                            onValueChange = { 
                                address = it
                                if (addressError != null) addressError = null
                            },
                            label = "Dirección",
                            isError = addressError != null,
                            errorMessage = addressError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PremiumTextField(
                            value = phone,
                            onValueChange = { 
                                phone = it
                                if (phoneError != null) phoneError = null
                            },
                            label = "Número de Teléfono",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = phoneError != null,
                            errorMessage = phoneError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

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
                            text = "Registrarse",
                            onClick = {
                                if (validateForm()) {
                                    val role = if (isProvider) "business" else "client"
                                    viewModel.register(
                                        email = email.trim(), 
                                        password = password, 
                                        role = role, 
                                        name = name.trim(),
                                        businessName = if (isProvider) businessName.trim() else null,
                                        address = if (isProvider) address.trim() else null,
                                        phone = if (isProvider) phone.trim() else null
                                    )
                                }
                            }
                        )
                    }
                    
                    if (authState is Resource.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (authState as Resource.Error).message ?: "Error al registrarse",
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
                text = "O regístrate con",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialLoginButton(icon = Icons.Default.AccountCircle, label = "Google") { /* TODO */ }
                Spacer(modifier = Modifier.width(24.dp))
                SocialLoginButton(icon = Icons.Default.Phone, label = "Teléfono") { /* TODO */ }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Inicia Sesión",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SocialLoginButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Composable
fun RoleSelector(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}