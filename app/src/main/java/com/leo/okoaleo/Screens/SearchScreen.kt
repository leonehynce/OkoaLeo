package com.leone.okoleo.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*
import com.leo.okoaleo.ui.theme.OkoaLeoTheme

data class Product(
    val name: String = "",
    val price: Int = 0,
    val location: String = "",
    val supermarket: String = "",
    val image: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var products by remember { mutableStateOf(listOf<Product>()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    // Fetch products from Firebase
    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                products = productList
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load products.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Filter products based on search query and filters
    val filteredProducts = products.filter { product ->
        val matchesQuery = query.text.isEmpty() || listOf(
            product.name,
            product.location,
            product.supermarket
        ).any { it.contains(query.text, ignoreCase = true) }

        val matchesLocation = selectedLocation.isEmpty() || product.location.equals(selectedLocation, ignoreCase = true)
        val matchesPrice = maxPrice.isEmpty() || product.price <= maxPrice.toIntOrNull() ?: Int.MAX_VALUE

        matchesQuery && matchesLocation && matchesPrice
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Products") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search by name, location, or supermarket") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Text("No products found.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(filteredProducts) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(product.image),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(end = 16.dp)
                                )
                                Column {
                                    Text(product.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Price: KES ${product.price}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Location: ${product.location}", style = MaterialTheme.typography.bodySmall)
                                    Text("Supermarket: ${product.supermarket}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filter Options") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = selectedLocation,
                            onValueChange = { selectedLocation = it },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = maxPrice,
                            onValueChange = { maxPrice = it },
                            label = { Text("Max Price") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectedLocation = ""
                        maxPrice = ""
                        showFilterDialog = false
                    }) {
                        Text("Clear")
                    }
                }
            )
        }
    }
}
