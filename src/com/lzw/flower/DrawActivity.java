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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.lzw.flower.Utils.Utils;
import com.lzw.flower.fragment.DrawFragment;
import com.lzw.flower.fragment.RecogFragment;
import com.lzw.flower.fragment.ResultFragment;
import com.lzw.flower.fragment.WatiFragment;

import java.io.IOException;
import java.util.List;

public class DrawActivity extends Activity implements View.OnClickListener {

  public static final int GOT_DATA = 0;
  ImageView imgView;
  Button okBtn;
  public static byte[] imgBytes;
  DrawView drawView;
  Bitmap originImg;
  String imgPath = "/mnt/sdcard/titan.jpg";
  public static DrawActivity instance;
  ListView flowerList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    instance = this;

    setContentView(R.layout.draw_layout);
    imgView = (ImageView) findViewById(R.id.img);
    drawView = (DrawView) findViewById(R.id.drawView);
    if (App.debug) {
      originImg = BitmapFactory.decodeFile(imgPath);
    } else {
      Intent intent=getIntent();
      Uri uri = intent.getData();
      if(uri!=null){
        String path=uri.getPath();
        originImg=BitmapFactory.decodeFile(path);
      }else{
        originImg=BitmapFactory.decodeResource(getResources(),R.drawable.flower_water);
      }
    }
    imgView.setImageBitmap(originImg);
    DrawFragment drawFragment=new DrawFragment();
    showFragment(new RecogFragment());
  }

  private void showFragment(Fragment fragment) {
    FragmentTransaction trans = getFragmentManager().beginTransaction();
    trans.replace(R.id.rightLayout,fragment);
    trans.commit();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.ok) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      Bitmap bm = drawView.getCacheBm();
      LayoutInflater inflater = LayoutInflater.from(this);
      View dialog = View.inflate(this, R.layout.dialog, null);
      ImageView originImgView = (ImageView) dialog.findViewById(R.id.origin),
          handImg = (ImageView) dialog.findViewById(R.id.hand),
          hand1Img = (ImageView) dialog.findViewById(R.id.hand1);
      originImgView.setImageBitmap(originImg);
      handImg.setImageBitmap(bm);
      //Bitmap conBm = BitmapUtils.toGreyImg(bm);
      hand1Img.setImageBitmap(bm);
      builder.setView(dialog);
      builder.show();
    }else if(id==R.id.recogOk){
      recogOk();
    }else if(id==R.id.recogNo){
      recogNo();
    }
  }

  private void recogNo() {
    showFragment(new DrawFragment());
  }

  Handler handler=new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message msg) {
      int what = msg.what;
      boolean res=false;
      if(what==GOT_DATA){
        List<Data> datas= (List<Data>) msg.obj;
        showFragment(new ResultFragment(datas));
        res=true;
      }
      return res;
    }
  });

  public void undo(View v) {
    drawView.undo();
  }

  public void redo(View v) {
    drawView.redo();
  }

  public void clear(View v) {
    drawView.clear();
  }

  public void crop(View v) {
  }

  public void settings(View v) {
  }

  public void camera(View v){
    Intent intent=new Intent(this,CameraActivity.class);
    startActivity(intent);
  }

  public void ok(View v) {
    Utils.showImage(this, drawView.getCacheBm());
  }

  public void recogOk() {
    showFragment(new WatiFragment());
    new Thread(new Runnable() {
      @Override
      public void run() {
        List<Data> datas=Web.getDatas();
        try {
          Web.downloadBitmaps(datas);
          Message msg = handler.obtainMessage();
          msg.what= GOT_DATA;
          msg.obj=datas;
          handler.sendMessage(msg);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
