package com.assist4j.data.springboot;


import com.assist4j.data.mongo.MongoClientFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class MongoConf {

	@Bean(name = "mongoSeeds")
	public List<ServerAddress> mongoSeeds(@Value("${mongo.host}") String host
			, @Value("${mongo.port}") int port) {
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();
		seeds.add(new ServerAddress(host, port));
		return seeds;
	}

	@Bean(name = "mongoCredential")
	public MongoCredential mongoCredentialList(@Value("${mongo.user.name}") String userName
			, @Value("${mongo.password}") String password
			, @Value("${mongo.auth.dbname}") String authDbName) {
		return MongoCredential.createScramSha1Credential(userName, authDbName, password.toCharArray());
	}

	@Bean(name = "mongo")
	public MongoClientFactory mongoClientFactory(@Value("${mongo.need.auth}") boolean needAuth
			, @Qualifier("mongoSeeds") List<ServerAddress> seeds
			, @Qualifier("mongoCredential") MongoCredential credential) {
		MongoClientFactory mongoClientFactory = new MongoClientFactory(needAuth, seeds, credential);
		return mongoClientFactory;
	}

	@Bean(name = "mongoDbFactory")
	public MongoDbFactory mongoDbFactory(@Value("${mongo.db}") String databaseName
			, @Qualifier("mongo") MongoClient mongoClient) {
		return new SimpleMongoDbFactory(mongoClient, databaseName);
	}

	@Bean(name = "mongoTemplate")
	public MongoTemplate mongoTemplate(@Qualifier("mongoDbFactory") MongoDbFactory mongoDbFactory) {
		return new MongoTemplate(mongoDbFactory);
	}
}
