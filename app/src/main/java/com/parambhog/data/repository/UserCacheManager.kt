package com.parambhog.data.repository

import android.content.Context
import com.parambhog.data.model.User

class UserCacheManager(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

    fun cacheUserLocally(user: User) {
        with(sharedPrefs.edit()) {
            putString("id", user.id)
            putString("email", user.email)
            putString("name", user.name)
            putString("contactNo", user.contactNo)
            putString("pincode", user.pincode)
            putString("state", user.state)
            putString("district", user.district)
            putString("address", user.address)
            putBoolean("signed_in", true)
            apply()
        }
    }

    fun isSignedIn(): Boolean {
        return sharedPrefs.getBoolean("signed_in", false)
    }
}