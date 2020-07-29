package com.assist4j.schedule;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public class ZkLeaderElector extends AbstractLeaderElector {
	private static final Logger log = LoggerFactory.getLogger(ZkLeaderElector.class);
	private static final String ZK_NODE_NAME_PRE = "/Schedule_leader_";

	private static ZooKeeper zk;


	/**
	 * zookeeper链接地址.
	 * e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
	 */
	private String zkConn;
	/**
	 * zookeeper session timeout in milliseconds
	 */
	private int zkTimeout;



	public ZkLeaderElector(String zkConn, int zkTimeout) {
		super();
		this.zkConn = zkConn;
		this.zkTimeout = zkTimeout;
	}

	private ZooKeeper getZk() {
		if (null == zk) {
			synchronized (ZkLeaderElector.class) {
				if (null == zk) {
					final CountDownLatch latch = new CountDownLatch(1);
					try {
						log.info("Connecting......");
						zk = new ZooKeeper(zkConn, zkTimeout, new Watcher() {
							@Override
							public void process(WatchedEvent event) {
								log.info("ZooKeeper event occurs here: " + event);
								if (event.getState() == Event.KeeperState.SyncConnected) {
									latch.countDown();
									log.info("Connected");
								}
							}
						});
						latch.await(5, TimeUnit.SECONDS);
					} catch (Exception e) {
						log.error("", e);
						throw new RuntimeException(e);
					}
				}
			}
		}

		if (zk == null) {
			throw new RuntimeException("Can't connect zookeeper.");
		}
		return zk;
	}

	@Override
	public boolean acquire(String lock) {
		String key = ZK_NODE_NAME_PRE + lock;
		String node = getLocalNode(key);
		String leaderNode = getLeaderNode(lock);
		if (leaderNode == null) {
			try {
				String path = getZk().create(lock, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				log.info("Create server node ({} => {})", path, node);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return node.equals(leaderNode);
	}

	@Override
	public void release(String lock) {
		String key = ZK_NODE_NAME_PRE + lock;
		if (zk == null) {
			return;
		}
		String node = getLocalNode(key);
		String leaderNode = getLeaderNode(key);
		if (node == null || !node.equals(leaderNode)) {
			return;
		}
		try {
			zk.delete(key, -1);
		} catch (InterruptedException | KeeperException e) {
			log.warn("", e);
		}
	}

	/**
	 * 获取当前leader
	 */
	@Override
	public String getLeaderNode(String lock) {
		String key = ZK_NODE_NAME_PRE + lock;
		byte[] val = null;
		try {
			Stat stat = getZk().exists(key, false);
			if (stat == null) {
				return null;
			}
			val = getZk().getData(key, false, stat);
		} catch (Exception e) {
			log.warn("get " + key + " error, ", e);
		}
		return val == null ? null : new String(val);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				log.error("", e);
			}
			zk = null;
		}
	}
}
