package com.vs.video_stream.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vs.video_stream.model.MediaMetaData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Publisher {

  @Value("${app.rabbitmq.trove.direct-exchange}")
  private String troveExchange;

  private final RabbitTemplate rabbitTemplate;

  public String publish(
      MediaMetaData mediaMetaData,
      String routingKey) {

    rabbitTemplate.convertAndSend(
        troveExchange,
        routingKey,
        mediaMetaData);
    return "success";
  }

}
