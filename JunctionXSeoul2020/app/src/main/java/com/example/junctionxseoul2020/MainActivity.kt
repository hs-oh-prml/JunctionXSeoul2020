package com.example.junctionxseoul2020

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.junctionxseoul2020.data.PostManager
import com.example.junctionxseoul2020.data.User
import com.example.junctionxseoul2020.data.UserManager

class MainActivity : AppCompatActivity() {

    lateinit var postManager: PostManager
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
        로그인 액티비티가 종료된 경우
        */
        if (requestCode == 1) {
            postManager = data?.getSerializableExtra("postManager") as PostManager
            userManager = data.getSerializableExtra("userManager") as UserManager
        }
    }

}
