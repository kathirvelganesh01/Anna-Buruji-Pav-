package com.example.data.model

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int = 1,
    val spiceLevel: SpiceLevel = SpiceLevel.MEDIUM,
    val extraButter: Boolean = false,
    val note: String = ""
) {
    val unitPrice: Double
        get() = menuItem.price

    val totalPrice: Double
        get() = unitPrice * quantity
}

enum class SpiceLevel(val label: String, val emoji: String) {
    MILD("Mild / Normal", "🌶️"),
    MEDIUM("Medium Spicy", "🌶️🌶️"),
    EXTRA_TEEKHA("Street Teekha 🔥", "🌶️🌶️🌶️")
}

enum class OrderType(val displayName: String) {
    DINE_IN("Standing / Dine In"),
    TAKEAWAY("Takeaway Parcel (+₹5)")
}

enum class PaymentStatus(val label: String) {
    PAID_UPI("Paid via UPI"),
    CASH_ON_DELIVERY("Pay Cash at Counter"),
    VERIFYING("Verifying Payment")
}

enum class OrderStatus(val title: String, val subtitle: String, val stepIndex: Int) {
    RECEIVED("Order Received", "Token registered at counter", 0),
    ON_TAWA("Sizzling on Tawa", "Chef Anna is cooking your order", 1),
    READY("Ready for Pickup!", "Please collect at counter with Token", 2),
    SERVED("Served & Enjoyed", "Khush Raho! Thank you for visiting", 3),
    CANCELLED("Cancelled", "Order was cancelled", -1)
}

data class Order(
    val orderId: String,
    val tokenNumber: Int,
    val customerName: String,
    val customerPhone: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val parcelFee: Double,
    val totalAmount: Double,
    val orderType: OrderType,
    val paymentStatus: PaymentStatus,
    val upiTransactionId: String = "",
    val status: OrderStatus = OrderStatus.RECEIVED,
    val timestamp: Long = System.currentTimeMillis(),
    val estimatedMinutes: Int = 8
)
