package com.gpsstreaming.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Util {
    public static String GMTtoISTDate(String GMTDateTime) {
        // Define the input datetime in GMT format
        var inputDateTime = GMTDateTime +" GMT";

        // Create a DateTimeFormatter object for parsing the input datetime
        var formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss z");

        // Parse the input datetime string into a LocalDateTime object
        var dateTime = LocalDateTime.parse(inputDateTime, formatter);

        // Define the ZoneId for IST timezone
        var istZoneId = ZoneId.of("Asia/Kolkata");

        // Create a ZonedDateTime object for the input datetime in IST timezone
        var istDateTime = ZonedDateTime.of(dateTime, ZoneId.of("GMT")).withZoneSameInstant(istZoneId);

        // Format the output datetime in IST timezone
        var outputDateTime = istDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Print the output datetime in IST format
        System.out.println("IST datetime: " + outputDateTime);
        return outputDateTime;
    }

    public static String bytesToHex(byte[] in)
    {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in)
        {   // Integer.toHexString(b);
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String getDateTime(String p)
    {
        var s = "20" + p.substring(0, 2) + "/" + p.substring(2, 4) + "/" + p.substring(4, 6) + " " + p.substring(6, 8) + ":" + p.substring(8, 10) + ":" + p.substring(10, 12);
        try
        {

            return s;
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    public static Long getTimeDiffInSeconds(String firstDate, String nextDate){
        Long lDiff = 0l;
        var f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date d1 = f.parse(firstDate);
            Date d2 = f.parse(nextDate);
            long seconds_f = d1.getTime();
            long seconds_n = d2.getTime();
            lDiff = (seconds_n - seconds_f) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lDiff;
    }

    public static String getCurrentDateTime(){
        var sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        var today = sdf.format(new Date());
        return today;
    }
    public static String ByteToStringBase10(byte[] oByte, int startPoint, int endPoint)
    {
        var sOut = "";
        for (int i = startPoint; i <= endPoint; i++)
        {
            if(Integer.parseInt(Byte.toString(oByte[i])) < 10)
                sOut += "0" + Integer.parseInt(Byte.toString(oByte[i]));
            else
                sOut += Integer.parseInt(Byte.toString(oByte[i]));
        }
        return sOut;
    }

    public static String GetLatOrLong(byte Byte_1, byte Byte_2, byte Byte_3, byte Byte_4)
    {
        List<Byte> oByt = new ArrayList<Byte>();
        oByt.add(Byte_1);
        oByt.add(Byte_2);
        oByt.add(Byte_3);
        oByt.add(Byte_4);

        Byte[] bytearray = oByt.toArray(new Byte[oByt.size()]);
        long ValDecimal = 0;
        for (int i = 0; i < bytearray.length; i++)
        {
            ValDecimal = (ValDecimal << 8) + (bytearray[i] & 0xff);
        }
        double X = (double)(ValDecimal/ 500 / 60 / 60);
        double Y = (double)(ValDecimal / 500.0 / 60.0) - (X * 60);
        return String.valueOf(X + Y / 60);
    }


    public static int[] getGPSinfoSatcount(byte bytes)
    {
        int[] cs = new int[2];
        var sHex = Byte.toString(bytes);
        int a = Integer.parseInt(sHex);
        var sBinary = Integer.toBinaryString(a);
        int iTotal = 8 - sBinary.length();

        for (int iCt = 0; iCt < iTotal; iCt++)
        {
            sBinary = "0" + sBinary;
        }
        var sTemp = sBinary;
        cs[0] = Integer.parseInt(sTemp.substring(4, 8));
        cs[1] = Integer.parseInt(sTemp.substring(0, 4));
        return cs;

    }

    public static int[] CourseorStatus(byte Byte_1, byte Byte_2)
    {
        int[] cs = new int[6];

        byte b1 = (byte) Byte_1;
        var s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');


        byte b2 = (byte) Byte_2;
        var s2 = String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');

        var sCourse = s1+s2;

        int iTotal = 16 - sCourse.length();

        for (int iCt = 0; iCt < iTotal; iCt++)
        {
            sCourse = "0" + sCourse;
        }
        var sTemp = sCourse;

        cs[0] = Integer.parseInt(sTemp.substring(6, 16),2);
        cs[1] = Integer.parseInt(sTemp.substring(5, 6));
        cs[2] = Integer.parseInt(sTemp.substring(4, 5));
        cs[3] = Integer.parseInt(sTemp.substring(3, 4));
        cs[4] = Integer.parseInt(sTemp.substring(2, 3));
        cs[5] = Integer.parseInt(sTemp.substring(1, 2));

        return cs;
    }

    public static String[] GetDeviceInfo(byte b)
    {
        String[] cs = new String[10];

        var sDeviceInfo = Byte.toString(b);
        int iTotal = 8 - sDeviceInfo.length();
        for (int iCt = 0; iCt < iTotal; iCt++)
        {
            sDeviceInfo = "0" + sDeviceInfo;
        }
        cs[0] = sDeviceInfo.substring(0, 1);
        cs[1] = sDeviceInfo.substring(1, 2);
        cs[2] = sDeviceInfo.substring(2, 3);
        if (sDeviceInfo.substring(2, 7) == "011")
            cs[3] = "1";
        else
            cs[3] = "0";

        if (sDeviceInfo.substring(2, 5) == "100")
            cs[4] = "1";
        else
            cs[4] = "0";

        if (sDeviceInfo.substring(2, 5) == "010")
            cs[5] = "1";
        else
            cs[5] = "0";

        if (sDeviceInfo.substring(2, 5) == "001")
            cs[6] = "1";
        else
            cs[6] = "0";

        if (sDeviceInfo.substring(2, 5) == "000")
            cs[7] = "1";
        else
            cs[7] = "0";

        cs[8] = sDeviceInfo.substring(5, 6);
        cs[9] = sDeviceInfo.substring(6, 7);

        return cs;
    }
    public static String ByteToString(byte[] oByte, int startPoint, int endPoint)
    {
        var sOut = "";
        for (int i = startPoint; i <= endPoint; i++)
        {
            sOut += Integer.toHexString(oByte[i] & 0xff);
        }
        return sOut;
    }
    public static String ByteToStringIMEI(byte[] oByte, int startPoint, int endPoint)
    {
        var sOut = "";
        for (int i = startPoint; i <= endPoint; i++)
        {
            if(i==startPoint)
            {
                sOut =sOut+Integer.toHexString(oByte[i] & 0xff);
            }
            else
            {
                sOut =sOut+CheckToAppend(Integer.toHexString(oByte[i] & 0xff));
            }

        }
        return sOut;
    }

    private static String CheckToAppend(String hexString)
    {
        int value=Integer.parseInt(hexString);
        var append=hexString;
        if(value<10)
        {
            append = "0" + append;
        }
        return append;
    }
}
