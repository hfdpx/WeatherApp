package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*
  和风天气返回的天气数据的大致格式：
  {
     "Heather":
     [
       {
          "status":"ok",
          "basic":{},
          "aqi":{},
          "now":{},
          "suggestion":{},
          "daily_forecast":[]
       }
     ]
  }

  其中basic，aqi，now，suggestion和daily_forecast的内部又会有具体的内容
  所以我们为这5个部分建立了5个实体类

 */

/*
   返回的数据中basic中的具体内容样例如下：
   "basic":{
       "city":"长沙",
       "id":"CN101190401",
       "update":{
              "loc":"2019-04-10 21:58"
        }
    }

    所以basic实体类的结构也和上面相似
 */
public class Basic {

    /*
    由于JSON中的一些字段不太适合直接作为Java字段命名，因此这里特意使用了@SerilizedName注解的方式
    来让JSON字段和Java字段建立映射关系
     */

    @SerializedName("city")
    public String cityName;//城市名称

    @SerializedName("id")
    public String weatherId;//城市对应的天气id

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;//天气更新时间

    }

}
