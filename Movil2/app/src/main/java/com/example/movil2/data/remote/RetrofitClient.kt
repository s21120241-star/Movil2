package com.example.movil2.data.remote

import android.content.Context
import com.example.movil2.Network.AddCookiesInterceptor
import com.example.movil2.Network.ReceivedCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private lateinit var serviceInstance: SicenetService

    fun init(context: Context) {
        val client = OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor(context))
            .addInterceptor(ReceivedCookiesInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://sicenet.surguanajuato.tecnm.mx/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        serviceInstance = retrofit.create(SicenetService::class.java)
    }

    val service: SicenetService
        get() = serviceInstance
}
