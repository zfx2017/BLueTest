package com.clj.fastble.utils;

import android.util.Log;

import com.clj.fastble.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HexUtil  {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        if (data == null)
            return null;
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }


    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }


    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    public static String formatHexString(byte[] data) {
        return formatHexString(data, false);
    }

    public static String formatHexString(byte[] data, boolean addSpace) {
        if (data == null || data.length < 1)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
            if (addSpace)
                sb.append(" ");
        }
        return sb.toString().trim();
    }
    public static byte[] decodeHex(char[] data) {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }


    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }
    public static byte[] getGBK(int i, String s) {
        byte[] b_gbk = new byte[1];
        String ss = s.substring(i, i + 1);
        Log.i("hzkTest--->>>", ss);
        try {
            b_gbk = ss.getBytes("GBK");//必须转为GBK编码
            Log.i("hzkTest--->>>", "Length=" + b_gbk.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return b_gbk;
    }
    public static byte[][] getHex(byte[] data, InputStream inputStream,int num) {
        int size = 16;//字模的size
        int sum = (size * size / 8);
        byte iHigh, iLow;
        iHigh = data[1];
        iLow = data[0];
        Log.i("hzkTest--->>>", "h=" + iHigh + ",l=" + iLow);
        int IOffset;//偏移量
        IOffset = (94 * (iLow + 256 - 161) + (iHigh + 256 - 161)) * sum;//+256防止byte值为负   汉字字模在字库中的偏移地址
        Log.i("hzkTest--->>>", "IOffset=" + IOffset);

        byte[] iBuff = new byte[0];
        try {
            inputStream.read(new byte[IOffset]);
            iBuff = new byte[size * 2];
            inputStream.read(iBuff);

            getBinary(iBuff,num);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBinary(iBuff,num);
    }
    private static byte[][] getBinary(byte[] iBuff,int num) {
        StringBuffer stringBuffer=new StringBuffer();
        int i, j, k;
        for (i = 0; i < 16; i++) {
            for (j = 0; j < 2; j++)
                for (k = 0; k < 8; k++) {
                    if ((iBuff[i * 2 + j] & (0x80 >> k)) >= 1) {
                        stringBuffer.append("1");
                    } else {
                        stringBuffer.append("0");
                    }
                }
              //  stringBuffer.append("\n");
        }
      //  Log.i("zfxTest--->>>", stringBuffer.reverse().toString());
        Log.i("zfxTest--->>>", stringBuffer.toString());
      //  System.out.println("brijing");
       // getRowLineReverseCode(stringBuffer.toString(),num);
        return getRowLineReverseCode1(stringBuffer.toString(),num);
    }
    public static String strTo16(StringBuffer s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        System.out.println(str);
        return str;
    }
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    public static byte[][] getRowLineReverseCode1(String sb,int num) {
        //得到前8行16列 128个
        System.out.println("这是第几个数字" + num);
        StringBuffer reseve = new StringBuffer();
        for(int j=0;j<16;j++) {
            for (int i = 0; i < 16; i++) {
               // System.out.println("我不想人数");
              //  System.out.print(sb.charAt(16 * i+j));
                reseve.append(sb.charAt(16 * i+j));
            }
        }
        Log.i("hzkTest--->>>", "转换之后" + reseve.toString());
        byte[][] data = new byte[8][20];
        for(int i=0;i<16;i++)
        {
            if(i<8) {
            String sb1 = reseve.substring(8*(30-2*i), 8*(30-2*i)+8);
            int x = Integer.parseInt(sb1.toString(), 2);
            String sb2 = reseve.substring(8*(31-2*i), 8*(31-2*i)+8);
            int y = Integer.parseInt(sb2.toString(), 2);
                data[2*(num)][2*i+4] = (byte) x;
                data[2*(num)][2*i+5] = (byte) y;
            }else{
                String sb1 = reseve.substring(8*(30-2*i), 8*(30-2*i)+8);
                int x = Integer.parseInt(sb1.toString(), 2);
                String sb2 = reseve.substring(8*(31-2*i), 8*(31-2*i)+8);
                int y = Integer.parseInt(sb2.toString(), 2);
                data[2*(num)+1][2*(i-8)+4] = (byte) x;
                data[2*(num)+1][2*(i-8)+5] = (byte) y;
            }
        }
       /* for(int i=0;i<32;i++)
        {
            String sb1 = reseve.substring(8*i, 8*i+8);
            int x = Integer.parseInt(sb1.toString(), 2);
            System.out.println(x);
            if(i<16) {
                data[2*(num)][i+4] = (byte) x;
            }else{
                data[2*(num)+1][(i-16)+4] = (byte) x;
            }
        }*/
        System.out.println("返回的数据"+ Arrays.toString(data[0]));
        return data;
    }
    public static byte[][] getRowLineReverseCode(String sb,int num) {
        //得到前8行16列 128个
        System.out.println("这是第几个数字"+num);
        String pre128 = sb.substring(0, 128);
        String pre256 = sb.substring(128, 256);
        Log.i("hzkTest--->>>", "pre128--->>" + pre128);
        Log.i("hzkTest--->>>", "pre128--->>" + pre256);
        //取出每16列的8个数据
        byte[][] data = new byte[8][20];
        for (int i = 0; i < 16; i++) {
            StringBuffer sb1 = new StringBuffer();
            StringBuffer sb2 = new StringBuffer();
            for (int j = 112; j >= 0; ) {
                String s = pre128.substring(i + j, i + j + 1);
                sb1.append(s);
                j -= 16;
            }
            for (int j = 112; j >= 0; ) {
                String s1 = pre256.substring(i + j, i + j + 1);
                sb2.append(s1);
                j -= 16;
            }
            int x = Integer.parseInt(sb1.toString(), 2);
            int y = Integer.parseInt(sb2.toString(), 2);
            System.out.println(x);
               if(i<=7) {
                   data[2*(num)][2 * (i + 1)+3] = (byte) y;
                   data[2*(num)][2*(i+1)+2] = (byte) x;
               }else{
                   data[2*(num)+1][2 * (i - 7)+3] = (byte) y;
                   data[2*(num)+1][2*(i-7)+2] = (byte) x;
               }
        }
        System.out.println("返回的数据"+ Arrays.toString(data[0]));
        return data;
    }
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String extractData(byte[] data, int position) {
        return HexUtil.formatHexString(new byte[]{data[position]});
    }

}
