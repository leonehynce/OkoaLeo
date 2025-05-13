package com.leone.okoleo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.leo.okoaleo.navigation.ROUTE_HOME
import com.leo.okoaleo.navigation.ROUTE_LOGIN
import com.leo.okoaleo.ui.theme.OkoaLeoTheme

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val db = Firebase.firestore

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join our business network",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill all fields"
                    } else if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                    } else {
                        isLoading = true
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    val userId = authTask.result.user?.uid ?: return@addOnCompleteListener
                                    val user = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "createdAt" to System.currentTimeMillis()
                                    )

                                    db.collection("users")
                                        .document(userId)
                                        .set(user)
                                        .addOnCompleteListener { dbTask ->
                                            isLoading = false
                                            if (dbTask.isSuccessful) {
                                                navController.navigate(ROUTE_HOME) {
                                                    popUpTo(0)
                                                }
                                            } else {
                                                errorMessage = "Registration failed: ${dbTask.exception?.message}"
                                            }
                                        }
                                } else {
                                    isLoading = false
                                    errorMessage = authTask.exception?.message ?: "Registration failed"
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Register", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Already have an account? Login",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
