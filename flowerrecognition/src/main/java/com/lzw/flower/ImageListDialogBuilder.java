package com.lzw.flower;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-4-29.
 */
public class ImageListDialogBuilder extends AlertDialog.Builder {
  List<Bitmap> imgs=new ArrayList<Bitmap>();
  Context cxt;
  public ImageListDialogBuilder(Context context) {
    super(context);
    this.cxt = context;
  }

  @Override
  public AlertDialog show(){
    final LayoutInflater inflater = LayoutInflater.from(cxt);
    View v = inflater.inflate(R.layout.stack_list, null);
    setView(v);
    ListView listView = (ListView) v.findViewById(R.id.listView);
    listView.setAdapter(new BaseAdapter() {
      @Override
      public int getCount() {
        return imgs.size();
      }

      @Override
      public Object getItem(int position) {
        return null;
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public View getView(int position, View conView, ViewGroup parent) {
        if(conView==null){
          conView=inflater.inflate(R.layout.image_item,null);
          ImageView imgView= (ImageView) conView.findViewById(R.id.img);
          conView.setTag(imgView);
        }
        ImageView imgView = (ImageView) conView.getTag();
        imgView.setImageBitmap(imgs.get(position));
        return conView;
      }
    });
    return super.show();
  }

  public ImageListDialogBuilder setImgs(List<Bitmap> imgs) {
    this.imgs = imgs;
    return this;
  }
}
