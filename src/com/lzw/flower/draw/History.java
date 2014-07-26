package com.lzw.flower.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import com.lzw.commons.Utils;
import com.lzw.flower.utils.ImageListDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by lzw on 14-4-29.
 */
public class History {
  Stack<Draw> histroy;
  CallBack callBack;
  int curPos;
  Bitmap srcBitmap;

  public History() {
    histroy = new Stack<Draw>();
    curPos = -1;
  }

  public void setSrcBitmap(Bitmap srcBitmap) {
    this.srcBitmap = srcBitmap;
  }

  public void setCallBack(CallBack callBack) {
    this.callBack = callBack;
  }

  public void saveToStack(Path path,Paint paint){
    Draw draw=new Draw();
    draw.path=new Path(path);
    draw.paint=new Paint(paint);
    saveToStack(draw);
  }

  public void saveToStack(Draw draw) {
    curPos++;
    while (histroy.size() > curPos) {
      histroy.pop();
    }
    //showStack(DrawActivity.instance);
    histroy.push(draw);
    Log.d("lzw",toString()+"");
    if (callBack != null) {
      callBack.onHistoryChanged();
    }
  }

  @Override
  public String toString() {
    return "stack size=" + histroy.size() + " curPos=" + curPos;
  }

  public Bitmap getBitmapAtDraw(int n) {
    Log.d("lzw","get bitmap at "+n);
    Canvas canvas = new Canvas();
    Bitmap bm = Utils.getCopyBitmap(srcBitmap);
    canvas.setBitmap(bm);
    for (int i = 0; i <= n; i++) {
      Draw draw = histroy.get(i);
      canvas.drawPath(draw.path, draw.paint);
    }
    return bm;
  }

  public void showStack(Context cxt) {
    ImageListDialogBuilder builder = new ImageListDialogBuilder(cxt);
    List<Bitmap> imgs = new ArrayList<Bitmap>();
    int n = histroy.size();
    for (int i = 0; i < n; i++) {
      imgs.add(getBitmapAtDraw(n - 1 - i));
    }
    builder.setImgs(imgs).show();
  }

  public Bitmap undo() throws UnsupportedOperationException {
    if (canUndo()) {
      curPos--;
      if (callBack != null) {
        callBack.onHistoryChanged();
      }
      Bitmap bitmap = getBitmapAtDraw(curPos);
      return bitmap;
    } else {
      throw new UnsupportedOperationException("don't have undo record");
    }
  }

  public Bitmap redo() throws UnsupportedOperationException {
    if (canRedo()) {
      curPos++;
      if (callBack != null) {
        callBack.onHistoryChanged();
      }
      Bitmap bitmap = getBitmapAtDraw(curPos);
      return bitmap;
    } else {
      throw new UnsupportedOperationException("don't have redo record");
    }
  }


  public boolean canUndo() {
    return curPos > 0;
  }

  public boolean canRedo() {
    return curPos + 1 < histroy.size();
  }

  public int getSize() {
    return histroy.size();
  }

  public int getCurPos() {
    return curPos;
  }

  public void clear() {
    curPos = -1;
    histroy.clear();
  }

  public Bitmap getHandBitmap() {
    Canvas canvas = new Canvas();
    Bitmap bm = Utils.getEmptyBitmap(srcBitmap.getWidth(), srcBitmap.getHeight());
    canvas.setBitmap(bm);
    for (int i = 0; i <= curPos; i++) {
      Draw draw = histroy.get(i);
      canvas.drawPath(draw.path, draw.paint);
    }
    return bm;
  }

  public interface CallBack {
    void onHistoryChanged();
  }
}
