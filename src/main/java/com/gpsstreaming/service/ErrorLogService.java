package com.gpsstreaming.service;

import com.gpsstreaming.model.ErrorLogEntity;
import com.gpsstreaming.reposotory.ErrorLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogService {

    @Autowired
    private ErrorLogsRepository errorLogsRepository;

    public ErrorLogEntity addErrorLogs(ErrorLogEntity errorLogEntity){
        System.out.println("ADD EXCEPTION INTO DB");
        return errorLogsRepository.save(errorLogEntity);
    }
    public void findByIMEINO(String IMEINO){
        System.out.println(errorLogsRepository.findByIMEINo(IMEINO));
    }
}
