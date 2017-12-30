package org.assist4j.data.ds.router;


import org.assist4j.data.ds.KvPair;


/**
 * 数据库从库路由策略
 * @author yuwei
 */
public interface Router {
	KvPair getTargetDataSource();
}
