package com.example.voiceassistent.numberapi

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NumberService {
    //Базовая часть адреса
    //Конвертер, необходимый для преобразования JSON'а в объекты
    //Создание объекта, при помощи которого будут выполняться запросы
    @JvmStatic
    val api: NumberApi
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://htmlweb.ru") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build()
            Log.i("NumberService", "builder received")
            return retrofit.create(NumberApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
        }
}