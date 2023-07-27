package com.gpsstreaming.handler;

import com.gpsstreaming.model.ErrorLogEntity;
import com.gpsstreaming.model.GPSInfoEntity;
import com.gpsstreaming.model.GPSStatusInfoEntity;
import com.gpsstreaming.service.ErrorLogService;
import com.gpsstreaming.service.GPSInfoService;
import com.gpsstreaming.service.GPSStatusInfoService;
import com.gpsstreaming.utils.CTC_ITU;
import com.gpsstreaming.utils.StringTools;
import com.gpsstreaming.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
@Service
public class ClientHandler {
    Socket clientSocket;
    String clientNumber ;
    String IMEINumber;

    @Autowired
    private GPSInfoService gpsInfoService;

    @Autowired
    private GPSStatusInfoService gpsStatusInfoService;

    @Autowired
    private ErrorLogService errorLogService;

    public static String sIMEINo = "";
    public void startClient(Socket clientSocket, String clientNumber) {
        this.IMEINumber = "";
        try {
            var dataReadingThread = new Thread(() -> processDeviceData(clientSocket,clientNumber));
            dataReadingThread.start();
            dataReadingThread.join();
        }
        catch(Exception e){
            System.out.println("Error from Start client data: " + e.getMessage());
        }
    }
    private void processDeviceData(Socket clientSocket,String clientNumber)
    {
        byte[] bytes = new byte[1024];
        try
        {
            var receivedStream = new DataInputStream(clientSocket.getInputStream());
            var iCounterStatusInfo = 0;
            var iCounterGPSInfo = 0;
            var currentDateTime = Util.getCurrentDateTime();

            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("START PROCESS DATA AT CLIENT NO : "+ clientNumber+ "\nSERVER NO : "+ LocalDateTime.now());

            int cnt = 0;
            while ( cnt!=2) /*For Local testing */
            //while (receivedStream.read(bytes, 0, bytes.length) !=0 )
            {
                /*For Local testing */
                if(cnt==0)
                  // bytes = new byte[]{120, 120, 13, 1, 3, 87, 68, 81, 0, 18, 66, 25, 0, 3, 3, -48, 13, 10};
                    bytes = new byte[]{120,120,13,1,3,87,68,81,0,18,66,25,2,42,-116,-93,13,10};
                if(cnt==1)
                 //bytes = new byte[]{120,120,31,18,23,6,2,15,42,5,-58,2,8,89,-128,7,-39,72,-48,82,21,86,1,-108,20,113,120,0,62,59,0,-76,-71,57,13,10};
                 bytes = new byte[]{120,120,10,19,69,6,4,0,1,2,43,112,-115,13,10,-93,13,10};
                /*For End Local testing */

                showReceivedBytesData(clientNumber,bytes);

                if ((int)(bytes[3]) == 1) {
                    sIMEINo = Util.ByteToStringIMEI(bytes, 4, 11);
                    this.IMEINumber = sIMEINo ;
                    byte[] serialBytes = new byte[2];
                    System.out.println("IMEI NO : "+sIMEINo);

                    System.arraycopy(bytes, 12, serialBytes, 0, 2);

                    var serialStr = StringTools.toHexString(serialBytes);

                    var toCRC = "0x0501" + serialStr;

                    byte[] toCRCbytes = StringTools.parseHex(toCRC, null);
                    int crc = CTC_ITU.CRC16ITU(toCRCbytes);

                    var crcToAdd = (StringTools.toHexString(crc));
                    crcToAdd = crcToAdd.substring(4,8);

                    var retStr = "78780501" + serialStr + crcToAdd + "0D0A";

                    byte[] bytea=Util.hexStringToByteArray(retStr);

                    var sendToClient = new DataOutputStream(clientSocket.getOutputStream());
                    sendToClient.write(bytea);
                    sendToClient.flush();
                }

                if ((int)(bytes[3]) == 18)
                {
                    var gpsInfoEntity = new GPSInfoEntity();
                    var imeiNo = this.IMEINumber;
                    gpsInfoEntity.setIMEINo(imeiNo);

                    var dateTime = Util.getDateTime(Util.ByteToStringBase10(bytes, 4, 13));
                    gpsInfoEntity.setDateTime(dateTime);

                    int[] oTemp = Util.getGPSinfoSatcount(bytes[10]);
                    var NoSatellite =  String.valueOf(Integer.parseInt((String.valueOf(oTemp[1])),2));
                    gpsInfoEntity.setNoSatellite(NoSatellite);

                    var ggpsInfo = String.valueOf(Integer.parseInt((String.valueOf(oTemp[0])),2));
                    gpsInfoEntity.setGGpsInfo(ggpsInfo);

                    var latlong = Util.GetLatOrLong(bytes[11], bytes[12], bytes[13], bytes[14]);
                    gpsInfoEntity.setLat(latlong);

                    var longitite = Util.GetLatOrLong(bytes[15], bytes[16], bytes[17], bytes[18]);
                    gpsInfoEntity.setLong(longitite);

                    var speed = String.valueOf(bytes[19] & 0xff);
                    gpsInfoEntity.setSpeed(speed);

                    int[] iTemp = Util.CourseorStatus((bytes[20]), bytes[21]);

                    var SouthLat = iTemp[1] == 0 ? gpsInfoEntity.SouthLat : gpsInfoEntity.NorthLat;
                    gpsInfoEntity.setSouthLat(SouthLat);

                    if (iTemp[1] == 0) {
                        gpsInfoEntity.setSouthLat("0");
                        gpsInfoEntity.setNorthLat("1");
                    }
                    else {
                        gpsInfoEntity.setSouthLat("1");
                        gpsInfoEntity.setNorthLat("0");
                    }

                    var EastLong = iTemp[2] == 0 ? gpsInfoEntity.EastLong : gpsInfoEntity.WestLong;
                    gpsInfoEntity.setEastLong(EastLong);

                    if (iTemp[2] == 0)
                    {
                        gpsInfoEntity.setEastLong("0");
                        gpsInfoEntity.setWestLong("1");
                    }
                    else{
                        gpsInfoEntity.setEastLong("1");
                        gpsInfoEntity.setWestLong("0");
                    }

                   var ISGPSLocated = String.valueOf(iTemp[3]);
                    gpsInfoEntity.setISGPSLocated(ISGPSLocated);

                    var ISGPSReal = String.valueOf(iTemp[4]);
                    gpsInfoEntity.setISGPSReal(ISGPSReal);

                    var Course = String.valueOf(iTemp[0]);
                    gpsInfoEntity.setCourse(Course);


                    if(Util.getTimeDiffInSeconds(currentDateTime,gpsInfoEntity.getDateTime()) >= 60){
                        currentDateTime = gpsInfoEntity.getDateTime();
                    }
                    if (gpsInfoEntity.getIMEINo().equals("357445100124219")) {
                        gpsInfoEntity.setVehicleName("MH01AR9931");
                    }
                    gpsInfoService.saveGpsInfo(gpsInfoEntity);

                    System.out.println("PROTOCOL 18  CLIENT NO: "+ clientNumber +"\nDateTime "+ gpsInfoEntity.getDateTime() +"\nLATITUDE : " + gpsInfoEntity.getLat() + "\nLONGITUDE : " + gpsInfoEntity.Long + "\nSPEED : " + gpsInfoEntity.getSpeed() + "\nIMEI NO: " + gpsInfoEntity.getIMEINo());

                    iCounterStatusInfo = 0;
                    iCounterGPSInfo++;

                    if(iCounterGPSInfo > 10) {
                        receivedStream.close();
                        break;
                    }
                }

                if ((int)(bytes[3]) == 19 ) {
                    var statusInfoEntity = new GPSStatusInfoEntity();

                    var imeiNumber = this.IMEINumber;
                    statusInfoEntity.setIMEINo(imeiNumber);

                    int xi= bytes[4];
                    String sBinary = Integer.toBinaryString(xi);

                    int len = sBinary.length();
                    for(int xx = 9 ; xx > len;xx--){
                        sBinary = "0".concat(sBinary);
                    }

                    var OilConnected = sBinary.substring(1,2);
                    statusInfoEntity.setOilConnected(OilConnected);

                    var GPSTrackingOn = sBinary.substring(2,3);
                    statusInfoEntity.setGPSTrackingOn(GPSTrackingOn);

                    if(sBinary.substring(3,6).equalsIgnoreCase("100")){
                        statusInfoEntity.setSOSAlarm("1");
                    }
                    else {
                        statusInfoEntity.setSOSAlarm("0");
                    }

                    if(sBinary.substring(3,6).equalsIgnoreCase("011")){
                        statusInfoEntity.setLowBatteryAlarm("1");
                    }
                    else{
                        statusInfoEntity.setLowBatteryAlarm("0");
                    }

                    if(sBinary.substring(3,6).equalsIgnoreCase("010")){
                        statusInfoEntity.setPowerCutAlarm("1");
                    }
                    else {
                        statusInfoEntity.setPowerCutAlarm("0");
                    }

                    if(sBinary.substring(3,6).equalsIgnoreCase("001")){
                        statusInfoEntity.setShockAlarm("1");
                    }
                    else{
                        statusInfoEntity.setShockAlarm("0");
                    }

                    if(sBinary.substring(3,6).equalsIgnoreCase("000")){
                        statusInfoEntity.setNormal("1");
                    }
                    else{
                        statusInfoEntity.setNormal("0");
                    }

                    var ChargeOn = sBinary.substring(6,7);
                    statusInfoEntity.setChargeOn(ChargeOn);

                    var AccHigh = sBinary.substring(7,8);
                    statusInfoEntity.setAccHigh(AccHigh);

                    var IsActivated = sBinary.substring(8,9);
                    statusInfoEntity.setIsActivated(IsActivated);


                    var Voltage = Util.ByteToString(bytes, 5, 5);
                    statusInfoEntity.setVoltage(Voltage);

                    var GSMSignalStrength = Util.ByteToString(bytes, 6, 6);
                    statusInfoEntity.setGSMSignalStrength(GSMSignalStrength);

                    Calendar oCal = Calendar.getInstance();
                    var DateTimeMilliseconds = String.valueOf(oCal.getTimeInMillis());
                    statusInfoEntity.setDateTimeMilliseconds(DateTimeMilliseconds);

                    gpsStatusInfoService.save(statusInfoEntity);
                    System.out.println("PROTOCOL 19 CLIENT NO : " + statusInfoEntity.getIMEINo());

                    iCounterGPSInfo = 0;
                    iCounterStatusInfo++;
                    if(iCounterStatusInfo > 5){
                        System.out.println("<><> Error Log written inside Status Info");
                        ErrorLogEntity errorLogEntity = new ErrorLogEntity();
                        errorLogEntity.setIMEINo(this.IMEINumber);
                        errorLogEntity.setDateTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                        errorLogEntity.setNote("GPSStatusInfoEntity");
                        errorLogService.addErrorLogs(errorLogEntity);
                        receivedStream.close();
                        break;
                    }

                    byte[] serialBytes = new byte[2];
                    System.arraycopy(bytes, 9, serialBytes, 0, 2);

                    String serialStr = StringTools.toHexString(serialBytes);

                    String toCRC = "0x0513" + serialStr;

                    byte[] toCRCbytes = StringTools.parseHex(toCRC, null);
                    int crc = CTC_ITU.CRC16ITU(toCRCbytes);

                    var crcToAdd = (StringTools.toHexString(crc));
                    crcToAdd=crcToAdd.substring(4,8);

                    var retStr = "78780513" + serialStr + crcToAdd + "0D0A";

                    byte[] byteval= Util.hexStringToByteArray(retStr);

                    var sendToClient = new DataOutputStream(clientSocket.getOutputStream());
                    sendToClient.write(byteval);
                    sendToClient.flush();
                }
                /*For Local testing */
                cnt++;
                if(cnt==2)
                {
                    break;
                }
                /*End Local testing */
            }  // END OF WHILE LOOP
            System.out.println("LOOP ENDED WITH IMEI NUMBER : " + this.IMEINumber);
        }
        catch(Exception e)
        {
            var errorLogEntity = new ErrorLogEntity();
            errorLogEntity.setIMEINo(this.IMEINumber);
            errorLogEntity.setDateTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            errorLogEntity.setNote("Error is:"+e.getMessage());
            errorLogService.addErrorLogs(errorLogEntity);
            System.out.println(e.getMessage());
        }
    }

    private void showReceivedBytesData(String clientNumber, byte[] bytes) {
        var receivedData = new StringBuilder();
        for(int i = 0; i< bytes.length; i++){
            if(bytes[i]==0){
                break;
            }
            receivedData.append(bytes[i]+",");
        }
        System.out.println("RECEIVED BYTE AT CLIENT NO : "+clientNumber+" : "+receivedData);
    }

    private static void insertProtocol(int cmd, String sResponse){
        Connection c = null;
        Statement s = null;
        try{
            var driver="com.mysql.jdbc.Driver";
            var url="jdbc:mysql://localhost/schoolbustracker";
            var uname="root";
            var pass="agilis@2002";
            Class.forName(driver);
            c=(Connection) DriverManager.getConnection(url,uname,pass);
            s=c.createStatement();
            s.executeUpdate("INSERT INTO gps_protocol(cmd, protocol) VALUES ("+cmd + ",'" + sResponse +"')");
            System.out.println("Protocol data inserted");
        }
        catch(Exception e){
            System.out.println("Error " + e.toString());
        }
        finally{
            if(c != null)
            {
                try
                {
                    c.close();
                }
                catch(SQLException e){}
            }
            if(s != null)
            {
                try
                {
                    s.close();
                }
                catch(SQLException e){}
            }
        }
    }
}
