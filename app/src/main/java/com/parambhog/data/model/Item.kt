package com.parambhog.data.model

data class Item(
    val itemID: Int,
    val title: String,
    val hTitle: String,
    val description: String,
    val hDescription: String,
    val imageUrl: String,
    val quantityMap: Map<Int, Int>
    // maps price to quantity
)