package com.example.voiceassistent.phoneapi;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Phone implements Serializable {
    @SerializedName("region")
    @Expose
    public Region region;

    @SerializedName("oper")
    @Expose
    public Operator current;

    public class Region{
        @SerializedName("name")
        @Expose
        public String name;
    }

    public class Operator{
        @SerializedName("name")
        @Expose
        public String name;
    }
}
