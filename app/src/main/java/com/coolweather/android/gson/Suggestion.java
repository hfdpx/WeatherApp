package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;
/*
和风天气返回的suggestion段数据内容和结构大致如下：
   "suggestion":{
         "comf":{
             "txt":"白天天气较热，虽然有雨，但是仍然无法削弱较高气温给人们带来的暑意，这种天气会让您感觉很不舒适。"
          },
          "cw":{
              "txt":"不宜洗车，为了24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"
          },
          "sport":{
                "txt":"有降水，且风力较强，推荐您在室内进行低强度运动，若坚持户外，请选择避雨防风的地点。"
          }
    }

    所以Suggestion实体类的结构和上面大致相仿
 */
public class Suggestion {

    /*
       由于JSON中的一些字段不太适合直接作为Java字段命名，因此这里特意使用了@SerilizedName注解的方式
       来让JSON字段和Java字段建立映射关系
    */

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {

        //描述文本
        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        //描述文本
        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        //描述文本
        @SerializedName("txt")
        public String info;

    }

}
