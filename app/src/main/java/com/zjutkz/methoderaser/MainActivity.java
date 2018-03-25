package com.zjutkz.methoderaser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Eraser
    public int test() {
        int a = 10;
        a = a + 1;
        return a;
    }
}
