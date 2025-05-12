package com.leo.okoaleo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.leo.okoaleo.navigation.AppNavHost
import com.leo.okoaleo.ui.theme.OkoaLeoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }

            enableEdgeToEdge()

            setContent {
                OkoaLeoApp()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing app", e)
            finish()
        }
    }
}

@Composable
fun OkoaLeoApp() {
    var isFirebaseReady by remember { mutableStateOf(false) }
    var firebaseError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current // ✅ Moved OUTSIDE LaunchedEffect

    LaunchedEffect(Unit) {
        try {
            val auth = FirebaseAuth.getInstance()
            auth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    Log.w("MainActivity", "User not logged in")
                } else {
                    Log.d("MainActivity", "User is logged in: ${user.email}")
                }
            }

            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }

            isFirebaseReady = true
        } catch (e: Exception) {
            firebaseError = "Firebase initialization failed: ${e.message}"
            Log.e("MainActivity", "Error during Firebase initialization", e)
        }
    }

    OkoaLeoTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                firebaseError != null -> {
                    ErrorScreen(
                        message = firebaseError ?: "Unknown error",
                        onRetry = {
                            firebaseError = null
                            isFirebaseReady = false
                        }
                    )
                }

                !isFirebaseReady -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    OkoaLeoTheme {
        OkoaLeoApp()
    }
}
