package com.example.voiceassistent.numberapi;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NumberService {
    public static NumberApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        Log.i("NumberService", "builder received");
        return retrofit.create(NumberApi.class); //Создание объекта, при помощи которого будут выполняться запросы
    }
}
