package com.example.junctionxseoul2020

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.junctionxseoul2020.apiService.RetrofitService
import com.example.junctionxseoul2020.data.ZepetoRequest
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TempActivity : AppCompatActivity() {

    lateinit var photoBoothList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)
        init()
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
        } catch (e:IOException) {
            e.printStackTrace()
        }
        scan.close()

    }
    fun init(){
        readFile()

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .baseUrl("https://render-api.zepeto.io/v2/")
            .build()
            .create(RetrofitService::class.java)
        ZepetoAPI()

        view_capture.setOnClickListener {
            captureView()
        }
        deep_link.setOnClickListener {

//            var pkg = "me.zepeto.main"
//            val url = "ZEPETO://HOME"
//            val str ="ZEPETO://HOME/MENU/BOOTH";
//            val intent =Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent)


            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("zepeto://home/capture")
            startActivity(intent)
//            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//            var i = packageManager.getLaunchIntentForPackage(pkg)
//            startActivity(i)
        }
    }
    fun captureView(){

        var bitmap = Bitmap.createBitmap(image_view.width, image_view.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        image_view.draw(canvas)

        var date = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        var filename = "tempImage${date}.jpg"
        image_view2.setImageBitmap(bitmap)

//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            var values = ContentValues().apply{
//                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
//                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
//                put(MediaStore.Images.Media.IS_PENDING, 1)
//            }
//            var collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//            var item = this.contentResolver.insert(collection, values)!!
//            this.contentResolver.openAssetFileDescriptor(item, "w", null).use {
//                var out = FileOutputStream(it!!.fileDescriptor)
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//                out.close()
//            }
//            values.clear()
//            values.put(MediaStore.Images.Media.IS_PENDING, 0)
//            this.contentResolver.update(item, values, null, null)
//
//            Toast.makeText(this, "Screen Capture", Toast.LENGTH_SHORT).show()
//        } else {
//            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() +
//                    File.separator +
//                    "junctionX"
//            val file = File(dir)
//            if (!file.exists()) {
//                file.mkdirs()
//            }
//
//            val imgFile = File(file, filename)
//            val os = FileOutputStream(imgFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
//            os.flush()
//            os.close()
//            val values = ContentValues()
//            with(values) {
//                put(MediaStore.Images.Media.TITLE, filename)
//                put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
//                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            }
//            this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//            Toast.makeText(this, "Screen Capture", Toast.LENGTH_SHORT).show()
//
//        }
    }

    lateinit var retrofit:RetrofitService
    @SuppressLint("CheckResult")
    fun ZepetoAPI(){
//        photoBoothList.shuffle()

        var photobooth_id = photoBoothList.random()
        var body = ZepetoRequest("booth",
            800,
            ZepetoRequest.hashCodes(arrayListOf("K4R33L"))
        )

        var jsonObj = JSONObject()
        jsonObj.put("type", "booth")
        jsonObj.put("width", "800")
        var strArr = ArrayList<String>()
        strArr.add("K4R33L")
        var jsonArr = JSONArray(strArr)
        var jsonO = JSONObject()
        jsonO.put("hashCodes", jsonArr)
        jsonObj.put("target", jsonO)

        retrofit
            .zepetoAPI(photobooth_id, "application/json", body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("Response", it.toString())
                var url = it.url
                runOnUiThread {
                    Glide.with(applicationContext).load(url).into(image_view)
                }
            },{
                Log.v("Fail","")
            })
    }
    fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

}
