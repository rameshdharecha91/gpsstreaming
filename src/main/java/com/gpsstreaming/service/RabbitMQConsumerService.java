package com.gpsstreaming.service;

import com.gpsstreaming.model.RabbitMQPublishDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumerService {

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(RabbitMQPublishDTO consumerDTO){
        System.out.println("CONSUMED RABBITMQ MESSAGE : "+consumerDTO);
    }
}