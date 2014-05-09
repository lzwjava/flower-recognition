package com.lzw.flower;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.lzw.flower.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by lzw on 14-4-29.
 */
public class History {
  Stack<Bitmap> histroy;
  int curPos;

  public History() {
    histroy = new Stack<Bitmap>();
    curPos = -1;
  }

  public void saveToStack(Bitmap cacheBm) {
    Bitmap original = cacheBm;
    Bitmap copy = getCopyBitmap(original);

    Bitmap bm =copy;
    curPos++;
    while (histroy.size() > curPos) {
      histroy.pop();
    }
    //showStack(DrawActivity.instance);
    histroy.push(bm);
  }

  public Bitmap getCopyBitmap(Bitmap original) {
    Bitmap copy = Bitmap.createBitmap(original.getWidth(),
        original.getHeight(), original.getConfig());
    Canvas copiedCanvas = new Canvas(copy);
    copiedCanvas.drawBitmap(original, 0f, 0f, null);
    return copy;
  }

  @Override
  public String toString() {
    return "stack size=" + histroy.size() + " curPos=" + curPos;
  }

  public void showStack(Context cxt) {
    ImageListDialogBuilder builder = new ImageListDialogBuilder(cxt);
    List<Bitmap> imgs = new ArrayList<Bitmap>();
    int n = histroy.size();
    for (int i = 0; i < n; i++) {
      Bitmap bm = histroy.get(n - 1 - i);
      imgs.add(bm);
    }
    builder.setImgs(imgs).show();
  }

  public Bitmap undo() throws UnsupportedOperationException {
    if (canUndo()) {
      curPos--;
      Bitmap bitmap = histroy.get(curPos);
      return getCopyBitmap(bitmap);
    } else {
      throw new UnsupportedOperationException("don't have undo record");
    }
  }

  public Bitmap redo() throws UnsupportedOperationException {
    if (canRedo()) {
      curPos++;
      Bitmap bitmap = histroy.get(curPos);
      return getCopyBitmap(bitmap);
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
}
