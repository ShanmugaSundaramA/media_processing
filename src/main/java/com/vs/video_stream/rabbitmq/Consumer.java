package com.vs.video_stream.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.vs.video_stream.model.MediaMetaData;
import com.vs.video_stream.service.MediaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Consumer {

  private final MediaService mediaService;

  @RabbitListener(queues = "trove_video_processing_queue", containerFactory = "rabbitListenerContainerFactory")
  public void troveConsumer(
      MediaMetaData message,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

    log.info("trove_video_processing_queue : {}", message);

    try {

      mediaService.processMedia(message);
      channel.basicAck(tag, false);
    } catch (Exception e) {

      log.error("Unexpected error processing message: {}. Error: {}", message, e.getMessage());
      channel.basicNack(tag, false, false);
    }
  }

}
