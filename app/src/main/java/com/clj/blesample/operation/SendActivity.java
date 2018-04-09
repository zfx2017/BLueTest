package com.clj.blesample.operation;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SendActivity extends AppCompatActivity {
    public static final String KEY_DATA = "key_data";
    private BleDevice bleDevice;
    private EditText editText;
    private Button button;
    private TextView txt;
    int[][] arr;
    int all_16_32 = 16;//16*16
    int all_2_4 = 2;//一个汉字等于两个字节
    int all_32_128 = 32;//汉字解析成16*16 所占字节数
    private int font_width = 8;//ascii码 8*16
    private int font_height = 16;
    private int all_16 = 16;
    byte[] data4 = new byte[8];
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_layout);

        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        txt = (TextView)findViewById(R.id.txt);
        txt.setMovementMethod(ScrollingMovementMethod.getInstance());
        read();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getChineseByte("郑方向");
                String hex = editText.getText().toString();
                if (TextUtils.isEmpty(hex)) {
                    addText(txt, "数据不能为空");
                    return;
                }
                byte[] bytes = hex.getBytes();
                int i = bytes.length;//i为字节长度
                int j = hex.length();//j为字符长度
                byte[][] data = new byte[8][20];
                if (i == j) {
                    data = drawString(hex);
                } else {
                    data = getChineseByte(hex);
                    try {
                        int m = hex.getBytes("GB2312").length;
                        byte[] data3 = hex.getBytes("GB2312");
                        System.out.println(Arrays.toString(data3));
                        for(int l = 0;l< m;l++)
                        {
                           data4[l] = data3[l];
                        }
                        Log.i("GB2312是--->>>", String.valueOf(data4.length));
                        data[0][2] = data4[0];
                        data[1][2] = data4[2];
                        data[2][2] = data4[4];
                        data[3][2] = data4[6];
                        data[0][3] = data4[1];
                        data[1][3] = data4[3];
                        data[2][3] = data4[5];
                        data[3][3] = data4[7];

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.println("返回的数据333"+ Arrays.toString(data));
                }
                data[0][1] = 0;
                data[1][1] = 16;
                data[2][1] = 32;
                data[3][1] = 48;
                data[4][1] = 64;
                data[5][1] = 80;
                data[6][1] = 96;
                data[7][1] = 112;
                Log.i("GB2312是--->>>", String.valueOf(data[0][2]));
                System.out.println("返回的数据333"+ Arrays.toString(data[3]));
                for (int k = 0; k < 1; k++) {

                    BleManager.getInstance().write(
                            bleDevice,
                            "0000fff0-0000-1000-8000-00805f9b34fb",
                            "0000fff6-0000-1000-8000-00805f9b34fb",
                            // hex.getBytes(),
                            data[k],
                           // "k".getBytes(),

                            new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addText(txt, "write success, current: " + current
                                                    + " total: " + total);
                                        }
                                    });
                                }

                                @Override
                                public void onWriteFailure(final BleException exception) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.i("worong", exception.toString());
                                            addText(txt, exception.toString());
                                        }
                                    });
                                }
                            });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }
    public byte[][] getChineseByte(String s) {
        int i;
        String s1 = null;
        byte[][] data = new byte[8][20];
        byte[][] data1 = new byte[8][20];
        for (i = 0; i < s.length(); i++) {
            //获取汉字的信息
            InputStream is = getResources().openRawResource(R.raw.hzk16k);
            data = HexUtil.getHex(HexUtil.getGBK(i, s), is,i);
            System.out.println("返回的数据22"+ Arrays.toString(data[0]));
            data1[2*i] = data[2*i];
           data1[2*i+1] = data[2*i+1];
        }
        return data1;
    }
    public byte[][] getEnglishByte(String s) {
        int i;
        String s1 = null;
        byte[][] data = new byte[8][20];
        byte[][] data1 = new byte[8][20];
        for (i = 0; i < s.length(); i++) {
            //获取汉字的信息
            InputStream is = getResources().openRawResource(R.raw.asciizimo);
            data = HexUtil.getHex(HexUtil.getGBK(i, s), is,i);
            data1[2*i+3] = data[2*i+3];
            data1[2*i+4] = data[2*i+4];
        }
        return data1;
    }
    protected byte[] read(int areaCode, int posCode) {
        byte[] data = null;
        try {
            int area = areaCode - 0xa0;  //区码
            int pos = posCode - 0xa0;    //位码
            InputStream in = getResources().openRawResource(R.raw.hzk16);  //打开中文字库的流
            long offset = all_32_128 * ((area - 1) * 94 + pos - 1);  //汉字在字库里的偏移量
            in.skip(offset);       //跳过偏移量
            data = new byte[all_32_128];  //定义缓存区大小
            in.read(data, 0, all_32_128);//读取汉字的点阵数据
            in.close();

        } catch (Exception ex) {
            System.err.println("*********rabbitWrite***********SORRY,THE FILE CAN'T BE READ");
        }
        return data;
    }

    //获取汉字的区、位
    protected int[] getByteCode(String str) {
        int[] byteCode = new int[2];
        try {
            byte[] data = str.getBytes("GB2312");
            byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
            byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }
    public byte[] readSingle(char c) {

            // 计算ascii码字符对应的位置
            InputStream inputStream = getResources().openRawResource(R.raw.asciizimo);
            long offset = c - ' ';
            System.out.println("偏移位置："+offset);
//            inputStream.skip(offset);
        try {
            System.out.println(inputStream.skip(offset*16));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[16];
        int r = 0;
        try {
            r = inputStream.read(buffer, 0, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (r != 16) {
            try {
                throw new IllegalAccessException("错误的起始索引位置：" + offset);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
            return buffer;
    }
    public byte[][] drawString(String str) {
        byte[][] data2 = new byte[8][17];
        byte[][] data1 = new byte[8][17];
        byte[] data = null;
        int[] code = null;
        int byteCount;
        int lCount;
        arr = new int[all_16_32][all_16_32];
        StringBuffer stringBuffer=new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < 0x80) {             //字母
                //arr = new byte[font_height][font_width];
                data = read_a(str.charAt(0));
                byteCount = 0;
                for (int line = 0; line < 16; line++) {
                    lCount = 0;
                    for (int k = 0; k < 1; k++) {
                        for (int j = 0; j < 8; j++) {
                            if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                                arr[line][lCount + 4] = 1;
                                System.out.print("*");
                                stringBuffer.append("1");
                            } else {
                                System.out.print("○");
                                stringBuffer.append("0");
                            }
                            lCount++;
                        }
                        byteCount++;
                    }
                    stringBuffer.append("00000000");
                   // HexUtil.getRowLineReverseCode(stringBuffer.toString(),str.length());
                    System.out.println();
                }
            }
            data2 = HexUtil.getRowLineReverseCode(stringBuffer.toString(),i);
            data1[2*i] = data2[2*i];
            data1[2*i+1] = data2[2*i+1];
        }
        System.out.println(stringBuffer);
        return data1;
    }
    public void read(){
        System.out.println("zfxwyx");
        final StringBuffer stringBuffer=new StringBuffer();
        BleManager.getInstance().read(
                bleDevice,
                "0000fff0-0000-1000-8000-00805f9b34fb",
                "0000fff7-0000-1000-8000-00805f9b34fb",
                new BleReadCallback() {

                    @Override
                    public void onReadSuccess(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(Arrays.toString(data));
                                //char ch = (char)data[1];
                                char[] originalTxt = new char[8];
                                for(int i=0;i<4;i++) {
                                   // byte[] b = {data[2 * i], data[2 * i + 1]};
                                    byte[] b = {-70, -57};
                                    String s = null;
                                    try {
                                        s = new String(b, "GB2312");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    stringBuffer.append(s);
                                     Log.i("nihao",s);
                                }
                                for(int i=5;i<9;i++)
                                {
                                    int num = 256*data[2*i]+data[2*i+1];
                                    char ch= (char)(num);
                                    System.out.println(ch);
                                    stringBuffer.append(ch);
                                    originalTxt[i-1] = ch;
                                }
                                System.out.println(originalTxt);
                                editText.setText(stringBuffer);
                                //addText(txt, HexUtil.formatHexString(data, true));
                            }
                        });
                    }

                    @Override
                    public void onReadFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // addText(txt, exception.toString());
                            }
                        });
                    }
                });
    }
    public byte[] read_a(char char_num) {
        byte[] dataE = null;
        int ascii = (int) char_num;
        try {
            dataE = new byte[all_16];//定义缓存区的大小
            InputStream inputStream = getResources().openRawResource(R.raw.asc16);  //打开ascii字库的流
            int offset = ascii * 16;//ascii码在字库里的偏移量
            inputStream.skip(offset);
            inputStream.read(dataE, 0, all_16);//读取字库中ascii码点阵数据
            inputStream.close();
            return dataE;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataE;
    }

}