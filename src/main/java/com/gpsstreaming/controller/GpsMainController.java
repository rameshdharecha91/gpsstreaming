package com.gpsstreaming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/gps")
public class GpsMainController {
    @GetMapping("/isup")
    public String isUp(){
        return "GPS is up now";
    }
}
