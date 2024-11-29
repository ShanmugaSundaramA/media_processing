package com.vs.video_stream.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${app.rabbitmq.trove.direct-exchange}")
  private String troveExchange;
  @Value("${app.rabbitmq.trove.trove-queue1}")
  private String troveVideoProcessingQueue;
  @Value("${app.rabbitmq.trove.trove-routing-key1}")
  private String troveVideoProcessingRoutingKey;

  @Bean
  DirectExchange troveDirectExchange() {
    return new DirectExchange(troveExchange);
  }

  @Bean
  Queue queue() {
    return new Queue(troveVideoProcessingQueue, true);
  }

  @Bean
  Binding bindingQueueOne(Queue queue, DirectExchange directExchange) {
    return BindingBuilder.bind(queue).to(directExchange).with(troveVideoProcessingRoutingKey);
  }

  @Bean
  MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    var factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    return factory;
  }

}
