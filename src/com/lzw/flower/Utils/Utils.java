package com.lzw.flower.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;
import com.lzw.flower.DrawActivity;

/**
 * Created by lzw on 14-4-29.
 */
public class Utils {
  public static void toast(Context context,int strId){
    toastIt(context,strId,false);
  }

  private static void toastIt(Context context, int strId, boolean isLong) {
    int ti;
    if(isLong) ti= Toast.LENGTH_LONG;
    else ti=Toast.LENGTH_SHORT;
    Toast.makeText(context, context.getString(strId), ti).show();
  }

  public static void toastLong(Context context,int strId){
    toastIt(context, strId,true);
  }

  public static void showImage(Activity activity, Bitmap pop) {
    ImageView view=new ImageView(activity);
    view.setImageBitmap(pop);
    new AlertDialog.Builder(activity).setView(view).show();
  }
}
