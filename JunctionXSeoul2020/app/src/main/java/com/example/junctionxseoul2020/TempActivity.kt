package com.example.junctionxseoul2020

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class TempActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)
        init()
    }

    fun init(){
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .baseUrl("https://render-api.zepeto.io/v2/")
            .build()
            .create(RetrofitService::class.java)
        ZepetoAPI()
    }
    lateinit var retrofit:RetrofitService
    @SuppressLint("CheckResult")
    fun ZepetoAPI(){
        var photobooth_id = "VIDEOBOOTH_451"
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
