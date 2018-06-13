package com.xuan.dotloadingbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DotLoadingBar dot_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dot_bar=findViewById(R.id.dot_bar);



    }

    public void startAnim(View view) {
        //加载数据显示
        dot_bar.setVisibility(View.VISIBLE);
    }

    public void stopAnim(View view) {

        //加载完成隐藏
        dot_bar.setVisibility(View.GONE);
    }
}
