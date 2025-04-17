package com.example.wavesoffood

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wavesoffood.databinding.ActivityAuthAcitivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AuthAcitivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthAcitivityBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthAcitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as androidx.navigation.fragment.NavHostFragment

        val navcontroller = navHostFragment.navController
        

    }
}