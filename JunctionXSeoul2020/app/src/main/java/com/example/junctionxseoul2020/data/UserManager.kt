package com.example.junctionxseoul2020.data

import java.io.Serializable
import java.util.*

class UserManager : Serializable {
    val users: Vector<User> = Vector<User>()

    init {

    }

    // 앱 처음 실행시 DB에서 유저 정보를 읽어오는 함수 -> 정상적으로 읽어오면 true 반환, 아니면 false 반환
    fun readUser(): Boolean {

        return true
    }

    // 새로운 유저가 회원가입 할 때 사용하는 함수
    fun addUser(uID: String) {

    }

    // 특정 유저를 찾을 때 사용하는 함수
    fun findUser(uId: String) {

    }
}