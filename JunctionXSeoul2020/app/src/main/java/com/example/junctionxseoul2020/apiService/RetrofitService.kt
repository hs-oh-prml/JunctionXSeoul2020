package com.example.junctionxseoul2020.apiService

import com.example.junctionxseoul2020.data.Zepeto
import com.example.junctionxseoul2020.data.ZepetoRequest
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface RetrofitService {
    // 검색
    @POST("graphics/zepeto/booth/{photoboothTitle}")
    fun zepetoAPI(
        @Path("photoboothTitle") photoboothTitle:String,
        @Header("Content-Type") content_type:String,
        @Body body: ZepetoRequest
    ): Observable<Zepeto>


}