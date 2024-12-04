package com.parambhog.data.model

data class ApiResponse(
    val message: String,
    val token: String,
    val user: User
)