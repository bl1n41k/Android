package com.example.voiceassistent.weatherapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=32622875b412da159ef6b62847955664")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
