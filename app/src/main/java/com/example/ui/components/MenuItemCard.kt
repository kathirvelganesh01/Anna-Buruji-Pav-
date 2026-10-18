package com.example.ui.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuItem
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.PriceBadgeBg
import com.example.ui.theme.SplBadgeBg
import com.example.ui.theme.StallCardBg
import com.example.ui.theme.StallCardBorder
import com.example.ui.theme.StreetGold
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun MenuItemCard(
    item: MenuItem,
    currentQuantityInCart: Int,
    onAddToCart: (MenuItem) -> Unit,
    onRemoveFromCart: (MenuItem) -> Unit,
    onConfigureItem: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = item.inStock

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StallCardBg)
            .border(
                1.dp,
                if (item.isSpecial) ChiliRed.copy(alpha = 0.8f) else StallCardBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
            .alpha(if (isAvailable) 1f else 0.55f)
            .testTag("menu_item_${item.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row with Item Name, Dots, and Golden Price Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Special Badge if applicable
                if (item.isSpecial) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SplBadgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SPL",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Item Name
                Text(
                    text = item.name,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Dotted leader line
                Text(
                    text = " ..........................",
                    color = Color(0xFF4A3E33),
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Golden Price Badge matching the poster
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PriceBadgeBg)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "₹${item.price.toInt()}",
                        color = TextDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = item.description,
                color = TextCream,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badges row (Egg Count, Pav Count, Preparation time) & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.eggCount > 0) {
                        Text(
                            text = "🥚 ${item.eggCount} Egg${if (item.eggCount > 1) "s" else ""}",
                            color = StreetGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (item.pavCount > 0) {
                        Text(
                            text = "🍞 ${item.pavCount} Pav",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "⏱️ ~${item.prepTimeMinutes}m",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Action Area: If sold out, show SOLD OUT. Else Stepper or Add Button.
                if (!isAvailable) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF331C1A))
                            .border(1.dp, ChiliRed, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = null,
                                tint = ChiliRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SOLD OUT",
                                color = ChiliRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else if (currentQuantityInCart > 0) {
                    // Stepper: [-] [qty] [+]
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2A231C))
                            .border(1.dp, StreetGold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onRemoveFromCart(item) },
                            modifier = Modifier.size(28.dp).testTag("decrease_${item.id}")
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease Quantity",
                                tint = StreetGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = "$currentQuantityInCart",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        IconButton(
                            onClick = { onAddToCart(item) },
                            modifier = Modifier.size(28.dp).testTag("increase_${item.id}")
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase Quantity",
                                tint = StreetGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    // Fast "+ ADD" Button
                    Button(
                        onClick = { onAddToCart(item) },
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("add_button_${item.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StreetGold,
                            contentColor = TextDark
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 12.dp,
                            vertical = 0.dp
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "+ ADD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
