package com.lzw.flower;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.lzw.flower.Utils.Utils;

/**
 * Created by lzw on 14-4-25.
 */
public class DrawView extends View {
  public static final int FOREGROUND = 0;
  public static final int BACKGROUND = 1;
  public static final int BACKGROUND_COLOR = Color.BLUE;
  public static final int FOREGROUND_COLOR = Color.RED;
  public static final int WIDTH = App.drawWidth;
  public static final int HEIGHT = App.drawHeight;
  Canvas cacheCanvas;
  Bitmap cacheBm;
  Paint paint;
  Path path;
  private float preX;
  private float preY;
  History history;
  int drawStyle;
  Bitmap originBitmap;

  public DrawView(Context context, AttributeSet attrs) {
    super(context, attrs);
    cacheCanvas=new Canvas();
    path=new Path();
    initPaint();
    clear(null);
  }

  public void clear(ImageView img) {
    cacheBm=getEmptyBitmap(getContext(), WIDTH, HEIGHT);
    cacheCanvas.setBitmap(cacheBm);
    history=new History();
    history.saveToStack(cacheBm);
    path.reset();
    invalidate();
  }

  public Bitmap getOriginBitmap() {
    return originBitmap;
  }

  public void setOriginBitmap(Bitmap originBitmap,ImageView img) {
    this.originBitmap=originBitmap;
    clear(img);
  }

  public static Bitmap getEmptyBitmap(Context cxt,int w,int h) {
    return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
  }

  private void initPaint() {
    paint=new Paint(Paint.DITHER_FLAG);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(10);
    paint.setColor(Color.BLUE);
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
      if(updatePaint()){
        cacheCanvas.drawPath(path,paint);
        history.saveToStack(cacheBm);
      }
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
    if(cacheBm!=null){
      canvas.drawBitmap(cacheBm,0,0,p);
    }
    if(updatePaint()){
      canvas.drawPath(path,paint);
    }
  }

  private boolean updatePaint() {
    int curPos=history.getCurPos();
    boolean coundPaint=false;
    if(curPos==0){
      paint.setColor(BACKGROUND_COLOR);
      coundPaint=true;
    }else if(curPos==1){
      paint.setColor(FOREGROUND_COLOR);
      coundPaint=true;
    }
    return coundPaint;
  }

  public boolean isDrawFinish(){
    int curPos = history.getCurPos();
    return curPos==2;
  }

  public Bitmap getCacheBm() {
    return cacheBm;
  }

  public void undo() {
    if(history.canUndo()){
       Bitmap bm=history.undo();
       invalidateCanvas(bm);
    }else{
      Utils.toast(getContext(), R.string.no_more);
    }
  }

  private void invalidateCanvas(Bitmap bm) {
    cacheBm=bm;
    cacheCanvas.setBitmap(bm);
    invalidate();
  }

  public void redo() {
    if(history.canRedo()){
      Bitmap bm=history.redo();
      invalidateCanvas(bm);
    }else{
      Utils.toast(getContext(), R.string.no_more);
    }
  }


  public void drawBack() {
    drawStyle = BACKGROUND;
    paint.setColor(BACKGROUND_COLOR);
  }

  public void drawFore() {
    drawStyle = FOREGROUND;
    paint.setColor(FOREGROUND_COLOR);
  }
}
