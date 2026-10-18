package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items ORDER BY price ASC")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    suspend fun getMenuItemById(id: String): MenuItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MenuItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MenuItemEntity)

    @Update
    suspend fun update(item: MenuItemEntity)

    @Query("UPDATE menu_items SET inStock = :inStock WHERE id = :id")
    suspend fun updateStock(id: String, inStock: Boolean)

    @Query("UPDATE menu_items SET price = :price WHERE id = :id")
    suspend fun updatePrice(id: String, price: Double)

    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun count(): Int

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM menu_items")
    suspend fun deleteAll()
}
