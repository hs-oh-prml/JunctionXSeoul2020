package com.example.junctionxseoul2020

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_hash_code.*

class HashCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hash_code)
        init()
    }

    private fun init() {
        //이름 3글자 이하이면 버튼 false
        et_hashCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, count: Int, p3: Int) {
                val input = et_hashCode.text.toString().trim().length
                btn_start.isEnabled = (input >= 2)
            }
        })

        btn_start.setOnClickListener {
            val name = et_hashCode.text.toString().trim()
            if(initUserInfo(name)) {
//
//                val intent = Intent(this, temp_realtimedb::class.java)
//                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "계정 등록 오류", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        checkHashCode.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("zepeto://home/menu/profile")
            startActivity(intent)
        }
    }

    private fun initUserInfo(hashCode: String):Boolean {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null){
            //DB에 현재 유저 정보 업로드
            val uid = user.uid
            val rdb = FirebaseDatabase.getInstance().getReference("user")
            rdb.child(uid).child("hashCode").setValue(hashCode)
            App.prefs.setUserInfo(uid, hashCode)
            return true
        }
        return false
    }
}