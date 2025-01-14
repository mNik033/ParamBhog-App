package com.parambhog.data.repository

import com.parambhog.data.model.Item
import com.parambhog.data.remote.RetrofitClient.apiService

object ItemRepository {
    private var itemList = arrayListOf<Item>()

    suspend fun loadItems() {
        itemList = apiService.fetchItems()
    }

    suspend fun getItemList(): ArrayList<Item> {
        if (itemList.isEmpty()) loadItems()
        return itemList
    }
}