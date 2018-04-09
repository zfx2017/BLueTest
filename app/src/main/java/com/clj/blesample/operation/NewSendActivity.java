package com.clj.blesample.operation;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.blesample.MainActivity;
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

public class NewSendActivity extends AppCompatActivity {
    public static final String KEY_DATA = "key_data";
    private BleDevice bleDevice;
    private TextView editText;
    private TextView editText2;
    private TextView editText3;
    private TextView textView;
    private Button button;
    private Button button2;
    private TextView txt;
    final Context context = this;
    private String  hex;
    private int  num;
    byte[] data4 = new byte[8];
    int[][] arr;
    int all_16_32 = 16;//16*16
    int all_2_4 = 2;//一个汉字等于两个字节
    int all_32_128 = 32;//汉字解析成16*16 所占字节数
    private int font_width = 8;//ascii码 8*16
    private int font_height = 16;
    private int all_16 = 16;
    private AsyncTask mAsyncTask;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsend_layout);
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        button = (Button) findViewById(R.id.buttonSend1);
        button2 = (Button) findViewById(R.id.buttonSend2);
        editText = (TextView) findViewById(R.id.writeText1);
        editText2 = (TextView) findViewById(R.id.writeText2);
        editText3 = (TextView) findViewById(R.id.mac);
        textView = (TextView) findViewById(R.id.textBack);
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        editText3.setText("设备名:"+name+"\n"+"MAC:"+mac);
       //txt = (TextView)findViewById(R.id.txt);
       //txt.setMovementMethod(ScrollingMovementMethod.getInstance());
         read();
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //getChineseByte("郑方向");
                // get prompts.xml view
                System.out.println("设备的名字是"+bleDevice.getName());
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("发送",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text

                                        hex = String.valueOf(userInput.getText());
                                        num = 1;
                                        if (TextUtils.isEmpty(hex)) {
                                            Toast.makeText(NewSendActivity.this,"输入不能为空", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        if(hex.length() > 4)
                                        {
                                            Toast.makeText(NewSendActivity.this,"最多输入四个字符", Toast.LENGTH_LONG).show();
                                            return;
                                        }else {
                                            Toast.makeText(NewSendActivity.this, "数据正在发送", Toast.LENGTH_LONG).show();
                                            editText.setText(hex);
                                        }
                                        //write(hex);
                                        new Thread(){
                                            public void run(){
                                                new writeTask().execute("JSON");
                                            }
                                        }.start();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
       // }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("发送",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text

                                        hex = String.valueOf(userInput.getText());
                                        num = 2;
                                        if (TextUtils.isEmpty(hex)) {
                                            Toast.makeText(NewSendActivity.this,"输入不能为空", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        if(hex.length() > 4)
                                        {
                                            Toast.makeText(NewSendActivity.this,"最多输入四个字符", Toast.LENGTH_LONG).show();
                                            return;
                                        }else {
                                            Toast.makeText(NewSendActivity.this, "数据正在发送", Toast.LENGTH_LONG).show();
                                            editText2.setText(hex);
                                        }
                                        //write(hex);
                                        new Thread(){
                                            public void run(){
                                                new writeTask().execute();
                                            }
                                        }.start();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
    private class writeTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPostExecute(String result) {
            //对UI组件的更新操作
            Toast.makeText(NewSendActivity.this, "发送成功", Toast.LENGTH_LONG).show();
        }
        @Override
        protected String doInBackground(String... params) {
            //耗时的操作
            write(hex,num);

            return params[0];
        }
        protected void  onCancelled (String result) {
        }
    }
    public byte[][] getAllResult(String hex,int num) {
        byte[][] data = new byte[0][];
        byte[][] data1 = new byte[8][17];

            for (int k = 0; k < hex.length(); k++) {
                String s = String.valueOf(hex.charAt(k));
                System.out.println(String.valueOf(hex.charAt(k)));
                byte[] bytes = s.getBytes();
                int i = bytes.length;//i为字节长度
                int j = s.length();//j为字符长度
                System.out.println(i);
                System.out.println(j);
                if (i == j) {
                    byte[] data5 = null;
                    int[] code = null;
                    int byteCount;
                    int lCount;
                    arr = new int[all_16_32][all_16_32];
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int m = 0; m < s.length(); m++) {
                        if (s.charAt(m) < 0x80) {             //字母
                            //arr = new byte[font_height][font_width];
                            data5 = read_a(s.charAt(0));
                            byteCount = 0;
                            for (int line = 0; line < 16; line++) {
                                lCount = 0;
                                for (int u = 0; u < 1; u++) {
                                    for (int w = 0; w < 8; w++) {
                                        if (((data5[byteCount] >> (7 - w)) & 0x1) == 1) {
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
                        data = HexUtil.getRowLineReverseCode1(stringBuffer.toString(), k);
                        data1[2 * k] = data[2 * k];
                        data1[2 * k + 1] = data[2 * k + 1];
                        try {
                            byte[] data3 = s.getBytes("GB2312");
                            data1[k][3] = data3[0];
                            Log.i("GB2312是--->>>", String.valueOf(data[0][2]));

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    InputStream is = getResources().openRawResource(R.raw.hzk16k);
                    data = HexUtil.getHex(HexUtil.getGBK(0, s), is, k);
                    data1[2 * k] = data[2 * k];
                    data1[2 * k + 1] = data[2 * k + 1];
                    try {
                        byte[] data3 = s.getBytes("GB2312");
                        System.out.println("偏移量是" + Arrays.toString(data3));
                        data1[k][2] = data3[0];
                        data1[k][3] = data3[1];
                        Log.i("GB2312是--->>>", String.valueOf(data4[1]));
                        Log.i("GB2312是--->>>", Arrays.toString(data4));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            data1[0][0] = (byte) num;
            data1[1][0] = (byte) num;
            data1[2][0] = (byte) num;
            data1[3][0] = (byte) num;
            data1[4][0] = (byte) num;
            data1[5][0] = (byte) num;
            data1[6][0] = (byte) num;
            data1[7][0] = (byte) num;
            data1[0][1] = 0;
            data1[1][1] = 16;
            data1[2][1] = 32;
            data1[3][1] = 48;
            data1[4][1] = 64;
            data1[5][1] = 80;
            data1[6][1] = 96;
            data1[7][1] = 112;
            return data1;
    }
    public byte[][] getChineseByte(String s) {
        int i;
        String s1 = null;
        byte[][] data = new byte[8][17];
        byte[][] data1 = new byte[8][17];
        for (i = 0; i < s.length(); i++) {
            //获取汉字的信息
            InputStream is = getResources().openRawResource(R.raw.hzk16k);
            data = HexUtil.getHex(HexUtil.getGBK(0, s), is,i);
            data1[2*i] = data[2*i];
            data1[2*i+1] = data[2*i+1];
        }
        return data1;
    }
    public void write(String hex,int num){
            //textView.setText("数据发送成功请勿重复点击");
            byte[][] data = new byte[0][];
            data = getAllResult(hex,num);
            for (int k = 0; k < 8; k++) {
                BleManager.getInstance().write(
                        bleDevice,
                        "0000fff0-0000-1000-8000-00805f9b34fb",
                        "0000fff6-0000-1000-8000-00805f9b34fb",
                        // hex.getBytes(),
                        data[k],

                        new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                Log.i("Success", "发送成功");
                               /* runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewSendActivity.this, "发送成功", Toast.LENGTH_LONG).show();
                                    }
                                });*/
                            }

                            @Override
                            public void onWriteFailure(final BleException exception) {
                                Log.i("rong", exception.toString());
                               /* runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("worong", exception.toString());
                                        Toast.makeText(NewSendActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                                    }
                                });*/
                            }
                        });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    public byte[][] getEnglishByte(String s) {
        int i;
        String s1 = null;
        byte[][] data = new byte[8][17];
        byte[][] data1 = new byte[8][17];
        for (i = 0; i < s.length(); i++) {
            //获取汉字的信息
            InputStream is = getResources().openRawResource(R.raw.asciizimo);
            data = HexUtil.getHex(HexUtil.getGBK(i, s), is,i);
            data1[2*i] = data[2*i];
            data1[2*i+1] = data[2*i+1];
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
            data2 = HexUtil.getRowLineReverseCode1(stringBuffer.toString(),i);
            data1[2*i] = data2[2*i];
            data1[2*i+1] = data2[2*i+1];
        }
        System.out.println(stringBuffer);
        return data1;
    }
    public void read(){
        System.out.println("zfxwyx");
        final StringBuffer stringBuffer=new StringBuffer();
      // final String[]  str = new String[8];
        BleManager.getInstance().read(
                bleDevice,
                "0000fff0-0000-1000-8000-00805f9b34fb",
                "0000fff9-0000-1000-8000-00805f9b34fb",
                new BleReadCallback() {

                    @Override
                    public void onReadSuccess(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(Arrays.toString(data));
                                //char ch = (char)data[1];
                                char[] originalTxt = new char[8];
                                if(data.length<20)
                                {
                                    Toast.makeText(NewSendActivity.this, "获取数据异常", Toast.LENGTH_LONG).show();
                                }else {
                                    for (int i = 0; i < 4; i++) {
                                        if (data[2 * i] == 0) {
                                            int num = data[2 * i + 1];
                                            char ch = (char) (num);
                                            System.out.println(ch);
                                            stringBuffer.append(ch);
                                            System.out.println(ch);
                                        } else {
                                            byte[] b = {data[2 * i], data[2 * i + 1]};
                                            String s = null;
                                            try {
                                                s = new String(b, "GB2312");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println(s);
                                            stringBuffer.append(s);
                                        }
                                    }
                                    for (int i = 5; i < 9; i++) {
                                        if (data[2 * i] == 0) {
                                            int num = data[2 * i + 1];
                                            char ch = (char) (num);
                                            System.out.println(ch);
                                            stringBuffer.append(ch);
                                            System.out.println(ch);
                                        } else {
                                            byte[] b = {data[2 * i], data[2 * i + 1]};
                                            String s = null;
                                            try {
                                                s = new String(b, "GB2312");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println(s);
                                            stringBuffer.append(s);
                                        }
                                    }
                                  /* for (int i = 5; i < 9; i++) {
                                       if (data[2 * i] == 0) {
                                           int num = data[2 * i + 1];
                                           char ch = (char) (num);
                                           System.out.println(ch);
                                           stringBuffer.append(ch);
                                       }
                                   }*/
                                    System.out.println(stringBuffer);
                                    System.out.println(stringBuffer.length());
                                    if (stringBuffer.length() <= 8) {
                                        editText.setText(stringBuffer.substring(0, 4));
                                        editText2.setText(stringBuffer.substring(4, 8));
                                    } else {
                                        Toast.makeText(NewSendActivity.this, "获取数据异常", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onReadFailure(final BleException exception){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewSendActivity.this, "数据读取失败", Toast.LENGTH_LONG).show();
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