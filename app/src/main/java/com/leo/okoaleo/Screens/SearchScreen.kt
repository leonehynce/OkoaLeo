package com.leone.okoleo.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class Product(
    val name: String = "",
    val price: Int = 0,
    val location: String = "",
    val supermarket: String = "",
    val image: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    val context = LocalContext.current
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var products by remember { mutableStateOf(listOf<Product>()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    // Firebase fetch with query filter
    LaunchedEffect(query.text) {
        // Reference to the products node in Firebase
        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        // Listen for changes to the database and filter results based on query text
        productsRef.orderByChild("name")
            .startAt(query.text)
            .endAt(query.text + "\uf8ff") // This enables a range query for the search
            .addValueEventListener(object : ValueEventListener {
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

    // Filter products based on user query and filter criteria
    val filteredProducts = products.filter { product ->
        val matchesQuery = query.text.isEmpty() || listOf(
            product.name, product.location, product.supermarket
        ).any { it.contains(query.text, ignoreCase = true) }

        val matchesLocation = selectedLocation.isEmpty() || product.location.equals(selectedLocation, ignoreCase = true)
        val matchesPrice = maxPrice.isEmpty() || product.price <= maxPrice.toIntOrNull() ?: Int.MAX_VALUE

        matchesQuery && matchesLocation && matchesPrice
    }

    // Scaffold with top bar and product listing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Products") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search input field
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search by name, location, or supermarket") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display filtered products
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

        // Filter dialog for location and max price
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
