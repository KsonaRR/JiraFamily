package com.example.jirafamily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val durationMilliseconds = 5000L
        handler.postDelayed({
        }, durationMilliseconds)
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}