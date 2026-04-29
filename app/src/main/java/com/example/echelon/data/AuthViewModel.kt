package com.example.echelon.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.echelon.models.UserModel
import com.example.echelon.navigation.ROUTE_DASHBOARD1
import com.example.echelon.navigation.ROUTE_DASHBOARD2
import com.example.echelon.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signup(
        username: String,
        email: String,
        password: String,
        confirmpassword: String,
        userRole: String,
        navController: NavController,
        context: Context
    ) {
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
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
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                val user = UserModel(username, email, userId, userRole)

                db.collection("Users").document(userId).set(user).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                        if (userRole == "Uploader") {
                            navController.navigate(ROUTE_DASHBOARD1)
                        } else {
                            navController.navigate(ROUTE_DASHBOARD2)
                        }
                    } else {
                        Toast.makeText(context, "Database error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Auth error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email: String, password: String, navController: NavController, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val role = document.getString("role")
                            if (role == "Uploader") {
                                navController.navigate(ROUTE_DASHBOARD1)
                            } else {
                                navController.navigate(ROUTE_DASHBOARD2)
                            }
                        } else {
                            Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Database error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
