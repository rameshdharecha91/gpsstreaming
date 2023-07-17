package com.gpsstreaming.database;

import com.gpsstreaming.model.ErrorLog;
import com.gpsstreaming.model.Protocol_18N19N22N26;
import com.gpsstreaming.utils.Util;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public Transaction() {
        String server;
        int port;
        String databaseName;
        String username;
        String password;
        try {
            // Read the environment variables from the .env file
            Map<String, String> env = new HashMap<String, String>();
            BufferedReader reader = new BufferedReader(new FileReader(".env"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length >= 2) {
                    String key = parts[0];
                    String value = parts[1];
                    env.put(key, value);
                }
            }
            reader.close();

            // Get the values of your credentials from the environment variables
            server = env.get("DB_HOST");
            port = Integer.parseInt(env.get("DB_PORT"));
            databaseName = env.get("DB_NAME");
            username = env.get("DB_USERNAME");
            password = env.get("DB_PASSWORD");
            MongoClientURI uri = new MongoClientURI("mongodb+srv://" + username + ":" + password + "@" + server + "/" + databaseName + "?retryWrites=true&w=majority");
            mongoClient = new MongoClient(uri);

            // Get a reference to the database
            database = mongoClient.getDatabase(databaseName);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void CloseConnection() {
        // Close the MongoDB client
        mongoClient.close();
    }

    public void InsertErrorLog(ErrorLog errorlog) {
        String collectionName = "gps_error_logs";
        MongoCollection<Document> collection = database.getCollection(collectionName);
        errorlog.DateTime = Util.GMTtoISTDate(errorlog.DateTime);
        Document document = new Document("IMEINo", errorlog.IMEINo)
                .append("DateTime", errorlog.DateTime)
                .append("Note", errorlog.Note);

        collection.insertOne(document);
    }

    public void InsertGPSinfo(Protocol_18N19N22N26 oprotocol)
    {
        try
        {
            String collectionName = "gps_stream_datas";
            // Get a reference to the collection
            MongoCollection<Document> collection = database.getCollection(collectionName);

            oprotocol.DateTime = Util.GMTtoISTDate(oprotocol.DateTime);

            if (oprotocol.IMEINo.equals("357445100124219")) {
                // Create a new document
                Document document = new Document("IMEINo", oprotocol.IMEINo)
                        .append("DateTime", oprotocol.DateTime)
                        .append("vehicleName", "MH01AR9931")
                        .append("NoSatellite", oprotocol.oGPSinfo.NoSatellite)
                        .append("GGpsInfo", oprotocol.oGPSinfo.GGpsInfo)
                        .append("Lat", oprotocol.oGPSinfo.Lat)
                        .append("Long", oprotocol.oGPSinfo.Long)
                        .append("Speed", oprotocol.oGPSinfo.Speed)
                        .append("Course", oprotocol.oGPSinfo.Course)
                        .append("SouthLat", oprotocol.oGPSinfo.SouthLat)
                        .append("NorthLat", oprotocol.oGPSinfo.NorthLat)
                        .append("EastLong", oprotocol.oGPSinfo.EastLong)
                        .append("WestLong", oprotocol.oGPSinfo.WestLong)
                        .append("ISGPSLocated", oprotocol.oGPSinfo.ISGPSLocated)
                        .append("ISGPSReal", oprotocol.oGPSinfo.ISGPSReal);

                // Insert the document
                collection.insertOne(document);
            }else{
                // Create a new document
                Document document = new Document("IMEINo", oprotocol.IMEINo)
                        .append("DateTime", oprotocol.DateTime)
                        .append("NoSatellite", oprotocol.oGPSinfo.NoSatellite)
                        .append("GGpsInfo", oprotocol.oGPSinfo.GGpsInfo)
                        .append("Lat", oprotocol.oGPSinfo.Lat)
                        .append("Long", oprotocol.oGPSinfo.Long)
                        .append("Speed", oprotocol.oGPSinfo.Speed)
                        .append("Course", oprotocol.oGPSinfo.Course)
                        .append("SouthLat", oprotocol.oGPSinfo.SouthLat)
                        .append("NorthLat", oprotocol.oGPSinfo.NorthLat)
                        .append("EastLong", oprotocol.oGPSinfo.EastLong)
                        .append("WestLong", oprotocol.oGPSinfo.WestLong)
                        .append("ISGPSLocated", oprotocol.oGPSinfo.ISGPSLocated)
                        .append("ISGPSReal", oprotocol.oGPSinfo.ISGPSReal);

                // Insert the document
                collection.insertOne(document);
            }
        }
        catch (Exception e1)
        {
            ErrorLog eL = new ErrorLog();
            eL.IMEINo = oprotocol.IMEINo;
            oprotocol.DateTime = Util.GMTtoISTDate(oprotocol.DateTime);
            eL.DateTime = oprotocol.DateTime;
            eL.Note = e1.toString();
            com.gpsstreaming.congif.Transaction oTransaction = new com.gpsstreaming.congif.Transaction();
            oTransaction.InsertErrorLog(eL);
            oTransaction.CloseConnection();
            eL = null;

            e1.printStackTrace();
        }
    }

    public void InsertStatusinfo(Protocol_18N19N22N26 oprotocol)
    {
        // try
        // {
        //     String server = "iotapp1.5d6lvc4.mongodb.net";
        //     int port = 27017;
        //     String databaseName = "chakraview_iot";
        //     String collectionName = "GPSStatusData";
        //     String username = "sagarbansalcc";
        //     String password = "GSkIA36nI1p8aR5P";
        //     MongoClientURI uri = new MongoClientURI("mongodb+srv://" + username + ":" + password + "@" + server + "/" + databaseName + "?retryWrites=true&w=majority");
        //     MongoClient mongoClient = new MongoClient(uri);

        //     // Get a reference to the database
        //     MongoDatabase database = mongoClient.getDatabase(databaseName);

        //     // Get a reference to the collection
        //     MongoCollection<Document> collection = database.getCollection(collectionName);

        //     // Create a new document
        //     Document document = new Document("IMEINo", oprotocol.IMEINo)
        //         .append("DateTime", oprotocol.oStatusinfo.DateTimeMilliseconds)
        //         .append("IsActivated", oprotocol.oStatusinfo.IsActivated)
        //         .append("AccHigh", oprotocol.oStatusinfo.AccHigh)
        //         .append("ChargeOn", oprotocol.oStatusinfo.ChargeOn)
        //         .append("SOSAlarm", oprotocol.oStatusinfo.LowBatteryAlarm)
        //         .append("PowerCutAlarm", oprotocol.oStatusinfo.PowerCutAlarm)
        //         .append("ShockAlarm", oprotocol.oStatusinfo.ShockAlarm)
        //         .append("Normal", oprotocol.oStatusinfo.Normal)
        //         .append("GPSTrackingOn", oprotocol.oStatusinfo.GPSTrackingOn)
        //         .append("OilConnected", oprotocol.oStatusinfo.OilConnected)
        //         .append("ISGPSLocated", oprotocol.oStatusinfo.ISGPSLocated)
        //         .append("ISGPSReal", oprotocol.oStatusinfo.ISGPSReal)
        //         .append("Voltage", oprotocol.oStatusinfo.Voltage)
        //         .append("GSMSignalStrength", oprotocol.oStatusinfo.GSMSignalStrength);

        //     // Insert the document
        //     collection.insertOne(document);

        //     // Close the MongoDB client
        //     mongoClient.close();
        // }
        // catch (Exception e1)
        // {
        //         ErrorLog eL = new ErrorLog();
        //         eL.IMEINo = oprotocol.IMEINo;
        //         eL.DateTime = oprotocol.oStatusinfo.DateTimeMilliseconds;
        //         eL.Note = e1.toString();
        //         Transaction oTransaction = new Transaction();
        //         oTransaction.InsertErrorLog(eL);
        //         eL = null;
        //         e1.printStackTrace();
        // }
    }
}
