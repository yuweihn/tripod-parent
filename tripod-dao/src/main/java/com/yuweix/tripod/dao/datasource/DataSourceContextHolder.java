package com.yuweix.tripod.dao.datasource;



/**
 * @author yuwei
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    /**
     * 获取数据源名
     */
    public static String getDataSource() {
        return HOLDER.get();
    }

    /**
     * 设置数据源名
     */
    public static void setDataSource(String dataSourceName) {
        HOLDER.set(dataSourceName);
    }

    /**
     * 移除数据源名
     */
    public  static void removeDataSource() {
        HOLDER.remove();
    }
}
