package com.lzw.flower.result;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import com.lzw.flower.R;

/**
 * Created by lzw on 14-5-4.
 */
public class ResultActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.result_activity);
  }
}
