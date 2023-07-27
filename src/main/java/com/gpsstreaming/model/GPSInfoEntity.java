package com.gpsstreaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "GPS_STREAMING_DATA") /*GPS_STREAM_DATA*/
public class GPSInfoEntity implements Serializable {
    public String IMEINo;
    public String DateTime;
    public String vehicleName;
    public String NoSatellite;
    public String GGpsInfo;
    public String Lat;
    public String Long;
    public String Speed;
    public String Course;
    public String SouthLat;
    public String NorthLat;
    public String EastLong;
    public String WestLong;
    public String ISGPSLocated;
    public String ISGPSReal;
}
