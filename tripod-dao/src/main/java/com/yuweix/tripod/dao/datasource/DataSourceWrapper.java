package com.yuweix.tripod.dao.datasource;


import javax.sql.DataSource;
import java.util.List;


/**
 *
 {
     "logicList": [{
         "logicName": "gateway",
         "shardList": [{
             "index": 0,
             "dataSource": "@@######"
         }, {
             "index": 1,
             "dataSource": "@@######"
         }]
     }]
 }
 *
 * @author yuwei
 */
public class DataSourceWrapper {
    private List<LogicDatabase> logicList;

    private static class LogicDatabase {
        private String logicName;
        private List<DatabaseShard> shardList;

        private static class DatabaseShard {
            private int index;
            private DataSource dataSource;
        }
    }
}
