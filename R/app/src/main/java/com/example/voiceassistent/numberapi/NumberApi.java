package com.example.voiceassistent.numberapi;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NumberApi {
    @GET("json/convert/num2str?dec=0")
    Call<Number> getCurrentWeather(@Query("num") String num);
}
