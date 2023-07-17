package com.gpsstreaming.model;

import com.gpsstreaming.database.Transaction;
import com.gpsstreaming.utils.StringTools;
import com.gpsstreaming.utils.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HandleClient {
    Socket clientSocket;

    String sConnString;
    String clientNumber ;

    String IMEINumber;
    public static String sIMEINo = "";
    public void StartClient(Socket inClientSocket, String clineNo)
    {
        try
        {
            this.clientSocket = inClientSocket;
            this.clientNumber = clineNo;
            this.IMEINumber = "";
            Thread ctThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    ProcessDeviceData();
                }
            });
            ctThread.start();
        }catch(Exception e){}
    }

    private void ProcessDeviceData()
    {
        byte[] bytes = new byte[1024];
        Transaction oTransaction = new Transaction();
        try
        {
            DataInputStream stream = new DataInputStream(clientSocket.getInputStream());

            Protocol_18N19N22N26 oProtocol_18N19N22N26;
            String sOldDateTime = new String();
            boolean bGPSRead = false, bStatusRead = false;
            int iCounterStatusInfo = 0, iCounterGPSInfo = 0;
            String currentDateTime ;
            currentDateTime = Util.getCurrentDateTime();
            System.out.println("Loop started "+ this.clientNumber);
            while (stream.read(bytes, 0, bytes.length) !=0 )
            {
                if ((int)(bytes[3]) == 1)
                {
                    sIMEINo = Util.ByteToStringIMEI(bytes, 4, 11);
                    this.IMEINumber = sIMEINo ;
                    //System.out.println("P# : " + Byte.toString(bytes[3])+ " IMEI : " + this.IMEINumber);
                    byte[] serialBytes = new byte[2];
                    System.arraycopy(bytes, 12, serialBytes, 0, 2);

                    String serialStr = StringTools.toHexString(serialBytes);

                    String toCRC = "0x0501" + serialStr;

                    byte[] toCRCbytes = StringTools.parseHex(toCRC, null);
                    int crc = CTC_ITU.CRC16ITU(toCRCbytes);

                    String crcToAdd = (StringTools.toHexString(crc));
                    crcToAdd=crcToAdd.substring(4,8);

                    String retStr = "78780501" + serialStr + crcToAdd + "0D0A";

                    byte[] byteval=Util.hexStringToByteArray(retStr);

                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.write(byteval);
                    outToServer.flush();
                }
                if ((int)(bytes[3]) == 18)
                {
                    oProtocol_18N19N22N26 = new Protocol_18N19N22N26();
                    GPSinfo oGPSinfo = new GPSinfo();

                    oProtocol_18N19N22N26.IMEINo = this.IMEINumber;
                    oProtocol_18N19N22N26.DateTime = Util.getDateTime(Util.ByteToStringBase10(bytes, 4, 13));
                    int[] oTemp = Util.getGPSinfoSatcount(bytes[10]);
                    oGPSinfo.NoSatellite =  String.valueOf(Integer.parseInt((String.valueOf(oTemp[1])),2));
                    oGPSinfo.GGpsInfo = String.valueOf(Integer.parseInt((String.valueOf(oTemp[0])),2));
                    oGPSinfo.Lat = Util.GetLatOrLong(bytes[11], bytes[12], bytes[13], bytes[14]);
                    oGPSinfo.Long = Util.GetLatOrLong(bytes[15], bytes[16], bytes[17], bytes[18]);
                    oGPSinfo.Speed = String.valueOf(bytes[19] & 0xff);
                    int[] iTemp = Util.CourseorStatus((bytes[20]), bytes[21]);
                    oGPSinfo.SouthLat = iTemp[1] == 0 ? oGPSinfo.SouthLat : oGPSinfo.NorthLat;
                    if (iTemp[1] == 0)
                    {
                        oGPSinfo.SouthLat = "0";
                        oGPSinfo.NorthLat = "1";
                    }
                    else
                    {
                        oGPSinfo.SouthLat = "1";
                        oGPSinfo.NorthLat = "0";
                    }

                    oGPSinfo.EastLong = iTemp[2] == 0 ? oGPSinfo.EastLong : oGPSinfo.WestLong;
                    if (iTemp[2] == 0)
                    {
                        oGPSinfo.EastLong = "0";
                        oGPSinfo.WestLong = "1";
                    }
                    else
                    {
                        oGPSinfo.EastLong = "1";
                        oGPSinfo.WestLong = "0";
                    }

                    oGPSinfo.ISGPSLocated = String.valueOf(iTemp[3]);
                    oGPSinfo.ISGPSReal = String.valueOf(iTemp[4]);
                    oGPSinfo.Course = String.valueOf(iTemp[0]);
                    oProtocol_18N19N22N26.oGPSinfo = oGPSinfo;

                    if(Util.getTimeDiffInSeconds(currentDateTime,oProtocol_18N19N22N26.DateTime) >=60){
                        currentDateTime = oProtocol_18N19N22N26.DateTime;
                    }
                    oTransaction.InsertGPSinfo(oProtocol_18N19N22N26);

                    System.out.println("Protocol 18  Client "+ this.clientNumber +" DateTime "+ oProtocol_18N19N22N26.DateTime +" - Lat " + oGPSinfo.Lat + " Long " + oGPSinfo.Long + " Speed " + oGPSinfo.Speed + " IMEINo " + this.IMEINumber);
                    iCounterStatusInfo = 0;
                    iCounterGPSInfo++;
                    if(iCounterGPSInfo>10)
                    {
                        System.out.println("<><> Error Log written");
                        // ErrorLog el = new ErrorLog();
                        // el.IMEINo = this.IMEINumber;
                        // el.DateTime = oProtocol_18N19N22N26.DateTime;
                        // el.Note = "GPSInfo";
                        // oTransaction.InsertErrorLog(el);
                        iCounterGPSInfo =0;
                        // el = null;
                        stream.close();
                        break;
                    }
                    oProtocol_18N19N22N26 = null;
                }

                if ((int)(bytes[3]) == 19 )
                {
                    oProtocol_18N19N22N26 = new Protocol_18N19N22N26();
                    Statusinfo oStatusinfo = new Statusinfo();

                    oProtocol_18N19N22N26.IMEINo = this.IMEINumber;
                    String[] sDevInfo = Util.GetDeviceInfo(bytes[4]);
                    int xi= (int) bytes[4];
                    String sBinary = Integer.toBinaryString(xi);

                    int len = sBinary.length();
                    for(int xx = 9 ; xx > len;xx--)
                        sBinary = "0".concat(sBinary);


                    oStatusinfo.OilConnected = sBinary.substring(1,2);
                    oStatusinfo.GPSTrackingOn = sBinary.substring(2,3);
                    if(sBinary.substring(3,6).equalsIgnoreCase("100"))
                        oStatusinfo.SOSAlarm = "1";
                    else
                        oStatusinfo.SOSAlarm = "0";

                    if(sBinary.substring(3,6).equalsIgnoreCase("011"))
                        oStatusinfo.LowBatteryAlarm = "1";
                    else
                        oStatusinfo.LowBatteryAlarm = "0";

                    if(sBinary.substring(3,6).equalsIgnoreCase("010"))
                        oStatusinfo.PowerCutAlarm = "1";
                    else
                        oStatusinfo.PowerCutAlarm = "0";

                    if(sBinary.substring(3,6).equalsIgnoreCase("001"))
                        oStatusinfo.ShockAlarm = "1";
                    else
                        oStatusinfo.ShockAlarm = "0";

                    if(sBinary.substring(3,6).equalsIgnoreCase("000"))
                        oStatusinfo.Normal = "1";
                    else
                        oStatusinfo.Normal = "0";

                    oStatusinfo.ChargeOn = sBinary.substring(6,7);
                    oStatusinfo.AccHigh = sBinary.substring(7,8);
                    oStatusinfo.IsActivated = sBinary.substring(8,9);


                    oStatusinfo.Voltage = Util.ByteToString(bytes, 5, 5);
                    oStatusinfo.GSMSignalStrength = Util.ByteToString(bytes, 6, 6);
                    Calendar oCal = Calendar.getInstance();
                    oStatusinfo.DateTimeMilliseconds = String.valueOf(oCal.getTimeInMillis());
                    oProtocol_18N19N22N26.Lang = Util.ByteToString(bytes, 7, 8);
                    oProtocol_18N19N22N26.oStatusinfo = oStatusinfo;
                    System.out.println("Protocol 19 " + this.IMEINumber);
                    oTransaction.InsertStatusinfo(oProtocol_18N19N22N26);
                    iCounterGPSInfo=0;
                    iCounterStatusInfo++;
                    if(iCounterStatusInfo>5){
                        System.out.println("<><> Error Log written inside Status Info");
                        ErrorLog el = new ErrorLog();
                        el.IMEINo = this.IMEINumber;
                        String curdate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                        el.DateTime = curdate;
                        el.Note = "StatusInfo";
                        oTransaction.InsertErrorLog(el);
                        iCounterStatusInfo=0;
                        el = null;
                        stream.close();
                        break;
                    }
                    oProtocol_18N19N22N26 = null;

                    byte[] serialBytes = new byte[2];
                    System.arraycopy(bytes, 9, serialBytes, 0, 2);

                    String serialStr = StringTools.toHexString(serialBytes);

                    String toCRC = "0x0513" + serialStr;

                    byte[] toCRCbytes = StringTools.parseHex(toCRC, null);
                    int crc = CTC_ITU.CRC16ITU(toCRCbytes);

                    String crcToAdd = (StringTools.toHexString(crc));
                    crcToAdd=crcToAdd.substring(4,8);

                    String retStr = "78780513" + serialStr + crcToAdd + "0D0A";

                    byte[] byteval= Util.hexStringToByteArray(retStr);

                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.write(byteval);
                    outToServer.flush();


                }

            }  // END OF WHILE LOOP
            System.out.println("Loop ended " + this.IMEINumber);
        }
        catch(Exception e)
        {
            ErrorLog el = new ErrorLog();
            el.IMEINo = this.IMEINumber;
            el.DateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            el.Note = e.toString();
            oTransaction.InsertErrorLog(el);
            el = null;

        }
        oTransaction.CloseConnection();
    }
    private static void insertProtocol(int cmd, String sResponse){
        Connection c = null;
        Statement s = null;
        try{
            String driver="com.mysql.jdbc.Driver";
            String url="jdbc:mysql://localhost/schoolbustracker";
            String uname="root";
            String pass="agilis@2002";
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
