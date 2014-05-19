package com.lzw.flower.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import com.lzw.flower.base.App;

import java.io.File;

/**
 * Created by lzw on 14-5-3.
 */
public class Crop {
  public static void startPhotoCrop(Activity cxt, Uri uri, String outputPath, int
      resultCode) {
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(uri, "image/*");
    int w= App.drawWidth;
    int h=App.drawHeight;
    int w1=w/100;
    int h1=h/100;
    intent.putExtra("crop", "true").putExtra("aspectX", w1).
        putExtra("aspectY", h1).putExtra("scale", true)
        .putExtra("outputX",w).putExtra("outputY",h).
        putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
    intent.putExtra("noFaceDetection", true);
    intent.putExtra("return-data", false);
    Uri uri1 = Uri.fromFile(new File(outputPath));
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
    cxt.startActivityForResult(intent, resultCode);
  }
}
