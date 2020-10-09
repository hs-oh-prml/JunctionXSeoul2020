package com.example.junctionxseoul2020

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_temp_realtimedb.*

class temp_realtimedb : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_realtimedb)

        textView.text = App.prefs.getUserUID()
        textView2.text = App.prefs.getUserHASHCODE()
    }
}