package com.lzw.flower.draw;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.lzw.flower.utils.Logger;

/**
 * Created by lzw on 14-5-20.
 */
public class DrawRectView extends View {
  float curX;
  float curY;
  float startX;
  float startY;
  Paint paint;
  Rect rect;
  boolean isDraw=false;
  OnFinishListener finishListener;

  public DrawRectView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initPaint();
  }

  public void setOnFinishListener(OnFinishListener finishListener) {
    this.finishListener = finishListener;
  }

  private void initPaint() {
    paint=new Paint();
    paint.setColor(Color.YELLOW);
    paint.setDither(true);
    paint.setStrokeWidth(10);
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.STROKE);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    super.onTouchEvent(event);
    int action=event.getAction();
    if(isDraw==false){
      invalidate();
      Logger.d("isDraw is false and return");
      return false;
    }
    if(action==MotionEvent.ACTION_DOWN){
      startX=event.getX();
      startY=event.getY();
    }else if(action==MotionEvent.ACTION_MOVE){
    }else if(action==MotionEvent.ACTION_UP){
      if(finishListener!=null){
        finishListener.onFinish(rect);
      }
    }
    setCurXY(event);
    invalidate();
     return true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if(isDraw){
      rect=new Rect((int)startX,(int)startY,(int)curX,(int)curY);
    }
    if(rect!=null){
      canvas.drawRect(rect,paint);
    }
  }

  private void setCurXY(MotionEvent event) {
    curX =event.getX();
    curY =event.getY();
  }

  public void setDraw(boolean b) {
    isDraw=b;
  }

  public void clear() {
    rect=null;
    requestLayout();
  }

  public Rect getRect() {
    return rect;
  }

  interface OnFinishListener {
    void onFinish(Rect rect);
  }
}
