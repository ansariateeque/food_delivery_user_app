package com.example.wavesoffood.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.IngredientlayoutBinding

class IngredientsAdapter(var arrayList: List<String>) :
    RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: IngredientlayoutBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    //
        return MyViewHolder(IngredientlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
     //
       return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       //
        var ingredients:String=arrayList.get(position)
        holder.binding.ingred.setText(ingredients)
    }
}