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
@Document(collection = "GPS_STATUS_DATA")
public class GPSStatusInfoEntity implements Serializable {
    public String IMEINo;
    public String IsActivated;
    public String AccHigh;
    public String ChargeOn;
    public String SOSAlarm;
    public String LowBatteryAlarm;
    public String PowerCutAlarm;
    public String ShockAlarm;
    public String Normal;
    public String GPSTrackingOn;
    public String OilConnected;
    public String ISGPSLocated;
    public String ISGPSReal;
    public String Voltage;
    public String GSMSignalStrength;
    public String DateTimeMilliseconds;
}
