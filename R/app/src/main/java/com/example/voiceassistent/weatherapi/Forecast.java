package com.example.voiceassistent.weatherapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Forecast implements Serializable {

    @SerializedName("current")
    @Expose
    public Weather current;

    @SerializedName("location")
    @Expose
    public Region region;

    public class Weather{
        @SerializedName("temperature")
        @Expose
        public Integer temperature;

        @SerializedName("weather_descriptions")
        @Expose
        public List<String> weather_descriptions;
    }

    public class Region{
        @SerializedName("region")
        @Expose
        public String region;
    }
}

