package com.gpsstreaming.reposotory;

import com.gpsstreaming.model.GPSInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GPSInfoRepository extends MongoRepository<GPSInfoEntity,Integer> {
}
