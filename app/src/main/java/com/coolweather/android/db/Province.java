package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/*
  LitePal中每一个实体类都要继承DataSupport类
 */
public class Province extends DataSupport {

    //每个实体类都该有的字段，用于识别不同的对象
    private int id;

    //省份的名字
    private String provinceName;

    //省份的代号
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
