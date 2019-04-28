package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
解析和处理数据的思路：
    1.利用JSONArray和JSONObject将数据解析出来
    2.组装成实体类对象
    3.调用save()方法将数据存储到数据库
 */
public class Utility {

    /*
    解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {

        //返回的服务器响应不为空
        if (!TextUtils.isEmpty(response)) {
            try {

                //利用JSON数组存储返回的JSON数据
                JSONArray allProvinces = new JSONArray(response);

                //遍历JSON数组
                for (int i = 0; i < allProvinces.length(); i++) {

                    //获得第i个JSON对象
                    JSONObject provinceObject = allProvinces.getJSONObject(i);

                    //封装到实体类中
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));

                    //将数据存储到数据库
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
      解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {

        //返回的服务器响应不为空
        if (!TextUtils.isEmpty(response)) {
            try {

                //利用JSON数组存储返回的JSON数据
                JSONArray allCities = new JSONArray(response);

                //遍历JSON数组
                for (int i = 0; i < allCities.length(); i++) {

                    //获得第i个JSON对象
                    JSONObject cityObject = allCities.getJSONObject(i);

                    //封装到实体类中
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);

                    //将数据存储到数据库
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

   /*
   解析和处理服务器返回的县级数据
    */
    public static boolean handleCountyResponse(String response, int cityId) {

        //返回的服务器响应不为空
        if (!TextUtils.isEmpty(response)) {
            try {

                //利用JSON数组存储返回的JSON数据
                JSONArray allCounties = new JSONArray(response);

                //遍历JSON数组
                for (int i = 0; i < allCounties.length(); i++) {

                    //获得第i个JSON对象
                    JSONObject countyObject = allCounties.getJSONObject(i);

                    //封装到实体类中
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);

                    //将数据存储到数据库
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {

            //通过JSONObject和JSONArray将天气数据中的主体内容解析出来
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");

            //转换成字符串形式
            String weatherContent = jsonArray.getJSONObject(0).toString();

            /*
               可以直接调用Gson的fromJson将JSON数据转换成Weather对象
               这就是为什么不使用JSONObject解析JSON数据而使用Gson解析的原因！
             */
            return new Gson().fromJson(weatherContent, Weather.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
