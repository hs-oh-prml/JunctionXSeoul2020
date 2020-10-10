package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PopupCommentWriteActivity : FragmentActivity() {

    lateinit var commentDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.6f
        window.attributes = lpWindow

        setContentView(R.layout.activity_popup_comment_write)
    }

    fun onCloseBtnClicked(view: View) {
        val intent: Intent = Intent()
        intent.putExtra("inputOK", false)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onSubmitPostBtnClicked(view: View) {
        val comment: String = findViewById<EditText>(R.id.editText2).text.toString().trim()
        val intent: Intent = Intent()
        intent.putExtra("comment", comment)
        intent.putExtra("inputOK", true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}