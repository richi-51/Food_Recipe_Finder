package com.example.quotes_app.presentation.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quotes_app.domain.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    recipe: Recipe,
    viewModel: OrderViewModel,
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val orderState by viewModel.orderState.collectAsState()
    var address by remember { mutableStateOf("") }
    var selectedShipping by remember { mutableStateOf("Standard") }
    var selectedPayment by remember { mutableStateOf("Credit Card") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val basePrice = 10.0 + (recipe.strMeal.length % 20)
    val shippingFee = if (selectedShipping == "Standard") 2.0 else 5.0
    val tax = basePrice * 0.10
    val total = basePrice + shippingFee + tax

    LaunchedEffect(orderState) {
        if (orderState is OrderState.Success) {
            showSuccessDialog = true
            viewModel.resetState()
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Order Confirmed!", fontWeight = FontWeight.Bold) },
            text = { Text("Your order for ${recipe.strMeal} has been placed successfully. It will be delivered to $address.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onCheckoutSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E2C), 
                    titleContentColor = Color.White, 
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Delivery Address
            Text(text = "Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = { Text("Enter your full delivery address...") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Order Item
            Text(text = "Order Item", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = recipe.strMealThumb,
                        contentDescription = "Recipe Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = recipe.strMeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = recipe.strCategory ?: "Unknown Category", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "$${String.format("%.2f", basePrice)}", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800), fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Shipping Method
            Text(text = "Shipping Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val shippingOptions = listOf("Standard" to 2.0, "Express" to 5.0)
            shippingOptions.forEach { (option, price) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedShipping = option }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedShipping == option,
                        onClick = { selectedShipping = option },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF9800))
                    )
                    Text(text = option, modifier = Modifier.weight(1f), fontSize = 16.sp)
                    Text(text = "+$${String.format("%.2f", price)}", fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Payment Method
            Text(text = "Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val paymentOptions = listOf("Credit Card", "PayPal", "Cash on Delivery")
            paymentOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedPayment = option }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPayment == option,
                        onClick = { selectedPayment = option },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF9800))
                    )
                    Text(text = option, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Calculation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Subtotal", color = Color.Gray)
                        Text(text = "$${String.format("%.2f", basePrice)}", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Tax (10%)", color = Color.Gray)
                        Text(text = "$${String.format("%.2f", tax)}", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Delivery Fee", color = Color.Gray)
                        Text(text = "$${String.format("%.2f", shippingFee)}", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total Payment", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "$${String.format("%.2f", total)}", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800), fontSize = 18.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            if (orderState is OrderState.Error) {
                Text(
                    text = (orderState as OrderState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (orderState is OrderState.Loading) {
                CircularProgressIndicator(
                    color = Color(0xFFFF9800),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Button(
                    onClick = { viewModel.placeOrder(recipe, address) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    enabled = address.isNotBlank()
                ) {
                    Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
