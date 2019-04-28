package com.coolweather.android.gson;


/*
和风天气返回的数据中aqi的结构大致如下：
     "aqi":{
         "city":{
              "aqi":"44",
              "pm25":13",
           }
      }

      所以AQI实体类大致结构和上相同
 */
public class AQI {

    //城市类
    public AQICity city;

    public class AQICity {

        //空气指数
        public String aqi;

        //pm2.5的指数
        public String pm25;

    }

}
