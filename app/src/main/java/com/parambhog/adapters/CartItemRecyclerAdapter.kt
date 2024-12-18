package com.parambhog.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parambhog.R
import com.parambhog.data.model.CartItem
import java.util.Locale

class CartItemRecyclerAdapter(
    private val cartItemList: MutableList<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) :
    RecyclerView.Adapter<CartItemRecyclerAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.cartItemTitle)
        val weight: TextView = itemView.findViewById(R.id.cartItemWeight)
        val quantity: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val price: TextView = itemView.findViewById(R.id.cartItemPrice)
        val plusButton: Button = itemView.findViewById(R.id.buttonPlus)
        val minusButton: Button = itemView.findViewById(R.id.buttonMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_card_view, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = cartItemList[position]

        holder.title.text = item.title
        holder.weight.text = String.format(Locale.getDefault(), "%d gm", item.weight)
        if (item.weight >= 1000)
            holder.weight.text = String.format(Locale.getDefault(), "%d kg", item.weight.div(1000))
        holder.quantity.text = String.format(Locale.getDefault(), "%d", item.quantity)
        holder.price.text = String.format(Locale.getDefault(), "₹ %d", item.quantity * item.price)

        holder.plusButton.setOnClickListener {
            onQuantityChanged(item, 1)
        }

        holder.minusButton.setOnClickListener {
            onQuantityChanged(item, -1)
        }
    }

    override fun getItemCount(): Int = cartItemList.size

    fun updateCartItems(newItems: List<CartItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = cartItemList.size
            override fun getNewListSize() = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = cartItemList[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return oldItem.itemID == newItem.itemID && oldItem.weight == newItem.weight
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = cartItemList[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return oldItem.quantity == newItem.quantity
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        cartItemList.clear()
        cartItemList.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}