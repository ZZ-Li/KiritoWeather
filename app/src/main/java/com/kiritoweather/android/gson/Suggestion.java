package com.kiritoweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ASUS on 2017/2/19.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("drsg")
    public Dress dress;

    public Sport sport;

    @SerializedName("trav")
    public Trip trip;

    @SerializedName("uv")
    public Uv uv;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Dress{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    public class Trip{
        @SerializedName("txt")
        public String info;
    }

    public class Uv{
        @SerializedName("txt")
        public String info;
    }
}
