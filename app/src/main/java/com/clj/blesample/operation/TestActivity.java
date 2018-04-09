package com.clj.blesample.operation;

/**
 * Created by zfx on 2018/3/23.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clj.blesample.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class TestActivity extends AppCompatActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        Button button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  /*
        * 循环取得每个汉字
        * */
            }
        });

    }


}
