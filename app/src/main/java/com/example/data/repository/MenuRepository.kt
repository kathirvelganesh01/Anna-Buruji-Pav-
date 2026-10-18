package com.example.data.repository

import com.example.data.local.MenuItemDao
import com.example.data.local.MenuItemEntity
import com.example.data.model.DefaultMenu
import com.example.data.model.ItemCategory
import com.example.data.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MenuRepository(private val menuItemDao: MenuItemDao) {

    suspend fun ensureDefaultMenuSeeded() {
        withContext(Dispatchers.IO) {
            // Delete removed items if they existed from prior seed
            menuItemDao.delete("item_extra_butter")
            menuItemDao.delete("item_cheese_slice")

            val count = menuItemDao.count()
            if (count == 0) {
                val entities = DefaultMenu.items.map { MenuItemEntity.fromDomain(it) }
                menuItemDao.insertAll(entities)
            }
        }
    }

    fun getAllMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAllMenuItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun updateStock(itemId: String, inStock: Boolean) {
        withContext(Dispatchers.IO) {
            menuItemDao.updateStock(itemId, inStock)
        }
    }

    suspend fun updatePrice(itemId: String, newPrice: Double) {
        withContext(Dispatchers.IO) {
            menuItemDao.updatePrice(itemId, newPrice)
        }
    }

    suspend fun addMenuItem(item: MenuItem) {
        withContext(Dispatchers.IO) {
            menuItemDao.insert(MenuItemEntity.fromDomain(item))
        }
    }

    suspend fun deleteMenuItem(itemId: String) {
        withContext(Dispatchers.IO) {
            menuItemDao.delete(itemId)
        }
    }

    suspend fun resetToDefaultMenu() {
        withContext(Dispatchers.IO) {
            menuItemDao.deleteAll()
            val entities = DefaultMenu.items.map { MenuItemEntity.fromDomain(it) }
            menuItemDao.insertAll(entities)
        }
    }
}
