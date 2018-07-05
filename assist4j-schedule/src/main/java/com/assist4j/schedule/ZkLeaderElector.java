package com.assist4j.schedule;


import java.io.IOException;
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
public class ZkLeaderElector extends AbstractLeaderElector {
	private static final Logger log = LoggerFactory.getLogger(ZkLeaderElector.class);
	/**
	 * root node
	 */
	private static final String EPHEMERAL_ROOT_NODE = "/Schedule_leader_";


	/**
	 * zookeeper connection string
	 */
	private String zkConn;

	/**
	 * zookeeper session timeout in milliseconds
	 */
	private int zkTimeout;

	/**
	 * task type flag
	 */
	private String projectNo;

	/**
	 * zookeeper ephemeral reference
	 */
	private String path;

	/**
	 * create ephemeral node flag
	 */
	private boolean registered = false;

	private ZooKeeper zkClient = null;

	public ZkLeaderElector(String zkConn, int zkTimeout, String projectNo) {
		this.zkConn = zkConn;
		this.zkTimeout = zkTimeout;
		this.projectNo = EPHEMERAL_ROOT_NODE + "/" + projectNo;
	}

	@Override
	public void init() {
		String thisNodeName = getNodeName();

		if (registered) {
			return;
		}
		try {
			initPath();

			//一旦创建了子节点，path的值已经改变，后面会自动加上序列号。
			this.path = getZkClient().create(projectNo + "/", thisNodeName.getBytes()
					, Collections.singletonList(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE))
					, CreateMode.EPHEMERAL_SEQUENTIAL);
			registered = true;
		} catch (Exception e) {
			destroy();
			log.error("Exception occurred when try to create EPHEMERAL_SEQUENTIAL znode.", e);
			throw new RuntimeException(projectNo + "/" + thisNodeName + "register failed", e);
		}
	}

	/**
	 * 初始化zk目录结构
	 */
	private void initPath() throws KeeperException, InterruptedException {
		if (getZkClient().exists(EPHEMERAL_ROOT_NODE, true) == null) {
			getZkClient().create(EPHEMERAL_ROOT_NODE, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// create persistent parent node
		}

		if (getZkClient().exists(projectNo, true) == null) {
			getZkClient().create(projectNo, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// create persistent parent next node
		}
	}

	@Override
	public synchronized boolean isLeader() {
		if (!registered) {
			init();
		}
		List<String> children = null;
		try {
			children = getZkClient().getChildren(projectNo, false);
		} catch (Exception e) {
			log.error("2.1 Exception occurred when try to get children of /Schedule_leader_", e);
			try {
				destroy();
			} catch (Exception e1) {
				log.error("2.2 Exception occurred when close zkClient /Schedule_leader_", e1);
			}
		}

		if (children == null || children.size() <= 0) {
			log.error(projectNo + " have no children");
			return false;
		}

		Collections.sort(children);
		String smallestChild = projectNo + "/" + children.get(0);
		log.info("My node: " + path + ", Leader node: " + smallestChild);

		return path.equals(smallestChild);
	}

	@Override
	public void destroy() {
		this.registered = false;
		this.path = null;

		if (zkClient != null) {
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
		if (zkClient == null) {
			synchronized (ZkLeaderElector.class) {
				if (zkClient == null) {
					try {
						zkClient = new ZooKeeper(zkConn, zkTimeout, new Watcher() {
							@Override
							public void process(WatchedEvent event) {
								log.info("event occ, here, " + event);
							}
						});
					} catch (IOException e) {
						log.warn("connect to zk err, " + zkConn + ", timeout: " + zkTimeout, e);
						throw new RuntimeException("connect to zookeeper error, " + zkConn, e);
					}
				}
			}
		}

		return zkClient;
	}

	/**
	 * 取当前leader的nodeName
	 */
	@Override
	public String getLeaderNodeName() {
		byte[] vals = null;
		try {
			vals = getZkClient().getData(path, false, null);
		} catch (Exception e) {
			log.warn("get " + path + " error, ", e);
		}

		String resStr = "";
		if (vals != null) {
			resStr = new String(vals);
		}
		return resStr;
	}
}
