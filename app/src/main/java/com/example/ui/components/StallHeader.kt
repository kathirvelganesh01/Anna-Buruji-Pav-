package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StallTimingStatus
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.MintGreen
import com.example.ui.theme.StallBlack
import com.example.ui.theme.StreetGold
import com.example.ui.theme.StreetGoldLight
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun StallHeader(
    timingStatus: StallTimingStatus,
    activeOrderCount: Int,
    onOpenManager: () -> Unit,
    onOpenActiveOrders: () -> Unit,
    onOpenStallQrDialog: () -> Unit,
    onCallStall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1711),
                        StallBlack
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Nav Icons & Sacred Om Sakthi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR Menu Dialog trigger
            IconButton(
                onClick = onOpenStallQrDialog,
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFF241F1A), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Show Stall QR Code",
                    tint = StreetGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Sacred Invocation from poster
            Text(
                text = "॥ Om Sakthi ॥",
                color = Color(0xFFFF5252),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )

            // Right icons: Active Orders Badge + Manager Access
            Row(verticalAlignment = Alignment.CenterVertically) {
                BadgedBox(
                    badge = {
                        if (activeOrderCount > 0) {
                            Badge(
                                containerColor = ChiliRed,
                                contentColor = TextWhite
                            ) {
                                Text("$activeOrderCount")
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onOpenActiveOrders,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF241F1A), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Active Orders",
                            tint = StreetGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onOpenManager,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF241F1A), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Manager Dashboard",
                        tint = StreetGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // "Fresh Tasty Hygienic" circular tag + Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, StreetGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "★ FRESH • TASTY • HYGIENIC ★",
                    color = StreetGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Poster Title: "Anna" (brush script style) + "Buruji Pav"
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Anna",
                color = TextWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp
            )
            Text(
                text = "Buruji Pav",
                color = StreetGold,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
        }

        // "Shell colony" crimson brush oval pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(ChiliRed)
                .padding(horizontal = 14.dp, vertical = 3.dp)
        ) {
            Text(
                text = "• Shell colony •",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // "Simple Food, Great Taste" tagline with chef hat emoji
        Text(
            text = "👨‍🍳 Simple Food • Great Taste ❤️",
            color = TextCream,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Live Operating Hours Banner (8:30 PM to 12:30 AM)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (timingStatus.isOpen) Color(0xFF132314) else Color(0xFF261D13))
                .border(
                    1.dp,
                    if (timingStatus.isOpen) MintGreen.copy(alpha = 0.5f) else StreetGold.copy(alpha = 0.4f),
                    RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing live indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(if (timingStatus.isOpen) pulseAlpha else 1f)
                    .background(if (timingStatus.isOpen) MintGreen else StreetGold, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = timingStatus.statusText,
                    color = if (timingStatus.isOpen) MintGreen else StreetGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = timingStatus.timeSubtitle,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Call Anna quick action
            IconButton(
                onClick = onCallStall,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Stall for Orders",
                    tint = StreetGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
