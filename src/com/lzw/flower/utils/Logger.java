package com.lzw.flower.utils;

import android.util.Log;

/**
 * Created by lzw on 14-4-29.
 */
public class Logger {
  public static void d(String fmt,Object ...objs){
    Log.d("lzw",String.format(fmt,objs));
  }
}
