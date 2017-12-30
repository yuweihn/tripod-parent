package com.assist4j.core;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;


/**
 * @author yuwei
 */
public class IdWorker {
	private static final long twepoch = 1288834974657L;
	/**
	 * 机器标识位数
	 */
	private static final long workerIdBits = 5L;
	/**
	 * 数据中心标识位数
	 */
	private static final long dataCenterIdBits = 5L;
	/**
	 * 机器ID最大值
	 */
//	private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	/**
	 * 数据中心ID最大值
	 */
//	private static final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
	/**
	 * 毫秒内自增位
	 */
	private static final long sequenceBits = 12L;
	/**
	 * 机器ID左移位数
	 */
	private static final long workerIdShift = sequenceBits;
	/**
	 * 数据中心ID左移位数
	 */
	private static final long dataCenterIdShift = sequenceBits + workerIdBits;
	/**
	 * 时间毫秒左移位数
	 */
	private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
	private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long workerId;
	private long dataCenterId;
	private long sequence = 0L;
	private long lastTimestamp = -1L;


	private IdWorker() {
		this.workerId = getMachineId();
		this.dataCenterId = getMachineId();
	}

	public static IdWorker create() {
		return new IdWorker();
	}


	public static String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		return getRandomString(base, length);
	}
	public static String getRandomString(String base, int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			builder.append(base.charAt(number));
		}
		return builder.toString();
	}


	/**
	 * 18位
	 */
	public synchronized long nextId() {
		//时间戳
		long timestamp = System.currentTimeMillis();
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}
		if (timestamp == lastTimestamp) {
			//当前毫秒内，则+1
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				//当前毫秒内计数满了，则等待下一秒
				timestamp = tillNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}
		lastTimestamp = timestamp;

		//ID偏移组合生成最终的ID，并返回ID
		return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
	}

	private long tillNextMillis(long lastTimestamp) {
		long timestamp = System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}

	/**
	 * 获取机器id
	 */
	private static long getMachineId() {
		long machineId = 0;
		try {
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
					continue;
				}
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				if (addresses.hasMoreElements()) {
					String ipAddress = addresses.nextElement().getHostAddress();
					machineId = Math.abs(ipAddress.hashCode() % 32);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return machineId;
	}
}
