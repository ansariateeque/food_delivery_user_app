package com.example.wavesoffood.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.R
import com.example.wavesoffood.RetrieveAddToCart
import com.example.wavesoffood.databinding.CartlayoutBinding
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CartAdapter(
    var list: MutableList<RetrieveAddToCart>,
    var context: Context,
    var viewmodel: MainViewModel,
) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: CartlayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            CartlayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var retrieveaddtocart = list[position]

        holder.binding.foodname.text = retrieveaddtocart.name

        val formattedPrice = if (retrieveaddtocart.price?.rem(1) == 0.0) {
            retrieveaddtocart.price?.toInt().toString()  // Convert to Int if no decimals
        } else {
            retrieveaddtocart.price.toString()  // Keep original if has decimals
        }

        holder.binding.foodprice.text = formattedPrice.toString()

        val secureUrl = retrieveaddtocart.uri?.replace("http://", "https://")

        Picasso.get().invalidate(secureUrl) // Clear cache for new load

        Picasso.get()
            .load(secureUrl)
            .fit()
            .centerCrop()
            .error(R.drawable.error_image)
            .networkPolicy(NetworkPolicy.NO_CACHE) // Force fresh fetch
            .into(holder.binding.foodimage)


        holder.binding.quantity.text = retrieveaddtocart.quantity.toString()

        holder.binding.plusicon.setOnClickListener(View.OnClickListener {
            var value = Integer.parseInt(holder.binding.quantity.text.toString())
            value++
            holder.binding.quantity.setText(value.toString())
            viewmodel.updateQuantityOfItem(retrieveaddtocart.pushkey,value.toString())

        })
        holder.binding.minusicon.setOnClickListener(View.OnClickListener {

            var value = Integer.parseInt(holder.binding.quantity.text.toString())
            if (value == 0) return@OnClickListener
            value--
            holder.binding.quantity.setText(value.toString())
            viewmodel.updateQuantityOfItem(retrieveaddtocart.pushkey,value.toString())
        })
        holder.binding.deletebtn.setOnClickListener(View.OnClickListener {

            var builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete it?")
                .setPositiveButton("yes", DialogInterface.OnClickListener { dialogInterface, i ->


                    val itemKey = retrieveaddtocart.pushkey // 🔥 Item ka key
                    Log.d("CartViewModel", itemKey.toString())
                    CoroutineScope(Dispatchers.IO).launch {
                        val isDeleted =
                            viewmodel.deleteaddtocartitem(itemKey)// ✅ Function call karega
                    }
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }).show()


        })
    }

    fun updateList(newList: List<RetrieveAddToCart>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged() // Notify adapter that data has changed
    }
}