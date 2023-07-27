package com.gpsstreaming.controller;

import com.gpsstreaming.model.ErrorLogEntity;
import com.gpsstreaming.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/gps")
public class GpsMainController {

    @Autowired
    private ErrorLogService service;
    @GetMapping("/add-errors")
    public String isUp(){
        ErrorLogEntity errorLogEntity = service.addErrorLogs(null);
        service.findByIMEINO(errorLogEntity.getIMEINo());
        return errorLogEntity.toString();
    }
}
