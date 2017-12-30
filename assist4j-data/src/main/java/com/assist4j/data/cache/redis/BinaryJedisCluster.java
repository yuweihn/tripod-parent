package com.assist4j.data.cache.redis;


import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterCommand;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


/**
 * @author wei
 */
public class BinaryJedisCluster extends JedisCluster {
	private static final int DEFAULT_TIMEOUT = 2000;
	private static final int DEFAULT_MAX_REDIRECTIONS = 5;

	private int maxRedirections;
	private JedisClusterConnectionHandler connectionHandler;




	public BinaryJedisCluster(Set<HostAndPort> nodes) {
		this(nodes, DEFAULT_TIMEOUT);
	}

	public BinaryJedisCluster(Set<HostAndPort> nodes, int timeout) {
		this(nodes, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS);
	}

	public BinaryJedisCluster(Set<HostAndPort> nodes, int timeout, int maxRedirections) {
		this(nodes, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, new GenericObjectPoolConfig());
	}

	public BinaryJedisCluster(Set<HostAndPort> nodes, int timeout, int maxRedirections, GenericObjectPoolConfig poolConfig) {
		super(nodes, timeout, maxRedirections, poolConfig);
		this.maxRedirections = maxRedirections;
		this.connectionHandler = new JedisSlotBasedConnectionHandler(nodes, poolConfig, timeout);
	}

	public String set(final String key, final byte[] value) {
		return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
			public String execute(Jedis connection) {
				return connection.set(SafeEncoder.encode(key), value);
			}
		}.run(key);
	}

	public Long publish(final String key, final String value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			@Override
			public Long execute(Jedis connection) {
				return connection.publish(SafeEncoder.encode(key), SafeEncoder.encode(value));
			}
		}.run(key);
	}

	public void subscribe(final JedisPubSub jedisPubSub, final String channel) {
		new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			@Override
			public Long execute(Jedis connection) {
				connection.subscribe(jedisPubSub, channel);
				return 0L;
			}
		}.run(channel);
	}

	public byte[] getBytes(final String key) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.get(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Boolean setbit(final String key, final long offset, final byte[] value) {
		return new JedisClusterCommand<Boolean>(connectionHandler, maxRedirections) {
			public Boolean execute(Jedis connection) {
				return connection.setbit(SafeEncoder.encode(key), offset, value);
			}
		}.run(key);
	}

	public Long setrange(final String key, final long offset, final byte[] value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.setrange(SafeEncoder.encode(key), offset, value);
			}
		}.run(key);
	}

	public byte[] getrangeBytes(final String key, final long startOffset, final long endOffset) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.getrange(SafeEncoder.encode(key), startOffset, endOffset);
			}
		}.run(key);
	}

	public byte[] getSetBytes(final String key, final byte[] value) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.getSet(SafeEncoder.encode(key), value);
			}
		}.run(key);
	}

	public Long setnx(final String key, final byte[] value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.setnx(SafeEncoder.encode(key), value);
			}
		}.run(key);
	}

	public String setex(final String key, final int seconds, final byte[] value) {
		return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
			public String execute(Jedis connection) {
				return connection.setex(SafeEncoder.encode(key), seconds, value);
			}
		}.run(key);
	}

	public byte[] substrBytes(final String key, final int start, final int end) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.substr(SafeEncoder.encode(key), start, end);
			}
		}.run(key);
	}

	public Long hset(final String key, final String field, final byte[] value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.hset(SafeEncoder.encode(key), field.getBytes(Charset.defaultCharset()), value);
			}
		}.run(key);
	}

	public byte[] hgetBytes(final String key, final String field) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.hget(SafeEncoder.encode(key), field.getBytes(Charset.defaultCharset()));
			}
		}.run(key);
	}

	public Long hsetnx(final String key, final String field, final byte[] value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.hsetnx(SafeEncoder.encode(key), field.getBytes(Charset.defaultCharset()), value);
			}
		}.run(key);
	}

	public String hmsetBytes(final String key, final Map<byte[], byte[]> hash) {
		return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
			public String execute(Jedis connection) {
				return connection.hmset(SafeEncoder.encode(key), hash);
			}
		}.run(key);
	}

	public List<byte[]> hmget(final String key, final byte[]... fields) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			public List<byte[]> execute(Jedis connection) {
				return connection.hmget(SafeEncoder.encode(key), fields);
			}
		}.run(key);
	}

	public Set<byte[]> hkeysBytes(final String key) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.hkeys(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public List<byte[]> hvalsBytes(final String key) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			public List<byte[]> execute(Jedis connection) {
				return connection.hvals(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Map<byte[], byte[]> hgetAllBytes(final String key) {
		return new JedisClusterCommand<Map<byte[], byte[]>>(connectionHandler, maxRedirections) {
			public Map<byte[], byte[]> execute(Jedis connection) {
				return connection.hgetAll(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Long rpush(final String key, final byte[]... string) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.rpush(SafeEncoder.encode(key), string);
			}
		}.run(key);
	}

	public Long lpush(final String key, final byte[]... string) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.lpush(SafeEncoder.encode(key), string);
			}
		}.run(key);
	}

	public List<byte[]> lrangeBytes(final String key, final long start, final long end) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			public List<byte[]> execute(Jedis connection) {
				return connection.lrange(SafeEncoder.encode(key), start, end);
			}
		}.run(key);
	}

	public byte[] lindexBytes(final String key, final long index) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.lindex(SafeEncoder.encode(key), index);
			}
		}.run(key);
	}

	public String lset(final String key, final long index, final byte[] value) {
		return new JedisClusterCommand<String>(connectionHandler, maxRedirections) {
			public String execute(Jedis connection) {
				return connection.lset(SafeEncoder.encode(key), index, value);
			}
		}.run(key);
	}

	public Long lrem(final String key, final long count, final byte[] value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.lrem(SafeEncoder.encode(key), count, value);
			}
		}.run(key);
	}

	public byte[] lpopBytes(final String key) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.lpop(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public byte[] rpopBytes(final String key) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.rpop(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Long sadd(final String key, final byte[]... member) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.sadd(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public Set<byte[]> smembersBytes(final String key) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.smembers(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Long srem(final String key, final byte[]... member) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.srem(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public byte[] spopBytes(final String key) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.spop(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Boolean sismember(final String key, final byte[] member) {
		return new JedisClusterCommand<Boolean>(connectionHandler, maxRedirections) {
			public Boolean execute(Jedis connection) {
				return connection.sismember(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public byte[] srandmemberBytes(final String key) {
		return new JedisClusterCommand<byte[]>(connectionHandler, maxRedirections) {
			public byte[] execute(Jedis connection) {
				return connection.srandmember(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public Long zadd(final String key, final double score, final byte[] member) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.zadd(SafeEncoder.encode(key), score, member);
			}
		}.run(key);
	}

	public Set<byte[]> zrangeBytes(final String key, final long start, final long end) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrange(SafeEncoder.encode(key), start, end);
			}
		}.run(key);
	}

	public Long zrem(final String key, final byte[]... member) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.zrem(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public Double zincrby(final String key, final double score, final byte[] member) {
		return new JedisClusterCommand<Double>(connectionHandler, maxRedirections) {
			public Double execute(Jedis connection) {
				return connection.zincrby(SafeEncoder.encode(key), score, member);
			}
		}.run(key);
	}

	public Long zrank(final String key, final byte[] member) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.zrank(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public Long zrevrank(final String key, final byte[] member) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.zrevrank(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public Set<byte[]> zrevrangeBytes(final String key, final long start, final long end) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrevrange(SafeEncoder.encode(key), start, end);
			}
		}.run(key);
	}

	public Double zscore(final String key, final byte[] member) {
		return new JedisClusterCommand<Double>(connectionHandler, maxRedirections) {
			public Double execute(Jedis connection) {
				return connection.zscore(SafeEncoder.encode(key), member);
			}
		}.run(key);
	}

	public List<byte[]> sortBytes(final String key) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			public List<byte[]> execute(Jedis connection) {
				return connection.sort(SafeEncoder.encode(key));
			}
		}.run(key);
	}

	public List<byte[]> sortBytes(final String key, final SortingParams sortingParameters) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			public List<byte[]> execute(Jedis connection) {
				return connection.sort(SafeEncoder.encode(key), sortingParameters);
			}
		}.run(key);
	}

	public Set<byte[]> zrangeByScoreBytes(final String key, final double min, final double max) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrangeByScore(SafeEncoder.encode(key), min, max);
			}
		}.run(key);
	}

	public Set<byte[]> zrangeByScoreBytes(final String key, final String min, final String max) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max));
			}
		}.run(key);
	}

	public Set<byte[]> zrevrangeByScoreBytes(final String key, final double max, final double min) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrevrangeByScore(SafeEncoder.encode(key), max, min);
			}
		}.run(key);
	}

	public Set<byte[]> zrangeByScoreBytes(final String key, final double min, final double max, final int offset, final int count) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrangeByScore(SafeEncoder.encode(key), min, max, offset, count);
			}
		}.run(key);
	}

	public Set<byte[]> zrevrangeByScoreBytes(final String key, final String max, final String min) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrevrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(max), SafeEncoder.encode(min));
			}
		}.run(key);
	}

	public Set<byte[]> zrangeByScoreBytes(final String key, final String min, final String max, final int offset, final int count) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(min), SafeEncoder.encode(max), offset, count);
			}
		}.run(key);
	}

	public Set<byte[]> zrevrangeByScoreBytes(final String key, final double max, final double min, final int offset, final int count) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrevrangeByScore(SafeEncoder.encode(key), max, min, offset, count);
			}
		}.run(key);
	}

	public Set<byte[]> zrevrangeByScoreBytes(final String key, final String max, final String min, final int offset, final int count) {
		return new JedisClusterCommand<Set<byte[]>>(connectionHandler, maxRedirections) {
			public Set<byte[]> execute(Jedis connection) {
				return connection.zrevrangeByScore(SafeEncoder.encode(key), SafeEncoder.encode(max), SafeEncoder.encode(min), offset, count);
			}
		}.run(key);
	}

	public Long linsert(final String key, final LIST_POSITION where, final byte[] pivot, final byte[] value) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.linsert(SafeEncoder.encode(key), where, pivot, value);
			}
		}.run(key);
	}

	public Long lpushx(final String key, final byte[]... string) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.lpushx(SafeEncoder.encode(key), string);
			}
		}.run(key);
	}

	public Long rpushx(final String key, final byte[]... string) {
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections) {
			public Long execute(Jedis connection) {
				return connection.rpushx(SafeEncoder.encode(key), string);
			}
		}.run(key);
	}

	public List<byte[]> blpopBytes(final String arg) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			@SuppressWarnings("deprecation")
			public List<byte[]> execute(Jedis connection) {
				return connection.blpop(SafeEncoder.encode(arg));
			}
		}.run(null);
	}

	public List<byte[]> brpopBytes(final String arg) {
		return new JedisClusterCommand<List<byte[]>>(connectionHandler, maxRedirections) {
			@SuppressWarnings("deprecation")
			public List<byte[]> execute(Jedis connection) {
				return connection.brpop(SafeEncoder.encode(arg));
			}
		}.run(null);
	}
}
