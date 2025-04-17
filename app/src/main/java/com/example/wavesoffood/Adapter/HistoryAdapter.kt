package com.example.wavesoffood.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.adminpaneloffoodwaves.Adapter.OrderDetailsAdapter
import com.example.wavesoffood.MainViewModel
import com.example.wavesoffood.OrderHistoryDetails
import com.example.wavesoffood.Orders
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.HistorylayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class HistoryAdapter(var list: List<Orders>, viewModel: MainViewModel) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: HistorylayoutBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun setList(orders: Orders) {

            binding.apply {
                tvOrderId.text = orders.orderID

                tvTimeId.text = convertTimestampToDate(orders.timestamp)


                val formattedPrice =
                    if (orders.totalAmount.rem(1) == 0.0) {
                        orders.totalAmount.toInt().toString()  // Convert to Int if no decimals
                    } else {
                        orders.totalAmount.toString()  // Keep original if has decimals
                    }

                tvTotalId.text = formattedPrice

                if (orders.status == "Pending") {
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                    tvOrderStatus.setText("Pending")
                    statusIndicator.setBackgroundResource(R.drawable.red_bullet_shape)
                    binding.delivery.visibility = View.GONE

                } else {
                    tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green_circle
                        )
                    )
                    tvOrderStatus.setText("Completed")
                    tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green_circle
                        )
                    )
                    binding.delivery.visibility = View.VISIBLE
                    binding.deliverat.text = convertTimestampToDate(orders.deliveredTimestamp)
                    statusIndicator.setBackgroundResource(R.drawable.bulletshape)

                }

                root.setOnClickListener{
                    var intent = Intent(context, OrderHistoryDetails::class.java)
                    intent.putExtra("order_data", orders)
                    context.startActivity(intent)
                }
            }
        }

        fun convertTimestampToDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //
        return MyViewHolder(
            HistorylayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), parent.context
        )
    }

    override fun getItemCount(): Int {
        //
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //
        val historyobjects = list[list.size - 1 - position]
        holder.setList(historyobjects)

    }


    fun update(newList: List<Orders>) {
        list = newList
        notifyDataSetChanged()
    }


}
