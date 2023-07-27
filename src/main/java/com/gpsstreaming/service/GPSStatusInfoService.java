package com.gpsstreaming.service;

import com.gpsstreaming.model.GPSStatusInfoEntity;
import com.gpsstreaming.model.RabbitMQPublishDTO;
import com.gpsstreaming.reposotory.GPSStatusInfoEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GPSStatusInfoService {
    @Autowired
    RabbitMQProducerService rabbitMQProducer;
    @Autowired
    GPSStatusInfoEntityRepository statusInfoEntityRepository;

    public GPSStatusInfoEntity save(GPSStatusInfoEntity statusInfoEntity){

        var publishDTO = new RabbitMQPublishDTO();
        publishDTO.setGpsStatusInfoEntity(statusInfoEntity);
        rabbitMQProducer.sendMessage(publishDTO);
        System.out.println("PUBLISHED MESSAGE TO RABBITMQ: " + publishDTO);
        System.out.println("SAVE GPS_STATUS_DATA INTO DB: ");
        return  statusInfoEntityRepository.save(statusInfoEntity);
    }
}
