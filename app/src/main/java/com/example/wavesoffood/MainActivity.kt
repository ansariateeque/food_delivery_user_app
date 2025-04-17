package com.example.wavesoffood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wavesoffood.Fragment.NotificationBottomF
import com.example.wavesoffood.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.notificationBell.setOnClickListener(View.OnClickListener {
           var bottomSheetDialogFragment= NotificationBottomF()
            bottomSheetDialogFragment.show(supportFragmentManager,"test")
        })
        var navController=findNavController(R.id.fragmentContainerView)
        var bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomnavi)
        bottomNavigationView.setupWithNavController(navController)

    }



}