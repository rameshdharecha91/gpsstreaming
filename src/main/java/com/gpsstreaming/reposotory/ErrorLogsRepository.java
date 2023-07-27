package com.gpsstreaming.reposotory;

import com.gpsstreaming.model.ErrorLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogsRepository extends MongoRepository<ErrorLogEntity,Integer> {
    ErrorLogEntity findByIMEINo(String IMEINo);
}
