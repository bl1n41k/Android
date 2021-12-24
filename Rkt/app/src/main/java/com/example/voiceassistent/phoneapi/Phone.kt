package com.example.voiceassistent.phoneapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Phone : Serializable {
    @SerializedName("region")
    @Expose
    var region: Region? = null

    @SerializedName("oper")
    @Expose
    var current: Operator? = null

    inner class Region {
        @SerializedName("name")
        @Expose
        var name: String? = null
    }

    inner class Operator {
        @SerializedName("name")
        @Expose
        var name: String? = null
    }
}