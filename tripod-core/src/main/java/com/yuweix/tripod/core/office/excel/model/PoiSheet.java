package com.yuweix.tripod.core.office.excel.model;


import java.util.List;


/**
 * @author yuwei
 * @date 2024/8/31 11:19
 **/
public class PoiSheet<T> {
    private String sheetName;
    private List<T> list;


    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }
}
