//package com.gpsstreaming.congif;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.sql.*;
//import java.util.*;
//import java.util.concurrent.*;
//import java.lang.Integer;
//import java.util.Calendar;
//import java.util.Date;
//import java.text.SimpleDateFormat;
//import java.text.ParseException;
//import com.mongodb.ServerAddress;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.MongoClient;
//import com.mongodb.MongoCredential;
//import com.mongodb.BasicDBObject;
//import org.bson.Document;
//import java.util.Arrays;
//import com.mongodb.MongoClientURI;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.util.HashMap;
//import java.util.Map;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class Program {
//    private ServerSocket serverSocket;
//    private int port;
//    long iCounter;
//
//    public Program(int port) {
//        this.port = port;
//    }
//
//    public static void main(String[] args) {
//        // Setting a default port number of server
//        int portNumber = 6006;
//
//        try {
//            // initializing the Socket Server
//            Program socketServer = new Program(portNumber);
//            socketServer.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void start() throws IOException {
//        System.out.println("Starting the socket server at port:" + port);
//        serverSocket = new ServerSocket(port);
//
//        //Listen for clients. Block till one connects
//
//        System.out.println("Waiting for clients...");
//        Socket client = null;
//
//        while (true) {
//            iCounter += 1;
//            client = serverSocket.accept();
//            System.out.println(">>" + " Client No : " + iCounter + " Started");
//            HandleClient oClient = new HandleClient();
//            oClient.StartClient(client, String.valueOf(iCounter));
//        }
//        //A client has connected to this server. Send welcome message
//
//    }
//
//}
//
//
//
//class HandleClient
//{
//    Socket clientSocket;
//
//    String sConnString;
//            String clientNumber ;
//
//    String IMEINumber;
//    public static String sIMEINo = "";
//    public void StartClient(Socket inClientSocket, String clineNo)
//    {
//        try
//        {
//            this.clientSocket = inClientSocket;
//            this.clientNumber = clineNo;
//            this.IMEINumber = "";
//            Thread ctThread = new Thread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                        ProcessDeviceData();
//                }
//            });
//            ctThread.start();
//        }catch(Exception e){}
//    }
//
//    private void ProcessDeviceData()
//    {
//        byte[] bytes = new byte[1024];
//        Transaction oTransaction = new Transaction();
//        try
//        {
//            DataInputStream stream = new DataInputStream(clientSocket.getInputStream());
//
//            Protocol_18N19N22N26 oProtocol_18N19N22N26;
//            String sOldDateTime = new String();
//            boolean bGPSRead = false, bStatusRead = false;
//            int iCounterStatusInfo = 0, iCounterGPSInfo = 0;
//            String currentDateTime ;
//            currentDateTime = Util.getCurrentDateTime();
//            System.out.println("Loop started "+ this.clientNumber);
//            while (stream.read(bytes, 0, bytes.length) !=0 )
//            {
//                    if ((int)(bytes[3]) == 1)
//                    {
//                            sIMEINo = Util.ByteToStringIMEI(bytes, 4, 11);
//                                            this.IMEINumber = sIMEINo ;
//                            //System.out.println("P# : " + Byte.toString(bytes[3])+ " IMEI : " + this.IMEINumber);
//                            byte[] serialBytes = new byte[2];
//                            System.arraycopy(bytes, 12, serialBytes, 0, 2);
//
//                            String serialStr = StringTools.toHexString(serialBytes);
//
//                            String toCRC = "0x0501" + serialStr;
//
//                            byte[] toCRCbytes = StringTools.parseHex(toCRC, null);
//                            int crc = CTC_ITU.CRC16ITU(toCRCbytes);
//
//                            String crcToAdd = (StringTools.toHexString(crc));
//                            crcToAdd=crcToAdd.substring(4,8);
//
//                            String retStr = "78780501" + serialStr + crcToAdd + "0D0A";
//
//                            byte[] byteval=Util.hexStringToByteArray(retStr);
//
//                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//                            outToServer.write(byteval);
//                            outToServer.flush();
//                    }
//                    if ((int)(bytes[3]) == 18)
//                    {
//                        oProtocol_18N19N22N26 = new Protocol_18N19N22N26();
//                        GPSinfo oGPSinfo = new GPSinfo();
//
//                        oProtocol_18N19N22N26.IMEINo = this.IMEINumber;
//                        oProtocol_18N19N22N26.DateTime = Util.getDateTime(Util.ByteToStringBase10(bytes, 4, 13));
//                        int[] oTemp = Util.getGPSinfoSatcount(bytes[10]);
//                        oGPSinfo.NoSatellite =  String.valueOf(Integer.parseInt((String.valueOf(oTemp[1])),2));
//                        oGPSinfo.GGpsInfo = String.valueOf(Integer.parseInt((String.valueOf(oTemp[0])),2));
//                        oGPSinfo.Lat = Util.GetLatOrLong(bytes[11], bytes[12], bytes[13], bytes[14]);
//                        oGPSinfo.Long = Util.GetLatOrLong(bytes[15], bytes[16], bytes[17], bytes[18]);
//                        oGPSinfo.Speed = String.valueOf(bytes[19] & 0xff);
//                        int[] iTemp = Util.CourseorStatus((bytes[20]), bytes[21]);
//                        oGPSinfo.SouthLat = iTemp[1] == 0 ? oGPSinfo.SouthLat : oGPSinfo.NorthLat;
//                        if (iTemp[1] == 0)
//                        {
//                            oGPSinfo.SouthLat = "0";
//                            oGPSinfo.NorthLat = "1";
//                        }
//                        else
//                        {
//                            oGPSinfo.SouthLat = "1";
//                            oGPSinfo.NorthLat = "0";
//                        }
//
//                        oGPSinfo.EastLong = iTemp[2] == 0 ? oGPSinfo.EastLong : oGPSinfo.WestLong;
//                        if (iTemp[2] == 0)
//                        {
//                            oGPSinfo.EastLong = "0";
//                            oGPSinfo.WestLong = "1";
//                        }
//                        else
//                        {
//                            oGPSinfo.EastLong = "1";
//                            oGPSinfo.WestLong = "0";
//                        }
//
//                        oGPSinfo.ISGPSLocated = String.valueOf(iTemp[3]);
//                        oGPSinfo.ISGPSReal = String.valueOf(iTemp[4]);
//                        oGPSinfo.Course = String.valueOf(iTemp[0]);
//                        oProtocol_18N19N22N26.oGPSinfo = oGPSinfo;
//
//                        if(Util.getTimeDiffInSeconds(currentDateTime,oProtocol_18N19N22N26.DateTime) >=60){
//                                currentDateTime = oProtocol_18N19N22N26.DateTime;
//                        }
//                        oTransaction.InsertGPSinfo(oProtocol_18N19N22N26);
//
//                        System.out.println("Protocol 18  Client "+ this.clientNumber +" DateTime "+ oProtocol_18N19N22N26.DateTime +" - Lat " + oGPSinfo.Lat + " Long " + oGPSinfo.Long + " Speed " + oGPSinfo.Speed + " IMEINo " + this.IMEINumber);
//                        iCounterStatusInfo = 0;
//                        iCounterGPSInfo++;
//                        if(iCounterGPSInfo>10)
//                        {
//                                System.out.println("<><> Error Log written");
//                                // ErrorLog el = new ErrorLog();
//                                // el.IMEINo = this.IMEINumber;
//                                // el.DateTime = oProtocol_18N19N22N26.DateTime;
//                                // el.Note = "GPSInfo";
//                                // oTransaction.InsertErrorLog(el);
//                                iCounterGPSInfo =0;
//                                // el = null;
//                                stream.close();
//                                break;
//                        }
//                        oProtocol_18N19N22N26 = null;
//                    }
//
//                    if ((int)(bytes[3]) == 19 )
//                    {
//                        oProtocol_18N19N22N26 = new Protocol_18N19N22N26();
//                        Statusinfo oStatusinfo = new Statusinfo();
//
//                        oProtocol_18N19N22N26.IMEINo = this.IMEINumber;
//                        String[] sDevInfo = Util.GetDeviceInfo(bytes[4]);
//                        int xi= (int) bytes[4];
//                        String sBinary = Integer.toBinaryString(xi);
//
//                        int len = sBinary.length();
//                        for(int xx = 9 ; xx > len;xx--)
//                                sBinary = "0".concat(sBinary);
//
//
//                        oStatusinfo.OilConnected = sBinary.substring(1,2);
//                        oStatusinfo.GPSTrackingOn = sBinary.substring(2,3);
//                        if(sBinary.substring(3,6).equalsIgnoreCase("100"))
//                            oStatusinfo.SOSAlarm = "1";
//                        else
//                            oStatusinfo.SOSAlarm = "0";
//
//                        if(sBinary.substring(3,6).equalsIgnoreCase("011"))
//                            oStatusinfo.LowBatteryAlarm = "1";
//                        else
//                            oStatusinfo.LowBatteryAlarm = "0";
//
//                        if(sBinary.substring(3,6).equalsIgnoreCase("010"))
//                            oStatusinfo.PowerCutAlarm = "1";
//                        else
//                            oStatusinfo.PowerCutAlarm = "0";
//
//                        if(sBinary.substring(3,6).equalsIgnoreCase("001"))
//                            oStatusinfo.ShockAlarm = "1";
//                        else
//                            oStatusinfo.ShockAlarm = "0";
//
//                        if(sBinary.substring(3,6).equalsIgnoreCase("000"))
//                            oStatusinfo.Normal = "1";
//                        else
//                            oStatusinfo.Normal = "0";
//
//                        oStatusinfo.ChargeOn = sBinary.substring(6,7);
//                        oStatusinfo.AccHigh = sBinary.substring(7,8);
//                        oStatusinfo.IsActivated = sBinary.substring(8,9);
//
//
//                        oStatusinfo.Voltage = Util.ByteToString(bytes, 5, 5);
//                        oStatusinfo.GSMSignalStrength = Util.ByteToString(bytes, 6, 6);
//                                                    Calendar oCal = Calendar.getInstance();
//                                                    oStatusinfo.DateTimeMilliseconds = String.valueOf(oCal.getTimeInMillis());
//                        oProtocol_18N19N22N26.Lang = Util.ByteToString(bytes, 7, 8);
//                        oProtocol_18N19N22N26.oStatusinfo = oStatusinfo;
//                        System.out.println("Protocol 19 " + this.IMEINumber);
//                        oTransaction.InsertStatusinfo(oProtocol_18N19N22N26);
//                        iCounterGPSInfo=0;
//                        iCounterStatusInfo++;
//                        if(iCounterStatusInfo>5){
//                            System.out.println("<><> Error Log written inside Status Info");
//                            ErrorLog el = new ErrorLog();
//                            el.IMEINo = this.IMEINumber;
//                            String curdate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
//                            el.DateTime = curdate;
//                            el.Note = "StatusInfo";
//                            oTransaction.InsertErrorLog(el);
//                            iCounterStatusInfo=0;
//                            el = null;
//                            stream.close();
//                            break;
//                        }
//                            oProtocol_18N19N22N26 = null;
//
//                        byte[] serialBytes = new byte[2];
//                        System.arraycopy(bytes, 9, serialBytes, 0, 2);
//
//                        String serialStr = StringTools.toHexString(serialBytes);
//
//                        String toCRC = "0x0513" + serialStr;
//
//                        byte[] toCRCbytes = StringTools.parseHex(toCRC, null);
//                        int crc = CTC_ITU.CRC16ITU(toCRCbytes);
//
//                        String crcToAdd = (StringTools.toHexString(crc));
//                        crcToAdd=crcToAdd.substring(4,8);
//
//                        String retStr = "78780513" + serialStr + crcToAdd + "0D0A";
//
//                        byte[] byteval=Util.hexStringToByteArray(retStr);
//
//                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//                        outToServer.write(byteval);
//                        outToServer.flush();
//
//
//                    }
//
//            }  // END OF WHILE LOOP
//            System.out.println("Loop ended " + this.IMEINumber);
//        }
//        catch(Exception e)
//        {
//            ErrorLog el = new ErrorLog();
//            el.IMEINo = this.IMEINumber;
//            el.DateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
//            el.Note = e.toString();
//            oTransaction.InsertErrorLog(el);
//            el = null;
//
//        }
//        oTransaction.CloseConnection();
//    }
//    private static void insertProtocol(int cmd, String sResponse){
//        Connection c = null;
//        Statement s = null;
//        try{
//            String driver="com.mysql.jdbc.Driver";
//            String url="jdbc:mysql://localhost/schoolbustracker";
//            String uname="root";
//            String pass="agilis@2002";
//            Class.forName(driver);
//            c=(Connection) DriverManager.getConnection(url,uname,pass);
//            s=c.createStatement();
//            s.executeUpdate("INSERT INTO gps_protocol(cmd, protocol) VALUES ("+cmd + ",'" + sResponse +"')");
//            System.out.println("Protocol data inserted");
//        }
//        catch(Exception e){
//            System.out.println("Error " + e.toString());
//        }
//        finally{
//            if(c != null)
//            {
//                try
//                {
//                    c.close();
//                }
//                catch(SQLException e){}
//            }
//            if(s != null)
//            {
//                try
//                {
//                        s.close();
//                }
//                catch(SQLException e){}
//            }
//        }
//    }
//}
//
//class CTC_ITU
//{
//    static int CRC16ITU(byte[] bytes)
//    {
//            int[] table = {
//                0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
//                0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
//                0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
//                0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
//                0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
//                0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
//                0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
//                0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
//                0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
//                0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
//                0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
//                0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
//                0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
//                0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
//                0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
//                0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
//                0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
//                0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
//                0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
//                0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
//                0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
//                0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
//                0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
//                0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
//                0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
//                0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
//                0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
//                0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
//                0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
//                0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
//                0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
//                0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78,
//            };
//
//            int crc = 0xffff;
//            for (byte b : bytes)
//            {
//                crc = ((crc >> 8) ^ table[(crc ^ b) & 0xff]);
//            }
//            crc = ~crc;
//
//            return crc;
//    }
//
//}
//
//
//class Util
//{
//    public static String GMTtoISTDate(String GMTDateTime) {
//        // Define the input datetime in GMT format
//        String inputDateTime = GMTDateTime +" GMT";
//
//        // Create a DateTimeFormatter object for parsing the input datetime
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss z");
//
//        // Parse the input datetime string into a LocalDateTime object
//        LocalDateTime dateTime = LocalDateTime.parse(inputDateTime, formatter);
//
//        // Define the ZoneId for IST timezone
//        ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
//
//        // Create a ZonedDateTime object for the input datetime in IST timezone
//        ZonedDateTime istDateTime = ZonedDateTime.of(dateTime, ZoneId.of("GMT")).withZoneSameInstant(istZoneId);
//
//        // Format the output datetime in IST timezone
//        String outputDateTime = istDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//        // Print the output datetime in IST format
//        System.out.println("IST datetime: " + outputDateTime);
//        return outputDateTime;
//    }
//
//    public static String bytesToHex(byte[] in)
//    {
//        final StringBuilder builder = new StringBuilder();
//        for(byte b : in)
//        {   // Integer.toHexString(b);
//            builder.append(String.format("%02x", b));
//        }
//        return builder.toString();
//    }
//
//    public static byte[] hexStringToByteArray(String s)
//    {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                                    + Character.digit(s.charAt(i+1), 16));
//        }
//        return data;
//    }
//
//    public static String getDateTime(String p)
//    {
//        String s = "20" + p.substring(0, 2) + "/" + p.substring(2, 4) + "/" + p.substring(4, 6) + " " + p.substring(6, 8) + ":" + p.substring(8, 10) + ":" + p.substring(10, 12);
//        try
//        {
//
//           return s;
//        }catch (Exception e){
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return s;
//    }
//
//    public static Long getTimeDiffInSeconds(String firstDate, String nextDate){
//        Long lDiff = 0l;
//        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        try {
//            Date d1 = f.parse(firstDate);
//            Date d2 = f.parse(nextDate);
//            long seconds_f = d1.getTime();
//            long seconds_n = d2.getTime();
//            lDiff = (seconds_n - seconds_f) / 1000;
//        } catch (ParseException e) {
//                e.printStackTrace();
//        }
//
//        return lDiff;
//    }
//
//    public static String getCurrentDateTime(){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
//        String today = sdf.format(new Date());
//        return today;
//    }
//    public static String ByteToStringBase10(byte[] oByte, int startPoint, int endPoint)
//    {
//        String sOut = "";
//        for (int i = startPoint; i <= endPoint; i++)
//        {
//                if(Integer.parseInt(Byte.toString(oByte[i])) < 10)
//                        sOut += "0" + Integer.parseInt(Byte.toString(oByte[i]));
//                else
//                        sOut += Integer.parseInt(Byte.toString(oByte[i]));
//        }
//        return sOut;
//    }
//
//    public static String GetLatOrLong(byte Byte_1, byte Byte_2, byte Byte_3, byte Byte_4)
//    {
//        List<Byte> oByt = new ArrayList<Byte>();
//        oByt.add(Byte_1);
//        oByt.add(Byte_2);
//        oByt.add(Byte_3);
//        oByt.add(Byte_4);
//
//        Byte[] bytearray = oByt.toArray(new Byte[oByt.size()]);
//        long ValDecimal = 0;
//        for (int i = 0; i < bytearray.length; i++)
//        {
//                ValDecimal = (ValDecimal << 8) + (bytearray[i] & 0xff);
//        }
//        double X = (double)(ValDecimal/ 500 / 60 / 60);
//        double Y = (double)(ValDecimal / 500.0 / 60.0) - (X * 60);
//        return String.valueOf(X + Y / 60);
//    }
//
//
//    public static int[] getGPSinfoSatcount(byte bytes)
//    {
//        int[] cs = new int[2];
//        String sHex = Byte.toString(bytes);
//        int a = Integer.parseInt(sHex);
//        String sBinary = Integer.toBinaryString(a);
//        int iTotal = 8 - sBinary.length();
//
//        for (int iCt = 0; iCt < iTotal; iCt++)
//        {
//            sBinary = "0" + sBinary;
//        }
//        String sTemp = sBinary;
//        cs[0] = Integer.parseInt(sTemp.substring(4, 8));
//        cs[1] = Integer.parseInt(sTemp.substring(0, 4));
//        return cs;
//
//    }
//
//    public static int[] CourseorStatus(byte Byte_1, byte Byte_2)
//    {
//        int[] cs = new int[6];
//
//        byte b1 = (byte) Byte_1;
//        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
//
//
//        byte b2 = (byte) Byte_2;
//        String s2 = String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
//
//        String sCourse = s1+s2;
//
//        int iTotal = 16 - sCourse.length();
//
//        for (int iCt = 0; iCt < iTotal; iCt++)
//        {
//            sCourse = "0" + sCourse;
//        }
//        String sTemp = sCourse;
//
//        cs[0] = Integer.parseInt(sTemp.substring(6, 16),2);
//        cs[1] = Integer.parseInt(sTemp.substring(5, 6));
//        cs[2] = Integer.parseInt(sTemp.substring(4, 5));
//        cs[3] = Integer.parseInt(sTemp.substring(3, 4));
//        cs[4] = Integer.parseInt(sTemp.substring(2, 3));
//        cs[5] = Integer.parseInt(sTemp.substring(1, 2));
//
//        return cs;
//    }
//
//    public static String[] GetDeviceInfo(byte b)
//    {
//        String[] cs = new String[10];
//
//        String sDeviceInfo = Byte.toString(b);
//        int iTotal = 8 - sDeviceInfo.length();
//        for (int iCt = 0; iCt < iTotal; iCt++)
//        {
//            sDeviceInfo = "0" + sDeviceInfo;
//        }
//        cs[0] = sDeviceInfo.substring(0, 1);
//        cs[1] = sDeviceInfo.substring(1, 2);
//        cs[2] = sDeviceInfo.substring(2, 3);
//        if (sDeviceInfo.substring(2, 7) == "011")
//            cs[3] = "1";
//        else
//            cs[3] = "0";
//
//        if (sDeviceInfo.substring(2, 5) == "100")
//            cs[4] = "1";
//        else
//            cs[4] = "0";
//
//        if (sDeviceInfo.substring(2, 5) == "010")
//            cs[5] = "1";
//        else
//            cs[5] = "0";
//
//        if (sDeviceInfo.substring(2, 5) == "001")
//            cs[6] = "1";
//        else
//            cs[6] = "0";
//
//        if (sDeviceInfo.substring(2, 5) == "000")
//            cs[7] = "1";
//        else
//            cs[7] = "0";
//
//        cs[8] = sDeviceInfo.substring(5, 6);
//        cs[9] = sDeviceInfo.substring(6, 7);
//
//        return cs;
//    }
//    public static String ByteToString(byte[] oByte, int startPoint, int endPoint)
//    {
//        String sOut = "";
//        for (int i = startPoint; i <= endPoint; i++)
//        {
//                sOut += Integer.toHexString(oByte[i] & 0xff);
//        }
//        return sOut;
//    }
//    public static String ByteToStringIMEI(byte[] oByte, int startPoint, int endPoint)
//    {
//        String sOut = "";
//        for (int i = startPoint; i <= endPoint; i++)
//        {
//            if(i==startPoint)
//            {
//                    sOut =sOut+Integer.toHexString(oByte[i] & 0xff);
//            }
//            else
//            {
//                    sOut =sOut+CheckToAppend(Integer.toHexString(oByte[i] & 0xff));
//            }
//
//        }
//        return sOut;
//    }
//
//    private static String CheckToAppend(String hexString)
//    {
//        int value=Integer.parseInt(hexString);
//        String append=hexString;
//        if(value<10)
//        {
//                append = "0" + append;
//        }
//        return append;
//    }
//
//}
//
//
//class StringTools
//{
//    public static String toHexString(byte b[])
//    {
//        return StringTools.toHexString(b,0,-1,null).toString();
//    }
//    public static StringBuffer toHexString(byte b[], int ofs, int len, StringBuffer sb)
//    {
//        if (sb == null) { sb = new StringBuffer(); }
//        if (b != null) {
//            int bstrt = (ofs < 0)? 0 : ofs;
//            int bstop = (len < 0)? b.length : Math.min(b.length,(ofs + len));
//            for (int i = bstrt; i < bstop; i++) { StringTools.toHexString(b[i], sb); }
//        }
//        return sb;
//    }
//    public static StringBuffer toHexString(byte b, StringBuffer sb)
//    {
//        if (sb == null) { sb = new StringBuffer(); }
//        sb.append(HEX.charAt((b >> 4) & 0xF));
//        sb.append(HEX.charAt(b & 0xF));
//        return sb;
//    }
//
//    public static String toHexString(int val)
//    {
//        return StringTools.toHexString((long)val & 0xFFFFFFFFL, 32);
//    }
//    public static String toHexString(long val, int bitLen)
//    {
//        /* bounds check 'bitLen' */
//        if (bitLen <= 0) {
//            if ((val & 0xFFFFFFFF00000000L) != 0L) {
//                bitLen = 64;
//            } else
//            if ((val & 0x00000000FFFF0000L) != 0L) {
//                bitLen = 32;
//            } else
//            if ((val & 0x000000000000FF00L) != 0L) {
//                bitLen = 16;
//            } else {
//                bitLen = 8;
//            }
//        } else
//        if (bitLen > 64) {
//            bitLen = 64;
//        }
//
//        /* format and return hex value */
//        int nybbleLen = ((bitLen + 7) / 8) * 2;
//        StringBuffer hex = new StringBuffer(Long.toHexString(val).toUpperCase());
//        //Print.logInfo("NybbleLen: " + nybbleLen + " : " + hex + " [" + hex.length());
//        if ((nybbleLen <= 16) && (nybbleLen > hex.length())) {
//            String mask = "0000000000000000"; // 64 bit (16 nybbles)
//            hex.insert(0, mask.substring(0, nybbleLen - hex.length()));
//        }
//        return hex.toString();
//
//    }
//    public static final String HEX = "0123456789ABCDEF";
//
//    public static byte[] parseHex(String data, byte dft[])
//    {
//        if (data != null) {
//
//            /* get data string */
//            String d = data.toUpperCase();
//            String s = d.startsWith("0X")? d.substring(2) : d;
//
//            /* remove any invalid trailing characters */
//            // scan until we find an invalid character (or the end of the string)
//            for (int i = 0; i < s.length(); i++) {
//                if (HEX.indexOf(s.charAt(i)) < 0) {
//                    s = s.substring(0, i);
//                    break;
//                }
//            }
//
//            /* return default if nothing to parse */
//            if (s.equals("")) {
//                return dft;
//            }
//
//            /* right justify */
//            if ((s.length() & 1) == 1) { s = "0" + s; } // right justified
//
//            /* parse data */
//            byte rtn[] = new byte[s.length() / 2];
//            for (int i = 0; i < s.length(); i += 2) {
//                int c1 = HEX.indexOf(s.charAt(i));
//                if (c1 < 0) { c1 = 0; /* Invalid Hex char */ }
//                int c2 = HEX.indexOf(s.charAt(i+1));
//                if (c2 < 0) { c2 = 0; /* Invalid Hex char */ }
//                rtn[i/2] = (byte)(((c1 << 4) & 0xF0) | (c2 & 0x0F));
//            }
//
//            /* return value */
//            return rtn;
//
//        } else {
//            return dft;
//        }
//    }
//
//}
//
//
//
//class GPSinfo
//{
//        public String NoSatellite;
//        public String GGpsInfo;
//        public String Lat;
//        public String Long;
//        public String Speed;
//        public String Course;
//        public String SouthLat;
//        public String NorthLat;
//        public String EastLong;
//        public String WestLong;
//        public String ISGPSLocated;
//        public String ISGPSReal;
//}
//
//class LBSinfo
//{
//    public String MCC;
//    public String MNC;
//    public String LAC;
//    public String CI;
//}
//
//class Statusinfo
//{
//    public String IsActivated;
//    public String AccHigh;
//    public String ChargeOn;
//    public String SOSAlarm;
//    public String LowBatteryAlarm;
//    public String PowerCutAlarm;
//    public String ShockAlarm;
//    public String Normal;
//    public String GPSTrackingOn;
//    public String OilConnected;
//    public String ISGPSLocated;
//    public String ISGPSReal;
//    public String Voltage;
//    public String GSMSignalStrength;
//    public String DateTimeMilliseconds;
//}
//
//class Protocol_18N19N22N26
//{
//    public GPSinfo oGPSinfo;
//    public LBSinfo oLBSinfo;
//    public Statusinfo oStatusinfo;
//    public String IMEINo;
//    public String DateTime;
//    public String LBSLenth;
//    public String PhoneNo;
//    public String Lang;
//}
//
//class ErrorLog
//{
//        public String DateTime;
//        public String IMEINo;
//        public String Note;
//}
//
//class Transaction {
//    private MongoClient mongoClient;
//    private MongoDatabase database;
//
//    public Transaction() {
//        String server;
//        int port;
//        String databaseName;
//        String username;
//        String password;
//        try {
//            // Read the environment variables from the .env file
//            Map<String, String> env = new HashMap<String, String>();
//            BufferedReader reader = new BufferedReader(new FileReader(".env"));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split("=");
//                if (parts.length >= 2) {
//                    String key = parts[0];
//                    String value = parts[1];
//                    env.put(key, value);
//                }
//            }
//            reader.close();
//
//            // Get the values of your credentials from the environment variables
//            server = env.get("DB_HOST");
//            port = Integer.parseInt(env.get("DB_PORT"));
//            databaseName = env.get("DB_NAME");
//            username = env.get("DB_USERNAME");
//            password = env.get("DB_PASSWORD");
//            MongoClientURI uri = new MongoClientURI("mongodb+srv://" + username + ":" + password + "@" + server + "/" + databaseName + "?retryWrites=true&w=majority");
//            mongoClient = new MongoClient(uri);
//
//            // Get a reference to the database
//            database = mongoClient.getDatabase(databaseName);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void CloseConnection() {
//        // Close the MongoDB client
//        mongoClient.close();
//    }
//
//    public void InsertErrorLog(ErrorLog errorlog) {
//        String collectionName = "gps_error_logs";
//        MongoCollection<Document> collection = database.getCollection(collectionName);
//        errorlog.DateTime = Util.GMTtoISTDate(errorlog.DateTime);
//        Document document = new Document("IMEINo", errorlog.IMEINo)
//                .append("DateTime", errorlog.DateTime)
//                .append("Note", errorlog.Note);
//
//        collection.insertOne(document);
//    }
//
//    public void InsertGPSinfo(Protocol_18N19N22N26 oprotocol)
//    {
//        try
//        {
//            String collectionName = "gps_stream_datas";
//            // Get a reference to the collection
//            MongoCollection<Document> collection = database.getCollection(collectionName);
//
//            oprotocol.DateTime = Util.GMTtoISTDate(oprotocol.DateTime);
//
//            if (oprotocol.IMEINo.equals("357445100124219")) {
//                // Create a new document
//                Document document = new Document("IMEINo", oprotocol.IMEINo)
//                    .append("DateTime", oprotocol.DateTime)
//                    .append("vehicleName", "MH01AR9931")
//                    .append("NoSatellite", oprotocol.oGPSinfo.NoSatellite)
//                    .append("GGpsInfo", oprotocol.oGPSinfo.GGpsInfo)
//                    .append("Lat", oprotocol.oGPSinfo.Lat)
//                    .append("Long", oprotocol.oGPSinfo.Long)
//                    .append("Speed", oprotocol.oGPSinfo.Speed)
//                    .append("Course", oprotocol.oGPSinfo.Course)
//                    .append("SouthLat", oprotocol.oGPSinfo.SouthLat)
//                    .append("NorthLat", oprotocol.oGPSinfo.NorthLat)
//                    .append("EastLong", oprotocol.oGPSinfo.EastLong)
//                    .append("WestLong", oprotocol.oGPSinfo.WestLong)
//                    .append("ISGPSLocated", oprotocol.oGPSinfo.ISGPSLocated)
//                    .append("ISGPSReal", oprotocol.oGPSinfo.ISGPSReal);
//
//                // Insert the document
//                collection.insertOne(document);
//            }else{
//                // Create a new document
//                Document document = new Document("IMEINo", oprotocol.IMEINo)
//                    .append("DateTime", oprotocol.DateTime)
//                    .append("NoSatellite", oprotocol.oGPSinfo.NoSatellite)
//                    .append("GGpsInfo", oprotocol.oGPSinfo.GGpsInfo)
//                    .append("Lat", oprotocol.oGPSinfo.Lat)
//                    .append("Long", oprotocol.oGPSinfo.Long)
//                    .append("Speed", oprotocol.oGPSinfo.Speed)
//                    .append("Course", oprotocol.oGPSinfo.Course)
//                    .append("SouthLat", oprotocol.oGPSinfo.SouthLat)
//                    .append("NorthLat", oprotocol.oGPSinfo.NorthLat)
//                    .append("EastLong", oprotocol.oGPSinfo.EastLong)
//                    .append("WestLong", oprotocol.oGPSinfo.WestLong)
//                    .append("ISGPSLocated", oprotocol.oGPSinfo.ISGPSLocated)
//                    .append("ISGPSReal", oprotocol.oGPSinfo.ISGPSReal);
//
//                // Insert the document
//                collection.insertOne(document);
//            }
//        }
//        catch (Exception e1)
//        {
//            ErrorLog eL = new ErrorLog();
//            eL.IMEINo = oprotocol.IMEINo;
//            oprotocol.DateTime = Util.GMTtoISTDate(oprotocol.DateTime);
//            eL.DateTime = oprotocol.DateTime;
//            eL.Note = e1.toString();
//            Transaction oTransaction = new Transaction();
//            oTransaction.InsertErrorLog(eL);
//            oTransaction.CloseConnection();
//            eL = null;
//
//            e1.printStackTrace();
//        }
//    }
//
//    public void InsertStatusinfo(Protocol_18N19N22N26 oprotocol)
//    {
//        // try
//        // {
//        //     String server = "iotapp1.5d6lvc4.mongodb.net";
//        //     int port = 27017;
//        //     String databaseName = "chakraview_iot";
//        //     String collectionName = "GPSStatusData";
//        //     String username = "sagarbansalcc";
//        //     String password = "GSkIA36nI1p8aR5P";
//        //     MongoClientURI uri = new MongoClientURI("mongodb+srv://" + username + ":" + password + "@" + server + "/" + databaseName + "?retryWrites=true&w=majority");
//        //     MongoClient mongoClient = new MongoClient(uri);
//
//        //     // Get a reference to the database
//        //     MongoDatabase database = mongoClient.getDatabase(databaseName);
//
//        //     // Get a reference to the collection
//        //     MongoCollection<Document> collection = database.getCollection(collectionName);
//
//        //     // Create a new document
//        //     Document document = new Document("IMEINo", oprotocol.IMEINo)
//        //         .append("DateTime", oprotocol.oStatusinfo.DateTimeMilliseconds)
//        //         .append("IsActivated", oprotocol.oStatusinfo.IsActivated)
//        //         .append("AccHigh", oprotocol.oStatusinfo.AccHigh)
//        //         .append("ChargeOn", oprotocol.oStatusinfo.ChargeOn)
//        //         .append("SOSAlarm", oprotocol.oStatusinfo.LowBatteryAlarm)
//        //         .append("PowerCutAlarm", oprotocol.oStatusinfo.PowerCutAlarm)
//        //         .append("ShockAlarm", oprotocol.oStatusinfo.ShockAlarm)
//        //         .append("Normal", oprotocol.oStatusinfo.Normal)
//        //         .append("GPSTrackingOn", oprotocol.oStatusinfo.GPSTrackingOn)
//        //         .append("OilConnected", oprotocol.oStatusinfo.OilConnected)
//        //         .append("ISGPSLocated", oprotocol.oStatusinfo.ISGPSLocated)
//        //         .append("ISGPSReal", oprotocol.oStatusinfo.ISGPSReal)
//        //         .append("Voltage", oprotocol.oStatusinfo.Voltage)
//        //         .append("GSMSignalStrength", oprotocol.oStatusinfo.GSMSignalStrength);
//
//        //     // Insert the document
//        //     collection.insertOne(document);
//
//        //     // Close the MongoDB client
//        //     mongoClient.close();
//        // }
//        // catch (Exception e1)
//        // {
//        //         ErrorLog eL = new ErrorLog();
//        //         eL.IMEINo = oprotocol.IMEINo;
//        //         eL.DateTime = oprotocol.oStatusinfo.DateTimeMilliseconds;
//        //         eL.Note = e1.toString();
//        //         Transaction oTransaction = new Transaction();
//        //         oTransaction.InsertErrorLog(eL);
//        //         eL = null;
//        //         e1.printStackTrace();
//        // }
//    }
//}
