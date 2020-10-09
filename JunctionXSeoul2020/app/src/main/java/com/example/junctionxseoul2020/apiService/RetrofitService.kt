package com.example.junctionxseoul2020.apiService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface RetrofitService {
    private fun provideOkHttpClient_Other(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val b= OkHttpClient.Builder()
        b.addInterceptor(interceptor)
        return b.build()
    }
    private fun provideLoggingInterceptor_Other(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    fun createGoogleAPIRetrofit(): RetrofitService {
        val retrofit= Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(
                provideOkHttpClient_Other(
                    provideLoggingInterceptor_Other()
                )
            )
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .build()
        return retrofit.create(RetrofitService::class.java)
    }
}