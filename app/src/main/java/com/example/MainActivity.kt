package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CustomerMenuScreen
import com.example.ui.screens.ManagerDashboardScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnnaBhurjiPavApp()
            }
        }
    }
}

@Composable
fun AnnaBhurjiPavApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val activeOrders by viewModel.activeOrders.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val stallStatus by viewModel.stallTimingStatus.collectAsStateWithLifecycle()
    val manualOverride by viewModel.manualOpenOverride.collectAsStateWithLifecycle()
    val trackedOrderId by viewModel.trackedOrderId.collectAsStateWithLifecycle()

    when (val screen = currentScreen) {
        is AppScreen.Menu -> {
            CustomerMenuScreen(
                menuItems = menuItems,
                cartItems = cartItems,
                timingStatus = stallStatus,
                activeOrders = activeOrders,
                onAddToCart = { viewModel.addToCart(it) },
                onRemoveFromCart = { viewModel.removeFromCart(it) },
                onUpdateSpiceLevel = { item, level -> viewModel.updateSpiceLevel(item, level) },
                onToggleButter = { viewModel.toggleButter(it) },
                onAddExtraPav = { viewModel.addExtraPav() },
                onPlaceOrder = { name, phone, orderType, paymentStatus ->
                    viewModel.placeOrder(name, phone, orderType, paymentStatus)
                },
                onOpenManager = { viewModel.navigateTo(AppScreen.ManagerDashboard) },
                onSelectOrder = { order ->
                    viewModel.navigateTo(AppScreen.OrderTracking(order.orderId))
                }
            )
        }

        is AppScreen.OrderTracking -> {
            BackHandler {
                viewModel.navigateBack()
            }

            val currentOrder = allOrders.find { it.orderId == (screen.orderId ?: trackedOrderId) }
                ?: activeOrders.firstOrNull()

            OrderTrackingScreen(
                order = currentOrder,
                onBackToMenu = { viewModel.navigateBack() }
            )
        }

        is AppScreen.ManagerDashboard -> {
            BackHandler {
                viewModel.navigateBack()
            }

            ManagerDashboardScreen(
                menuItems = menuItems,
                activeOrders = activeOrders,
                allOrders = allOrders,
                isManualOpenOverride = manualOverride,
                onToggleStock = { itemId, inStock -> viewModel.toggleStock(itemId, inStock) },
                onUpdatePrice = { itemId, price -> viewModel.updatePrice(itemId, price) },
                onAddNewItem = { item -> viewModel.addNewItem(item) },
                onResetDefaultMenu = { viewModel.resetDefaultMenu() },
                onUpdateOrderStatus = { orderId, status -> viewModel.updateOrderStatus(orderId, status) },
                onSetStallOverride = { override -> viewModel.setStallOverride(override) },
                onCloseDashboard = { viewModel.navigateBack() }
            )
        }
    }
}
