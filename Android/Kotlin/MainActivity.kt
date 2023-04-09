package com.example.ollie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ollie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater)

    }
}