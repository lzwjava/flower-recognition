package com.lzw.flower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by lzw on 14-5-3.
 */
public class Crop {
  public static void startPhotoCrop(Activity cxt, Uri uri, String outputPath, int
      resultCode) {
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(uri, "image/*");
    intent.putExtra("crop", "true").putExtra("aspectX", 2).
        putExtra("aspectY", 1).putExtra("scale", true).putExtra("outputX",800)
        .putExtra("outputY",400).
        putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
    Uri uri1 = Uri.fromFile(new File(outputPath));
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
    cxt.startActivityForResult(intent, resultCode);
  }
}
