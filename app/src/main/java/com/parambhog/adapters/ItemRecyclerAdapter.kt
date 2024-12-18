package com.parambhog.adapters

import android.content.res.Resources.getSystem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.parambhog.BlurredImageView
import com.parambhog.R
import com.parambhog.data.model.Item
import java.util.Locale

class ItemRecyclerAdapter(
    private val itemList: ArrayList<Item>,
    private val cartActionListener: OnCartActionListener
) :
    RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder>() {

    interface OnCartActionListener {
        fun onAddToCart(itemID: Int, title: String, price: Int, weight: Int, quantity: Int)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.itemTitle)
        val weight: TextView = itemView.findViewById(R.id.itemWeight)
        val desc: TextView = itemView.findViewById(R.id.itemDesc)
        val image: BlurredImageView = itemView.findViewById(R.id.itemImage)
        val priceMenu: MaterialAutoCompleteTextView = itemView.findViewById(R.id.itemPriceMenu)
        val detailsContainer: LinearLayout = itemView.findViewById(R.id.detailsContainer)
        val addToCartBtn: Button = itemView.findViewById(R.id.buttonAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_view, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.desc.text = item.description

        val entry = item.quantityMap.iterator().next()
        holder.priceMenu.setText(String.format(Locale.getDefault(), "₹ %d", entry.key))
        holder.weight.text = String.format(Locale.getDefault(), "%d gm", entry.value)

        val prices = item.quantityMap.keys.map { "₹ $it" }
        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_dropdown_item_1line,
            prices.toList()
        )
        holder.priceMenu.setAdapter(adapter)

        var selectedPrice = entry.key
        holder.priceMenu.setOnItemClickListener { _, _, i, _ ->
            selectedPrice = item.quantityMap.keys.elementAt(i)
            holder.weight.text =
                String.format(Locale.getDefault(), "%d gm", item.quantityMap[selectedPrice])
            if (item.quantityMap[selectedPrice]!! >= 1000) holder.weight.text = String.format(
                Locale.getDefault(), "%d kg", item.quantityMap[selectedPrice]?.div(1000)
            )
        }

        Glide.with(holder.itemView)
            .load(item.imageUrl)
            .centerCrop()
            .into(holder.image)

        holder.detailsContainer.post {
            val blurHeight = holder.detailsContainer.height
            holder.image.setBlurArea(blurHeight, 16 * getSystem().displayMetrics.density)
        }

        holder.addToCartBtn.setOnClickListener {
            cartActionListener.onAddToCart(item.itemID, item.title, selectedPrice, item.quantityMap[selectedPrice]!!, 1)
        }
    }

    override fun getItemCount(): Int = itemList.size
}