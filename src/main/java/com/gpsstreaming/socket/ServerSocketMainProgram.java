package com.gpsstreaming.socket;


import com.gpsstreaming.model.HandleClient;

import java.io.IOException;
import java.net.Socket;

public class ServerSocketMainProgram {
    private java.net.ServerSocket serverSocket;
    private int port;
    long iCounter;

    public ServerSocketMainProgram(int port){
        this.port = port;
    }
    public void start() throws IOException {
        System.out.println("Starting the socket server at port:" + port);
        serverSocket = new java.net.ServerSocket(port);

        //Listen for clients. Block till one connects

        System.out.println("Waiting for clients...");
        Socket client = null;

        while (true) {
            iCounter += 1;
            client = serverSocket.accept();
            System.out.println(">>" + " Client No : " + iCounter + " Started");
            HandleClient oClient = new HandleClient();
            oClient.StartClient(client, String.valueOf(iCounter));
        }
        //A client has connected to this server. Send welcome message

    }
}
