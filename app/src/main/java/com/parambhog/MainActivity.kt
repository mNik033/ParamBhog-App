package com.parambhog

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.parambhog.adapters.ItemRecyclerAdapter
import com.parambhog.data.model.Item
import com.parambhog.databinding.ActivityMainBinding

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

        itemList = arrayListOf(
            Item(1, "Panch Mewa", "पंच मेवा", "A mixture of dry fruits such as almonds, cashews, raisins, dry dates (chhohara), etc.","बादाम, काजू, किशमिश, सूखे खजूर (छोहारा) आदि का मिश्रण।","https://parambhog.com/static/media/panch_mewa.43878b090b98cfed53bb.jpg", mapOf(151 to 300, 251 to 600, 501 to 1100)),
            Item(2, "Malai Peda", "मलाई पेड़ा", "Khoa/mawa, sugar, and milk, etc.","खोया/मावा, चीनी, और दूध।","https://parambhog.com/static/media/malai_peda.cc904f60e7f177cabd39.webp", mapOf(151 to 250, 251 to 500, 501 to 1000)),
            Item(3, "Kesar Peda", "केसर पेड़ा", "Khoa/mawa, Kesar, sugar, and milk, etc.","खोया/मावा, केसर, चीनी, और दूध।","https://parambhog.com/static/media/kesar_peda.aa221d501d077f53a524.avif", mapOf(151 to 200, 251 to 450, 501 to 900)),
            Item(4, "Laddu", "लड्डू", "Desi ghee, moti boondi, etc.","देशी घी, मोटी बूंदी।","https://parambhog.com/static/media/laddu.9abcd2e94a90c38d49ea.webp", mapOf(151 to 400, 251 to 800)),
            Item(5, "Mishri Khopra", "मिश्री खोप्रा", "A mixture of Mishri, Makhana, and Khopra, etc.","मिश्री, मखाना और खोप्रा का मिश्रण।","https://parambhog.com/static/media/mishri_khopra.ebbddb9415a7d48d3a3d.jpg", mapOf(111 to 700, 151 to 1000)),
            Item(6, "Churma", "चूरमा", "Flour, sugar, and ghee, etc.","आटा, चीनी, और घी।","https://parambhog.com/static/media/churma.7cc90d1fa28b7fe55959.webp", mapOf(151 to 450, 251 to 800))
        )

        val adapter = ItemRecyclerAdapter(itemList)
        binding.homeRecyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.homeRecyclerView.adapter = adapter
    }
}