package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_popup_post.*

class PopupPostActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.6f
        window.attributes = lpWindow

        setContentView(R.layout.activity_popup_post)

        storyTextView.text = intent.getStringExtra("story")!!
        uploadTime.text = intent.getIntExtra("uploadTime", 0).toString() + "에 작성됨"
    }

    fun onCloseBtnClicked(view: View) {
        val intent: Intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onStoryTextViewClicked(view: View) {
        val textView: TextView = view as TextView

        if (textView.layout != null) {
            // ellipsize 여부 확인
            if (textView.layout.getEllipsisCount(textView.lineCount - 1) > 0) {
                textView.maxLines = 1000
                textView.ellipsize = null
            } else {
                textView.maxLines = 2
                textView.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }
}