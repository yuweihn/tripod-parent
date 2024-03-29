package com.yuweix.tripod.dao.datasource;


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class TargetDataSource {
    private String logicName;
    /**
     * 逻辑库后占位符长度
     * eg.
     * user  ====>>>>  user_0000
     * @return   逻辑库后占位符长度
     */
    private int suffixLength = 4;

    private List<DataSource> dataSourceList;


    public TargetDataSource setLogicName(String logicName) {
        this.logicName = logicName;
        return this;
    }

    public TargetDataSource setSuffixLength(int suffixLength) {
        this.suffixLength = suffixLength;
        return this;
    }

    public TargetDataSource addDataList(DataSource dataSource) {
        if (this.dataSourceList == null) {
            this.dataSourceList = new ArrayList<>();
        }
        this.dataSourceList.add(dataSource);
        return this;
    }

    private String getKey(int index) {
        return logicName + "_" + String.format("%0" + suffixLength + "d", index);
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
