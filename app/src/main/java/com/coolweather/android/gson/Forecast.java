package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;


/*
和风天气返回的数据中daily_forecast中具有的内容大致如下：
    "daily_forecast":[
     {
           "data":"2019-04-10",
           "cond":{
               "txt_d":"阵雨"
           },
           "tmp":{
                "max":"34",
                :min":"27"
           }
         },

         {
           "data":"2019-04-10",
           "cond":{
               "txt_d":"阵雨"
           },
           "tmp":{
                "max":"34",
                :min":"27"
           }
         },
         ...............
      }

      分析：daily_forecast中包含的是一个数组，数组中每一项都代表未来一天的天气信息，针对这种情况
            我们只需要定义出单日天气的实体类就可以了，然后在声明实体类使用时使用集合类型来进行声明
 */

//单日天气实体类
public class Forecast {

    //日期
    public String date;

    //温度
    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        //最高温
        public String max;

        //最低温
        public String min;

    }


    public class More {

        //天气文字描述
        @SerializedName("txt_d")
        public String info;

    }

}
