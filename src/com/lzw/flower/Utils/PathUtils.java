package com.lzw.flower.Utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by lzw on 14-4-30.
 */
public class PathUtils {
  static String appDir = "/flower/";

  public static String getAppDir() {
    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + appDir;
    checkDir(dir);
    return dir;
  }

  public static void checkDir(String dirPath) {
    File dir = new File(dirPath);
    if (dir.exists() == false) {
      dir.mkdirs();
    }
  }

  public static String getCameraPath() {
    return getAppDir() + "tmp";
  }

  public static String getBitmapPath() {
    return getAppDir() + "tmp.png";
  }

  public static String getCropPath() {
    return getAppDir() + "crop";
  }

  public static String getOriginPath() {
    return getAppDir() + "origin.png";
  }

  public static String getHandPath() {
    return getAppDir() + "hand.png";
  }
}
