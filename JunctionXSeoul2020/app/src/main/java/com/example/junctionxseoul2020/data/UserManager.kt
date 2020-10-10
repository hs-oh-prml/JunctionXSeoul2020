package com.example.junctionxseoul2020.data

import android.os.Bundle
import android.os.Message
import android.util.Log
import com.example.junctionxseoul2020.LoginActivity
import com.google.firebase.database.*
import java.io.Serializable
import java.util.*

class UserManager(var handler: LoginActivity.mHandler) : Serializable {
    val users: Vector<User> = Vector<User>()

    lateinit var userDB: DatabaseReference

    init {

    }

    // 앱 처음 실행시 DB에서 유저 정보를 읽어오는 함수 -> 정상적으로 읽어오면 true 반환, 아니면 false 반환
    fun readUser() {
        userDB = FirebaseDatabase.getInstance().getReference("user")

        userDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("Log_User_DB", p0.value.toString())

                val message: Message = handler.obtainMessage()
                val bundle: Bundle = Bundle()
                bundle.putBoolean("isThreadEnd_user", true)

                message.data = bundle
                handler.sendMessage(message)
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })
    }

    // 새로운 유저가 회원가입 할 때 사용하는 함수
    fun addUser(uID: String) {

    }

    // 특정 유저를 찾을 때 사용하는 함수
    fun findUser(uId: String) {

    }
}