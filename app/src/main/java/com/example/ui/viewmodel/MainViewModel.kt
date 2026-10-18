package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ShopTimingHelper
import com.example.data.StallTimingStatus
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentStatus
import com.example.data.model.SpiceLevel
import com.example.data.repository.MenuRepository
import com.example.data.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Menu : AppScreen()
    data class OrderTracking(val orderId: String) : AppScreen()
    object ManagerDashboard : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val menuRepository = MenuRepository(database.menuItemDao())
    private val orderRepository = OrderRepository(database.orderDao())

    val menuItems: StateFlow<List<MenuItem>> = menuRepository.getAllMenuItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOrders: StateFlow<List<Order>> = orderRepository.getActiveOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = orderRepository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Menu)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _manualOpenOverride = MutableStateFlow<Boolean?>(null)
    val manualOpenOverride: StateFlow<Boolean?> = _manualOpenOverride.asStateFlow()

    private val _stallTimingStatus = MutableStateFlow(ShopTimingHelper.calculateStallStatus())
    val stallTimingStatus: StateFlow<StallTimingStatus> = _stallTimingStatus.asStateFlow()

    private val _trackedOrderId = MutableStateFlow<String?>(null)
    val trackedOrderId: StateFlow<String?> = _trackedOrderId.asStateFlow()

    init {
        viewModelScope.launch {
            menuRepository.ensureDefaultMenuSeeded()
        }

        // Periodic stall timing refresh (every minute)
        viewModelScope.launch {
            while (true) {
                _stallTimingStatus.value = ShopTimingHelper.calculateStallStatus(_manualOpenOverride.value)
                delay(60_000)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (screen is AppScreen.OrderTracking) {
            _trackedOrderId.value = screen.orderId
        }
        _currentScreen.value = screen
    }

    fun navigateBack() {
        _currentScreen.value = AppScreen.Menu
    }

    // --- Cart Actions ---

    fun addToCart(item: MenuItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(menuItem = item, quantity = 1))
        }
        _cartItems.value = current
    }

    fun removeFromCart(item: MenuItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id }
        if (index >= 0) {
            val existing = current[index]
            if (existing.quantity > 1) {
                current[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun updateSpiceLevel(item: MenuItem, level: SpiceLevel) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id }
        if (index >= 0) {
            current[index] = current[index].copy(spiceLevel = level)
            _cartItems.value = current
        }
    }

    fun toggleButter(item: MenuItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id }
        if (index >= 0) {
            current[index] = current[index].copy(extraButter = !current[index].extraButter)
            _cartItems.value = current
        }
    }

    fun addExtraPav() {
        val pavItem = menuItems.value.find { it.id == "item_pav" }
        if (pavItem != null) {
            addToCart(pavItem)
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // --- Order Placement ---

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        orderType: OrderType,
        paymentStatus: PaymentStatus
    ) {
        viewModelScope.launch {
            val itemsToOrder = _cartItems.value
            if (itemsToOrder.isEmpty()) return@launch

            val subtotal = itemsToOrder.sumOf { it.totalPrice }
            val parcelFee = if (orderType == OrderType.TAKEAWAY) 5.0 else 0.0
            val totalAmount = subtotal + parcelFee

            val timestamp = System.currentTimeMillis()
            val orderId = "AB-${timestamp % 10000}"
            val upiTxnId = if (paymentStatus == PaymentStatus.PAID_UPI) {
                "UPI/OKHDFC/${orderId.replace("-", "")}${timestamp % 100000}"
            } else ""

            val newOrder = Order(
                orderId = orderId,
                tokenNumber = 1, // Will be incremented by repository
                customerName = customerName,
                customerPhone = customerPhone,
                items = itemsToOrder,
                subtotal = subtotal,
                parcelFee = parcelFee,
                totalAmount = totalAmount,
                orderType = orderType,
                paymentStatus = paymentStatus,
                upiTransactionId = upiTxnId,
                status = OrderStatus.RECEIVED,
                timestamp = timestamp
            )

            val created = orderRepository.placeOrder(newOrder)
            clearCart()
            _trackedOrderId.value = created.orderId
            _currentScreen.value = AppScreen.OrderTracking(created.orderId)

            // Street food kitchen simulation: If in demo mode, auto-advance orders over realistic intervals
            simulateKitchenOrderProgress(created.orderId)
        }
    }

    private fun simulateKitchenOrderProgress(orderId: String) {
        viewModelScope.launch {
            // After 12 seconds: Sizzling on Tawa
            delay(12_000)
            val order = orderRepository.getOrderByIdOnce(orderId)
            if (order != null && order.status == OrderStatus.RECEIVED) {
                orderRepository.updateOrderStatus(orderId, OrderStatus.ON_TAWA)
            }

            // After another 18 seconds: Ready for pickup!
            delay(18_000)
            val updated = orderRepository.getOrderByIdOnce(orderId)
            if (updated != null && updated.status == OrderStatus.ON_TAWA) {
                orderRepository.updateOrderStatus(orderId, OrderStatus.READY)
            }
        }
    }

    // --- Manager Operations ---

    fun toggleStock(itemId: String, inStock: Boolean) {
        viewModelScope.launch {
            menuRepository.updateStock(itemId, inStock)
        }
    }

    fun updatePrice(itemId: String, newPrice: Double) {
        viewModelScope.launch {
            menuRepository.updatePrice(itemId, newPrice)
        }
    }

    fun addNewItem(item: MenuItem) {
        viewModelScope.launch {
            menuRepository.addMenuItem(item)
        }
    }

    fun resetDefaultMenu() {
        viewModelScope.launch {
            menuRepository.resetToDefaultMenu()
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status)
        }
    }

    fun setStallOverride(override: Boolean?) {
        _manualOpenOverride.value = override
        _stallTimingStatus.value = ShopTimingHelper.calculateStallStatus(override)
    }
}
