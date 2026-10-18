package com.example.data.model

object DefaultMenu {
    val items = listOf(
        MenuItem(
            id = "item_buruji_single_2pav",
            name = "Single Buruji 2 Pav",
            price = 50.0,
            category = ItemCategory.BURUJI_COMBOS,
            description = "Hot street-style scrambled egg tossed with spicy masala, green chillies & onions, served with 2 toasted pavs.",
            isSpecial = false,
            inStock = true,
            pavCount = 2,
            eggCount = 1,
            prepTimeMinutes = 4
        ),
        MenuItem(
            id = "item_buruji_double_2pav",
            name = "Double Buruji 2 Pav",
            price = 90.0,
            category = ItemCategory.BURUJI_COMBOS,
            description = "Double portion of rich spicy bhurji cooked in Amul butter with fresh coriander, served with 2 hot butter pavs.",
            isSpecial = false,
            inStock = true,
            pavCount = 2,
            eggCount = 2,
            prepTimeMinutes = 5
        ),
        MenuItem(
            id = "item_spl_buruji_2pav",
            name = "SPL Buruji 2 Pav",
            price = 130.0,
            category = ItemCategory.BURUJI_COMBOS,
            description = "Chef Anna's signature Special recipe with 3 eggs, extra rich spices & special garam masala + 2 pavs.",
            isSpecial = true,
            inStock = true,
            pavCount = 2,
            eggCount = 3,
            prepTimeMinutes = 6
        ),
        MenuItem(
            id = "item_buruji_single",
            name = "Single Buruji",
            price = 40.0,
            category = ItemCategory.SINGLES,
            description = "Single egg bhurji portion without pav. Quick protein fix on the tawa.",
            isSpecial = false,
            inStock = true,
            pavCount = 0,
            eggCount = 1,
            prepTimeMinutes = 3
        ),
        MenuItem(
            id = "item_omlette_single_2pav",
            name = "Single Omlette 2 Pav",
            price = 40.0,
            category = ItemCategory.OMLETTE_COMBOS,
            description = "Classic single fluffy omelette seasoned with pepper, onions, green chillies, served with 2 soft pavs.",
            isSpecial = false,
            inStock = true,
            pavCount = 2,
            eggCount = 1,
            prepTimeMinutes = 4
        ),
        MenuItem(
            id = "item_omlette_double_2pav",
            name = "Double Omlette 2 Pav",
            price = 70.0,
            category = ItemCategory.OMLETTE_COMBOS,
            description = "Hearty double egg omelette folded golden-brown on the hot tawa, paired with 2 crisp buttered pavs.",
            isSpecial = false,
            inStock = true,
            pavCount = 2,
            eggCount = 2,
            prepTimeMinutes = 5
        ),
        MenuItem(
            id = "item_omlette_double",
            name = "Double Omlette",
            price = 60.0,
            category = ItemCategory.SINGLES,
            description = "Double egg spiced omelette without pav. Loaded with onions & herbs.",
            isSpecial = false,
            inStock = true,
            pavCount = 0,
            eggCount = 2,
            prepTimeMinutes = 4
        ),
        MenuItem(
            id = "item_omlette_single",
            name = "Single Omlette",
            price = 30.0,
            category = ItemCategory.SINGLES,
            description = "Single egg street omelette fried crisp on hot cast-iron skillet.",
            isSpecial = false,
            inStock = true,
            pavCount = 0,
            eggCount = 1,
            prepTimeMinutes = 3
        ),
        MenuItem(
            id = "item_pav",
            name = "Pav",
            price = 5.0,
            category = ItemCategory.EXTRAS,
            description = "Fresh Mumbai ladi pav toasted on buttered tawa.",
            isSpecial = false,
            inStock = true,
            pavCount = 1,
            eggCount = 0,
            prepTimeMinutes = 1,
            spicyLevelAvailable = false
        )
    )
}
