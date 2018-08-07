package com.assist4j.schedule;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public class ZkLeaderElector extends AbstractLeaderElector {
	private static final Logger log = LoggerFactory.getLogger(ZkLeaderElector.class);
	private static final String ZK_NODE_NAME_PRE = "/Schedule_leader_";

	private ZooKeeper zk;


	/**
	 * zookeeper链接地址.
	 * e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
	 */
	private String zkConn;
	/**
	 * zookeeper session timeout in milliseconds
	 */
	private int zkTimeout;
	/**
	 * zookeeper node name
	 */
	private String zkNodeName;



	public ZkLeaderElector(String zkConn, int zkTimeout, String prjNo) {
		this.zkConn = zkConn;
		this.zkTimeout = zkTimeout;
		this.zkNodeName = ZK_NODE_NAME_PRE + prjNo;
	}


	@Override
	public void init() {
		connectZookeeper();
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

	/**
	 * 连接zookeeper。
	 */
	private void connectZookeeper() {
		final CountDownLatch connectZookeeperLatch = new CountDownLatch(1);
		try {
			zk = new ZooKeeper(zkConn, zkTimeout, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						connectZookeeperLatch.countDown();
					}
					/**
					 * 如果断开，则重新连接
					 */
					if (event.getState() == Event.KeeperState.Disconnected) {
						log.info("Connecting......");
						connectZookeeper();
						log.info("Connected");
					}
				}
			});
			connectZookeeperLatch.await(5, TimeUnit.SECONDS);

			if (zk == null) {
				throw new RuntimeException("Can't connect zookeeper.");
			}
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	protected boolean createLeaderNode(String node) {
		try {
			String path = zk.create(zkNodeName, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			log.info("Create server node ({} => {})", path, node);
			return true;
		} catch (KeeperException | InterruptedException e) {
			log.error("", e);
			return false;
		}
	}

	/**
	 * 获取当前leader
	 */
	@Override
	public String getLeaderNode() {
		byte[] val = null;
		try {
			Stat stat = zk.exists(zkNodeName, false);
			if (stat == null) {
				return null;
			}
			val = zk.getData(zkNodeName, false, stat);
		} catch (Exception e) {
			log.error("get " + zkNodeName + " error, ", e);
		}
		return val == null ? null : new String(val);
	}
}
