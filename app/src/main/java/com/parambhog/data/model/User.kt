package com.parambhog.data.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val contactNo: String?,
    val pincode: String?,
    val state: String?,
    val district: String?,
    val address: String?
)