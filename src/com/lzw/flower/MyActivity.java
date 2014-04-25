package com.lzw.flower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.View;

import java.io.IOException;

public class MyActivity extends Activity implements View.OnClickListener {
  /**
   * Called when the activity is first created.
   */
  Camera camera;
  SurfaceView surface;
  int width,height;
  SurfaceHolder holder;
  View captureView;
  boolean isPreview;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    surface= (SurfaceView) findViewById(R.id.surface);
    captureView=findViewById(R.id.capture);
    captureView.setOnClickListener(this);
    holder = surface.getHolder();
    holder.addCallback(new SurfaceCallBack());
    width=surface.getWidth();
    height=surface.getHeight();
    fromHandDraw();
  }

  private void fromHandDraw() {
    Intent intent=getIntent();
    if(intent.getAction().equals("com.lzw.flower.DrawActivity")){
      startPreview();
    }
  }

  void startPreview(){
    if(isPreview==false){
      isPreview=true;
      camera.startPreview();
    }
  }

  void stopPreview(){
    if(isPreview){
      isPreview=false;
      camera.stopPreview();
    }
  }

  public void releaseCamera() {
    if (camera != null)
    {
      if (isPreview) camera.stopPreview();
      camera.release();
      camera = null;
    }
  }

  @Override
  public void onClick(View v) {
    int id=v.getId();
    if(id==R.id.capture){
      capture();
    }
  }

  private void capture() {
    camera.autoFocus(new AutoFocusCallback() {
      @Override
      public void onAutoFocus(boolean success, Camera camera) {
        if(success){
          camera.takePicture(null,null,new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
              stopPreview();
              DrawActivity.imgBytes=data;
              Intent intent=new Intent(MyActivity.this,DrawActivity.class);
              startActivity(intent);
            }
          });
        }
      }
    });
  }

  private class SurfaceCallBack implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      initCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      releaseCamera();
    }
  }

  private void initCamera() {
    camera=Camera.open(0);
    camera.setDisplayOrientation(90);
    if(camera!=null){
      Camera.Parameters params=camera.getParameters();
      params.setPreviewSize(width,height);
      params.setPreviewFpsRange(4,10);
      params.setPictureFormat(ImageFormat.JPEG);
      params.setPictureSize(width,height);
      try {
        camera.setPreviewDisplay(holder);
        startPreview();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
