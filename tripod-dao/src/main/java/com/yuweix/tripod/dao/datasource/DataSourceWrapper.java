package com.yuweix.tripod.dao.datasource;


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class DataSourceWrapper {
    private String logicName;

    private List<DataSource> dataSourceList;


    public DataSourceWrapper(String logicName) {
        this.logicName = logicName;
    }

    public DataSourceWrapper setDataSourceList(List<DataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
        return this;
    }

    public DataSourceWrapper addDataList(DataSource dataSource) {
        if (this.dataSourceList == null) {
            this.dataSourceList = new ArrayList<>();
        }
        this.dataSourceList.add(dataSource);
        return this;
    }

    private String getKey(int index) {
        return logicName + "_" + String.format("%0" + suffixLength + "d", index);
    }

    private static class LogicDatabase {

    }

    public Map<Object, Object> getTargetDataSourcesMap() {
        Map<Object, Object> map = new HashMap<>();
        if (dataSourceList == null || dataSourceList.isEmpty()) {
            return map;
        }
        if (dataSourceList.size() == 1) {
            map.put(logicName, dataSourceList.get(0));
        } else {
            for (int i = 0, sz = dataSourceList.size(); i < sz; i++) {
                map.put(this.getKey(i), dataSourceList.get(i));
            }
        }
        return map;
    }
}
