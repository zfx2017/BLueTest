package com.clj.blesample.operation;

import com.clj.blesample.R;

import java.io.InputStream;

/**
 * Created by zfx on 2018/3/27.
 */

public class EditTextToData {
    int[][] arr;
    int all_16_32 = 16;//16*16
    int all_2_4 = 2;//一个汉字等于两个字节
    int all_32_128 = 32;//汉字解析成16*16 所占字节数
    private int font_width = 8;//ascii码 8*16
    private int font_height = 16;
    private int all_16 = 16;

}
