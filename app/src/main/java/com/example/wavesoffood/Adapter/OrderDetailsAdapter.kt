package com.example.adminpaneloffoodwaves.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.R
import com.example.wavesoffood.RetrieveAddToCart
import com.example.wavesoffood.databinding.ItemOrderDetailsBinding
import com.squareup.picasso.Picasso
import kotlin.time.times

class OrderDetailsAdapter(
    private val context: Context,
    private val itemList: List<RetrieveAddToCart>
) : RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder>() {

    class MyViewHolder(val binding:ItemOrderDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemOrderDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        // ✅ Set Food Image using Picasoo
        val secureUrl = item.uri?.replace("http://", "https://")
        Picasso.get()
            .load(secureUrl)
            .into(holder.binding.imgFood)


        // ✅ Set Food Name
        holder.binding.tvFoodName.text = item.name

        // ✅ Set Quantity & One Item Price


        holder.binding.tvQuantity.text = "${item.quantity}"

        val formattedPrice = if (item.price?.rem(1) == 0.0) {
            item.price.toInt().toString()  // Convert to Int if no decimals
        } else {
            item.price.toString()  // Keep original if has decimals
        }

        holder.binding.tvItemPrice.text = "Price: ₹${formattedPrice} per item"

        // ✅ Calculate and Set Total Price
        val totalPrice = (item.quantity?.toInt()?.times(formattedPrice.toInt()))

        holder.binding.tvTotalPrice.text = "₹$totalPrice"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
