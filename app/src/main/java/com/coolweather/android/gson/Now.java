package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;


/*
和风天气返回的数据中now的结构大致如下：
     "now":{
         "tmp":"29",
         "cond":{
              "txt":阵雨"
          }
      }

   所以Now实体类的结构和上面基本相同
 */
public class Now {

    /*
    由于JSON中的一些字段不太适合直接作为Java字段命名，因此这里特意使用了@SerilizedName注解的方式
    来让JSON字段和Java字段建立映射关系
     */

    //温度
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        //天气描述
        @SerializedName("txt")
        public String info;

    }

}
