package com.gpsstreaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMQPublishDTO implements Serializable {
    private GPSInfoEntity gpsInfoEntity;
    private GPSStatusInfoEntity gpsStatusInfoEntity;
}
