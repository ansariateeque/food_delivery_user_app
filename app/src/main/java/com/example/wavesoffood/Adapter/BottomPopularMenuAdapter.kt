package com.example.wavesoffood.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.FoodDetails
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.BottommenulayoutBinding
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class BottomPopularMenuAdapter(var list: List<RetriveAddedItem>, var context: Context) :
    RecyclerView.Adapter<BottomPopularMenuAdapter.MYViewHolder>() {
    class MYViewHolder(var binding: BottommenulayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MYViewHolder {
        return MYViewHolder(
            BottommenulayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MYViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var retriveAddedItem = list[position]
        holder.binding.foodname.text = retriveAddedItem.name

        val formattedPrice = if (retriveAddedItem.price?.rem(1) == 0.0) {
            retriveAddedItem.price?.toInt().toString()  // Convert to Int if no decimals
        } else {
            retriveAddedItem.price.toString()  // Keep original if has decimals
        }

        holder.binding.foodprice.text = formattedPrice.toString()

        val secureUrl = retriveAddedItem.uri?.replace("http://", "https://")

        Picasso.get().invalidate(secureUrl) // Clear cache for new load

        Picasso.get()
            .load(secureUrl)
            .fit()
            .centerCrop()
            .error(R.drawable.error_image)
            .networkPolicy(NetworkPolicy.NO_CACHE) // Force fresh fetch
            .into(holder.binding.foodimage1)

        holder.binding.layout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(context, FoodDetails::class.java)

                intent.putExtra("retrieveaddeditem",list[position])
                context.startActivity(intent)


            }
        })
        holder.binding.nameofhotel.text=retriveAddedItem.nameofrestuarant
    }

    fun updateList(newList: List<RetriveAddedItem>) {
        list = newList as List<RetriveAddedItem>
        notifyDataSetChanged() // Notify adapter that data has changed
    }
}