package com.lzw.flower.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lzw on 14-7-26.
 */
public class ZoomImageView extends View {
  private static final int STATUS_INIT = 0;
  private static final int STATUS_ZOOM_OUT = 1;
  private static final int STATUS_ZOOM_IN = 2;
  private static final int STATUS_MOVE = 3;
  Bitmap srcBitmap;
  Matrix matrix = new Matrix();
  int width;
  int height;
  private float initRatio;
  private int totalTranslateX;
  private int totalTranslateY;
  int centerPointX;
  int centerPointY;
  float totalRatio;
  int status;
  private float lastFingerDist;
  private int curBitmapWidth;
  private int curBitmapHeight;
  private float scaledRatio;
  private float lastMoveX;
  private float lastMoveY;
  private int moveDistY;
  private int moveDistX;

  public ZoomImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    status = STATUS_INIT;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (changed) {
      width = getWidth();
      height = getHeight();
    }
  }

  void setImageBitmap(Bitmap bitmap) {
    srcBitmap = bitmap;
    invalidate();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_POINTER_DOWN:
        lastFingerDist = calFingerDistance(event);
        break;
      case MotionEvent.ACTION_MOVE:
        if (event.getPointerCount() == 1) {
          float moveX = event.getX();
          float moveY = event.getY();
          if (lastMoveX == -1 && lastMoveY == -1) {
            lastMoveY = moveY;
            lastMoveX = moveX;
          }
          moveDistX = (int) (moveX - lastMoveX);
          moveDistY = (int) (moveY - lastMoveY);
          if (moveDistX + totalTranslateX > 0 || moveDistX + totalTranslateX + curBitmapWidth < width) {
            moveDistX = 0;
          }
          if (moveDistY + totalTranslateY > 0 || moveDistY + totalTranslateY + curBitmapHeight < height) {
            moveDistY = 0;
          }
          status = STATUS_MOVE;
          invalidate();
          lastMoveX = moveX;
          lastMoveY = moveY;
        } else if (event.getPointerCount() == 2) {
          float fingerDist = calFingerDistance(event);
          calFingerCenter(event);
          if (fingerDist > lastFingerDist) {
            status = STATUS_ZOOM_OUT;
          } else {
            status = STATUS_ZOOM_IN;
          }
          scaledRatio = fingerDist * 1.0f / lastFingerDist;
          totalRatio = totalRatio * scaledRatio;
          if (totalRatio < initRatio) {
            totalRatio = initRatio;
          } else if (totalRatio > initRatio * 4) {
            totalRatio = initRatio * 4;
          }
          lastFingerDist = fingerDist;
          invalidate();
        }
        break;
      case MotionEvent.ACTION_UP:
        lastMoveX = -1;
        lastMoveY = -1;
        break;
      case MotionEvent.ACTION_POINTER_UP:
        lastMoveX = -1;
        lastMoveY = -1;
        break;
      default:
        break;
    }
    return true;
  }

  private void calFingerCenter(MotionEvent event) {
    centerPointX = (int) ((event.getX(0) + event.getX(1)) / 2);
    centerPointY = (int) ((event.getY(0) + event.getY(1)) / 2);
  }

  private float calFingerDistance(MotionEvent event) {
    float dx = event.getX(0) - event.getX(1);
    float dy = event.getY(0) - event.getY(1);
    float hypot = (float) Math.hypot(dx, dy);
    return hypot;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    switch (status) {
      case STATUS_MOVE:
        move(canvas);
        break;
      case STATUS_INIT:
        initBitmap(canvas);
        break;
      case STATUS_ZOOM_IN:
      case STATUS_ZOOM_OUT:
        zoom(canvas);
        break;
      default:
        canvas.drawBitmap(srcBitmap, matrix, null);
    }
  }

  private void move(Canvas canvas) {
    matrix.reset();
    matrix.postScale(totalRatio, totalRatio);
    totalTranslateX = moveDistX + totalTranslateX;
    totalTranslateY = moveDistY + totalTranslateY;
    matrix.postTranslate(totalTranslateX, totalTranslateY);
    canvas.drawBitmap(srcBitmap, matrix, null);
  }

  private void zoom(Canvas canvas) {
    matrix.reset();
    matrix.postScale(totalRatio, totalRatio);
    int scaledWidth = (int) (srcBitmap.getWidth() * totalRatio);
    int scaledHeight = (int) (srcBitmap.getHeight() * totalRatio);
    int translateX;
    int translateY;
    if (curBitmapWidth < width) {
      translateX = (width - scaledWidth) / 2;
    } else {
      translateX = (int) (centerPointX + (totalTranslateX - centerPointX) * scaledRatio);
      if (translateX > 0) {
        translateX = 0;
      } else if (scaledWidth + translateX < width) {
        translateX = width - scaledWidth;
      }
    }
    if (curBitmapHeight < height) {
      translateY = (height - scaledHeight) / 2;
    } else {
      translateY = (int) (centerPointY + (totalTranslateY - centerPointY) * scaledRatio);
      if (translateY > 0) {
        translateY = 0;
      } else if (scaledHeight + translateY < height) {
        translateY = height - scaledHeight;
      }
    }
    totalTranslateX = translateX;
    totalTranslateY = translateY;
    curBitmapWidth = scaledWidth;
    curBitmapHeight = scaledHeight;
    matrix.postTranslate(translateX, translateY);
    canvas.drawBitmap(srcBitmap, matrix, null);
  }

  private void initBitmap(Canvas canvas) {
    matrix.reset();
    int bitmapWidth = srcBitmap.getWidth();
    int bitmapHeight = srcBitmap.getHeight();
    if (bitmapWidth > width || bitmapHeight > height) {
      int translateX = 0, translateY = 0;
      float ratio = 0;
      if (bitmapWidth - width > bitmapHeight - height) {
        ratio = width * 1.0f / bitmapWidth;
        matrix.postScale(ratio, ratio);
        int rationedHeight = (int) (bitmapHeight * ratio);
        translateY = (height - rationedHeight) / 2;
        matrix.postTranslate(0, translateY);
      } else {
        ratio = height * 1.0f / bitmapHeight;
        matrix.postScale(ratio, ratio);
        int rationedwidth = (int) (bitmapWidth * ratio);
        translateX = (width - rationedwidth) / 2;
        matrix.postTranslate(translateX, 0);
      }
      totalRatio = initRatio = ratio;
      totalTranslateX = translateX;
      totalTranslateY = translateY;
      curBitmapWidth = (int) (ratio * srcBitmap.getWidth());
      curBitmapHeight = (int) (ratio * srcBitmap.getHeight());
    } else {
      int translateX = (width - bitmapWidth) / 2;
      int translateY = (height - bitmapHeight) / 2;
      matrix.postTranslate(translateX, translateY);
      totalTranslateX = translateX;
      totalTranslateY = translateY;
      totalRatio = initRatio = 1;
      curBitmapWidth = srcBitmap.getWidth();
      curBitmapHeight = srcBitmap.getHeight();
    }
    canvas.drawBitmap(srcBitmap, matrix, null);
  }
}
