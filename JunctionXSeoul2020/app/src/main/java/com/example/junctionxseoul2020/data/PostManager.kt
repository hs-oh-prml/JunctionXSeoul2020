package com.example.junctionxseoul2020.data

import android.os.Bundle
import android.os.Message
import android.util.Log
import com.example.junctionxseoul2020.LoginActivity
import com.google.firebase.database.*
import java.io.Serializable
import java.util.*

class PostManager(var handler: LoginActivity.mHandler): Serializable {
    val posts: Vector<Post> = Vector<Post>()

    lateinit var postDB: DatabaseReference

    init {

    }

    // 앱 처음 실행시 DB에서 게시글 정보를 읽어오는 함수 -> 정상적으로 읽어오면 true 반환, 아니면 false 반환
    fun readPost() {
        postDB = FirebaseDatabase.getInstance().getReference("post")

        postDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("Log_Post_DB", p0.value.toString())

                val message: Message = handler.obtainMessage()
                val bundle: Bundle = Bundle()
                bundle.putBoolean("isThreadEnd_post", true)

                message.data = bundle
                handler.sendMessage(message)
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })
    }

    // 유저가 게시글을 작성할 때 사용하는 함수
    fun addPost(uID: Int) {

    }

    // 유저가 자신이 작성한 게시글을 찾고자 할 때 사용하는 함수
    fun findPost() {

    }

    // 일정시간 경과 후에 게시글이 삭제되야 할 때 사용하는 함수
    fun deletePost() {

    }
}