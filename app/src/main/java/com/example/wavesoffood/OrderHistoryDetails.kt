package com.example.wavesoffood

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminpaneloffoodwaves.Adapter.OrderDetailsAdapter
import com.example.wavesoffood.databinding.ActivityOrderHistoryDetailsBinding

class OrderHistoryDetails : AppCompatActivity() {
    lateinit var binding: ActivityOrderHistoryDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding =
            ActivityOrderHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val order = intent.getParcelableExtra<Orders>("order_data")


        if (order == null) {
            Log.e("OrderHistoryDetails", "order_data intent se null aa raha hai!")
            return  // Agar null hai toh activity ko aage process mat karne do
        }

        binding.hotelname.visibility = View.VISIBLE
        binding.totalprice.visibility = View.VISIBLE

        val formattedPrice = if (order.totalAmount.rem(1) == 0.0) {
            order.totalAmount.toInt().toString()  // Convert to Int if no decimals
        } else {
            order.totalAmount.toString()  // Keep original if has decimals
        }


        binding.hotelname.text = "Order From: ${order.hotelname}"
        binding.totalprice.text = "TotalAmount: ${formattedPrice}"


        var adapter = order.let { OrderDetailsAdapter(context = this, it.items) }
        binding.recycle.layoutManager = LinearLayoutManager(this)
        binding.recycle.adapter = adapter

    }
}