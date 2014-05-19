package com.lzw.flower.result;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by lzw on 14-5-17.
 */
public class Image {
  String tag;
  String filename;
  String filePath;
  String contentType = "image/png";

  public Image(String filePath, String filename, String tag) {
    this.filePath = filePath;
    this.filename = filename;
    this.tag = tag;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public byte[] getData() {
    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    return stream.toByteArray();
  }
}
