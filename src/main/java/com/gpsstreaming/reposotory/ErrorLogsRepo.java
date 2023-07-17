package com.gpsstreaming.reposotory;

import com.gpsstreaming.model.ErrorLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ErrorLogsRepo extends MongoRepository<ErrorLog,Integer> {
}
