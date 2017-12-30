package org.assist4j.data.ds;


import org.assist4j.data.ds.router.Router;
import org.assist4j.data.ds.router.RoundRobinRouter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wei
 */
public class DataSourceCluster {
	private String key;
	private boolean isDefault;
	private KvPair master;
	private List<KvPair> slaveList;
	private Router router = new RoundRobinRouter(this);


	public DataSourceCluster addSlave(KvPair slave) {
		if(slaveList == null) {
			slaveList = new ArrayList<KvPair>();
		}
		slaveList.add(slave);
		return this;
	}

	//////////////////////////////////////////////////////////////////
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean aDefault) {
		isDefault = aDefault;
	}

	public KvPair getMaster() {
		return master;
	}

	public void setMaster(KvPair master) {
		this.master = master;
	}

	public List<KvPair> getSlaveList() {
		return slaveList;
	}

	public void setSlaveList(List<KvPair> slaveList) {
		this.slaveList = slaveList;
	}

	public Router getRouter() {
		return router;
	}

	public void setRouter(Router router) {
		this.router = router;
	}
}
