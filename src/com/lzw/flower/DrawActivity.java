package com.lzw.flower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.lzw.flower.Utils.PathUtils;
import com.lzw.flower.Utils.Utils;
import com.lzw.flower.fragment.DrawFragment;
import com.lzw.flower.fragment.RecogFragment;
import com.lzw.flower.fragment.ResultFragment;
import com.lzw.flower.fragment.WaitFragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DrawActivity extends Activity implements View.OnClickListener {
  public static final int CAMERA_RESULT = 1;
  public static final int CROP_RESULT = 2;

  public static final int DRAW_FRAGMENT = 0;
  public static final int RECOG_FRAGMENT = 1;
  public static final int RESULT_FRAGMENT = 2;
  ImageView imgView;
  Button okBtn;
  public static byte[] imgBytes;
  DrawView drawView;
  Bitmap originImg;
  String imgPath = "/mnt/sdcard/titan.jpg";
  public static DrawActivity instance;
  ListView flowerList;
  View dir, clear;
  public static final int IMAGE_RESULT = 0;
  String cropPath;
  Tooltip toolTip;
  int curFragment=-1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    instance = this;
    cropPath =PathUtils.getCropPath();
    setContentView(R.layout.draw_layout);
    imgView = (ImageView) findViewById(R.id.img);
    drawView = (DrawView) findViewById(R.id.drawView);
    clear = findViewById(R.id.clear);
    dir = findViewById(R.id.dir);
    dir.setOnClickListener(this);
    clear.setOnClickListener(this);
    setSize();
    if (App.debug==false) {
      originImg = BitmapFactory.decodeFile(imgPath);
    } else {
      Intent intent = getIntent();
      Uri uri = intent.getData();
      if (uri != null) {
        setImageByUri(uri, 0);
      } else {
        //Uri path = Uri.parse("android.resource://com.lzw.flower/"
        // + R.drawable.flower_water);
        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.flower_water);
        String imgPath=PathUtils.getCameraPath();
        BitmapUtils.saveBitmapToPath(bitmap,imgPath);
        Uri uri1=Uri.fromFile(new File(imgPath));
        setImageByUri(uri1, 0);
      }
    }
    showDrawFragment();
    toolTip=new Tooltip(this);
    if(App.debug){
      recogOk();
    }
  }

  private void showDrawFragment() {
    curFragment= DRAW_FRAGMENT;
    showFragment(new DrawFragment());
  }

  private void setSize() {
    setWholeSize();
    setViewSize(imgView);
    setViewSize(drawView);
  }

  private void setWholeSize() {
    int width = getResources().getDimensionPixelSize(R.dimen.draw_width);
    int height=getResources().getDimensionPixelSize(R.dimen.draw_height);
    App.drawWidth=width;
    App.drawHeight=height;
  }

  private void setViewSize(View v) {
    ViewGroup.LayoutParams lp = v.getLayoutParams();
    lp.width=App.drawWidth;
    lp.height=App.drawHeight;
    v.setLayoutParams(lp);
  }

  private void setImageByUri(final Uri uri, final float angle) {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Bitmap bitmap = null;
        try {
          bitmap = MediaStore.Images.Media.getBitmap(
              getApplicationContext().getContentResolver(),
              uri);
        } catch (IOException e) {
          e.printStackTrace();
        }
        int originW,originH;
        originW=bitmap.getWidth();
        originH=bitmap.getHeight();
        if(originW!=App.drawWidth || originH!=App.drawHeight){
          int originRadio=(int)(originW*1.0f/originH);
          int radio=(int)(App.drawWidth*1.0f/App.drawHeight);
          if(originRadio==radio){
            bitmap=Bitmap.createScaledBitmap(bitmap,App.drawWidth,App.drawHeight,false);
          }else{
            Crop.startPhotoCrop(DrawActivity.this,uri, cropPath, CROP_RESULT);
            return;
          }
        }
        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.addBitmapToMemoryCache("origin", bitmap);
        originImg=bitmap;
        imgView.setImageBitmap(bitmap);
        int w = imgView.getWidth();
        int h = imgView.getHeight();
        Logger.d("imageview w=%d h=%d",w,h);
        Logger.d("origin w=%d h=%d",originImg.getWidth(),originImg.getHeight());
        drawView.setOriginBitmap(originImg, imgView);
        Logger.d("drawview w=%d h=%d",drawView.getWidth(),drawView.getHeight());
      }
    }, 500);
  }

  private void showFragment(Fragment fragment) {
    FragmentTransaction trans = getFragmentManager().beginTransaction();
    trans.replace(R.id.rightLayout, fragment);
    trans.commit();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.ok) {
      if(drawView.isDrawFinish()){
        showRecogFragment();
      }else{
        Utils.alertDialog(this,R.string.please_draw_finish);
      }
      //saveBitmap();
    } else if (id == R.id.recogOk) {
      recogOk();
    } else if (id == R.id.recogNo) {
      recogNo();
    } else if (id == R.id.dir) {
      Utils.getGalleryPhoto(this, IMAGE_RESULT);
    } else if (id == R.id.clear) {
      clearEverything();
    } else if (id == R.id.resultNo) {
      showRecogFragment();
    }else if(id==R.id.help){
      if(curFragment==DRAW_FRAGMENT){
        toolTip.start();
      }
    }
  }

  private void showRecogFragment() {
    curFragment= RECOG_FRAGMENT;
    showFragment(new RecogFragment());
  }

  public void clearEverything() {
    drawView.clear(imgView);
    showDrawFragment();
  }

  public void saveBitmap() {
    Bitmap cachebm = drawView.getCacheBm();
    showDrawPicture(cachebm);
  }

  public void showDrawPicture(Bitmap tmpBm) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    Bitmap bm = drawView.getCacheBm();
    LayoutInflater inflater = LayoutInflater.from(this);
    View dialog = View.inflate(this, R.layout.dialog, null);
    ImageView originImgView = (ImageView) dialog.findViewById(R.id.origin),
        handImg = (ImageView) dialog.findViewById(R.id.hand),
        hand1Img = (ImageView) dialog.findViewById(R.id.hand1);
    Bitmap imageViewBitmap = drawView.getOriginBitmap();
    //Bitmap imageViewBitmap=Bitmap.createBitmap(imageViewBitmap,0,0,tmpBm.getWidth(),tmpBm.getHeight());

    originImgView.setImageBitmap(imageViewBitmap);
    handImg.setImageBitmap(tmpBm);
    //Bitmap conBm = BitmapUtils.toGreyImg(bm);
    hand1Img.setImageBitmap(tmpBm);
    builder.setView(dialog);
    builder.show();

    BitmapUtils.saveBitmapToPath(imageViewBitmap, "/mnt/sdcard/1.png");
    BitmapUtils.saveBitmapToPath(tmpBm, "/mnt/sdcard/2.png");
  }

  private void recogNo() {
    showFragment(new DrawFragment());
  }

  public void undo(View v) {
    drawView.undo();
  }

  public void redo(View v) {
    drawView.redo();
  }

  public void settings(View v) {
  }

  public void camera(View v) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri uri;
    uri = getCameraUri();
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    startActivityForResult(intent, CAMERA_RESULT);
  }

  public Uri getCameraUri() {
    Uri uri;
    String path = PathUtils.getCameraPath();
    uri = Uri.fromFile(new File(path));
    return uri;
  }

  public void ok(View v) {
    Utils.showImage(this, drawView.getCacheBm());
  }

  public void recogOk() {
    Intent intent=new Intent(this,ResultActivity.class);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requesetCode, int resultCode, Intent data) {
    if (resultCode != RESULT_CANCELED) {
      switch (requesetCode) {
        case IMAGE_RESULT:
          if (data != null) {
            Uri data1 = data.getData();
            setImageByUri(data1, 0);
          }
          break;
        case CAMERA_RESULT:
          setImageByUri(getCameraUri(), 0);
          break;
        case CROP_RESULT:
          Uri uri=Uri.fromFile(new File(cropPath));
          setImageByUri(uri,0);
          break;
      }
    }
  }

}
