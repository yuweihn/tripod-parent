package org.assist4j.data.cache.redis;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import redis.clients.jedis.HostAndPort;


/**
 * @author wei
 */
public class JedisClusterFactory implements FactoryBean<BinaryJedisCluster> {
	private List<HostAndPort> redisNodeList;
	private BinaryJedisCluster binaryJedisCluster;
	private int timeout;
	private int maxRedirections;
	private GenericObjectPoolConfig jedisPoolConfig;




	@Override
	public BinaryJedisCluster getObject() throws Exception {
		return binaryJedisCluster;
	}

	@Override
	public Class<? extends BinaryJedisCluster> getObjectType() {
		return this.binaryJedisCluster != null ? this.binaryJedisCluster.getClass() : BinaryJedisCluster.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void init() throws Exception {
		Assert.notEmpty(redisNodeList, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
		Set<HostAndPort> nodes = new HashSet<HostAndPort>(redisNodeList);
		binaryJedisCluster = new BinaryJedisCluster(nodes, timeout, maxRedirections, jedisPoolConfig);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setRedisNodeList(List<HostAndPort> redisNodeList) {
		this.redisNodeList = redisNodeList;
	}

	public List<HostAndPort> getRedisNodeList() {
		return redisNodeList;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public GenericObjectPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(GenericObjectPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}
}
