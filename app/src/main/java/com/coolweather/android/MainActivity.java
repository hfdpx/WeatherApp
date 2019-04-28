package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载布局文件
        setContentView(R.layout.activity_main);

        //从缓存中读取需要的数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //缓存中存在需要的数据
        if (prefs.getString("weather", null) != null) {

            //直接跳转到weatherActivity
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);

            //结束
            finish();
        }
    }

}