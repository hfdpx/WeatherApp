package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/*
关于包的说明：
    db包存放的是数据模型
    gson包存放的是GSON模型相关的代码
    service包用于存放服务相关的代码
    util包用于存放工具相关的代码
 */

/*
关于使用的库的说明：
    LitePal用于对数据库进行操作
    OkHttp用于进行网络请求
    GSON用于解析JSON数据
    Glide用于加载和展示图片
 */

/*
关于数据库表结构和实体类的说明：
    province 省信息表  ---->对应 Province类
    city 市信息表      ---->对应 City类
    county 县信息表    ---->对应 county类
 */

/*
  LitePal中每一个实体类都要继承DataSupport类
 */

public class City extends DataSupport {

    //每个实体类都该有的字段，用于识别不同的对象
    private int id;

    //市的名字
    private String cityName;

    //市的代号
    private int cityCode;

    //市所属省的id值
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

}
