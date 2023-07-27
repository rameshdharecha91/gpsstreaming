package com.gpsstreaming.socket;


import com.gpsstreaming.handler.ClientHandler;
import com.gpsstreaming.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.net.ServerSocket;
import java.net.Socket;

@Service
@PropertySource("classpath:application.yml")
public class GPSServerSocket {

    @Value("${socket.server.port}")
    private String port;
    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private ClientHandler clientHandler;

    public void startListingDeviceData() throws Exception {
        long iCounter = 0;
        System.out.println("----------------------------------------------------");
        System.out.println("     SOCKET SERVER RUNNING AT PORT NO : "+port+"    ");
        System.out.println("-----------------------------------------------------");

        var serverSocket = new ServerSocket(Integer.parseInt(port));
        Socket clientSocket;
        while (true) {
            clientSocket = serverSocket.accept();
            iCounter++;
            clientHandler.startClient(clientSocket, String.valueOf(iCounter));
        }
    }
}
