package com.assist4j.schedule;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


/**
 * @author yuwei
 */
public class LeaderElector {
	private static final Logger log = LoggerFactory.getLogger(LeaderElector.class);

	/**
	 * zookeeper connection string
	 */
	private String zkConnectionString;

	/**
	 * zookeeper session timeout
	 */
	private int zkSessionTimeout;

	/**
	 * task type flag
	 */
	private String ephemeralZnodeNamePrefix;

	/**
	 * zookeeper root node
	 */
	private static final String ephemeralZnodeParent = "/JOBKEEPER_LEADER_";

	/**
	 * zookeeper ephemeral reference
	 */
	private String path;

	/**
	 * task node flag
	 */
	private String nodeTag;

	/**
	 * create ephemeral node flag
	 */
	private boolean registered = false;

	private ZooKeeper zkclient = null;

	public LeaderElector(String zkConnectionString, int zkSessionTimeout, String ephemeralZnodeNamePrefix) {
		this.zkConnectionString = zkConnectionString;
		this.zkSessionTimeout = zkSessionTimeout;
		this.ephemeralZnodeNamePrefix = ephemeralZnodeNamePrefix;
	}

	public void register() {
		//如果nodeTag为空则取本地网卡作为nodeTag,
		if (null == nodeTag || "".equals(nodeTag.trim())) {
			this.nodeTag = getLocalInnerIP();
		}

		if (registered) {
			return;
		}
		try {
			initPath();

			//一旦创建了子节点，path的值已经改变，后面会自动加上序列号。
			this.path = getZkClient().create(getSecondPath() + "/", nodeTag.getBytes(),
					Collections.singletonList(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE)),
					CreateMode.EPHEMERAL_SEQUENTIAL);
			registered = true;
		} catch (Exception e) {
			try {
				destroy();
			} catch (InterruptedException e1) {
				log.error("destroy error", e1);
			}
			log.error("Exception occurred when try to create EPHEMERAL_SEQUENTIAL znode.", e);
			throw new RuntimeException(getSecondPath() + "/" + this.nodeTag + "register failed", e);
		}
	}

	/**
	 * 初始化zk目录结构
	 */
	private void initPath() throws KeeperException, InterruptedException {
		if (getZkClient().exists(ephemeralZnodeParent, true) == null) {
			getZkClient().create(ephemeralZnodeParent, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// create persistent parent node
		}

		if (getZkClient().exists(getSecondPath(), true) == null) {
			getZkClient().create(getSecondPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// create persistent parent next node
		}
	}

	/**
	 * 获取本机内网IP
	 */
	private static String getLocalInnerIP() {
		String reqIp = null;
		try {
			reqIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return reqIp;
	}

	public synchronized boolean isLeader() {
		if (!registered) {
			register();
		}
		List<String> children = null;
		try {
			children = getZkClient().getChildren(getSecondPath(), false);
		} catch (Exception e) {
			log.error("2.1 Exception occurred when try to get children of /JOBKEEPER_LEADER_", e);
			try {
				destroy();
			} catch (Exception e1) {
				log.error("2.2 Exception occurred when close zkclient /JOBKEEPER_LEADER_", e1);
			}
		}

		if (CollectionUtils.isEmpty(children)) {
			log.error(getSecondPath() + "have no children");
			return false;
		}

		Collections.sort(children);
		String smallestChild = getSecondPath() + "/" + children.get(0);
		log.info("My node:" + path + ", Leader node:" + smallestChild);

		if (path.equals(smallestChild)) {
			return true;
		}

		return false;
	}

	public void destroy() throws InterruptedException {
		this.registered = false;
		this.path = null;

		if (null != zkclient) {
			zkclient.close();
			zkclient = null;
		}
	}

	public String getEphemeralZnodeNamePrefix() {
		return ephemeralZnodeNamePrefix;
	}

	public void setEphemeralZnodeNamePrefix(String ephemeralZnodeNamePrefix) {
		this.ephemeralZnodeNamePrefix = ephemeralZnodeNamePrefix;
	}

	public String getEphemeralZnodeParent() {
		return ephemeralZnodeParent;
	}

	/*
	 * public void setEphemeralZnodeParent(String ephemeralZnodeParent) {
	 * this.ephemeralZnodeParent = ephemeralZnodeParent; }
	 */

	public String getPath() {
		return path;
	}

	/**
	 * 创建zookeeper客户端，单例
	 */
	private ZooKeeper getZkClient() {
		if (null == zkclient) {
			synchronized (LeaderElector.class) {
				if (null == zkclient) {
					try {
						zkclient = new ZooKeeper(zkConnectionString, zkSessionTimeout, new Watcher() {
							@Override
							public void process(WatchedEvent event) {
								log.info("event occ,here," + event);
							}
						});
					} catch (IOException e) {
						log.warn("connect to zk err, " + zkConnectionString + ",timeout:" + zkSessionTimeout, e);
						throw new RuntimeException("connect to zookeeper error," + zkConnectionString, e);
					}
				}
			}
		}

		return zkclient;
	}

	public String getZkConnectionString() {
		return zkConnectionString;
	}

	public void setZkConnectionString(String zkConnectionString) {
		this.zkConnectionString = zkConnectionString;
	}

	public int getZkSessionTimeout() {
		return zkSessionTimeout;
	}

	public void setZkSessionTimeout(int zkSessionTimeout) {
		this.zkSessionTimeout = zkSessionTimeout;
	}

	public String getNodeTag() {
		return nodeTag;
	}

	public void setNodeTag(String nodeTag) {
		this.nodeTag = nodeTag;
	}

	public boolean isRegistered() {
		return registered;
	}

	/**
	 * 取当前leader的nodeTag
	 *
	 * @return
	 */
	public String getLeaderNodeTag() {
		byte[] vals = null;
		try {
			vals = getZkClient().getData(path, false, null);
		} catch (Exception e) {
			log.warn("get " + path + " error,", e);
		}

		String resStr = "";
		if (null != vals) {
			resStr = new String(vals);
		}
		return resStr;
	}

	/**
	 * create task node path
	 */
	private String getSecondPath() {
		return ephemeralZnodeParent + "/" + ephemeralZnodeNamePrefix;
	}
}
