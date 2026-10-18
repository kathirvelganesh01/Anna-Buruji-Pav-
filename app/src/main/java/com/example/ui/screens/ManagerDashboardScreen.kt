package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemCategory
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.MintGreen
import com.example.ui.theme.StallBlack
import com.example.ui.theme.StallCardBg
import com.example.ui.theme.StallCardBorder
import com.example.ui.theme.StallDarkSurface
import com.example.ui.theme.StreetGold
import com.example.ui.theme.StreetGoldLight
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerDashboardScreen(
    menuItems: List<MenuItem>,
    activeOrders: List<Order>,
    allOrders: List<Order>,
    isManualOpenOverride: Boolean?,
    onToggleStock: (itemId: String, inStock: Boolean) -> Unit,
    onUpdatePrice: (itemId: String, newPrice: Double) -> Unit,
    onAddNewItem: (MenuItem) -> Unit,
    onResetDefaultMenu: () -> Unit,
    onUpdateOrderStatus: (orderId: String, status: OrderStatus) -> Unit,
    onSetStallOverride: (Boolean?) -> Unit,
    onCloseDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Stock & Menu, 1: Live Tawa Orders, 2: Stall Shift

    // Dialog state for editing price
    var itemToEditPrice by remember { mutableStateOf<MenuItem?>(null) }
    var newPriceText by remember { mutableStateOf("") }

    // Dialog state for adding custom item
    var showAddItemDialog by remember { mutableStateOf(false) }

    // PIN Authentication Gate (Default PIN 1234 for quick stall access)
    if (!isAuthenticated) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manager / Stall Staff Access", color = TextWhite, fontSize = 16.sp) },
                    navigationIcon = {
                        IconButton(onClick = onCloseDashboard) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StreetGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = StallDarkSurface)
                )
            },
            containerColor = StallBlack
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StallCardBg)
                        .border(1.dp, StreetGold, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(StreetGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TextDark, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Anna Buruji Pav Stall Dashboard",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Enter 4-digit Manager PIN (Default: 1234)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4) pinInput = it },
                        label = { Text("Manager PIN", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("manager_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreetGold,
                            unfocusedBorderColor = StallCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextCream
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (pinInput == "1234" || pinInput == "0000") {
                                isAuthenticated = true
                            } else {
                                Toast.makeText(context, "Incorrect PIN! Enter 1234", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("manager_login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = StreetGold, contentColor = TextDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Unlock Dashboard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Manager Control Room", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Dynamic Stock • Live Orders • Peak Operations", color = StreetGold, fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCloseDashboard) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Dashboard", tint = StreetGold)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddItemDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", tint = StreetGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StallDarkSurface)
            )
        },
        containerColor = StallBlack
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Dashboard Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StallDarkSurface,
                contentColor = StreetGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = StreetGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Menu & Stock (${menuItems.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) StreetGold else TextCream,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Live Tawa (${activeOrders.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) StreetGold else TextCream,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Shift & Timing",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) StreetGold else TextCream,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: Menu Stock and Pricing Management
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        item {
                            // Quick Action Banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF211B15))
                                    .border(1.dp, StallCardBorder, RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Peak Rush Quick Stock Control",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Toggle items ON/OFF instantly when eggs or pav run out",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                OutlinedButton(
                                    onClick = onResetDefaultMenu,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StreetGold),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(menuItems, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = StallCardBg),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (item.inStock) StallCardBorder else ChiliRed.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (item.isSpecial) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(ChiliRed)
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("SPL", color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = item.name,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Clickable price badge to edit price
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF2A231C))
                                                    .border(1.dp, StreetGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        itemToEditPrice = item
                                                        newPriceText = item.price.toInt().toString()
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "₹${item.price.toInt()}",
                                                        color = StreetGold,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit Price",
                                                        tint = StreetGold,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = if (item.inStock) "● In Stock" else "✕ SOLD OUT",
                                                color = if (item.inStock) MintGreen else ChiliRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // Switch for instantaneous in-stock / sold out toggle
                                    Switch(
                                        checked = item.inStock,
                                        onCheckedChange = { inStock ->
                                            onToggleStock(item.id, inStock)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = StreetGold,
                                            checkedTrackColor = Color(0xFF4A3E33),
                                            uncheckedThumbColor = ChiliRed,
                                            uncheckedTrackColor = Color(0xFF2D1815)
                                        ),
                                        modifier = Modifier.testTag("stock_switch_${item.id}")
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                1 -> {
                    // TAB 1: Live Tawa Orders (KDS Kitchen Display)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        if (activeOrders.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.OutdoorGrill,
                                            contentDescription = null,
                                            tint = StreetGold,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No pending orders on Tawa!",
                                            color = TextCream,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "New QR orders will appear here in real time.",
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            items(activeOrders, key = { it.orderId }) { order ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = StallCardBg),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        when (order.status) {
                                            OrderStatus.READY -> MintGreen
                                            OrderStatus.ON_TAWA -> StreetGold
                                            else -> StallCardBorder
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        // Top Row: Token # & Order Type
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(StreetGold)
                                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "TOKEN #${String.format(java.util.Locale.US, "%02d", order.tokenNumber)}",
                                                        color = TextDark,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = order.customerName,
                                                    color = TextWhite,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            Text(
                                                text = order.orderType.displayName,
                                                color = StreetGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Itemized List
                                        order.items.forEach { item ->
                                            Text(
                                                text = "• ${item.quantity}x ${item.menuItem.name} (${item.spiceLevel.label}${if (item.extraButter) " +Butter" else ""})",
                                                color = TextCream,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Total: ₹${order.totalAmount.toInt()} (${order.paymentStatus.label})",
                                                color = TextMuted,
                                                fontSize = 12.sp
                                            )

                                            // Status advancement action buttons
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                when (order.status) {
                                                    OrderStatus.RECEIVED -> {
                                                        Button(
                                                            onClick = { onUpdateOrderStatus(order.orderId, OrderStatus.ON_TAWA) },
                                                            colors = ButtonDefaults.buttonColors(containerColor = StreetGold, contentColor = TextDark),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.height(34.dp)
                                                        ) {
                                                            Text("🍳 Cook on Tawa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    OrderStatus.ON_TAWA -> {
                                                        Button(
                                                            onClick = { onUpdateOrderStatus(order.orderId, OrderStatus.READY) },
                                                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen, contentColor = TextWhite),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.height(34.dp)
                                                        ) {
                                                            Text("🔔 Call Token (Ready)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    OrderStatus.READY -> {
                                                        Button(
                                                            onClick = { onUpdateOrderStatus(order.orderId, OrderStatus.SERVED) },
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C), contentColor = TextWhite),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.height(34.dp)
                                                        ) {
                                                            Text("✅ Mark Served", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    else -> {}
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Shift, Shop Timing Override & Metrics
                    val totalRevenue = allOrders.filter { it.status == OrderStatus.SERVED || it.status == OrderStatus.READY }.sumOf { it.totalAmount }
                    val totalOrders = allOrders.size

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Metrics Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StallCardBg),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StallCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Tonight's Stall Performance", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Orders", color = TextMuted, fontSize = 12.sp)
                                        Text("$totalOrders", color = StreetGold, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                    }
                                    Column {
                                        Text("Total Sales", color = TextMuted, fontSize = 12.sp)
                                        Text("₹${totalRevenue.toInt()}", color = MintGreen, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                    }
                                    Column {
                                        Text("Pending on Tawa", color = TextMuted, fontSize = 12.sp)
                                        Text("${activeOrders.size}", color = ChiliRed, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stall Hours Override Controls
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StallCardBg),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StallCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Operating Hours Setting", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Standard Hours: 8:30 PM to 12:30 AM", color = StreetGold, fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onSetStallOverride(null) },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isManualOpenOverride == null) StreetGold else Color.Transparent,
                                            contentColor = if (isManualOpenOverride == null) TextDark else TextCream
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Auto (8:30-12:30)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onSetStallOverride(true) },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isManualOpenOverride == true) MintGreen else Color.Transparent,
                                            contentColor = if (isManualOpenOverride == true) TextDark else TextCream
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Force Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onSetStallOverride(false) },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isManualOpenOverride == false) ChiliRed else Color.Transparent,
                                            contentColor = if (isManualOpenOverride == false) TextWhite else TextCream
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Force Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for Editing Price
    itemToEditPrice?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToEditPrice = null },
            containerColor = StallCardBg,
            title = { Text("Update Price: ${item.name}", color = TextWhite, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Enter new price in INR (₹):", color = TextCream, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPriceText,
                        onValueChange = { newPriceText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreetGold,
                            unfocusedBorderColor = StallCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextCream
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newPriceText.toDoubleOrNull()
                        if (parsed != null && parsed > 0) {
                            onUpdatePrice(item.id, parsed)
                            itemToEditPrice = null
                        } else {
                            Toast.makeText(context, "Enter a valid price", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StreetGold, contentColor = TextDark)
                ) {
                    Text("Save Price")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToEditPrice = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Dialog for Adding a New Item
    if (showAddItemDialog) {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(ItemCategory.BURUJI_COMBOS) }

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            containerColor = StallCardBg,
            title = { Text("Add Item to Anna's Menu", color = TextWhite, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Item Name", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StreetGold, focusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price (₹)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StreetGold, focusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StreetGold, focusedTextColor = TextWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pr = price.toDoubleOrNull()
                        if (name.isNotBlank() && pr != null) {
                            val newItem = MenuItem(
                                id = "custom_${System.currentTimeMillis()}",
                                name = name,
                                price = pr,
                                category = category,
                                description = description.ifBlank { "Fresh street-style delicacy on hot tawa." },
                                inStock = true
                            )
                            onAddNewItem(newItem)
                            showAddItemDialog = false
                        } else {
                            Toast.makeText(context, "Please enter valid item name and price", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StreetGold, contentColor = TextDark)
                ) {
                    Text("Add Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
