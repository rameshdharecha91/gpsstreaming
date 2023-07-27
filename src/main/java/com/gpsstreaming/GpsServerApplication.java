package com.gpsstreaming;

import com.gpsstreaming.socket.GPSServerSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GpsServerApplication {

	@Autowired
	GPSServerSocket gpsServerSocket;


	public static void main(String[] args) throws Exception {
		var appContext = SpringApplication.run(GpsServerApplication.class, args);
		appContext.getBean(GPSServerSocket.class).startListingDeviceData(); // Star GPS listening data
	}
}
