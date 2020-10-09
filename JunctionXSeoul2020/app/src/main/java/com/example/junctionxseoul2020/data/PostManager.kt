package com.example.junctionxseoul2020.data

import java.io.Serializable
import java.util.*

class PostManager: Serializable {
    val posts: Vector<Post> = Vector<Post>()

    init {

    }

    // 앱 처음 실행시 DB에서 게시글 정보를 읽어오는 함수 -> 정상적으로 읽어오면 true 반환, 아니면 false 반환
    fun readPost(): Boolean {

        return true
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