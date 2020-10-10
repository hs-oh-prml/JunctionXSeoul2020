package com.example.junctionxseoul2020

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.junctionxseoul2020.apiService.RetrofitService
import com.example.junctionxseoul2020.data.Post
import com.example.junctionxseoul2020.data.ZepetoRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_temp.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PopupWriteActivity : FragmentActivity() {

    lateinit var postDB: DatabaseReference

    lateinit var editText: EditText
    lateinit var zepetoImg: ImageView

    var latitude: Double = 0.0
    var longitude: Double = 0.0


    lateinit var retrofit: RetrofitService          // retrofit API manager
    lateinit var photoBoothList: ArrayList<String>  // photobooth id list
    lateinit var hashCode:String                    // user's hashcode
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

        // initialize user's hash code
        hashCode = "K4R33L"
        // load photobooth list from file 'data.txt'
        readFile()
        // retrofit object
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .baseUrl("https://render-api.zepeto.io/v2/")
            .build()
            .create(RetrofitService::class.java)
        // zepetoImg 이미지뷰에 이미지 업로드 시작
        ZepetoAPI()
        // zepetoImg 이미지뷰에 이미지 업로드 종료
    }
    fun readFile(){
        photoBoothList = ArrayList()
//        Thread(Runnable {
//
//            var inputStream = resources.openRawResource(R.raw.data);
//            var scan = Scanner(inputStream)
//            try {
//                while (scan.hasNext()) {
//                    var line = scan.nextLine()
//                    photoBoothList.add(line)
////                    Log.d("PHOTO_BOOTH", line)
//                }
//            } catch (e:IOException) {
//                e.printStackTrace()
//            }
//            scan.close()
//        }).start()


        var inputStream = resources.openRawResource(R.raw.data);
        var scan = Scanner(inputStream)
        try {
            while (scan.hasNext()) {
                var line = scan.nextLine()
                photoBoothList.add(line)
//                    Log.d("PHOTO_BOOTH", line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        scan.close()

    }

    // function: Call Zepeto Rendering API
    @SuppressLint("CheckResult")
    fun ZepetoAPI(){
//        photoBoothList.shuffle()

        var photobooth_id = photoBoothList.random()
        var body = ZepetoRequest("booth",
            400,
            ZepetoRequest.hashCodes(arrayListOf(hashCode))
        )

//        var jsonObj = JSONObject()
//        jsonObj.put("type", "booth")
//        jsonObj.put("width", "800")
//        var strArr = ArrayList<String>()
//        strArr.add("K4R33L")
//        var jsonArr = JSONArray(strArr)
//        var jsonO = JSONObject()
//        jsonO.put("hashCodes", jsonArr)
//        jsonObj.put("target", jsonO)

        retrofit
            .zepetoAPI(photobooth_id, "application/json", body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("Response", it.toString())
                var url = it.url
                runOnUiThread {
                    Glide.with(applicationContext).load(url).into(zepetoImg)
                }
            },{
                Log.v("Fail","")
            })
    }
    fun createOkHttpClient(): OkHttpClient {        // monitoring HTTP log
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    // screen capture: view -> image
    fun captureView(){

        var bitmap = Bitmap.createBitmap(zepetoImg.width, zepetoImg.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        image_view.draw(canvas)

    }


    fun onCloseBtnClicked(view: View) {
        val intent: Intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onSubmitPostBtnClicked(view: View) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
        val formatted = current.format(formatter)
        Log.e("now time", formatted)

        var story: String = editText.text.toString().trim()
        story = URLEncoder.encode(story, "utf-8")

        /*
        zepetoImg 이미지뷰에 업로드 되어있는 이미지를 가져오고,
        formatted : 업로드시간, story : 이야기, 제페토 이미지, 업로드 할 때의 latitude, longitude
        총 5가지의 데이터를 DB에 저장해야 한다.
        */
        // DB에 저장하는 코드 시작
        val uID = App.prefs.getUserUID()!!
        postDB = FirebaseDatabase.getInstance().getReference("post")
        val pID = postDB.push().key!!
        val url = URLEncoder.encode("https://firebasestorage.googleapis.com/v0/b/junctionxseoul2020.appspot.com/o/laptop.jpg?alt=media&token=8df3b0b4-62bd-452f-af49-4463bab37c4a","utf-8")
        val item = Post(pID, url, uID, story, formatted, latitude, longitude, null)
        postDB.child("/$pID").setValue(item)
        // DB에 저장하는 코드 종료
    }
}

//val pID: String,
//val img: String,
//val uID: String,
//val story: String,
//val uploadTime: String,
//val uploadLat: Double,
//val uploadLng: Double,
//val comments: java.util.ArrayList<String>?