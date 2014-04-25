package com.lzw.flower;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by lzw on 14-4-25.
 */
public class DrawActivity extends Activity implements View.OnClickListener {
  ImageView imgView;
  Button okBtn;
  public static byte[] imgBytes;
  DrawView drawView;
  DrawView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.draw_layout);
    imgView= (ImageView) findViewById(R.id.img);
    okBtn= (Button) findViewById(R.id.ok);
    drawView= (DrawView) findViewById(R.id.drawView);
    okBtn.setOnClickListener(this);
    if(imgBytes!=null){
      Bitmap bitmap= BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
      imgView.setImageBitmap(bitmap);
    }
  }

  @Override
  public void onClick(View v) {
    int id=v.getId();
    if(id==R.id.ok){
      AlertDialog.Builder builder=new AlertDialog.Builder(this);
      builder.setIcon()
    }
  }
}
