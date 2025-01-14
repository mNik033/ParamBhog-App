package com.parambhog.data.model

data class Item(
    val _id: String,
    val id: Int,
    val title: String,
    val hTitle: String,
    val description: String,
    val hDescription: String,
    val image: String,
    val tags: List<Tag>
)

data class Tag(
    val price: Int,
    val weight: Int
)