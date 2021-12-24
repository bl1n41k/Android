package com.example.voiceassistent.phoneapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PhoneApi {
    @GET("json/mnp/phone/{number}/")
    fun getCurrent(@Path("number") phoneNumber: String?): Call<Phone?>?
}