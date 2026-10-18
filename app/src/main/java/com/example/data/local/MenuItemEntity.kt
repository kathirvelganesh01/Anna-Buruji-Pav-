package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ItemCategory
import com.example.data.model.MenuItem

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String,
    val isSpecial: Boolean,
    val inStock: Boolean,
    val pavCount: Int,
    val eggCount: Int,
    val prepTimeMinutes: Int,
    val spicyLevelAvailable: Boolean
) {
    fun toDomain(): MenuItem {
        return MenuItem(
            id = id,
            name = name,
            price = price,
            category = try {
                ItemCategory.valueOf(category)
            } catch (e: Exception) {
                ItemCategory.ALL
            },
            description = description,
            isSpecial = isSpecial,
            inStock = inStock,
            pavCount = pavCount,
            eggCount = eggCount,
            prepTimeMinutes = prepTimeMinutes,
            spicyLevelAvailable = spicyLevelAvailable
        )
    }

    companion object {
        fun fromDomain(item: MenuItem): MenuItemEntity {
            return MenuItemEntity(
                id = item.id,
                name = item.name,
                price = item.price,
                category = item.category.name,
                description = item.description,
                isSpecial = item.isSpecial,
                inStock = item.inStock,
                pavCount = item.pavCount,
                eggCount = item.eggCount,
                prepTimeMinutes = item.prepTimeMinutes,
                spicyLevelAvailable = item.spicyLevelAvailable
            )
        }
    }
}
