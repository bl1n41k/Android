package com.example.voiceassistent.phoneapi

import android.util.Log
import androidx.core.util.Consumer
import com.example.voiceassistent.phoneapi.PhoneService.api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PhoneToString {
    @JvmStatic
    fun getNumber(num: String?, callback: Consumer<String?>) {
        val api = api
        val call = api.getCurrent(num)
        call!!.enqueue(object : Callback<Phone?> {
            override fun onResponse(call: Call<Phone?>, response: Response<Phone?>) {
                val result = response.body()
                if (result!!.current != null) {
                    val reg = result.region!!.name
                    val oper = result.current!!.name
                    callback.accept("$reg $oper")
                } else {
                    callback.accept("Ошибка получения.")
                }
            }

            override fun onFailure(call: Call<Phone?>, t: Throwable) {
                Log.e("PhoneToString", "onFailure=" + t.message)
            }
        })
    }
}