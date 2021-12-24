package com.example.voiceassistent.phoneapi;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhoneService {
    public static PhoneApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        return retrofit.create(PhoneApi.class); //Создание объекта, при помощи которого будут выполняться запросы
    }
}
