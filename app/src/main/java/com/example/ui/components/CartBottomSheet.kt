package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.MenuItem
import com.example.data.model.OrderType
import com.example.data.model.PaymentStatus
import com.example.data.model.SpiceLevel
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.MintGreen
import com.example.ui.theme.StallBlack
import com.example.ui.theme.StallCardBg
import com.example.ui.theme.StallCardBorder
import com.example.ui.theme.StallDarkSurface
import com.example.ui.theme.StreetGold
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

const val UPI_ID = "kathirvelthevar-3@okhdfcbank"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    sheetState: SheetState,
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onIncreaseQuantity: (MenuItem) -> Unit,
    onDecreaseQuantity: (MenuItem) -> Unit,
    onUpdateSpiceLevel: (MenuItem, SpiceLevel) -> Unit,
    onToggleButter: (MenuItem) -> Unit,
    onAddExtraPav: () -> Unit,
    onPlaceOrder: (customerName: String, customerPhone: String, orderType: OrderType, paymentStatus: PaymentStatus) -> Unit
) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var orderType by remember { mutableStateOf(OrderType.DINE_IN) }
    var paymentMethod by remember { mutableStateOf(PaymentStatus.PAID_UPI) }
    var showUpiQr by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val parcelFee = if (orderType == OrderType.TAKEAWAY) 5.0 else 0.0
    val totalAmount = subtotal + parcelFee

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StallDarkSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF4A3E33), RoundedCornerShape(2.dp))
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Hot Tawa Order",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Anna Buruji Pav • Shell Colony",
                            color = StreetGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Cart",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Order Type Selector: Dine In vs Parcel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StallCardBg)
                        .border(1.dp, StallCardBorder, RoundedCornerShape(10.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (orderType == OrderType.DINE_IN) StreetGold else Color.Transparent)
                            .clickable { orderType = OrderType.DINE_IN }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🍽️ Dine In (Stall)",
                            color = if (orderType == OrderType.DINE_IN) TextDark else TextCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (orderType == OrderType.TAKEAWAY) StreetGold else Color.Transparent)
                            .clickable { orderType = OrderType.TAKEAWAY }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛍️ Parcel (+₹5)",
                            color = if (orderType == OrderType.TAKEAWAY) TextDark else TextCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Items List
            items(cartItems) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StallCardBg)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuItem.name,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "₹${item.unitPrice.toInt()} each",
                                color = StreetGold,
                                fontSize = 12.sp
                            )
                        }

                        // Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF261F18))
                                .border(1.dp, StreetGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        ) {
                            IconButton(
                                onClick = { onDecreaseQuantity(item.menuItem) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = StreetGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "${item.quantity}",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onIncreaseQuantity(item.menuItem) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = StreetGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Spice Selector if available
                    if (item.menuItem.spicyLevelAvailable) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SpiceLevel.values().forEach { level ->
                                val selected = item.spiceLevel == level
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) ChiliRed else Color(0xFF1E1712))
                                        .border(
                                            1.dp,
                                            if (selected) ChiliRed else StallCardBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onUpdateSpiceLevel(item.menuItem, level) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = level.label,
                                        color = if (selected) TextWhite else TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick add extra pav prompt
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF231C16))
                        .border(1.dp, StreetGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { onAddExtraPav() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🍞 Want extra pav? Add 1 Pav for ₹5",
                        color = StreetGold,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "+ ADD",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Customer Info inputs
                Text(
                    text = "Customer Details for Live Token",
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Your Name (e.g. Rahul)", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_customer_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StreetGold,
                        unfocusedBorderColor = StallCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextCream
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { if (it.length <= 10) customerPhone = it },
                    label = { Text("Mobile Number (Optional for SMS / Call)", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_customer_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StreetGold,
                        unfocusedBorderColor = StallCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextCream
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method Selector
                Text(
                    text = "Payment Option",
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // UPI Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (paymentMethod == PaymentStatus.PAID_UPI) Color(0xFF261F12) else StallCardBg)
                            .border(
                                1.5.dp,
                                if (paymentMethod == PaymentStatus.PAID_UPI) StreetGold else StallCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                paymentMethod = PaymentStatus.PAID_UPI
                                showUpiQr = true
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = StreetGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Instant UPI QR",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "GPay/PhonePe/Paytm",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Cash at counter option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (paymentMethod == PaymentStatus.CASH_ON_DELIVERY) Color(0xFF261F12) else StallCardBg)
                            .border(
                                1.5.dp,
                                if (paymentMethod == PaymentStatus.CASH_ON_DELIVERY) StreetGold else StallCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                paymentMethod = PaymentStatus.CASH_ON_DELIVERY
                                showUpiQr = false
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = null,
                                tint = StreetGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pay Cash",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Pay at Counter",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // If UPI chosen, show live UPI QR code box
                if (paymentMethod == PaymentStatus.PAID_UPI) {
                    Spacer(modifier = Modifier.height(14.dp))
                    UpiQrCard(
                        upiId = UPI_ID,
                        amount = totalAmount,
                        tokenNumber = 0,
                        onOpenUpiApp = {
                            val uri = Uri.parse("upi://pay?pa=$UPI_ID&pn=Anna%20Buruji%20Pav&am=${String.format(java.util.Locale.US, "%.2f", totalAmount)}&cu=INR&tn=Order%20Payment")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No UPI app installed. Please scan QR or copy UPI ID.", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1B1713))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = TextCream, fontSize = 13.sp)
                        Text("₹${subtotal.toInt()}", color = TextCream, fontSize = 13.sp)
                    }
                    if (orderType == OrderType.TAKEAWAY) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Parcel / Packaging Bag", color = TextCream, fontSize = 13.sp)
                            Text("₹5", color = StreetGold, fontSize = 13.sp)
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
                        Text("Grand Total", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "₹${totalAmount.toInt()}",
                            color = StreetGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Place Order Button
                Button(
                    onClick = {
                        val name = if (customerName.isBlank()) "Guest Foodie" else customerName
                        onPlaceOrder(name, customerPhone, orderType, paymentMethod)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_order_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreetGold,
                        contentColor = TextDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "PLACE ORDER • ₹${totalAmount.toInt()} ⚡",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
