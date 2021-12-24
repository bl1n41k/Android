package com.example.voiceassistent.phoneapi;



import java.lang.annotation.Native;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface PhoneApi {
    @GET("json/mnp/phone/{number}/")
    Call<Phone> getCurrent(@Path("number") String phoneNumber);
}
