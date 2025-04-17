package com.example.wavesoffood.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.Models.NotificationModel
import com.example.wavesoffood.databinding.NotificationItemBinding

class NotificationAdapter(var arrayList: ArrayList<NotificationModel>):RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    class MyViewHolder(var binding:NotificationItemBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var notificationModel=arrayList.get(position)
        holder.binding.notificationImage.setImageResource(notificationModel.notification_img)
        holder.binding.notificationMsg.setText(notificationModel.notification_msg)
    }
}