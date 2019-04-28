package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/*
  LitePal中每一个实体类都要继承DataSupport类
 */
public class County extends DataSupport {

    //每个实体类都该有的字段，用于识别不同的对象
    private int id;

    //县的名字
    private String countyName;

    //县所对应的天气id
    private String weatherId;

    //县所属市的id
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

}
