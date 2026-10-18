package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val tokenNumber: Int,
    val customerName: String,
    val customerPhone: String,
    val itemsJson: String,
    val subtotal: Double,
    val parcelFee: Double,
    val totalAmount: Double,
    val orderType: String,
    val paymentStatus: String,
    val upiTransactionId: String,
    val status: String,
    val timestamp: Long,
    val estimatedMinutes: Int
) {
    fun toDomain(moshi: Moshi): Order {
        val type = Types.newParameterizedType(List::class.java, CartItem::class.java)
        val adapter = moshi.adapter<List<CartItem>>(type)
        val parsedItems: List<CartItem> = try {
            adapter.fromJson(itemsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return Order(
            orderId = orderId,
            tokenNumber = tokenNumber,
            customerName = customerName,
            customerPhone = customerPhone,
            items = parsedItems,
            subtotal = subtotal,
            parcelFee = parcelFee,
            totalAmount = totalAmount,
            orderType = try { OrderType.valueOf(orderType) } catch (e: Exception) { OrderType.DINE_IN },
            paymentStatus = try { PaymentStatus.valueOf(paymentStatus) } catch (e: Exception) { PaymentStatus.PAID_UPI },
            upiTransactionId = upiTransactionId,
            status = try { OrderStatus.valueOf(status) } catch (e: Exception) { OrderStatus.RECEIVED },
            timestamp = timestamp,
            estimatedMinutes = estimatedMinutes
        )
    }

    companion object {
        fun fromDomain(order: Order, moshi: Moshi): OrderEntity {
            val type = Types.newParameterizedType(List::class.java, CartItem::class.java)
            val adapter = moshi.adapter<List<CartItem>>(type)
            val json = adapter.toJson(order.items)

            return OrderEntity(
                orderId = order.orderId,
                tokenNumber = order.tokenNumber,
                customerName = order.customerName,
                customerPhone = order.customerPhone,
                itemsJson = json,
                subtotal = order.subtotal,
                parcelFee = order.parcelFee,
                totalAmount = order.totalAmount,
                orderType = order.orderType.name,
                paymentStatus = order.paymentStatus.name,
                upiTransactionId = order.upiTransactionId,
                status = order.status.name,
                timestamp = order.timestamp,
                estimatedMinutes = order.estimatedMinutes
            )
        }
    }
}
