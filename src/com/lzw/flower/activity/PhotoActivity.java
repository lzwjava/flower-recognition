package com.lzw.flower.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.lzw.commons.NetAsyncTask;
import com.lzw.flower.R;
import com.lzw.flower.avobject.Photo;
import com.lzw.flower.base.ImageLoader;
import com.lzw.flower.utils.BitmapUtils;
import com.lzw.flower.utils.PathUtils;
import com.lzw.flower.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by lzw on 14-4-23.
 */
public class PhotoActivity extends Activity implements View.OnClickListener {
  public static final int GALLERY_REQUEST_CODE = 0;
  private static final int CAMERA_REQ_CODE = 1;
  public static final int CROP_REQ = 2;
  ImageLoader imageLoader;
  private Activity cxt;
  View camera,gallery,upload;
  Uri photoUri;
  ImageView photoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.person_photo_layout);
    cxt = this;
    imageLoader = ImageLoader.getInstance();
    findView();
  }

  private void findView() {
    camera=findViewById(R.id.camera);
    gallery=findViewById(R.id.gallery);
    upload=findViewById(R.id.upload);
    photoView= (ImageView) findViewById(R.id.photo);
    gallery.setOnClickListener(this);
    camera.setOnClickListener(this);
    upload.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    int id=v.getId();
    if(id==R.id.gallery){
      Utils.getGalleryPhoto(this, GALLERY_REQUEST_CODE);
    }else if(id==R.id.camera){
      Utils.takePhoto(cxt,CAMERA_REQ_CODE);
    }else if(id==R.id.upload){
      if(photoUri==null){
        return;
      }
      NetAsyncTask task = new NetAsyncTask(cxt) {

        @Override
        protected void doInBack() throws Exception {
          String path = PathUtils.getCameraPath();
          AVUser curUser = AVUser.getCurrentUser();
          AVFile avFile = AVFile.withAbsoluteLocalPath(curUser.getUsername()+".jpg", path);
          avFile.save();
          Photo photo = new Photo();
          photo.setPhoto(avFile);
          photo.setUser(curUser);
          photo.save();
        }

        @Override
        protected void onPost(boolean res) {
          if (res) {
            Utils.toast(cxt, R.string.uploadSucceed);
          } else {
            Utils.toast(cxt, R.string.no_network);
          }
        }
      };
      task.execute();
    }
  }

  public static void startPhotoZoom(Activity cxt, Uri uri, String outputPath, int
      resultCode) {
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(uri, "image/*");
    intent.putExtra("crop", "true").putExtra("scale", true).
        putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    Uri uri1 = Uri.fromFile(new File(outputPath));
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
    cxt.startActivityForResult(intent, resultCode);
  }

  @Override
  protected void onActivityResult(int requesetCode, int resultCode, Intent data) {
    if (resultCode != RESULT_CANCELED) {
      switch (requesetCode) {
        case GALLERY_REQUEST_CODE:
          if (data != null) {
            Uri data1 = data.getData();
            startPhotoZoom(cxt,data1, PathUtils.getCameraPath(), CROP_REQ);
            setImageByUri(data1);
          }
          break;
        case CAMERA_REQ_CODE:
          startPhotoZoom(cxt,Utils.getCameraUri(), PathUtils.getCameraPath(), CROP_REQ);
          break;
        case CROP_REQ:
          setImageByUri(Utils.getCameraUri());
          break;
      }
    }
  }

  private void setImageByUri(Uri uri) {
    try {
      Bitmap bitmap = BitmapUtils.getBitmapByUri(cxt, uri);
      photoUri=uri;
      photoView.setImageBitmap(bitmap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void getGalleryPhoto(Activity cxt) {
    Intent intent = new Intent();
    intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
    cxt.startActivityForResult(intent, GALLERY_REQUEST_CODE);
  }
}
