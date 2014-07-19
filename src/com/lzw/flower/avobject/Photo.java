package com.lzw.flower.avobject;

import android.graphics.Bitmap;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by lzw on 14-4-24.
 */

@AVClassName("Photo")
public class Photo extends AVObject {
  public static final String PHOTO = "photo";
  public static final String USER = "user";
  Bitmap bitmap;
  //AVUser user;

  public AVFile getPhoto() {
    return getAVFile(PHOTO);
  }

  public void setPhoto(AVFile photo) {
    put(PHOTO, photo);
  }

  public AVUser getUser() {
    return getAVUser(USER);
  }

  public void setUser(AVUser user) {
    put(USER, user);
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }
}
