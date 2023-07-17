package com.gpsstreaming.utils;

public class StringTools {
    public static String toHexString(byte b[])
    {
        return StringTools.toHexString(b,0,-1,null).toString();
    }
    public static StringBuffer toHexString(byte b[], int ofs, int len, StringBuffer sb)
    {
        if (sb == null) { sb = new StringBuffer(); }
        if (b != null) {
            int bstrt = (ofs < 0)? 0 : ofs;
            int bstop = (len < 0)? b.length : Math.min(b.length,(ofs + len));
            for (int i = bstrt; i < bstop; i++) { StringTools.toHexString(b[i], sb); }
        }
        return sb;
    }
    public static StringBuffer toHexString(byte b, StringBuffer sb)
    {
        if (sb == null) { sb = new StringBuffer(); }
        sb.append(HEX.charAt((b >> 4) & 0xF));
        sb.append(HEX.charAt(b & 0xF));
        return sb;
    }

    public static String toHexString(int val)
    {
        return StringTools.toHexString((long)val & 0xFFFFFFFFL, 32);
    }
    public static String toHexString(long val, int bitLen)
    {
        /* bounds check 'bitLen' */
        if (bitLen <= 0) {
            if ((val & 0xFFFFFFFF00000000L) != 0L) {
                bitLen = 64;
            } else
            if ((val & 0x00000000FFFF0000L) != 0L) {
                bitLen = 32;
            } else
            if ((val & 0x000000000000FF00L) != 0L) {
                bitLen = 16;
            } else {
                bitLen = 8;
            }
        } else
        if (bitLen > 64) {
            bitLen = 64;
        }

        /* format and return hex value */
        int nybbleLen = ((bitLen + 7) / 8) * 2;
        StringBuffer hex = new StringBuffer(Long.toHexString(val).toUpperCase());
        //Print.logInfo("NybbleLen: " + nybbleLen + " : " + hex + " [" + hex.length());
        if ((nybbleLen <= 16) && (nybbleLen > hex.length())) {
            String mask = "0000000000000000"; // 64 bit (16 nybbles)
            hex.insert(0, mask.substring(0, nybbleLen - hex.length()));
        }
        return hex.toString();

    }
    public static final String HEX = "0123456789ABCDEF";

    public static byte[] parseHex(String data, byte dft[])
    {
        if (data != null) {

            /* get data string */
            String d = data.toUpperCase();
            String s = d.startsWith("0X")? d.substring(2) : d;

            /* remove any invalid trailing characters */
            // scan until we find an invalid character (or the end of the string)
            for (int i = 0; i < s.length(); i++) {
                if (HEX.indexOf(s.charAt(i)) < 0) {
                    s = s.substring(0, i);
                    break;
                }
            }

            /* return default if nothing to parse */
            if (s.equals("")) {
                return dft;
            }

            /* right justify */
            if ((s.length() & 1) == 1) { s = "0" + s; } // right justified

            /* parse data */
            byte rtn[] = new byte[s.length() / 2];
            for (int i = 0; i < s.length(); i += 2) {
                int c1 = HEX.indexOf(s.charAt(i));
                if (c1 < 0) { c1 = 0; /* Invalid Hex char */ }
                int c2 = HEX.indexOf(s.charAt(i+1));
                if (c2 < 0) { c2 = 0; /* Invalid Hex char */ }
                rtn[i/2] = (byte)(((c1 << 4) & 0xF0) | (c2 & 0x0F));
            }

            /* return value */
            return rtn;

        } else {
            return dft;
        }
    }
}
