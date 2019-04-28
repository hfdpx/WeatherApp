package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/*总的天气实体类
     和风天气返回的数据中6个字段：status，basic，aqi，now，suggestion和daily_forecast分别对应下面的6个类
     将这6个类封装成一个大的实体类，方便使用和扩展
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
