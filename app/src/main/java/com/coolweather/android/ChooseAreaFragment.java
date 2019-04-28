package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
  碎片 迷你活动 可作为活动界面的组成部分
 */
public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    //用于后台区分用户点击的是省还是市还是县的信息
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //信息加载进度对话框
    private ProgressDialog progressDialog;

    //标题文本控件
    private TextView titleText;

    //返回按钮
    private Button backButton;

    //列表数组
    private ListView listView;

    //ListView的数组适配器
    private ArrayAdapter<String> adapter;

    //用于存放将要展示给用户的数据
    private List<String> dataList = new ArrayList<>();

   //省列表
    private List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private List<County> countyList;

    //用户选中的省份
    private Province selectedProvince;

    //用户选中的市
    private City selectedCity;

    //用户当前选中的级别
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //获取控件实例
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        //去初始化了ArrayAdapter，将其设置为ListView的适配器
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        //调用上述初始化代码
        super.onActivityCreated(savedInstanceState);

        //为ListView设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //点击了某个省
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();

                //点击了某个市
                }else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();

                //点击了某个县
                } else if (currentLevel == LEVEL_COUNTY) {

                    //得到某个县的天气信息id
                    String weatherId = countyList.get(position).getWeatherId();

                    //instanceof关键字：判断一个对象是不是某个类的实例
                    //当前活动是主活动
                    if (getActivity() instanceof MainActivity) {

                        //启动WeatherActivity
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);

                        //将当前最低行政级别对应的天气信息id传进去
                        intent.putExtra("weather_id", weatherId);

                        //启动
                        startActivity(intent);
                        //结束
                        getActivity().finish();

                    //当前活动以及是天气活动了
                    } else if (getActivity() instanceof WeatherActivity) {

                        //得到当前活动实体
                        WeatherActivity activity = (WeatherActivity) getActivity();

                        //关闭滑动菜单
                        activity.drawerLayout.closeDrawers();

                        //显示下拉刷新进度条
                        activity.swipeRefresh.setRefreshing(true);

                        //请求天气信息
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        //为Button设置点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        //加载省级数据
        queryProvinces();
    }

    /*
      查询全国所有的省的信息，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {

        //先将头部标题设置为中国
        titleText.setText("中国");

        //隐藏返回按钮
        backButton.setVisibility(View.GONE);

        //从数据库中读取省级数据
        provinceList = DataSupport.findAll(Province.class);

        //数据库中存在省级数据
        if (provinceList.size() > 0) {

            //将展示列表清空
            dataList.clear();

            //将从数据库得到的省级数据存放到展示列表中
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }

            /*
            ArrayAdepter的notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时
            需要强制的调用getView来刷新每个Item的内容
             */
            adapter.notifyDataSetChanged();

            //将第0个Item显示在最上面，也就是头部显示的是第一个Item
            listView.setSelection(0);

            //当前用户浏览的页面级别变为省级
            currentLevel = LEVEL_PROVINCE;

        //数据库中不存在省级数据，则访问服务器获取省级数据
        } else {

            //服务器地址
            String address = "http://guolin.tech/api/china";

            //获取省级数据
            queryFromServer(address, "province");
        }
    }

   /*
    查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
    */
    private void queryCities() {

        //将标题名称设置为这些市所属省的名称
        titleText.setText(selectedProvince.getProvinceName());

        //显示返回按钮
        backButton.setVisibility(View.VISIBLE);

        //通过所属省的id从数据库中查找市的数据
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);

        //数据库中存在该省的市的数据
        if (cityList.size() > 0) {

            //将展示列表中的数据清空
            dataList.clear();

            //将从数据库获得的数据放到展示列表中
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }

            //当适配器内容改变时，刷新页面
            adapter.notifyDataSetChanged();

            //从第一个开始显示
            listView.setSelection(0);

            //当前页面所属级别为市级
            currentLevel = LEVEL_CITY;

        //数据库不存在该省的市的数据，需要从服务器获取该省的市的数据
        } else {

            //获得省份的代号
            int provinceCode = selectedProvince.getProvinceCode();

            //通过字符串拼接获得该省的所有市的数据的链接
            String address = "http://guolin.tech/api/china/" + provinceCode;

            //从服务器查询当前省的所有市的数据
            queryFromServer(address, "city");
        }
    }


    /*
    查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {

        //将页面标题设置为当前市的名称
        titleText.setText(selectedCity.getCityName());

        //返回按钮设置为可见
        backButton.setVisibility(View.VISIBLE);

        //通过当前市的代号 从数据库查询当前市的使用县的数据信息
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);

        //数据库中存在需要查找的数据
        if (countyList.size() > 0) {

            //将展示列表清空
            dataList.clear();

            //将从数据库获得的数据放入展示列表中
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }

            //适配器数据内容有变时自动刷新页面
            adapter.notifyDataSetChanged();

            //用户从第一个Item浏览起
            listView.setSelection(0);

            //当前用户浏览页面的级别设置为县级
            currentLevel = LEVEL_COUNTY;

        //没有从数据库获得需要的数据
        } else {

            //获得当前市所属的省份的代号
            int provinceCode = selectedProvince.getProvinceCode();

            //获得当前市的代号
            int cityCode = selectedCity.getCityCode();

            //拼接成当前市所有县的数据的链接地址
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;

            //访问服务器，获取当前市所有县的数据
            queryFromServer(address, "county");
        }
    }

    /*
    根据传入的地址和类型从服务器上查询省，市，县数据。
     */
    private void queryFromServer(String address, final String type) {

        //显示进度对话框
        showProgressDialog();

        //向服务器发起请求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //将返回的信息转换成字符串格式
                String responseText = response.body().string();

                //（请求数据，写到数据库）的结果
                boolean result = false;

                //请求的是省级数据
                if ("province".equals(type)) {

                    //调用工具类，请求省级数据并将省级数据写回数据库
                    result = Utility.handleProvinceResponse(responseText);

                //请求的是市级数据
                } else if ("city".equals(type)) {

                    //调用工具类，请求市级数据并将市级数据写回数据库
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());

                //请求的是县级数据
                } else if ("county".equals(type)) {

                    //调用工具类，请求县级数据并将县级数据写回数据库
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                //请求数据和写回数据成功了
                if (result) {

                    //从请求数据线程回到主线程处理逻辑
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //关闭进度对话框
                            closeProgressDialog();

                            //当前页面为省
                            if ("province".equals(type)) {

                                //查询省级数据
                                queryProvinces();

                            //当前页面为市
                            } else if ("city".equals(type)) {

                                //查询市级数据
                                queryCities();

                            //当前页面为县
                            } else if ("county".equals(type)) {

                                //查询县级数据
                                queryCounties();
                            }
                        }
                    });
                }
            }

            //服务器没有返回数据
            @Override
            public void onFailure(Call call, IOException e) {

                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override

                    public void run() {

                        //关闭进度对话框
                        closeProgressDialog();

                        //弹出加载失败对话框
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /*
    显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    /*
    关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
