package com.leone.okoleo.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.leo.okoaleo.ui.theme.OkoaLeoTheme

data class Product(
    val name: String,
    val barcode: String,
    val price: Double
)

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductScanScreen(
    onBack: () -> Unit,
    navController: NavHostController
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var scanError by remember { mutableStateOf<String?>(null) }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val barcodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedProduct = mockProductLookup(result.contents)
            product = scannedProduct
            scanError = null
        } else {
            scanError = "Scan cancelled or failed."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Product") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                !cameraPermissionState.status.isGranted -> {
                    Text(
                        text = "Camera permission is required to scan barcodes.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }

                product == null -> {
                    Text("Product Scanner", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val options = ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                            setPrompt("Scan a barcode")
                            setCameraId(0)
                            setBeepEnabled(true)
                            setBarcodeImageEnabled(false)
                        }
                        barcodeLauncher.launch(options)
                    }) {
                        Text("Start Scanning")
                    }
                    scanError?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Text("Product Found", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Name: ${product!!.name}", style = MaterialTheme.typography.bodyLarge)
                    Text("Barcode: ${product!!.barcode}", style = MaterialTheme.typography.bodyLarge)
                    Text("Price: $${"%.2f".format(product!!.price)}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { product = null }) {
                        Text("Scan Another Product")
                    }
                }
            }
        }
    }
}

private fun mockProductLookup(barcode: String): Product {
    return Product(
        name = "Sample Product",
        barcode = barcode,
        price = 19.99
    )
}

@Preview(showBackground = true)
@Composable
fun ProductScanScreenPreview() {
    OkoaLeoTheme {
        val navController = rememberNavController()
        ProductScanScreen(
            onBack = {},
            navController = navController
        )
    }
}
