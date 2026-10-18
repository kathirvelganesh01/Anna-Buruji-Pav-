package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.DigitalReceiptCard
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
fun OrderTrackingScreen(
    order: Order?,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedViewTab by remember { mutableIntStateOf(0) } // 0: Live Tawa Tracker, 1: Digital Receipt

    // Trigger vibration when order becomes READY
    LaunchedEffect(order?.status) {
        if (order?.status == OrderStatus.READY) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(500)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedViewTab == 0) "Live Order Tracker" else "Digital Tax Invoice / Receipt",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Order #${order?.orderId ?: "---"} • Anna Buruji Pav",
                            color = StreetGold,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToMenu) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Menu",
                            tint = StreetGold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { selectedViewTab = if (selectedViewTab == 0) 1 else 0 },
                        modifier = Modifier.testTag("toggle_receipt_view_button")
                    ) {
                        Icon(
                            imageVector = if (selectedViewTab == 0) Icons.Default.ReceiptLong else Icons.Default.OutdoorGrill,
                            contentDescription = "Toggle Receipt",
                            tint = StreetGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StallDarkSurface
                )
            )
        },
        containerColor = StallBlack
    ) { innerPadding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No active order found.", color = TextMuted)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // View Switcher Pill Tabs (Live Tracker vs Digital Receipt)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StallCardBg)
                        .border(1.dp, StallCardBorder, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selectedViewTab == 0) StreetGold else Color.Transparent)
                            .clickable { selectedViewTab = 0 }
                            .padding(vertical = 8.dp)
                            .testTag("tab_live_tracker"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.OutdoorGrill,
                                contentDescription = null,
                                tint = if (selectedViewTab == 0) TextDark else TextCream,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Tawa Status",
                                color = if (selectedViewTab == 0) TextDark else TextCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selectedViewTab == 1) StreetGold else Color.Transparent)
                            .clickable { selectedViewTab = 1 }
                            .padding(vertical = 8.dp)
                            .testTag("tab_digital_receipt"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = if (selectedViewTab == 1) TextDark else TextCream,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Digital Receipt 🧾",
                                color = if (selectedViewTab == 1) TextDark else TextCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            if (selectedViewTab == 1) {
                // ==================== TAB 1: VISUAL DIGITAL RECEIPT ====================
                item {
                    DigitalReceiptCard(order = order)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBackToMenu,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StreetGold, contentColor = TextDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Order More / Back to Menu", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            } else {
                // ==================== TAB 0: LIVE TRACKER VIEW ====================
                // Big Street Food Token Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (order.status == OrderStatus.READY) Color(0xFF142917) else Color(0xFF241D17)
                            )
                            .border(
                                2.dp,
                                if (order.status == OrderStatus.READY) MintGreen else StreetGold,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "YOUR TOKEN NUMBER",
                                color = if (order.status == OrderStatus.READY) MintGreen else TextCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "#${String.format(java.util.Locale.US, "%02d", order.tokenNumber)}",
                                color = if (order.status == OrderStatus.READY) MintGreen else StreetGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 64.sp,
                                lineHeight = 68.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Customer: ${order.customerName} • ${order.orderType.displayName}",
                                color = TextWhite,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )

                            if (order.status == OrderStatus.READY) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MintGreen)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = TextDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "TAKE TOKEN TO COUNTER NOW!",
                                            color = TextDark,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stepper Progression Bar
                    TrackingProgressCard(currentStatus = order.status)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Quick Action Banner to View Digital Receipt
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF241D17))
                            .border(1.dp, StreetGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { selectedViewTab = 1 }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(StreetGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "View Digital Receipt",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Tax invoice, UPI payment status & breakdown",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "View >",
                            color = StreetGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Order Summary Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(StallCardBg)
                            .border(1.dp, StallCardBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order Summary",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF261F18))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = order.paymentStatus.label,
                                    color = StreetGold,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${item.quantity}x ${item.menuItem.name}",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = item.spiceLevel.label,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "₹${item.totalPrice.toInt()}",
                                    color = TextCream,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (order.parcelFee > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Parcel Packaging", color = TextMuted, fontSize = 13.sp)
                                Text("₹${order.parcelFee.toInt()}", color = TextMuted, fontSize = 13.sp)
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = StallCardBorder
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Paid / Payable", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "₹${order.totalAmount.toInt()}",
                                color = StreetGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stall Contact Card from poster
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1B1612))
                            .border(1.dp, Color(0xFF332A22), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "For Orders & Bulk Enquiries",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📞 9892000648 / 9869105048",
                            color = StreetGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9892000648"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StreetGold),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call 9892000648", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9869105048"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StreetGold),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call 9869105048", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Return to Menu / Order More
                    Button(
                        onClick = onBackToMenu,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StreetGold,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Order More / Back to Menu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Poster slogan tagline
                    Text(
                        text = "Khao, Khilao, Khush Raho! ❤️",
                        color = ChiliRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun TrackingProgressCard(currentStatus: OrderStatus) {
    val steps = listOf(
        OrderStatus.RECEIVED to "Order Received",
        OrderStatus.ON_TAWA to "Sizzling on Tawa",
        OrderStatus.READY to "Ready for Pickup",
        OrderStatus.SERVED to "Order Served"
    )

    val currentStep = currentStatus.stepIndex

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StallCardBg)
            .border(1.dp, StallCardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Live Tawa Preparation Status",
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        steps.forEachIndexed { index, (status, label) ->
            val isDone = index <= currentStep
            val isCurrent = index == currentStep

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Indicator
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            when {
                                isCurrent -> StreetGold
                                isDone -> MintGreen
                                else -> Color(0xFF2B231D)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone && !isCurrent) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = if (isCurrent) TextDark else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = label,
                        color = when {
                            isCurrent -> StreetGold
                            isDone -> TextWhite
                            else -> TextMuted
                        },
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    Text(
                        text = status.subtitle,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 13.dp)
                        .width(2.dp)
                        .height(14.dp)
                        .background(if (index < currentStep) MintGreen else Color(0xFF2B231D))
                )
            }
        }
    }
}
