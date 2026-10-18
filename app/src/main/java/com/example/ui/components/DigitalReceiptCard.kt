package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderType
import com.example.data.model.PaymentStatus
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.MintGreen
import com.example.ui.theme.StallBlack
import com.example.ui.theme.StreetGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DigitalReceiptCard(
    order: Order,
    onShareReceipt: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }

    val upiTxnRef = remember(order.orderId, order.upiTransactionId) {
        if (order.upiTransactionId.isNotBlank()) {
            order.upiTransactionId
        } else {
            "UPI/OKHDFC/${order.orderId.replace("-", "")}${order.timestamp % 100000}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFDF9)) // Authentic parchment paper
            .padding(20.dp)
            .testTag("digital_receipt_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Sacred Invocation & Stall Logo
        Text(
            text = "॥ Om Sakthi ॥",
            color = ChiliRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "ANNA BURUJI PAV",
            color = Color(0xFF1E1610),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Text(
            text = "Shell Colony, Chembur, Mumbai • 8:30 PM - 12:30 AM",
            color = Color(0xFF6B5E52),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ph: 9892000648 / 9869105048",
            color = Color(0xFF6B5E52),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Perforated Tear Line with side notches
        PerforatedDottedLine()

        Spacer(modifier = Modifier.height(12.dp))

        // Big Token Stamp & Order Meta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RECEIPT #${order.orderId}",
                    color = Color(0xFF1E1610),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = formattedDate,
                    color = Color(0xFF7A6D60),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Cust: ${order.customerName} (${order.orderType.displayName})",
                    color = Color(0xFF1E1610),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Bold Token Stamp
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1610))
                    .border(1.5.dp, StreetGold, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOKEN",
                        color = StreetGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "#${String.format(Locale.US, "%02d", order.tokenNumber)}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Itemized Order Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2ECE1), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ITEM", color = Color(0xFF4A3E33), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("QTY", color = Color(0xFF4A3E33), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("RATE", color = Color(0xFF4A3E33), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("TOTAL", color = Color(0xFF4A3E33), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Item Rows
        order.items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.menuItem.name,
                        color = Color(0xFF1E1610),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.menuItem.spicyLevelAvailable) {
                        Text(
                            text = item.spiceLevel.label,
                            color = Color(0xFF8C7D6F),
                            fontSize = 10.sp
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.quantity}",
                        color = Color(0xFF1E1610),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "₹${item.unitPrice.toInt()}",
                        color = Color(0xFF5A4E42),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "₹${item.totalPrice.toInt()}",
                        color = Color(0xFF1E1610),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(42.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        if (order.parcelFee > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Parcel Packaging",
                    color = Color(0xFF5A4E42),
                    fontSize = 12.sp
                )
                Text(
                    text = "₹${order.parcelFee.toInt()}",
                    color = Color(0xFF1E1610),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        PerforatedDottedLine()
        Spacer(modifier = Modifier.height(10.dp))

        // Subtotal and Grand Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GRAND TOTAL",
                color = Color(0xFF1E1610),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "₹${order.totalAmount.toInt()}",
                color = Color(0xFF1E1610),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // UPI Payment Status Verification Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (order.paymentStatus == PaymentStatus.PAID_UPI) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                )
                .border(
                    1.dp,
                    if (order.paymentStatus == PaymentStatus.PAID_UPI) MintGreen else StreetGold,
                    RoundedCornerShape(10.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (order.paymentStatus == PaymentStatus.PAID_UPI) Icons.Default.CheckCircle else Icons.Default.Payment,
                            contentDescription = null,
                            tint = if (order.paymentStatus == PaymentStatus.PAID_UPI) Color(0xFF2E7D32) else Color(0xFFE65100),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (order.paymentStatus == PaymentStatus.PAID_UPI) "PAYMENT SUCCESSFUL" else "PAYMENT AT COUNTER",
                            color = if (order.paymentStatus == PaymentStatus.PAID_UPI) Color(0xFF2E7D32) else Color(0xFFE65100),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "₹${order.totalAmount.toInt()}",
                        color = Color(0xFF1E1610),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "UPI Payee ID: $UPI_ID",
                    color = Color(0xFF2E5E35),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "UPI Ref ID: $upiTxnRef",
                    color = Color(0xFF556B59),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Barcode decorative representation
        ReceiptBarcode(text = order.orderId)

        Spacer(modifier = Modifier.height(10.dp))

        // Slogan & Counter instruction
        Text(
            text = "Khao, Khilao, Khush Raho! ❤️",
            color = ChiliRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Please show this digital token at the stall counter when called.",
            color = Color(0xFF7A6D60),
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Share & Copy Receipt Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val receiptSummary = buildString {
                        appendLine("🧾 *ANNA BURUJI PAV - DIGITAL RECEIPT*")
                        appendLine("॥ Om Sakthi ॥")
                        appendLine("Shell Colony, Chembur, Mumbai")
                        appendLine("--------------------------------")
                        appendLine("Token Number: *#${String.format(Locale.US, "%02d", order.tokenNumber)}*")
                        appendLine("Order ID: ${order.orderId}")
                        appendLine("Date: $formattedDate")
                        appendLine("Customer: ${order.customerName}")
                        appendLine("Type: ${order.orderType.displayName}")
                        appendLine("--------------------------------")
                        appendLine("ITEMS:")
                        order.items.forEach {
                            appendLine("• ${it.quantity}x ${it.menuItem.name} = ₹${it.totalPrice.toInt()}")
                        }
                        if (order.parcelFee > 0) {
                            appendLine("• Parcel Fee: ₹${order.parcelFee.toInt()}")
                        }
                        appendLine("--------------------------------")
                        appendLine("GRAND TOTAL: *₹${order.totalAmount.toInt()}*")
                        appendLine("Status: ${order.paymentStatus.label}")
                        if (order.paymentStatus == PaymentStatus.PAID_UPI) {
                            appendLine("UPI ID: $UPI_ID")
                            appendLine("UPI Ref: $upiTxnRef")
                        }
                        appendLine("--------------------------------")
                        appendLine("Khao, Khilao, Khush Raho! ❤️")
                        appendLine("Contact: 9892000648 / 9869105048")
                    }

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, receiptSummary)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Digital Receipt")
                    context.startActivity(shareIntent)
                    onShareReceipt()
                },
                modifier = Modifier.weight(1f).height(38.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E1610)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString("Receipt #${order.orderId} - Token #${order.tokenNumber} - ₹${order.totalAmount.toInt()} - $UPI_ID"))
                },
                modifier = Modifier.weight(1f).height(38.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E1610)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy ID", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PerforatedDottedLine(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        drawLine(
            color = Color(0xFFD4C8BA),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 2.dp.toPx(),
            pathEffect = pathEffect
        )
    }
}

@Composable
fun ReceiptBarcode(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .width(180.dp)
                .height(26.dp)
        ) {
            val hash = text.hashCode().toLong()
            val barCount = 38
            val barWidth = size.width / (barCount * 1.5f)

            for (i in 0 until barCount) {
                val isThick = ((hash shr (i % 30)) and 1L) == 1L
                val w = if (isThick) barWidth * 1.6f else barWidth * 0.8f
                val x = i * (size.width / barCount)
                drawRect(
                    color = Color(0xFF2A231C),
                    topLeft = Offset(x, 0f),
                    size = androidx.compose.ui.geometry.Size(w, size.height)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "* $text *",
            color = Color(0xFF7A6D60),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
    }
}
