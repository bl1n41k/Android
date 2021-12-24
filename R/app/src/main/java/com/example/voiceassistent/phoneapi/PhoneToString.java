package com.example.voiceassistent.phoneapi;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.voiceassistent.AI;
import com.example.voiceassistent.numberapi.Number;
import com.example.voiceassistent.numberapi.NumberApi;
import com.example.voiceassistent.numberapi.NumberService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneToString {
    public static void getNumber(String num, final Consumer<String> callback){
        PhoneApi api = PhoneService.getApi();
        Call<Phone> call = api.getCurrent(num);
        call.enqueue(new Callback<Phone>() {
            @Override
            public void onResponse(Call<Phone> call, Response<Phone> response){
                Phone result = response.body();

                if (result.current!=null) {
                    String reg  = result.region.name;
                    String oper = result.current.name;
                    callback.accept(reg + " " + oper);

                }else{
                    callback.accept(AI.NUMBER_GETTING_ERROR);
                }
            }
            @Override
            public void onFailure(Call<Phone> call, Throwable t) {
                Log.e("PhoneToString", "onFailure="+t.getMessage());
            }
        });
    }
}
