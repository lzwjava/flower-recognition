package com.lzw.flower.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.lzw.flower.R;
import com.lzw.flower.activity.LoginActivity;
import com.lzw.flower.draw.DrawActivity;

/**
 * Created by lzw on 14-5-21.
 */
public class SplashActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.spalash_layout);
    int time=1000;
    if(App.debug){
      time=0;
    }
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
      }
    },time);
  }
}
