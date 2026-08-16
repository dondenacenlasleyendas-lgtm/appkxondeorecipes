package com.kxondeo.appkxondeorecipes

import kotlinx.serialization.Serializable

@Serializable
data class FoodCostIngredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val pricePerUnit: Double
) {
    val totalPrice: Double get() = quantity * pricePerUnit
}

@Serializable
data class FoodCost(
    val id: Int,
    val title: String,
    val ingredients: List<FoodCostIngredient>,
    val portions: Int,
    val overheadPercentage: Double = 10.0,
    val targetMarginPercentage: Double = 70.0,
    val folderId: Int? = null
) {
    val totalRawCost: Double get() = ingredients.sumOf { it.totalPrice }
    val rawCostPerPortion: Double get() = if (portions > 0) totalRawCost / portions else 0.0
    val costWithOverhead: Double get() = totalRawCost * (1 + overheadPercentage / 100)
    val costWithOverheadPerPortion: Double get() = if (portions > 0) costWithOverhead / portions else 0.0
    val suggestedPrice: Double get() = if (targetMarginPercentage < 100) costWithOverhead / (1 - targetMarginPercentage / 100) else 0.0
    val suggestedPricePerPortion: Double get() = if (portions > 0) suggestedPrice / portions else 0.0
}
