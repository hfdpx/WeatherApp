package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
  活动的任务：请求天气数据，以及将数据展示到界面上
 */
public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    //天气刷新
    public SwipeRefreshLayout swipeRefresh;

    //下滑框
    private ScrollView weatherLayout;

    //返回按钮：打开滑动菜单
    private Button navButton;

    //主页面相关控件
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    //必应图片充当背景图片
    private ImageView bingPicImg;

    //天气id
    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //以下代码目的：将背景图和状态栏融合在一起
        //系统版本号大于21才支持如下功能
        if (Build.VERSION.SDK_INT >= 21) {

            //拿到当前活动的DecorView
            View decorView = getWindow().getDecorView();

            //传入参数改变IU显示：活动的布局会显示在状态栏的上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            //将状态栏设置为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //设置天气页面总布局
        setContentView(R.layout.activity_weather);

        // 初始化各控件，获取控件的实例
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条的颜色
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);


        //轻量级辅助存储类
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //从缓存中读取天气数据
        String weatherString = prefs.getString("weather", null);


        //缓存中存在对应的数据
        if (weatherString != null) {

            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);

            //获得当前城市对应的天气id
            mWeatherId = weather.basic.weatherId;

            //显示Weather实体类中的数据
            showWeatherInfo(weather);

        //缓存中不存在对应数据
        } else {

            // 无缓存时去服务器查询天气

            //根据当前最低行政级别所对应的天气id去查询天气的具体信息
            mWeatherId = getIntent().getStringExtra("weather_id");

            //请求数据时将ScrollView进行隐藏
            weatherLayout.setVisibility(View.INVISIBLE);

            //通过当前最低行政级别对应的天气id去和风天气服务器查询当前县天气的具体信息
            requestWeather(mWeatherId);
        }

        //下拉刷新监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //请求天气信息
                requestWeather(mWeatherId);
            }
        });

        //弹出菜单按钮监听器
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //监测到按钮被点击
            public void onClick(View v) {
                //划出菜单界面
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //从缓存中读取背景图片
        String bingPic = prefs.getString("bing_pic", null);

        //缓存中存在背景图片
        if (bingPic != null) {

            //直接加载
            Glide.with(this).load(bingPic).into(bingPicImg);
        //缓存中不存在背景图片
        } else {
            //向必应网站请求背景图片
            loadBingPic();
        }
    }

    /*
    根据当前最低行政级别的天气id请求城市具体天气信息。
     */
    public void requestWeather(final String weatherId) {

        //根据天气id和申请的APIkey以及在和风天气网站留下的链接拼接成一个具体的接口地址
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

        //调用工具类发生Http请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //回复的内容进行字符串化处理
                final String responseText = response.body().string();

                //将返回的JSON数据解析成Weather实体类
                final Weather weather = Utility.handleWeatherResponse(responseText);

                //运行一个线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //天气实体类不为空并且天气实体的状态码为ok
                        if (weather != null && "ok".equals(weather.status)) {

                            //将数据缓存到SharedPreferences中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            //提交修改的数据 原子操作，异步，因为不一定要保证提交修改成功而且没有后续操作，没有可以不用使用commit
                            editor.apply();

                            //从得到的天气信息中得到天气信息的id值
                            mWeatherId = weather.basic.weatherId;

                            //展示得到的天气数据
                            showWeatherInfo(weather);

                        //天气实体类为空或者天气实体的状态码不为ok
                        } else {

                            //弹出提示信息
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        //表示刷新事件结束
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            //网络请求失败的回调函数
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                //回到当前线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //向用户显示请求失败的信息
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        //表示刷新事件结束
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //加载背景图片
        loadBingPic();
    }

    /*
      加载必应每日一图
     */
    private void loadBingPic() {

        //必应每日一图的接口
        String requestBingPic = "http://guolin.tech/api/bing_pic";

        //向接口发送请求，请求图片的链接
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //获得图片的链接
                final String bingPic = response.body().string();

                //将图片链接加载到缓存中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                //提交数据
                editor.apply();

                //切换到当前线程加载图片
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

   /*
     处理并展示Weather实体类中的数据。
    */
    private void showWeatherInfo(Weather weather) {

        //从实体类中取出数据
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        //将对应的数据设置到页面上
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //移除子视图，避免异常，只有这样才能实现联动效果，否则会滚动报错
        forecastLayout.removeAllViews();

        //今天天气和未来几天的天气也加到界面中去
        for (Forecast forecast : weather.forecastList) {

            //定义一个控件基类并且在布局中找到对应的位置
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            //在布局中找到对应的控件
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            //控件文本设置
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            //将这一天的天气信息的基类加入到父页面中去
            forecastLayout.addView(view);
        }

        //空气质量指数不为空
        if (weather.aqi != null) {

            //设置空气质量指数和PM2.5指数
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        //从Weather实体类中获取数据
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;

        //设置数据
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        //ScrollView重新设置为可见
        weatherLayout.setVisibility(View.VISIBLE);


        //启动数据自动更新服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
