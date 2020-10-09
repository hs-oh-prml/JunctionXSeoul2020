package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.example.junctionxseoul2020.data.PostManager
import com.example.junctionxseoul2020.data.UserManager

class LoginActivity : AppCompatActivity() {

    lateinit var uID: String
    val postManager: PostManager = PostManager()
    val userManager: UserManager = UserManager()

    val myProgressBar: MyProgressBar = MyProgressBar()
    val handler: mHandler = mHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    // 회원가입시 호출
    fun onJoinBtnClicked(view: View) {
        // 구글 회원가입 코드 시작

        // 구글 회원가입 코드 종료

        myProgressBar.progressON(this, null)
        val thread: mThread = mThread(true)
        thread.start()
    }

    // 로그인시 호출
    fun onLoginBtnClicked(view: View) {
        // 구글 로그인 코드 시작

        // 구글 로그인 코드 종료

        myProgressBar.progressON(this, null)
        val thread: mThread = mThread(false)
        thread.start()
    }

    fun endActivity() {
        val intent: Intent = Intent()
        intent.putExtra("uID", uID)
        intent.putExtra("postManager", postManager)
        intent.putExtra("userManager", userManager)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    inner class mHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val bundle: Bundle = msg.data
            if (!(bundle.isEmpty)) {
                if (bundle.getBoolean("isThreadEnd")) {
                    endActivity()
                }
            }

            myProgressBar.progressOFF()
        }
    }

    inner class mThread(val isJoin: Boolean) : Thread() {
        override fun run() {
            val message: Message = handler.obtainMessage()
            val bundle: Bundle = Bundle()

            if (isJoin) {
                userManager.addUser(uID)
            }

            val boolean1: Boolean = postManager.readPost()
            val boolean2: Boolean = userManager.readUser()

            if (boolean1 && boolean2)
                bundle.putBoolean("isThreadEnd", true)
            else
                bundle.putBoolean("isThreadEnd", false)

            message.data = bundle
            handler.sendMessage(message)
        }
    }
}