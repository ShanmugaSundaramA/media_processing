package com.vs.video_stream.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Value("${spring.swagger.uri}")
  private String uri;

  @Bean
  OpenAPI customOpenAPI() {

    License mitLicense = new License()
        .name("Apache 2.0")
        .url("http://springdoc.org");
    Info info = new Info()
        .title("Trove Media Processing Service")
        .version("1.0")
        .description("Trove Media Streaming end points.")
        .termsOfService("http://swagger.io/terms/")
        .license(mitLicense);
    return new OpenAPI()
        .info(info)
        .addServersItem(new Server().url(uri).description(null));
  }
}