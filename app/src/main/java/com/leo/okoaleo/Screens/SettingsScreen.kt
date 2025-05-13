package com.leone.okoleo.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.leo.okoaleo.ui.theme.OkoaLeoTheme
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordChanged by remember { mutableStateOf(false) }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {

        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Divider()
        Text("Account", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            firebaseAuth.signOut()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("settings") { inclusive = true }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Spacer(Modifier.width(8.dp))
            Text("Back")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Text("Change Password", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (isPasswordChanged) {
            Text("Password Changed Successfully!", color = MaterialTheme.colorScheme.primary)
        }

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                    Icon(
                        imageVector = if (showCurrentPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showNewPassword = !showNewPassword }) {
                    Icon(
                        imageVector = if (showNewPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    newPassword != confirmPassword -> {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.length < 6 -> {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Set isLoading to true while updating the password
                        isLoading = true
                        changePassword(firebaseAuth, currentPassword, newPassword, context) {
                            isPasswordChanged = it
                            isLoading = false // Reset loading state after the password change
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Disable button when loading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Change Password")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Text("Feedback & Support", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("market://details?id=${context.packageName}")
                    }
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                    }
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Rate Our App ★★★★★")
        }

        TextButton(
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@okoaleo.com")
                    putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
                }
                context.startActivity(emailIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Send Feedback")
        }
    }
}

fun changePassword(
    firebaseAuth: FirebaseAuth,
    currentPassword: String,
    newPassword: String,
    context: Context,
    onPasswordChanged: (Boolean) -> Unit
) {
    val user = firebaseAuth.currentUser
    user?.let {
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        onPasswordChanged(true)
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        onPasswordChanged(false)
                        Toast.makeText(context, "Password change failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                onPasswordChanged(false)
                Toast.makeText(context, "Current password incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    OkoaLeoTheme {
        val navController = rememberNavController()
        SettingsScreen(navController = navController)
    }
}
