package com.example.echelon.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.echelon.models.UserModel
import com.example.echelon.navigation.ROUTE_DASHBOARD1
import com.example.echelon.navigation.ROUTE_DASHBOARD2
import com.example.echelon.navigation.ROUTE_LOGIN
import com.example.echelon.navigation.ROUTE_REGISTER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Users")

    fun signup(
        username: String,
        email: String,
        password: String,
        confirmpassword: String,
        userRole: String,
        navController: NavController,
        context: Context
    ) {
        val trimmedEmail = email.trim()
        val trimmedUsername = username.trim()

        if (trimmedUsername.isBlank() || trimmedEmail.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmpassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            try {
                // 1. Create User in Auth
                val task = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
                val userId = task.user?.uid ?: ""
                
                // 2. Prepare User Object
                val user = UserModel(trimmedUsername, trimmedEmail, userId, userRole)

                // 3. Save to Realtime Database (matching your HouseViewModel)
                dbRef.child(userId).setValue(user).await()
                
                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                
                // 4. Navigate based on role
                if (userRole == "Uploader") {
                    navController.navigate(ROUTE_DASHBOARD1) {
                        popUpTo(ROUTE_REGISTER) { inclusive = true }
                    }
                } else {
                    navController.navigate(ROUTE_DASHBOARD2) {
                        popUpTo(ROUTE_REGISTER) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed", e)
                Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun login(email: String, password: String, navController: NavController, context: Context) {
        val trimmedEmail = email.trim()
        
        if (trimmedEmail.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            try {
                // 1. Auth Sign In
                val task = auth.signInWithEmailAndPassword(trimmedEmail, password).await()
                val userId = task.user?.uid
                
                if (userId != null) {
                    // 2. Fetch User Role from Realtime Database
                    val snapshot = dbRef.child(userId).get().await()
                    
                    if (snapshot.exists()) {
                        val role = snapshot.child("role").getValue(String::class.java)
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        
                        if (role == "Uploader") {
                            navController.navigate(ROUTE_DASHBOARD1) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        } else {
                            navController.navigate(ROUTE_DASHBOARD2) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                    } else {
                        Toast.makeText(context, "User data not found in database", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0)
        }
    }
}
