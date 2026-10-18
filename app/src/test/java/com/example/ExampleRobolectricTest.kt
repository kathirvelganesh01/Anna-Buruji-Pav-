package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShopTimingHelper
import com.example.data.model.CartItem
import com.example.data.model.DefaultMenu
import com.example.data.model.ItemCategory
import com.example.data.model.SpiceLevel
import com.example.ui.components.QrMatrixGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Anna Bhurji Pav", appName)
    }

    @Test
    fun `default menu items match poster prices and specials`() {
        val singleBuruji = DefaultMenu.items.find { it.name == "Single Buruji 2 Pav" }
        assertNotNull(singleBuruji)
        assertEquals(50.0, singleBuruji!!.price, 0.01)
        assertEquals(2, singleBuruji.pavCount)

        val doubleBuruji = DefaultMenu.items.find { it.name == "Double Buruji 2 Pav" }
        assertNotNull(doubleBuruji)
        assertEquals(90.0, doubleBuruji!!.price, 0.01)

        val splBuruji = DefaultMenu.items.find { it.name == "SPL Buruji 2 Pav" }
        assertNotNull(splBuruji)
        assertEquals(130.0, splBuruji!!.price, 0.01)
        assertTrue(splBuruji.isSpecial)

        val singleOmlette2Pav = DefaultMenu.items.find { it.name == "Single Omlette 2 Pav" }
        assertNotNull(singleOmlette2Pav)
        assertEquals(40.0, singleOmlette2Pav!!.price, 0.01)

        val doubleOmlette2Pav = DefaultMenu.items.find { it.name == "Double Omlette 2 Pav" }
        assertNotNull(doubleOmlette2Pav)
        assertEquals(70.0, doubleOmlette2Pav!!.price, 0.01)

        val extraPav = DefaultMenu.items.find { it.name == "Pav" }
        assertNotNull(extraPav)
        assertEquals(5.0, extraPav!!.price, 0.01)

        // Verify Amul butter cube and cheese topping are removed
        val butterItem = DefaultMenu.items.find { it.name.contains("Butter", ignoreCase = true) }
        val cheeseItem = DefaultMenu.items.find { it.name.contains("Cheese", ignoreCase = true) }
        org.junit.Assert.assertNull(butterItem)
        org.junit.Assert.assertNull(cheeseItem)
        assertEquals(9, DefaultMenu.items.size)
    }

    @Test
    fun `cart item pricing calculation with quantity`() {
        val splItem = DefaultMenu.items.first { it.isSpecial } // ₹130
        val cartItem = CartItem(
            menuItem = splItem,
            quantity = 2,
            spiceLevel = SpiceLevel.EXTRA_TEEKHA
        )

        assertEquals(130.0, cartItem.unitPrice, 0.01)
        assertEquals(260.0, cartItem.totalPrice, 0.01)
    }

    @Test
    fun `stall timing helper manual override`() {
        val forceOpenStatus = ShopTimingHelper.calculateStallStatus(manualOverride = true)
        assertTrue(forceOpenStatus.isOpen)
        assertTrue(forceOpenStatus.statusText.contains("OPEN"))

        val forceCloseStatus = ShopTimingHelper.calculateStallStatus(manualOverride = false)
        assertFalse(forceCloseStatus.isOpen)
        assertTrue(forceCloseStatus.statusText.contains("CLOSED"))
    }

    @Test
    fun `qr matrix generator creates valid 25x25 matrix with finder patterns`() {
        val upiUri = "upi://pay?pa=kathirvelthevar-3@okhdfcbank&pn=Anna%20Buruji%20Pav&am=90.00&cu=INR"
        val matrix = QrMatrixGenerator.generateMatrix(upiUri)
        assertEquals(25, matrix.size)
        assertEquals(25, matrix[0].size)

        // Top-left finder corner (0,0) must be true (black module)
        assertTrue(matrix[0][0])
        assertTrue(matrix[0][6])
        assertTrue(matrix[6][0])
        assertTrue(matrix[6][6])
    }

    @Test
    fun `order digital receipt data reflects items, total, and upi payment status`() {
        val singleBuruji = DefaultMenu.items.first { it.name == "Single Buruji 2 Pav" }
        val doubleBuruji = DefaultMenu.items.first { it.name == "Double Buruji 2 Pav" }

        val items = listOf(
            CartItem(menuItem = singleBuruji, quantity = 1),
            CartItem(menuItem = doubleBuruji, quantity = 1)
        )
        val subtotal = items.sumOf { it.totalPrice } // 50 + 90 = 140
        val parcelFee = 5.0
        val total = subtotal + parcelFee

        val order = com.example.data.model.Order(
            orderId = "AB-1234",
            tokenNumber = 7,
            customerName = "Vikram",
            customerPhone = "9892000648",
            items = items,
            subtotal = subtotal,
            parcelFee = parcelFee,
            totalAmount = total,
            orderType = com.example.data.model.OrderType.TAKEAWAY,
            paymentStatus = com.example.data.model.PaymentStatus.PAID_UPI,
            upiTransactionId = "UPI/OKHDFC/AB123489123"
        )

        assertEquals(145.0, order.totalAmount, 0.01)
        assertEquals(com.example.data.model.PaymentStatus.PAID_UPI, order.paymentStatus)
        assertTrue(order.upiTransactionId.startsWith("UPI/OKHDFC/"))
        assertEquals(2, order.items.size)
        assertEquals(7, order.tokenNumber)
    }
}
