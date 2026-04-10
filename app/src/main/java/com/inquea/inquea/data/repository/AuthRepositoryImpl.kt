package com.inquea.inquea.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.inquea.inquea.domain.model.UserProfile
import com.inquea.inquea.domain.repository.AuthRepository
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId != null) {
                val doc = firestore.collection("users").document(userId).get().await()
                val role = doc.getString("role") ?: "client"
                emit(Resource.Success(role))
            } else {
                emit(Resource.Error("El ID de usuario es nulo"))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            emit(Resource.Error("El correo electrónico no está registrado."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("El correo electrónico o la contraseña son incorrectos."))
        } catch (e: Exception) {
            emit(Resource.Error("Ocurrió un error desconocido al iniciar sesión."))
        }
    }

    override fun register(
        email: String, 
        password: String, 
        role: String, 
        name: String,
        businessName: String?,
        address: String?,
        phone: String?
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            val userId = user?.uid
            if (userId != null && user != null) {
                // Update FirebaseUser profile with name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()

                val userMap = hashMapOf<String, Any>(
                    "name" to name,
                    "email" to email,
                    "role" to role
                )
                if (phone != null) userMap["phone"] = phone
                
                firestore.collection("users").document(userId).set(userMap).await()
                
                if (role == "business" && businessName != null && address != null) {
                    val businessProfile = hashMapOf(
                        "id" to userId,
                        "name" to businessName,
                        "location" to address,
                        "specialty" to "",
                        "description" to ""
                    )
                    firestore.collection("businesses").document(userId).set(businessProfile).await()
                }
                
                emit(Resource.Success(role))
            } else {
                emit(Resource.Error("El ID de usuario es nulo"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(Resource.Error("El correo electrónico ya está en uso por otra cuenta."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("La contraseña es muy débil o el formato de correo es inválido."))
        } catch (e: Exception) {
            emit(Resource.Error("Ocurrió un error desconocido al registrarse."))
        }
    }

    override fun getCurrentUserRole(): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val doc = firestore.collection("users").document(userId).get().await()
                val role = doc.getString("role")
                emit(Resource.Success(role))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener el rol del usuario."))
        }
    }

    override fun getCurrentUserProfile(): Flow<Resource<UserProfile?>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val doc = firestore.collection("users").document(userId).get().await()
                val profile = doc.toObject(UserProfile::class.java)
                emit(Resource.Success(profile?.copy(id = userId)))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener el perfil del usuario."))
        }
    }

    override fun logout() {
        auth.signOut()
    }
}
