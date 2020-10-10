package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log

class StartLoading : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_loading)

        startLoading()
    }

    private fun startLoading() {
        val handler: Handler = Handler()
        handler.postDelayed({
            val intent: Intent = Intent()
            setResult(RESULT_OK, intent);
            Log.i("finish", "in startLoading function")
            finish()
        }, 2000)
    }
}