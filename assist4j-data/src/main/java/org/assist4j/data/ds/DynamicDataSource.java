package org.assist4j.data.ds;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	public DynamicDataSource(List<DataSourceCluster> dsClusterList) {
		super();
		Assert.notEmpty(dsClusterList, "[dsClusterList] is required.");
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		for (DataSourceCluster dsCluster : dsClusterList) {
			if(dsCluster.getIsDefault()) {
				this.setDefaultTargetDataSource(dsCluster.getMaster().getValue());
			}
			targetDataSources.put(dsCluster.getMaster().getKey(), dsCluster.getMaster().getValue());
			if(!CollectionUtils.isEmpty(dsCluster.getSlaveList())) {
				for (KvPair dsSlave : dsCluster.getSlaveList()) {
					targetDataSources.put(dsSlave.getKey(), dsSlave.getValue());
				}
			}
		}
		this.setTargetDataSources(targetDataSources);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceHolder.getDataSource();
	}
}
