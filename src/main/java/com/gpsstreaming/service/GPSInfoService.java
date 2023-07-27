package com.gpsstreaming.service;

import com.gpsstreaming.model.GPSInfoEntity;
import com.gpsstreaming.model.RabbitMQPublishDTO;
import com.gpsstreaming.reposotory.GPSInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GPSInfoService {

    @Autowired
    RabbitMQProducerService rabbitMQProducer;
    @Autowired
    private GPSInfoRepository gpsInfoRepository;

    public GPSInfoEntity saveGpsInfo(GPSInfoEntity entity){

        var publishDTO = new RabbitMQPublishDTO();
        publishDTO.setGpsInfoEntity(entity);
        rabbitMQProducer.sendMessage(publishDTO);
        System.out.println("PUBLISHED MESSAGE TO RABBITMQ: " + publishDTO);
        System.out.println("SAVE GPS_STREAMING_DATA INTO DB");
        return gpsInfoRepository.save(entity);
    }
}
