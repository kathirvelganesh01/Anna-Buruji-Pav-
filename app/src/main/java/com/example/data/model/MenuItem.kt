package com.example.data.model

enum class ItemCategory(val displayName: String, val iconLabel: String) {
    ALL("All Items", "🍽️"),
    BURUJI_COMBOS("Buruji Combos", "🍳"),
    OMLETTE_COMBOS("Omlette Combos", "🥪"),
    SINGLES("Single Portions", "🥘"),
    EXTRAS("Pav & Extras", "🍞")
}

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: ItemCategory,
    val description: String,
    val isSpecial: Boolean = false,
    val inStock: Boolean = true,
    val pavCount: Int = 0,
    val eggCount: Int = 1,
    val prepTimeMinutes: Int = 5,
    val spicyLevelAvailable: Boolean = true
)
