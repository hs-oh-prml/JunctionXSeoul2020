package com.example.junctionxseoul2020

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.junctionxseoul2020.data.Post
import com.example.junctionxseoul2020.data.PostManager
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var postManager: PostManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        postManager = PostManager()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if(data != null) {

                val vec = data.getStringArrayListExtra("postManager")
                val gson = Gson()
                for (i in vec) {
                    postManager.posts.add(gson.fromJson(i, Post::class.java))
                }
                Log.d("Log_Post",postManager.posts.toString())
            } else {
                Log.d("Log_DATA", "data null")
            }
        }
    }

}
