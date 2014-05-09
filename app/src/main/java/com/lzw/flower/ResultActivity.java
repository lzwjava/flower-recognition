package com.lzw.flower;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.lzw.flower.fragment.ResultFragment;

import java.io.IOException;
import java.util.List;

/**
 * Created by lzw on 14-5-4.
 */
public class ResultActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.result_activity);
  }
}
