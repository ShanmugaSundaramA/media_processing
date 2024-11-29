package com.vs.video_stream.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.vs.video_stream.repository.primary",
    mongoTemplateRef = "primaryMongoTemplate")
public class PrimaryMongoConfig {

  @Primary
  @Bean(name = "primaryMongoDatabaseFactory")
  public MongoDatabaseFactory primaryMongoDatabaseFactory(
      @Value("${spring.data.mongodb.primary.uri}") String uri) {
    return new SimpleMongoClientDatabaseFactory(uri);
  }

  @Primary
  @Bean(name = "primaryMongoTemplate")
  public MongoTemplate primaryMongoTemplate(
      @Qualifier("primaryMongoDatabaseFactory") MongoDatabaseFactory mongoDatabaseFactory) {
    return new MongoTemplate(mongoDatabaseFactory);
  }
}
