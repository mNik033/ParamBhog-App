package com.parambhog

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.parambhog.adapters.ItemRecyclerAdapter
import com.parambhog.data.model.Item
import com.parambhog.data.repository.CartRepository
import com.parambhog.data.repository.ItemRepository
import com.parambhog.databinding.ActivityMainBinding
import com.parambhog.viewmodels.CartViewModel
import com.parambhog.viewmodels.CartViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var itemList: ArrayList<Item>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = CartRepository(applicationContext)
        val cartViewModel =
            ViewModelProvider(this, CartViewModelFactory(repository))[CartViewModel::class.java]
        itemList = ItemRepository().getItemList()

        val adapter =
            ItemRecyclerAdapter(itemList, object : ItemRecyclerAdapter.OnCartActionListener {
                override fun onAddToCart(itemID: Int, title: String, price: Int, weight: Int, diffQuantity: Int) {
                    cartViewModel.addItemToCart(itemID, title, price, weight, diffQuantity)
                }
            })

        binding.homeRecyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.homeRecyclerView.adapter = adapter

        cartViewModel.totalWeight.observe(this) { totalWeight ->
            if (totalWeight > 2000) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.order_weight_limit_exceeded),
                    Snackbar.LENGTH_SHORT
                ).setAction(getString(R.string.review_cart)) {
                    navigateToCart()
                }.show()
            }
        }

        binding.buttonCart.setOnClickListener {
            navigateToCart()
        }
    }

    private fun navigateToCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }
}