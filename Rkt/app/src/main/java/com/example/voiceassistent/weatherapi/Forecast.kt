package com.example.voiceassistent.weatherapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Forecast : Serializable {
    @JvmField
    @SerializedName("current")
    @Expose
    var current: Weather? = null

    @JvmField
    @SerializedName("location")
    @Expose
    var region: Region? = null

    inner class Weather {
        @JvmField
        @SerializedName("temperature")
        @Expose
        var temperature: Int? = null

        @JvmField
        @SerializedName("weather_descriptions")
        @Expose
        var weather_descriptions: List<String>? = null
    }

    inner class Region {
        @JvmField
        @SerializedName("region")
        @Expose
        var region: String? = null
    }
}