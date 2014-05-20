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
    int factor=gcd(w,h);
    int w1=w/factor;
    int h1=h/factor;
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

  static int gcd(int a,int b){
    if(b==0){
      return a;
    }else{
      return gcd(b,a%b);
    }
  }
}
