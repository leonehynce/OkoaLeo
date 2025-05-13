package com.leo.okoaleo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
//import com.leo.okoaleo.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = "Welcome to OkoaLeo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            HomeActionButton("Scan") { navController.navigate("scan") }
            HomeActionButton("Search") { navController.navigate("search") }
            HomeActionButton("Rewards") { navController.navigate("rewards") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Featured Product",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Optional image placeholder (uncomment and set correct resource if needed)
                // Image(
                //     painter = painterResource(id = R.drawable.featured_product),
                //     contentDescription = "Featured Product",
                //     modifier = Modifier
                //         .fillMaxWidth()
                //         .height(180.dp),
                //     contentScale = ContentScale.Crop
                // )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Eco-Friendly Toothpaste",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Certified safe, affordable, and effective for your dental care needs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("scan") },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Scan Product", color = Color.White)
                }
            }
        }

        // Floating Action Button for Settings
        FloatingActionButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
    }
}

@Composable
fun HomeActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}
