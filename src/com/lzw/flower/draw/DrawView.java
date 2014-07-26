package com.lzw.flower.draw;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.lzw.flower.R;
import com.lzw.flower.utils.Utils;

/**
 * Created by lzw on 14-4-25.
 */
public class DrawView extends View {
  public static final int FOREGROUND = 0;
  public static final int BACKGROUND = 1;
  public static final int BACKGROUND_COLOR = Color.BLUE;
  public static final int FOREGROUND_COLOR = Color.RED;
  Canvas cacheCanvas;
  Bitmap cacheBm;
  Paint paint;
  Path path;
  private float preX;
  private float preY;
  History history;
  int drawStyle;
  Context ctx;
  boolean scaleMode = false;
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
  private int strokeWidth;

  public DrawView(Context context, AttributeSet attrs) {
    super(context, attrs);
    ctx = context;
    cacheCanvas = new Canvas();
    path = new Path();
    initPaint();
    history = new History();
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

  public void setSrcBitmap(Bitmap srcBitmap) {
    history.clear();
    path.reset();
    this.srcBitmap = srcBitmap;
    cacheBm =com.lzw.commons.Utils.getCopyBitmap(srcBitmap) ;
    history.setSrcBitmap(cacheBm);
    history.saveToStack(path,paint);
    cacheCanvas.setBitmap(cacheBm);
    initBitmap();
    invalidate();
  }

  public void setScaleMode(boolean scaleMode) {
    this.scaleMode = scaleMode;
  }

  private void initPaint() {
    paint = new Paint(Paint.DITHER_FLAG);
    paint.setStyle(Paint.Style.STROKE);
    strokeWidth = 10;
    paint.setStrokeWidth(strokeWidth);
    paint.setColor(Color.BLUE);
    paint.setDither(true);
    paint.setAntiAlias(true);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (scaleMode == false) {
      int action = event.getAction();
      float x = event.getX();
      float y = event.getY();
      if (action == MotionEvent.ACTION_DOWN) {
        path.moveTo(x, y);
      } else if (action == MotionEvent.ACTION_MOVE) {
        path.quadTo(preX, preY, x, y);
      } else if (action == MotionEvent.ACTION_UP) {
        Matrix matrix1 = new Matrix();
        matrix.invert(matrix1);
        path.transform(matrix1);
        paint.setStrokeWidth(strokeWidth * 1.0f / totalRatio);
        history.saveToStack(path,paint);
        cacheCanvas.drawPath(path, paint);
        paint.setStrokeWidth(strokeWidth);
        path.reset();
      }
      setPrev(event);
      invalidate();
    } else {
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
    }
    return true;
  }

  private void setPrev(MotionEvent event) {
    preX = event.getX();
    preY = event.getY();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (scaleMode) {
      switch (status) {
        case STATUS_MOVE:
          move(canvas);
          break;
        case STATUS_ZOOM_IN:
        case STATUS_ZOOM_OUT:
          zoom(canvas);
          break;
        default:
          if (cacheBm != null) {
            canvas.drawBitmap(cacheBm, matrix, null);
            canvas.drawPath(path, paint);
          }
      }
    } else {
      if (cacheBm != null) {
        canvas.drawBitmap(cacheBm, matrix, null);
        canvas.drawPath(path, paint);
      }
    }
  }

  @Deprecated
  private boolean updatePaint() {
    int curPos = history.getCurPos();
    boolean coundPaint = false;
    if (curPos == 0) {
      paint.setColor(BACKGROUND_COLOR);
      coundPaint = true;
    } else if (curPos == 1) {
      paint.setColor(FOREGROUND_COLOR);
      coundPaint = true;
    }
    return coundPaint;
  }

  public boolean isDrawFinish() {
    int curPos = history.getCurPos();
    return curPos >= 2;
  }

  public Bitmap getCacheBm() {
    return cacheBm;
  }

  public void undo() {
    if (history.canUndo()) {
      Bitmap bm = history.undo();
      invalidateCanvas(bm);
    } else {
      Utils.toast(getContext(), R.string.no_more);
    }
  }

  private void invalidateCanvas(Bitmap bm) {
    cacheBm = bm;
    cacheCanvas.setBitmap(cacheBm);
    invalidate();
  }

  public void redo() {
    if (history.canRedo()) {
      Bitmap bm = history.redo();
      invalidateCanvas(bm);
    } else {
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

  public Bitmap getSrcBitmap() {
    return srcBitmap;
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


  private void move(Canvas canvas) {
    matrix.reset();
    matrix.postScale(totalRatio, totalRatio);
    totalTranslateX = moveDistX + totalTranslateX;
    totalTranslateY = moveDistY + totalTranslateY;
    matrix.postTranslate(totalTranslateX, totalTranslateY);
    canvas.drawBitmap(cacheBm, matrix, null);
  }

  private void zoom(Canvas canvas) {
    matrix.reset();
    matrix.postScale(totalRatio, totalRatio);
    int scaledWidth = (int) (cacheBm.getWidth() * totalRatio);
    int scaledHeight = (int) (cacheBm.getHeight() * totalRatio);
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
    canvas.drawBitmap(cacheBm, matrix, null);
  }

  private void initBitmap() {
    matrix.reset();
    int bitmapWidth = cacheBm.getWidth();
    int bitmapHeight = cacheBm.getHeight();
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
      curBitmapWidth = (int) (ratio * cacheBm.getWidth());
      curBitmapHeight = (int) (ratio * cacheBm.getHeight());
    } else {
      int translateX = (width - bitmapWidth) / 2;
      int translateY = (height - bitmapHeight) / 2;
      matrix.postTranslate(translateX, translateY);
      totalTranslateX = translateX;
      totalTranslateY = translateY;
      totalRatio = initRatio = 1;
      curBitmapWidth = cacheBm.getWidth();
      curBitmapHeight = cacheBm.getHeight();
    }
  }

  public Bitmap getHandBitmap() {
    return history.getHandBitmap();
  }
}
