package com.example.zack.plugintest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abstractclass.ITestInterface;
import com.abstractclass.RealClass;
import com.test.lib.MyTestLib;
import com.test.lib.MyTestLibUtil;

public class MainActivity extends Activity implements ITestInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RealClass test1 = new RealClass();
        test1.doAbstractHandler();
        test1.doHandler();
        test1.setTestInterface(this);
        test1.doSelfHandler();
        doHandleProjectOne();
        callTestLibFunction();
        callTestLibFunction("");
    }

    @Override
    public void onTestInterface() {
        Log.e("MainActivity", "onTestInterface");
    }

    @Override
    public void onNoUseInterface() {

    }

    private void doHandleProjectOne() {
        findViewById(R.id.btProjectOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("plugintest://projectone");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    MainActivity.this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Intent intent = new Intent(MainActivity.this, ProjectOneActivity.class);
                //MainActivity.this.startActivity(intent);
            }
        });
    }

    private void callTestLibFunction() {
        MyTestLib myTestLib = new MyTestLib();
        myTestLib.doTestLib();
        MyTestLibUtil myTestLibUtil = new MyTestLibUtil();
        myTestLibUtil.doTestLibUtil();
    }

    private void callTestLibFunction(String str) {
        MyTestLib myTestLib = new MyTestLib();
        myTestLib.doTestLib();
        MyTestLibUtil myTestLibUtil = new MyTestLibUtil();
        myTestLibUtil.doTestLibUtil();
        Log.i("dddd", str);
    }


    private void callTestLibFunction222222(String str) {
        MyTestLib myTestLib = new MyTestLib();
        myTestLib.doTestLib();
        MyTestLibUtil myTestLibUtil = new MyTestLibUtil();
        myTestLibUtil.doTestLibUtil();
        Log.i("dddd", str);
    }
}
