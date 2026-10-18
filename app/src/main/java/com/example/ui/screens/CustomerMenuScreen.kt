package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StallTimingStatus
import com.example.data.model.CartItem
import com.example.data.model.ItemCategory
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.model.OrderType
import com.example.data.model.PaymentStatus
import com.example.data.model.SpiceLevel
import com.example.ui.components.CartBottomBar
import com.example.ui.components.CartBottomSheet
import com.example.ui.components.CategoryTabs
import com.example.ui.components.MenuItemCard
import com.example.ui.components.StallHeader
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.StallBlack
import com.example.ui.theme.StallCardBg
import com.example.ui.theme.StallCardBorder
import com.example.ui.theme.StreetGold
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMenuScreen(
    menuItems: List<MenuItem>,
    cartItems: List<CartItem>,
    timingStatus: StallTimingStatus,
    activeOrders: List<Order>,
    onAddToCart: (MenuItem) -> Unit,
    onRemoveFromCart: (MenuItem) -> Unit,
    onUpdateSpiceLevel: (MenuItem, SpiceLevel) -> Unit,
    onToggleButter: (MenuItem) -> Unit,
    onAddExtraPav: () -> Unit,
    onPlaceOrder: (name: String, phone: String, orderType: OrderType, paymentStatus: PaymentStatus) -> Unit,
    onOpenManager: () -> Unit,
    onSelectOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf(ItemCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isCartOpen by remember { mutableStateOf(false) }
    var isQrDialogOpen by remember { mutableStateOf(false) }
    var isActiveOrdersDialogOpen by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Filter items based on category and search query
    val filteredItems by remember(menuItems, selectedCategory, searchQuery) {
        derivedStateOf {
            menuItems.filter { item ->
                val matchesCategory = (selectedCategory == ItemCategory.ALL) || (item.category == selectedCategory)
                val matchesQuery = searchQuery.isBlank() ||
                        item.name.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }
    }

    // Map of quantities in cart for instant O(1) lookup
    val cartQuantities by remember(cartItems) {
        derivedStateOf {
            cartItems.associate { it.menuItem.id to it.quantity }
        }
    }

    Scaffold(
        bottomBar = {
            CartBottomBar(
                cartItems = cartItems,
                onOpenCart = { isCartOpen = true }
            )
        },
        containerColor = StallBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header with branding, timing, and manager/order action buttons
            item {
                StallHeader(
                    timingStatus = timingStatus,
                    activeOrderCount = activeOrders.size,
                    onOpenManager = onOpenManager,
                    onOpenActiveOrders = { isActiveOrdersDialogOpen = true },
                    onOpenStallQrDialog = { isQrDialogOpen = true },
                    onCallStall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9892000648"))
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Bhurji, Omlette, Pav...", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = StreetGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("menu_search_bar"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreetGold,
                            unfocusedBorderColor = StallCardBorder,
                            focusedContainerColor = StallCardBg,
                            unfocusedContainerColor = StallCardBg,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextCream
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Category Tabs Filter
                CategoryTabs(
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it }
                )

                // "MENU" Vintage Banner matching poster
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StreetGold)
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "— MENU —",
                            color = Color(0xFF141210),
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // Menu Items List
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items found matching \"$searchQuery\"",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        MenuItemCard(
                            item = item,
                            currentQuantityInCart = cartQuantities[item.id] ?: 0,
                            onAddToCart = onAddToCart,
                            onRemoveFromCart = onRemoveFromCart,
                            onConfigureItem = { /* Handled directly in cart */ }
                        )
                    }
                }
            }

            // Bottom Poster Ribbon: "Khao, Khilao, Khush Raho!"
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ChiliRed)
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Khao, Khilao, Khush Raho! ❤️",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Shell Colony, Chembur • Open 8:30 PM - 12:30 AM",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Orders: 9892000648 / 9869105048",
                        color = StreetGold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(70.dp)) // Extra space for floating cart
                }
            }
        }
    }

    // Cart Bottom Sheet
    if (isCartOpen) {
        CartBottomSheet(
            sheetState = sheetState,
            cartItems = cartItems,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    isCartOpen = false
                }
            },
            onIncreaseQuantity = onAddToCart,
            onDecreaseQuantity = onRemoveFromCart,
            onUpdateSpiceLevel = onUpdateSpiceLevel,
            onToggleButter = onToggleButter,
            onAddExtraPav = onAddExtraPav,
            onPlaceOrder = { name, phone, orderType, paymentStatus ->
                coroutineScope.launch {
                    sheetState.hide()
                    isCartOpen = false
                    onPlaceOrder(name, phone, orderType, paymentStatus)
                }
            }
        )
    }

    // QR Display Dialog
    if (isQrDialogOpen) {
        QrDisplayDialog(onDismiss = { isQrDialogOpen = false })
    }

    // Active Orders Dialog
    if (isActiveOrdersDialogOpen) {
        ActiveOrdersDialog(
            orders = activeOrders,
            onSelectOrder = onSelectOrder,
            onDismiss = { isActiveOrdersDialogOpen = false }
        )
    }
}
