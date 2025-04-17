package com.example.wavesoffood

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wavesoffood.Adapter.IngredientsAdapter
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.databinding.ActivityFoodDetailsBinding
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class FoodDetails : AppCompatActivity() {

    lateinit var binding: ActivityFoodDetailsBinding
    lateinit var retriveAddedItem: RetriveAddedItem
    lateinit var viewModel: MainViewModel
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel =
            ViewModelProvider(
                this,
                (application as MyApplication).viewModelFactory // ✅ Get Factory from Application class
            )[MainViewModel::class.java]
        retriveAddedItem = intent.getParcelableExtra<RetriveAddedItem>("retrieveaddeditem")!!

        binding.foodname.text = retriveAddedItem.name

        val secureUrl = retriveAddedItem.uri?.replace("http://", "https://")

        Picasso.get().invalidate(secureUrl) // Clear cache for new load

        Picasso.get()
            .load(secureUrl)
            .error(R.drawable.error_image)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(binding.foodimage)


        val formattedPrice = if (retriveAddedItem.price?.rem(1) == 0.0) {
            retriveAddedItem.price?.toInt().toString()  // Convert to Int if no decimals
        } else {
            retriveAddedItem.price.toString()  // Keep original if has decimals
        }

        binding.foodprice.text = formattedPrice.toString()


        binding.description.text = retriveAddedItem.description.toString()





        binding.recycleview.adapter = retriveAddedItem.ingredeinets?.let { IngredientsAdapter(it) }
        binding.recycleview.layoutManager = GridLayoutManager(this, 2)

        binding.imageView7.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


        binding.addtocart.setOnClickListener(View.OnClickListener {
            viewModel.addtocart(retriveAddedItem)
        })

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loading.collect { isLoading ->
                    if (isLoading) {
                        showLottieDialog()
                    } else {
                        dialog.dismiss()
                    }
                }

            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addtocartresult.collect { it: Pair<Boolean, String>? ->

                    if (it?.first!!) {
                        Toast.makeText(this@FoodDetails, "${it.second}", Toast.LENGTH_SHORT)
                            .show()
                        onBackPressed()


                    } else {
                        Toast.makeText(this@FoodDetails, "${it.second}", Toast.LENGTH_SHORT)
                            .show()


                    }
                }
            }
        }


    }
    fun showLottieDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialgoue_layout, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


}