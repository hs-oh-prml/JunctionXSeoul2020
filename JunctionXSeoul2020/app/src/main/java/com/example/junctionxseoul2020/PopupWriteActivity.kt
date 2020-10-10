package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PopupWriteActivity : FragmentActivity() {

    lateinit var editText: EditText
    lateinit var zepetoImg: ImageView

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.6f
        window.attributes = lpWindow

        setContentView(R.layout.activity_popup_write)

        editText = findViewById(R.id.editText1)
        zepetoImg = findViewById(R.id.zepetoImg)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        // zepetoImg 이미지뷰에 이미지 업로드 시작

        // zepetoImg 이미지뷰에 이미지 업로드 종료
    }

    fun onCloseBtnClicked(view: View) {
        val intent: Intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onSubmitPostBtnClicked(view: View) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
        val formatted = current.format(formatter)
        Log.e("now time", formatted)

        val story: String = editText.text.toString().trim()

        /*
        zepetoImg 이미지뷰에 업로드 되어있는 이미지를 가져오고,
        formatted : 업로드시간, story : 이야기, 제페토 이미지, 업로드 할 때의 latitude, longitude
        총 5가지의 데이터를 DB에 저장해야 한다.
        */
        // DB에 저장하는 코드 시작

        // DB에 저장하는 코드 종료
    }
}