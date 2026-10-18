package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.local.OrderEntity
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrderRepository(private val orderDao: OrderDao) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toDomain(moshi) }
        }
    }

    fun getActiveOrders(): Flow<List<Order>> {
        return orderDao.getActiveOrders().map { entities ->
            entities.map { it.toDomain(moshi) }
        }
    }

    fun getOrderById(orderId: String): Flow<Order?> {
        return orderDao.getOrderById(orderId).map { entity ->
            entity?.toDomain(moshi)
        }
    }

    suspend fun getOrderByIdOnce(orderId: String): Order? {
        return withContext(Dispatchers.IO) {
            orderDao.getOrderByIdOnce(orderId)?.toDomain(moshi)
        }
    }

    suspend fun placeOrder(order: Order): Order {
        return withContext(Dispatchers.IO) {
            val maxToken = orderDao.getMaxTokenNumber() ?: 0
            val nextToken = if (maxToken >= 99) 1 else maxToken + 1
            val assignedOrder = order.copy(tokenNumber = nextToken)

            val entity = OrderEntity.fromDomain(assignedOrder, moshi)
            orderDao.insertOrder(entity)
            assignedOrder
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        withContext(Dispatchers.IO) {
            orderDao.updateOrderStatus(orderId, status.name)
        }
    }

    suspend fun clearCompletedOrders() {
        withContext(Dispatchers.IO) {
            // Can be called by manager to reset shift
        }
    }
}
