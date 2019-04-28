package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/*
  Http请求与处理类
 */
public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {

        //发起一条Http请求
        OkHttpClient client = new OkHttpClient();

        //传入请求地址
        Request request = new Request.Builder().url(address).build();

        //注册回调来处理服务器响应
        client.newCall(request).enqueue(callback);
    }

}
