package com.example.voiceassistent.numberapi;

import android.util.Log;

import androidx.core.util.Consumer;
import com.example.voiceassistent.AI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NumberToString {
    public static void getNumber(String num, final Consumer<String> callback){
        NumberApi api = NumberService.getApi();
        Call<Number> call = api.getCurrentWeather(num);
        call.enqueue(new Callback<Number>() {
            @Override
            public void onResponse(Call<Number> call, Response<Number> response){
                Number result = response.body();
                Log.i("NumberToString", "onResponse");
                if (result.current!=null) {
                    String answer = result.current;
                    Log.i("NumberToString", "answer=" + answer);
                    callback.accept(answer);

                }else{
                    Log.e("NumberToString", "Empty answer");
                    callback.accept(AI.NUMBER_GETTING_ERROR);
                }
            }
            @Override
            public void onFailure(Call<Number> call, Throwable t) {
                Log.e("NumberToString", "onFailure="+t.getMessage());
            }
        });
    }
}
