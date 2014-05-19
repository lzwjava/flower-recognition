package com.lzw.flower.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.lzw.flower.R;

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

  public static void alertDialog(Activity activity, int msgId) {
    new AlertDialog.Builder(activity).
        setMessage(activity.getString(msgId)).show();
  }

  public static void getGalleryPhoto(Activity cxt,int returnCode) {
    Intent intent = new Intent();
    intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
    cxt.startActivityForResult(intent, returnCode);
  }


  public static ProgressDialog showSpinnerDialog(Activity activity){
    ProgressDialog dialog = new ProgressDialog(activity);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
    int w=metrics.widthPixels/2;
    int h=metrics.heightPixels/2;
    Window window = dialog.getWindow();
    WindowManager.LayoutParams lp=window.getAttributes();
    lp.gravity= Gravity.CENTER;
    dialog.onWindowAttributesChanged(lp);
    dialog.setCancelable(true);
    dialog.show();
    dialog.setContentView(R.layout.spinner_dialog);
    return dialog;
  }

  public static AlertDialog showSpinnerAlert(Activity activity){
    ProgressDialog dialog=new ProgressDialog(activity);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setMessage(activity.getResources().getString(R.string.is_loading));
    dialog.show();
    return dialog;
  }
}
