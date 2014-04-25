package com.lzw.flower;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lzw on 14-4-25.
 */
public class DrawView extends View {
  Canvas cacheCanvas;
  Bitmap cacheBm;
  Paint paint;
  Path path;
  private float preX;
  private float preY;

  public DrawView(Context context, AttributeSet attrs) {
    super(context, attrs);
    DisplayMetrics metrics=getResources().getDisplayMetrics();
    int w=metrics.widthPixels;
    int h=metrics.heightPixels;
    cacheBm =Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
    cacheCanvas=new Canvas();
    cacheCanvas.setBitmap(cacheBm);
    path=new Path();
    initPaint();
  }

  private void initPaint() {
    paint=new Paint(Paint.DITHER_FLAG);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(5);
    paint.setDither(true);
    paint.setAntiAlias(true);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action=event.getAction();
    float x=event.getX();
    float y=event.getY();
    if(action==MotionEvent.ACTION_DOWN){
      path.moveTo(x,y);
      setPrev(event);
    }else if(action==MotionEvent.ACTION_MOVE){
      path.quadTo(preX,preY,x,y);
      setPrev(event);
    }else if(action==MotionEvent.ACTION_UP){
      cacheCanvas.drawPath(path,paint);
      path.reset();
    }
    invalidate();
    return true;
  }

  private void setPrev(MotionEvent event) {
    preX =event.getX();
    preY =event.getY();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Paint p=new Paint();
    canvas.drawBitmap(cacheBm,0,0,p);
    canvas.drawPath(path,paint);
  }
}
