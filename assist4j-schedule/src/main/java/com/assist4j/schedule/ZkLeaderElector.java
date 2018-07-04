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


/**
 * @author yuwei
 */
public class ZkLeaderElector implements LeaderElector {
	private static final Logger log = LoggerFactory.getLogger(ZkLeaderElector.class);
	/**
	 * root node
	 */
	private static final String EPHEMERAL_ROOT_NODE = "/Schedule_leader_";


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
	private String ephemeralBodeNamePrefix;

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

	private ZooKeeper zkClient = null;

	public ZkLeaderElector(String zkConnectionString, int zkSessionTimeout, String ephemeralBodeNamePrefix) {
		this.zkConnectionString = zkConnectionString;
		this.zkSessionTimeout = zkSessionTimeout;
		this.ephemeralBodeNamePrefix = ephemeralBodeNamePrefix;
	}

	public void init() {
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
			this.path = getZkClient().create(getSecondPath() + "/", nodeTag.getBytes()
					, Collections.singletonList(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE))
					, CreateMode.EPHEMERAL_SEQUENTIAL);
			registered = true;
		} catch (Exception e) {
			destroy();
			log.error("Exception occurred when try to create EPHEMERAL_SEQUENTIAL znode.", e);
			throw new RuntimeException(getSecondPath() + "/" + this.nodeTag + "register failed", e);
		}
	}

	/**
	 * 初始化zk目录结构
	 */
	private void initPath() throws KeeperException, InterruptedException {
		if (getZkClient().exists(EPHEMERAL_ROOT_NODE, true) == null) {
			getZkClient().create(EPHEMERAL_ROOT_NODE, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// create persistent parent node
		}

		if (getZkClient().exists(getSecondPath(), true) == null) {
			getZkClient().create(getSecondPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// create persistent parent next node
		}
	}

	/**
	 * 获取本机内网IP
	 */
	private static String getLocalInnerIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized boolean isLeader() {
		if (!registered) {
			init();
		}
		List<String> children = null;
		try {
			children = getZkClient().getChildren(getSecondPath(), false);
		} catch (Exception e) {
			log.error("2.1 Exception occurred when try to get children of /Schedule_leader_", e);
			try {
				destroy();
			} catch (Exception e1) {
				log.error("2.2 Exception occurred when close zkClient /Schedule_leader_", e1);
			}
		}

		if (children == null || children.size() <= 0) {
			log.error(getSecondPath() + " have no children");
			return false;
		}

		Collections.sort(children);
		String smallestChild = getSecondPath() + "/" + children.get(0);
		log.info("My node: " + path + ", Leader node: " + smallestChild);

		return path.equals(smallestChild);
	}

	public void destroy() {
		this.registered = false;
		this.path = null;

		if (null != zkClient) {
			try {
				zkClient.close();
			} catch (InterruptedException e) {
				log.error("", e);
			}
			zkClient = null;
		}
	}

	/**
	 * 创建zookeeper客户端，单例
	 */
	private ZooKeeper getZkClient() {
		if (null == zkClient) {
			synchronized (ZkLeaderElector.class) {
				if (null == zkClient) {
					try {
						zkClient = new ZooKeeper(zkConnectionString, zkSessionTimeout, new Watcher() {
							@Override
							public void process(WatchedEvent event) {
								log.info("event occ, here, " + event);
							}
						});
					} catch (IOException e) {
						log.warn("connect to zk err, " + zkConnectionString + ", timeout: " + zkSessionTimeout, e);
						throw new RuntimeException("connect to zookeeper error, " + zkConnectionString, e);
					}
				}
			}
		}

		return zkClient;
	}

	public String getNodeTag() {
		return nodeTag;
	}

	public void setNodeTag(String nodeTag) {
		this.nodeTag = nodeTag;
	}

	/**
	 * 取当前leader的nodeTag
	 */
	public String getLeaderNodeTag() {
		byte[] vals = null;
		try {
			vals = getZkClient().getData(path, false, null);
		} catch (Exception e) {
			log.warn("get " + path + " error, ", e);
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
		return EPHEMERAL_ROOT_NODE + "/" + ephemeralBodeNamePrefix;
	}
}
