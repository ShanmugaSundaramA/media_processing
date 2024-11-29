package com.vs.video_stream.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.vs.video_stream.repository.auth", mongoTemplateRef = "authMongoTemplate")
public class AuthMongoConfig {

  @Bean(name = "authMongoDatabaseFactory")
  public MongoDatabaseFactory authMongoDatabaseFactory(
      @Value("${spring.data.mongodb.auth.uri}") String uri) {
    return new SimpleMongoClientDatabaseFactory(uri);
  }

  @Bean(name = "authMongoTemplate")
  public MongoTemplate authMongoTemplate(
      @Qualifier("authMongoDatabaseFactory") MongoDatabaseFactory mongoDatabaseFactory) {
    return new MongoTemplate(mongoDatabaseFactory);
  }
}
