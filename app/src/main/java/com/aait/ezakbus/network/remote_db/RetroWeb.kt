package com.aait.ezakbus.network.remote_db

import com.aait.ezakbus.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetroWeb {
    val ENDPOINT="https://ezzk.4hoste.com/api/"
    val MAPURL="https://maps.googleapis.com/"
    val serviceApi: ServiceApi
    val serviceMapApi:ServiceApi

    init {
        serviceApi = Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient(provideLoggingInterceptor()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ServiceApi::class.java)
    }
    init {
        serviceMapApi = Retrofit.Builder()
            .baseUrl(MAPURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient(provideLoggingInterceptor()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ServiceApi::class.java)
    }
    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
    private fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val b = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            b.addInterceptor(interceptor)
        }
        return b.build()
    }
 }
