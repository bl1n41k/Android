package com.example.voiceassistent.weatherapi;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.voiceassistent.AI;
import com.example.voiceassistent.translateapi.Languages;
import com.example.voiceassistent.translateapi.TranslateAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {


    public static void getForecast(String city, final Consumer<String> callback){
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response){
                //String symbol = "!";
                Forecast result = response.body();
                Log.i("ForecastToString", "onResponse");
                if (result.current!=null) {
                    String region = result.region.region;
                    String descriptions = result.current.weather_descriptions.get(0);
                    String textToTranslate = "In " + region
                            + " it is now about " + result.current.temperature  + " degrees Celsius"
                            + ", " + descriptions;;
                    TranslateAPI.getTranslate(Languages.ENGLISH.getLanguageCode(), Languages.RUSSIAN.getLanguageCode(), textToTranslate, new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            if (s.equals(AI.TRANSLATE_ERROR)){
                                String answer = "В " + region
                                        + " сейчас где-то " + result.current.temperature  + " "
                                        + AI.getWord(result.current.temperature, "градусов", "градус", " градуса")
                                        + " и " + descriptions;
                                callback.accept(answer);
                            }
                            else
                            {
                                //Добавить api для получения падежей
                                //https://htmlweb.ru/service/sklonjator.php

                                /*String[] listResult = s.split(symbol);
                                String answer = "В " + listResult[0]
                                        + " сейчас где-то " + result.current.temperature  + " "
                                        + AI.getWord(result.current.temperature, "градусов", "градус", " градуса")
                                        + " и " + listResult[1].substring(1).toLowerCase();*/
                                callback.accept(s);
                            }
                        }
                    });


                }else {
                    Log.e("ForecastToString", "Empty answer");
                    callback.accept(AI.WEATHER_ERROR);
                }
            }
            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.e("ForecastToString", "onFailure="+t.getMessage());
            }
        });


    }

}
