package com.leo.okoaleo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage != null,
                singleLine = true
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Email and password cannot be empty"
                        Log.w("LoginScreen", "Login attempt with empty fields")
                        return@Button
                    }

                    if (!email.contains("@") || !email.contains(".")) {
                        errorMessage = "Invalid email format"
                        Log.w("LoginScreen", "Invalid email format: $email")
                        return@Button
                    }

                    if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        Log.w("LoginScreen", "Password too short")
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    val auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Log.d("LoginScreen", "Login successful for $email")
                                try {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                    Log.d("LoginScreen", "Navigated to home")
                                } catch (e: Exception) {
                                    errorMessage = "Navigation error: ${e.message}"
                                    Log.e("LoginScreen", "Navigation failed", e)
                                }
                            } else {
                                val exception = task.exception
                                errorMessage = when (exception) {
                                    is FirebaseAuthException -> {
                                        when (exception.errorCode) {
                                            "ERROR_INVALID_EMAIL" -> "Invalid email format"
                                            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                                            "ERROR_USER_NOT_FOUND" -> "User not found"
                                            else -> "Login failed: ${exception.message}"
                                        }
                                    }
                                    else -> "Login failed: ${exception?.message}"
                                }
                                Log.e("LoginScreen", "Login failed", exception)
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login")
                }
            }
        }
    }
}
