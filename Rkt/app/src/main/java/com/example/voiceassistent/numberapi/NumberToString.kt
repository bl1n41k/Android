package com.example.voiceassistent.numberapi

import android.util.Log
import androidx.core.util.Consumer
import com.example.voiceassistent.numberapi.NumberService.api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NumberToString {
    @JvmStatic
    fun getNumber(num: String?, callback: Consumer<String?>) {
        val api = api
        val call = api.getCurrentWeather(num)
        call!!.enqueue(object : Callback<Number?> {
            override fun onResponse(call: Call<Number?>, response: Response<Number?>) {
                val result = response.body()
                Log.i("NumberToString", "onResponse")
                if (result!!.current != null) {
                    val answer = result.current
                    Log.i("NumberToString", "answer=$answer")
                    callback.accept(answer)
                } else {
                    Log.e("NumberToString", "Empty answer")
                    callback.accept("Ошибка получения.")
                }
            }

            override fun onFailure(call: Call<Number?>, t: Throwable) {
                Log.e("NumberToString", "onFailure=" + t.message)
            }
        })
    }
}