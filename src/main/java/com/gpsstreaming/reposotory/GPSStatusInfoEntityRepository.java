package com.gpsstreaming.reposotory;

import com.gpsstreaming.model.GPSStatusInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GPSStatusInfoEntityRepository extends MongoRepository<GPSStatusInfoEntity,Integer> {
}
